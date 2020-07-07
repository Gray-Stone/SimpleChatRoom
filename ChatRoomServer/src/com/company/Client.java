package com.company;

import java.io.*;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Client extends Thread {
    Socket soc;
    String nickName =null;
    int ID ;
    BufferedReader recBuff;
    BufferedWriter sendBuff;
    boolean alive = false;
    int receivedCount =0;


    class Message {
        String msg = "";
        volatile boolean fetched = true;
    }
    Message newMessage = new Message();


    Client(Socket soc, int ID)
    {
        alive = true;
        this.soc = soc;
        this.ID = ID;
//        get the IO of the socket
        try {
            recBuff = new BufferedReader(new InputStreamReader(soc.getInputStream()));
            sendBuff = new BufferedWriter(new OutputStreamWriter(soc.getOutputStream()));
//            if bad things happened, close the socket.
        } catch (IOException e) {
            System.out.printf("ID: %d : Error on initialize client IO. \n", ID);
            closeSoc();
            alive = false;
        }
    }


    /**
     * Try to get the nickName of this client. will return false if timeout.
     * @return weather a nickName is successfully gotten
     */
    boolean getNickName()
    {
        nickName ="";
        try {
            int timeout = 3 ;
            for(int i=0;i<timeout;i++)
            {
                TimeUnit.SECONDS.sleep(1);
                if (recBuff.ready()) {
                    nickName = recBuff.readLine();
                    break;
                }
            }
        } catch (IOException e) {
            System.out.printf("ID: %d : ERROR unable to get user nickName \n", ID);
        } catch (InterruptedException e) {
            System.out.printf("ID: %d : ERROR time error waiting for nickName \n", ID);
            e.printStackTrace();
        }

        if (nickName.equals("")) { return false;}
        return nickName != null;
    }


    boolean sendMsg(String msg)
    {
        try {
            System.out.printf("ID: %d : Sending Msg to client\n",ID);
            sendBuff.write(msg,0,msg.length());
            sendBuff.newLine();
            sendBuff.flush();
        } catch (IOException e) {
            System.out.printf("ID: %d : ERROR when sending \n ",ID);
            alive = false;
            return false;
        }
         return true;
    }

    boolean receiveMsg()
    {
        String tempS =null ;
        try {
        tempS = recBuff.readLine();
        } catch (IOException e) {
            System.out.printf("ID: %d : ERROR when receiving \n ", ID);
            alive = false;
            return false;
        }
        if (tempS == null) {
            System.out.printf("ID: %d : received termination ", ID);
            alive = false;
            return false;
        }

        // wait for buffer to be open, then upload the message.
        receivedCount++;
        while (newMessage.fetched==false) {
            ;
        }
        newMessage.msg = tempS;
        newMessage.fetched = false;
        return true;
    }

    @Override
    public void run()
    {
        this.setName("ClientNo:" + String.valueOf(this.ID));
        this.setPriority(7);

        System.out.printf("ID: %d : Client receiver running\n",this.ID);
        // wait for a message to be read
        while(true) {
            if (receiveMsg() == false)
            {break;}
            if(alive==false)
            {break;}

            try {
                this.sleep(5);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }



    void closeSoc()
    {
        try {
            System.out.printf("ID: %d : closing socket: %s ", ID ,soc.getRemoteSocketAddress().toString());
            soc.close();
            System.out.println(" --> socket closed");
        } catch (IOException ee) {
            System.err.printf("ERROR Trying to close this \n" );
        }
        finally {
            alive=false;
        }
    }

    @Override
    protected void finalize() throws Throwable {
        if (!soc.isClosed()){
            System.out.print("in finalize");
            closeSoc();
        }
        super.finalize();
    }
}
