package com.company;

import java.io.IOException;
import java.net.ServerSocket;

public class Main {

    public static void main(String[] args) {
        try {
            int portNum = 8080;
            System.out.printf("trying to create a server at port %d \n ", portNum);
            ServerSocket server1 = new ServerSocket( portNum );
        } catch (IOException e) {
//            System.err.println( "error when trying to create a server");
            e.printStackTrace();
        }
    }
}
