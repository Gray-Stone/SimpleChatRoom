package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.IllegalBlockingModeException;

public class Acceptor extends Thread {

    ClientData data;
    public int runCountA = 0;

    Acceptor(ClientData data) throws IOException {
        data.chtSev = new ServerSocket(data.portNum);
        this.data = data;
    }

    public void run() {
        System.out.println("Waiting for clients to connect ");
        this.setName("AcceptorThread");
        while (true) {
            runAcceptor();
        }
    }

    // task for accepting user
    public void runAcceptor() {
        Client tempCli;
        int idCount = 0;
        // wait for connection
        Socket soc1 = null;
        try {
            soc1 = data.chtSev.accept();
        } catch (IOException e) {
            System.out.println("Error trying to accept client, retrying");
            return;
        } catch (IllegalBlockingModeException eB) {
            System.out.println("Error trying to accept client, terminating");
        }

        // construct a new user
        System.out.printf("Client #%d Connected : %s \n", idCount, soc1.getRemoteSocketAddress().toString());
        tempCli = new Client(soc1, idCount);
        if (tempCli.alive == false) // check if any problem with initialization.
        {
            System.out.printf("ID: %d : Fail to initialize \n ", idCount);
            tempCli.closeSoc();
            return; // don't bother with this client anymore
        }
        if (tempCli.getNickName() == false) {
            System.out.printf("ID: %d : Fail to get a nickname \n ", idCount);
            tempCli.closeSoc();
            return ; // don't bother with this client anymore
        }

        //TODO check duplicate name
        for (Client c : data.clientHashMap.values()) {
            if (c.nickName.equals(tempCli.nickName)) {
                System.out.printf("Client ID: %d nickname conflict with client ID : %d.", tempCli.ID, c.ID);
                tempCli.nickName = tempCli.nickName + String.valueOf(tempCli.ID);
                System.out.printf("Client ID: %d :new nickname ,%s \n", tempCli.ID, tempCli.ID);
            }
        }

        // the client is alive and working. can be added to the clientList
        data.manageClientMap(ClientData.mapAction.add, tempCli);

        // now this client can start running it's receiver thread
        tempCli.start();

        idCount++;
        runCountA++;
    }
}






