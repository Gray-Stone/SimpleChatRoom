package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.IllegalBlockingModeException;
import java.util.HashMap;

public class ClientData {

    int portNum =0;
    ServerSocket chtSev;
    HashMap<Integer,Client> clientHashMap = new HashMap<Integer, Client>();;
    HashMap<String, Integer> nameHashMap = new HashMap<String, Integer>();
    volatile int newClientID = -1 ;
    volatile int removedClientID = -1 ;
    enum mapAction {add,remove}


//    constructor
    ClientData(int portNum) {
        this .portNum =portNum;
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
                for (Client c : clientHashMap.values())
                {
                    c.sendMsg(">>SYS: " + client.nickName + " has joined<<");
                }
                System.out.println(">>SYS: " + client.nickName + " has joined<<");

                clientHashMap.put(client.ID, client);
                nameHashMap.put(client.nickName,client.ID);
                newClientID = client.ID;

            }
            case remove -> {

                System.out.println(">>SYS: " + client.nickName + " has left<<");

                clientHashMap.remove(client.ID);
                nameHashMap.remove(client.nickName);
                removedClientID = client.ID;

                // notify remaining user
                for (Client c : clientHashMap.values())
                {
                    c.sendMsg(">>SYS: " + client.nickName + " has left<<");
                }
            }
        }
    }



}
