package com.company;

import java.net.ServerSocket;
import java.util.HashMap;

public class ClientData {

    int portNum =0;
    ServerSocket chtSev;
    HashMap<Integer,Client> clientHashMap = new HashMap<Integer, Client>();;
    HashMap<String, Integer> nameHashMap = new HashMap<String, Integer>();

    volatile boolean clientHashMapAvailable=true;

    volatile int newClientID = -1 ;
    volatile int removedClientID = -1 ;
    enum mapAction {add,remove}
    String allUserNickName = "All User";


//    constructor
    ClientData(int portNum) {
        this .portNum =portNum;
    }


    void processMessage(Client cli)
    {
        // Private msg format: P:toName:message
        // BroadCast msg format: B:message

        System.out.printf("Message %s from client %d \n",cli.newMessage.msg, cli.ID);
        String msg =  cli.newMessage.msg;
        cli.newMessage.fetched = true;

        // public message
        if (msg.substring(0,2).equals("B:"))
        {
            for( Client c : clientHashMap.values() )
            {
                c.sendMsg(cli.nickName +": "+ msg.substring(2));
            }
            return;
        }
        // this is a private message
        else if (msg.substring(0,2).equals("P:") ) {
            Client sendClient = null;
            String[] tokens = msg.split(":");
            if (tokens.length<2)
            {
                cli.sendMsg("\t\t>>SEV: wrong private message format<<");
                return ;
            }
            for( Client c : clientHashMap.values() )
            {
                if(c.nickName.equals(tokens[1])) {
                    sendClient = c;
                }
            }
            if (sendClient == null){
                cli.sendMsg("\t\t>>SEV: wrong private message target<<");
                return ;
            }
            sendClient.sendMsg("Private message from " + cli.nickName +": "  + msg.substring(msg.indexOf(":",2)+1) );
            cli.sendMsg("Private message to " + sendClient.nickName +": "  + msg.substring(msg.indexOf(":",2)+1) );
            return ;
        }
        // the split is incorrect
        cli.sendMsg("\t\t>>SEV: wrong message target format<<");
        return ;
    }


    synchronized void manageClientMap ( mapAction action , Client client )
    {
        while(clientHashMapAvailable = false){
            ;
        }

        try {
            clientHashMapAvailable = false;
            switch (action) {
                case add -> {
                    System.out.println("\t\t>>SEV: " + client.nickName + " has joined<<");

                    clientHashMap.put(client.ID, client);
                    nameHashMap.put(client.nickName, client.ID);
                    newClientID = client.ID;

                    // notify all users with namelist and contant
                    String userList = generateUserList();
                    for (Client c : clientHashMap.values()) {
                        c.sendMsg("\t\t>>SEV: " + client.nickName + " has joined<<");
                        c.sendMsg(userList);
                    }

                }
                case remove -> {

                    System.out.printf("ID: %d : %s has left" ,client.ID , client.nickName );

                    clientHashMap.remove(client.ID);
                    nameHashMap.remove(client.nickName);
                    removedClientID = client.ID;

                    // notify remaining user
                    String userList = generateUserList();
                    for (Client c : clientHashMap.values()) {
                        c.sendMsg(userList);
                        c.sendMsg("\t\t>>SEV: " + client.nickName + " has left<<");
                    }
                }
            }
        } finally {
            clientHashMapAvailable = true;
        }
    }

    String generateUserList()
    {
        String userList =allUserNickName;
        for (Client c : clientHashMap.values())
        {
            userList = userList+ ":" + c.nickName;
        }
        return userList;
    }



}
