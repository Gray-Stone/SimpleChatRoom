package com.company;

public class Manager extends Thread {

    ClientData data;
    public volatile int runCountM =0;
    public volatile int validRunM =0;
    Manager(ClientData data)
    {
        this.data = data;
    }

    @Override
    public void run() {
        System.out.println("Managing all the clients");

        this.setName("ManagerThread");
        while(true)
        {runManager();}
    }

    public  void runManager()
    {


            runCountM++;


            for (Client cli : data.clientHashMap.values() )
            {
                validRunM++;
//                System.out.printf(" .. Manage user: %s ID: %d \n",cli.nickName,cli.ID);

                // check if user need to be removed
                if (cli.alive == false)
                {
                    // TODO declear the death of this client
                    cli.closeSoc();
                    data.manageClientMap(ClientData.mapAction.remove,cli);
                    continue;
                }

                // there are new messages to be process.
                if (cli.newMessage.fetched == false)
                {
                    // Private msg format: P:toName:message
                    // BroadCast msg format: B:message
                    data.processMessage(cli);
                }
            }


    }
}
