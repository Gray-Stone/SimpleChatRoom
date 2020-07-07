package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.IllegalBlockingModeException;
import java.util.HashMap;

public class ClientServer {

    ServerSocket chtSev;
    HashMap<Integer,Client> clientHashMap = new HashMap<Integer, Client>();;
    HashMap<String, Integer> nameHashMap = new HashMap<String, Integer>();
    volatile int newClientID = -1 ;
    volatile int removedClientID = -1 ;
    enum mapAction {add,remove}

    Acceptor acceptor;
    Manager manager;

//    constructor
    ClientServer(int portNum) throws IOException {
        chtSev = new ServerSocket(portNum);
        acceptor = new Acceptor();
        manager = new Manager();

    }

    class Acceptor extends Thread{

        public int runCountA =0;

        @Override
        // task for accepting user
        public synchronized void  run()  {
            this.setName("AcceptorThread");
            Client tempCli ;
            int idCount =0;
            while(true)
            {
                // wait for connection
                System.out.println("waiting for clients to connect ");
                Socket soc1 = null;
                try {
                    soc1 = chtSev.accept();
                } catch (IOException e) {
                    System.out.println("Error trying to accept client, retrying");
                    continue;
                } catch (IllegalBlockingModeException eB){
                    System.out.println("Error trying to accept client, terminating");
                }

                // construct a new user
                System.out.printf("Client #%d Connected : %s \n", idCount, soc1.getRemoteSocketAddress().toString());
                tempCli = new Client(soc1,idCount);
                if (tempCli.alive == false) // check if any problem with initialization.
                {
                    System.out.printf("ID: %d : Fail to initialize \n ", idCount);
                    tempCli.closeSoc();
                    continue; // don't bother with this client anymore
                }
                if ( tempCli.getNickName() ==false)
                {
                    System.out.printf("ID: %d : Fail to get a nickname \n ", idCount);
                    tempCli.closeSoc();
                    continue; // don't bother with this client anymore
                }

                //TODO check duplicate name
                for (Client c : clientHashMap.values())
                {
                    if (c.nickName.equals(tempCli.nickName))
                    {
                        System.out.printf("Client ID: %d nickname conflict with client ID : %d.",tempCli.ID,c.ID);
                        tempCli.nickName = tempCli.nickName + String.valueOf(tempCli.ID);
                        System.out.printf("Client ID: %d :new nickname ,%s \n",tempCli.ID,tempCli.ID);
                    }
                }

                // the client is alive and working. can be added to the clientList
                manageClientMap (mapAction.add, tempCli);

                // now this client can start running it's receiver thread
                tempCli.start();

                idCount++;
                runCountA++;

                try {
                    this.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class Manager extends Thread{

        public int runCountM =0;

        @Override
        public synchronized void run()
        {
            System.out.println("Managing all the clients");
            this.setName("ManagerThread");
            this.setPriority(7);
            while(true)
            {
                if (clientHashMap.isEmpty()) {continue;}
                for (Client cli : clientHashMap.values() )
                {
                    System.out.printf(" .. Manage user: %s ID: %d \n",cli.nickName,cli.ID);

                    // check if user need to be removed
                    if (cli.alive == false)
                    {
                        // TODO declear the death of this client
                        cli.closeSoc();
                        clientHashMap.remove(cli.ID);
                        continue;
                    }

                    // there are new messages to be process.
                    if (cli.newMessage.fetched == false)
                    {
                        // Private msg format: P:toName:message
                        // BroadCast msg format: B:message
                        processMessage(cli);
                    }
                }

                runCountM++;
                try {
                    this.sleep(5);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    void processMessage(Client cli)
    {
        // Private msg format: P:toName:message
        // BroadCast msg format: B:message

        System.out.printf("Message %s from client %d \n",cli.newMessage.msg, cli.ID);
        String[] tokens = cli.newMessage.msg.split(":");
        cli.newMessage.fetched = true;

        if (tokens.length==2)
        {
            for( Client c : clientHashMap.values() )
            {
                c.sendMsg(cli.nickName +" : "+ tokens[1]);
            }
        }
        // this is a private message
        else if (tokens.length ==3 ) {
            Client sendClient = null;
            for( Client c : clientHashMap.values() )
            {
                if(c.nickName.equals(tokens[1])) {
                    sendClient = c;
                }
            }
            if (sendClient == null){
                cli.sendMsg(">>SYS: wrong private message target<<");
            }
            else {
                sendClient.sendMsg(" private from " + cli.nickName + " : " + tokens[2]);
            }
        }
        // the split is incorrect
        else { cli.sendMsg(">>SYS: wrong message target format<<"); }
    }


    synchronized void manageClientMap ( mapAction action , Client client )
    {
        switch (action) {
            case add -> {

                // notify other users
                if(clientHashMap.values() != null)
                {
                    for (Client c : clientHashMap.values())
                    {
                        c.sendMsg(">>SYS: " + client.nickName + " has joined<<");
                    }
                }

                clientHashMap.put(client.ID, client);
                nameHashMap.put(client.nickName,client.ID);
                newClientID = client.ID;

            }
            case remove -> {
                clientHashMap.remove(client.ID);
                nameHashMap.remove(client.nickName);
                removedClientID = client.ID;

                // notify remaining user
                if(clientHashMap.values() != null)
                {
                    for (Client c : clientHashMap.values())
                    {
                        c.sendMsg(">>SYS: " + client.nickName + " has left<<");
                    }
                }
            }
        }
    }



}
