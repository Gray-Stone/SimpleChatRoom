package GUIClient;

public class Main {


    public static void main(String[]  args) {

        int portNum = 2333;
        String host = "127.0.0.1";

        Comm comm1 = new Comm(host, portNum);

        ClientView client = new ClientView(comm1);
        System.out.println("before the receiverRun");
//        client.runReceiver();


//        javax.swing.SwingUtilities.invokeLater(new Runnable() {
//            public void run() {
//                GUIClient.ClientView client = new GUIClient.ClientView(comm1);
//                new Thread(){
//                    public void run()
//                    {
//                        this.setName("receiver thread");
//                        client.runReceiver();
//                    }
//                };
//
//            }



    }
}
