package GUIClient;

import java.io.*;
import java.net.Socket;

public class Comm {
    Socket soc;
    BufferedReader recBuff;
    BufferedWriter sendBuff;
    int portNum;
    String host;
    boolean alive = false;


    Comm(String host, int portNum) {
        this.portNum = portNum;
    }

    String initConnection(String nickName)
    {
//        System.out.printf("trying to connect to a server at port %d \n ", portNum);
        try {
            soc = new Socket(host,portNum);
        } catch (IOException e) {
//            e.printStackTrace();
            closeSoc();
            return "\t\t--> Error trying to connect to server <--";
        }
        alive = true;

        // get communication channels
        try {
            recBuff = new BufferedReader(new InputStreamReader(soc.getInputStream(),"UTF-8"));
            sendBuff = new BufferedWriter(new OutputStreamWriter(soc.getOutputStream(),"UTF-8"));
        } catch (IOException e) {
//            e.printStackTrace();
            closeSoc();
            return "\t\t--> Error trying to establish communication <--";
        }

        //send the nickname
        if (! sendMsg(nickName)){
            return "\t\t--> Conncetion Failed when trying to set the nickName <--";
        }

//        System.out.printf("connected to: %s \n", soc1.getRemoteSocketAddress().toString());
        return String.format("\t\t--> Connected to: %s <--", soc.getRemoteSocketAddress().toString());
    }


    boolean sendMsg(String msg)
    {
        try {
            System.out.printf("COMM: Sending Msg to Server\n");
            sendBuff.write(msg,0,msg.length());
            sendBuff.newLine();
            sendBuff.flush();
        } catch (IOException e) {
            System.out.printf("COMM: ERROR when sending \n ");
            closeSoc();
            return false;
        }
        return true;
    }

    // return the received message or null if connection is lost.
    String receiveMsg()
    {
        try {
            return recBuff.readLine();
        } catch (IOException e) {
            if(e.getMessage().equals("Socket closed"))
                return null;
            e.printStackTrace();
            closeSoc();
//            return "Connection is lost";
        }
        return null;
    }


    void closeSoc()
    {
        try {
            System.out.printf("COMM: closing socket: %s ",soc.getRemoteSocketAddress().toString());
            soc.close();
            System.out.println(" --> socket closed");
        } catch (IOException ee) {
            System.err.printf("COMM: ERROR Trying to close this \n" );
        } catch (NullPointerException np)
        {

        }
        finally {
            alive = false;
        }
    }


}
