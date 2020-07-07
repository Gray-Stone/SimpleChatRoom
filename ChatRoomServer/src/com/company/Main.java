package com.company;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        int portNum = 2333;





        System.out.printf("trying to create a server at port %d \n ", portNum);

        ClientServer server = new ClientServer(portNum);

        server.manager.start();

        server.acceptor.start();


        while(true)
        {
            System.out.printf(">>> acc rc : %d --",server.acceptor.runCountA);
            System.out.printf("mag rc : %d<< \n", server.manager.runCountM);

//            System.out.printf(">>> acc interrupt : %b --",server.acceptor.isInterrupted());
//            System.out.printf("mag interrupt : %b<< \n", server.manager.isInterrupted());

            Thread.sleep(5000);
        }



    }



}







