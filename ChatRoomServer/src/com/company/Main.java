package com.company;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

public class Main {

    public static void main(String[] args) throws IOException {
        int portNum = 2333;
        System.out.printf("trying to create a server at port %d \n ", portNum);
        ServerSocket sev1 = new ServerSocket(portNum);
        try {

            System.out.println("waitting for clients to connect ");
            Socket soc1 = sev1.accept();
            System.out.printf("Client Connected : %s \n",soc1.getRemoteSocketAddress().toString());

            try { // use try-catch-finally to ensure socket is closed.
                // construct the IO object.
                BufferedReader inBuff = new BufferedReader(new InputStreamReader(soc1.getInputStream()));
                BufferedWriter outBuff = new BufferedWriter(new OutputStreamWriter(soc1.getOutputStream()));

                System.out.println("Loop Reading from the socket");
                String inMsg = "";
                int msgCount = 0;
                while (true) {
                    inMsg = inBuff.readLine();
                    if (inMsg == null)
                        break;
                    System.out.printf(" =>Received message %d : --> %s<= \n", msgCount, inMsg);
                    msgCount++;
                }
                System.out.println("Detected client closing.");
            } catch (IOException e) {
                System.err.println("ERROR reading message from client");
                if( ! e.getMessage().equals("Connection reset"))
                    e.printStackTrace();
                else
                    System.err.printf("connection reset, socket %s will be closed\n",soc1.getRemoteSocketAddress().toString());
            } finally {
                try {
                    System.out.printf("closing socket: %s ",soc1.getRemoteSocketAddress().toString());
                    soc1.close();
                    System.out.println(" --> socket closed");
                } catch (IOException e) {
                    System.err.println("ERROR Trying to close soc1");
//                    e.getCause()
                    e.printStackTrace();
                }
            }

        } catch (IOException e) {
//            System.err.println( "error when trying to create a server");
            e.printStackTrace();
        }
        finally {
            System.out.printf("closing server : %s ", sev1.getLocalSocketAddress().toString());
            sev1.close();
            System.out.println(" --> Server closed");
        }
    }
}







