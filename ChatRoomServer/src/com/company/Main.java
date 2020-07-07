package com.company;

import java.io.*;
import java.net.ServerSocket;

public class Main {

    public static void main(String[] args) throws IOException {
        int portNum = 2333;


        String test ="P:toID:message";
        String[] tokens = test.split(":");



        System.out.printf("trying to create a server at port %d \n ", portNum);

        ClientServer server = new ClientServer(portNum);

        server.acceptor.start();
        server.manager.start();


        try {
            server.manager.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }



}







