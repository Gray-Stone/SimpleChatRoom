package com.company;

import java.io.*;
import java.net.Socket;

public class Main {

    public static void main(String[] args) {
        try {
            int portNum = 2333;
            System.out.printf("trying to connect to a server at port %d \n ", portNum);
            Socket soc1 = new Socket("127.0.0.1",portNum);
            System.out.printf("connected to: %s \n", soc1.getRemoteSocketAddress().toString());
            try{
//                construct IO buffer
                BufferedReader inBuff = new BufferedReader(new InputStreamReader(soc1.getInputStream()));
                BufferedWriter outBuff = new BufferedWriter(new OutputStreamWriter(soc1.getOutputStream()));

                String msg = null;

                sendMsg(outBuff,"nameApple");

                sendMsg(outBuff,"B:Hellow World");
                sendMsg(outBuff,"B:Hellow 2");

                sendMsg(outBuff,"P:nameApple: to myself");

                while(inBuff.ready()){
                    System.out.print("From server \n       ->>");
                    System.out.println(inBuff.readLine());
                }

                sendMsg(outBuff,"P:peach: hey");

                while(inBuff.ready()){
                    System.out.print("From server \n       ->>");
                    System.out.println(inBuff.readLine());
                }


            } catch (IOException e) {
                e.printStackTrace();
            }
            finally {
                System.out.println("terminating socket");
                soc1.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    static boolean sendMsg(BufferedWriter outBuff,String msg)
    {
        try {
            outBuff.write(msg,0,msg.length());
            outBuff.newLine();
            outBuff.flush();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}
