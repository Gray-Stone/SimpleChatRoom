package GUIClient;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class ClientView extends JFrame implements ActionListener, KeyListener {
    private JPanel headPanel, bodyPanel;
    //elements for head panel
    private JButton connectBtn;
    private JComboBox nameComboBox;
    private JLabel messageToLabel;

    //elements for body panel
    private JLabel nickNameLabel;
    private JTextField nickNameInput;
    private JScrollPane scrollPane;
    private JScrollBar scrollBar;
    private JTextArea chatContent;
    private JTextField inputBox;
    private JButton sendBtn;

    Thread receiverThread;
    volatile boolean receiverThreadEnd = true;


    Comm comm;
    String allUserNickName = "All User";

    enum ConnBtnText {Connected, NotConnected}



    public ClientView(Comm comm1) {
        this.comm = comm1;
        initView();
    }

    void setConnectBtn(ConnBtnText text){
        switch (text) {
            case Connected -> connectBtn.setText("disConnect");
            case NotConnected -> connectBtn.setText("Connect");
        }
    }

    void printChatLine(String line){
        chatContent.append("\n  ");
        chatContent.append(line);
        // auto scroll to bottom
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
        }
        scrollBar.setValue(Integer.MAX_VALUE);

    }


    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getSource() == sendBtn) {
            //send message
            sendMsg();
            checkConnection();
            inputBox.setText("");
        }

        else if(e.getSource() == connectBtn) {
            // two cases: connected/ disconnected

            // if the connection exist
            if (comm.alive){
                comm.closeSoc();
//                checkConnection();
            }
            // connection doesn't exist
            else{
                String nickName = nickNameInput.getText();
                nickNameInput.setText(nickName);
                System.out.println(nickName);
                if(nickName.equals("") || nickName.equals(allUserNickName)){
                    //Invalid username
                    printChatLine("\t\t--> SYS: SYS: Invalid username <-- ");
                }else {
                    printChatLine("\t\t--> SYS: Trying to connect < --");
//                    comm.sendMsg(nickName);
                    printChatLine(comm.initConnection(nickName));
                    checkConnection();
                    startReceiverThread();
                }
            }
        }

    }

    @Override
    public void keyPressed(KeyEvent e) {
        if(e.getKeyCode() == KeyEvent.VK_ENTER) {
            sendMsg();
            checkConnection();
            inputBox.setText("");
        }
    }

    boolean sendMsg()
    {
        String msg = inputBox.getText();
        String target = nameComboBox.getSelectedItem().toString();
        if (target.equals(allUserNickName))
        {
            msg = ("B:"+msg);
        }
        else{
            msg = ("P:"+target+":"+msg);
        }
        return comm.sendMsg(msg);
    }

    void startReceiverThread(){
        receiverThread = new Thread(() -> {
            while(receiverThreadEnd == false ) {
                receiveMsg();
            }
        });
        receiverThreadEnd = false;
        receiverThread.start();
    }

    void receiveMsg()
    {
        System.out.println("wait for message");
        String msg = comm.receiveMsg();

        // case of end of connection
        if(msg == null){
            comm.alive=false;
            printChatLine("\t\t--> SYS: connection end <-- ");
//            receiverThreadEnd = true;
            checkConnection();
            return;
        }
        System.out.println("receive message" + msg);

        // case of message to update user names
        String[] nameList =  msg.split(":");
        if (nameList[0].equals(allUserNickName)){
            nameComboBox.removeAllItems();
            for(String name : nameList)
            {
                nameComboBox.addItem(name);
            }
            return ;
        }
        printChatLine(msg);
    }

    void runReceiver()
    {

    }

    void checkConnection()
    {
        if( comm.alive == false)
        {
            //case of not connected
            // disable message input
            // enable nickname input
            // change buttom to connect
            setConnectBtn(ConnBtnText.NotConnected);
            inputBox.setEditable(false);
            nameComboBox.removeAllItems();
            nameComboBox.addItem(allUserNickName);
            nickNameInput.setEditable(true);

            receiverThreadEnd = true;

        }else{
            setConnectBtn(ConnBtnText.Connected);
            inputBox.setEditable(true);
            nickNameInput.setEditable(false);
        }
    }



    @Override
    public void keyTyped(KeyEvent e) {

    }
    @Override
    public void keyReleased(KeyEvent e) {

    }

    /*  setup the GUI view.
    *   initialize graphic component.
    * */
    public void initView() {
        System.out.println("Init GUi");

        // create frame for chatroom
        JFrame.setDefaultLookAndFeelDecorated(true);
        JFrame frame = new JFrame("ChatRoom");
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setVisible(true);
        //create and add elements for head panel
        headPanel = new JPanel();

        nickNameLabel = new JLabel("My Nickname: ");
        nickNameInput = new JTextField(7);
        nickNameInput.setText("user1");
        connectBtn = new JButton("Connect");

        headPanel.add(nickNameLabel);
        headPanel.add(nickNameInput);
        headPanel.add(connectBtn);

        //create and add elements for body panel
        bodyPanel = new JPanel();
        chatContent = new JTextArea(30, 30);
        chatContent.setEditable(false);
        scrollPane = new JScrollPane(chatContent);
        scrollBar = scrollPane.getVerticalScrollBar();

        inputBox = new JTextField(30);
        inputBox.setEditable(false);
        sendBtn = new JButton("Send");

        messageToLabel = new JLabel("Message To: ");
        nameComboBox=new JComboBox();
        nameComboBox.addItem(allUserNickName);

        bodyPanel.add(messageToLabel);
        bodyPanel.add(nameComboBox);
        bodyPanel.add(inputBox);
        bodyPanel.add(sendBtn);


        headPanel.setLayout(new FlowLayout(FlowLayout.CENTER));
        bodyPanel.setLayout(new FlowLayout(FlowLayout.CENTER));

        frame.getContentPane().add(headPanel, BorderLayout.NORTH);
        frame.getContentPane().add(scrollPane, BorderLayout.CENTER);
        frame.getContentPane().add(bodyPanel, BorderLayout.SOUTH);

        // show window
        frame.pack();
        frame.setVisible(true);

        inputBox.addKeyListener(this);
        sendBtn.addActionListener(this);
        connectBtn.addActionListener(this);
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                comm.closeSoc();
                //network service disconnect
            }
        });


    }



}

