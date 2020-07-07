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


                int msgCount = 0;
                while(msgCount<10)
                {
                    msg =  "Number " + String.valueOf(msgCount)+ " from client 1..";
                    System.out.printf(" => Sending String : %s \n",msg);
                    outBuff.write(msg,0,msg.length());
                    outBuff.newLine();
                    outBuff.flush();
                    msgCount++;
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
