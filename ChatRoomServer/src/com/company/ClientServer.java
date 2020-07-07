package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.IllegalBlockingModeException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

public class ClientServer {

    ServerSocket chtSev;
    HashMap<Integer,Client> clientHashMap = new HashMap<Integer, Client>();;
    HashMap<String, Integer> nameHashMap = new HashMap<String, Integer>();
    volatile int newClientID = -1 ;
    volatile int removedClientID = -1 ;
    enum mapAction {add,remove}

    Thread acceptor;
    Thread manager;

//    constructor
    ClientServer(int portNum) throws IOException {
        ServerSocket chtSev = new ServerSocket(portNum);
        acceptor = new Acceptor();
        manager = new Manager();
    }

    class Acceptor extends Thread{

        @Override
        // task for accepting user
        public synchronized void  run()  {
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

                // the client is alive and working. can be added to the clientList
                manageClientMap (mapAction.add, tempCli);

                // now this client can start running it's receiver thread
                tempCli.start();

                idCount++;
            }
        }
    }

    class Manager extends Thread{
        @Override
        public synchronized void run()
        {
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
                        // Private msg format: P|toID|message
                        // BroadCast msg format: B|message
                        processMessage(cli);
                    }
                }
            }
        }
    }


    void processMessage(Client cli)
    {
        String[] tokens = cli.newMessage.msg.split("|");
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
            try{
                if (clientHashMap.containsKey(Integer.valueOf(tokens[1]))){
                    for( Client c : clientHashMap.values() )
                    {
                        if( c.ID == (Integer.valueOf(tokens[1])) ) {
                            c.sendMsg(" private from " + cli.nickName +" : "+ tokens[1]);
                        }
                    }
                }
                else { throw new NumberFormatException();
                }

            } catch (NumberFormatException e) {
                System.out.printf(" ID: %d private message target error \n",cli.nickName,cli.ID);
                cli.sendMsg("SYS: wrong private message target");
            }
        }
    }


    synchronized void manageClientMap ( mapAction action , Client client )
    {
        switch (action) {
            case add -> {
                clientHashMap.put(client.ID, client);
                nameHashMap.put(client.nickName,client.ID);
                newClientID = client.ID;
            }
            case remove -> {
                clientHashMap.remove(client.ID);
                nameHashMap.remove(client.nickName);
                removedClientID = client.ID;
            }
        }
    }

}
