package com.company;

import java.io.*;

public class Main {

    public static void main(String[] args) throws IOException, InterruptedException {
        int portNum = 2333;


        System.out.printf("trying to create a server at port %d \n ", portNum);

        ClientData dataBase = new ClientData(portNum);

        Acceptor acceptor = new Acceptor(dataBase);
        Manager manager = new Manager(dataBase);

        acceptor.start();
        manager.start();

        while(true)
        {
//            System.out.printf(">>> acc rc : %d --",acceptor.runCountA);
//            System.out.printf("mag rc : %d ::  %d << \n", manager.runCountM/1000, manager.validRunM/1000);
//
////            System.out.printf(">>> acc interrupt : %b --",server.acceptor.isInterrupted());
////            System.out.printf("mag interrupt : %b<< \n", server.manager.isInterrupted());

            Thread.sleep(5000);
        }



    }



}







