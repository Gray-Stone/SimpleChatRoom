package com.company;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.channels.IllegalBlockingModeException;

public class Acceptor extends Thread {

    ClientData data;
    public int runCountA = 0;
    int idCount = 0;


    Acceptor(ClientData data) throws IOException {
        data.chtSev = new ServerSocket(data.portNum);
        this.data = data;
    }

    public void run() {
        System.out.println("Waiting for clients to connect ");
        this.setName("AcceptorThread");
        while (true) {
            try {
                runAcceptor();
            }catch (Exception e){
                System.out.println("ERROR in Acceptor Thread");
                e.printStackTrace();
            }
        }
    }

    // task for accepting user
    public void runAcceptor() {
        Client tempCli;
        // wait for connection
        Socket soc1 = null;
        System.out.println("");
        try {
            soc1 = data.chtSev.accept();
            idCount++;

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

        tempCli.sendMsg("\t\t>>SEV: Connected");

        if (tempCli.receiveNickName() == false) {
            System.out.printf("ID: %d : Fail to get a nickname \n ", idCount);
            tempCli.closeSoc();
            return ; // don't bother with this client anymore
        }

        //TODO check inValid name
        if (data.allUserNickName.equals(tempCli.nickName)) {
            System.out.printf("Client ID: %d nickname Invalid\n", tempCli.ID);
            tempCli.sendMsg("\t\t>>SEV : nickname is invalid.");
            tempCli.closeSoc();
            return ;
        }

        for (Client c : data.clientHashMap.values()) {
            if (c.nickName.equals(tempCli.nickName)) {
                System.out.printf("Client ID: %d nickname conflict with client ID : %d.\n", tempCli.ID, c.ID);
                tempCli.sendMsg("\t\t>>SEV : nickname is in use.");
                tempCli.closeSoc();
                return ;
            }
        }

        // the client is alive and working. can be added to the clientList
        data.manageClientMap(ClientData.mapAction.add, tempCli);

        // now this client can start running it's receiver thread
        tempCli.start();

        runCountA++;
    }
}






