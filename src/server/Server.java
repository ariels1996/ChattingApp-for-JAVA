package server;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;

public class Server {
    public static JFrame window;
    public static int ports;
    public static JTextArea console;
    public static JList<String> user;

    JButton start, exit, enter;
    JTextField serverName, usingPort, message;

    //main
    public static void main(String[] args) {
        new Server();
    }

    private Server() {
        init();
    }

    private void init() {
        window = new JFrame("Server | Yonsei Chat");
        window.setLayout(null);
        window.setBounds(200, 200, 600, 400);
        window.setResizable(false);

        JLabel serverBoard = new JLabel("운영자 이름:");
        serverBoard.setBounds(10, 8, 80, 30);
        window.add(serverBoard);

        serverName = new JTextField();
        serverName.setBounds(80, 8, 60, 30);
        window.add(serverName);

        JLabel portLabel = new JLabel("Port:");
        portLabel.setBounds(150, 8, 60, 30);
        window.add(portLabel);

        usingPort = new JTextField();
        usingPort.setBounds(200, 8, 70, 30);
        window.add(usingPort);

        start = new JButton("시작");
        start.setBounds(280, 8, 90, 30);
        window.add(start);

        exit = new JButton("닫기");
        exit.setBounds(380, 8, 110, 30);
        window.add(exit);

        console = new JTextArea();
        console.setBounds(10, 70, 340, 320);
        console.setEditable(false);

        console.setLineWrap(true);  // automatic content line feed
        console.setWrapStyleWord(true);

        JLabel label_text = new JLabel("서버 로그");
        label_text.setBounds(100, 47, 190, 30);
        window.add(label_text);

        JScrollPane paneText = new JScrollPane(console);
        paneText.setBounds(10, 70, 340, 220);
        window.add(paneText);

        JLabel userListLabel = new JLabel("유저 목록");
        userListLabel.setBounds(357, 47, 180, 30);
        window.add(userListLabel);

        user = new JList<String>();
        JScrollPane paneUser = new JScrollPane(user);
        paneUser.setBounds(355, 70, 130, 220);
        window.add(paneUser);

        message = new JTextField();
        message.setBounds(10, 310, 188, 30);
        window.add(message);

        enter = new JButton("제출");
        enter.setBounds(220, 310,80, 30);
        window.add(enter);

        myEvent();  // 리스너를 추가합니다.
        window.setVisible(true);
    }

    private void myEvent() {

        start.addActionListener(new ActionListener() {

            public void actionPerformed(ActionEvent e) {
                if (ServerRun.s_socket != null && !ServerRun.s_socket.isClosed()) {
                    JOptionPane.showMessageDialog(window, "이미 실행 중입니다!");
                } else {
                    ports = getPort();
                    if (ports != 0) {
                        try {
                            ServerRun.flag = true;
                            new Thread(new ServerRun(ports)).start();
                            start.setText("구동중");
                            exit.setText("닫기");
                        } catch (IOException e1) {
                            JOptionPane.showMessageDialog(window, "시작하지 못했습니다.");
                        }
                    }
                }
            }
        });

        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (ServerRun.userList != null && ServerRun.userList.size() != 0) {
                    try {
                        new ServerEnter(ServerRun.userList, "", "5", "");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                System.exit(0);
            }
        });

        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (ServerRun.s_socket == null || ServerRun.s_socket.isClosed()) {
                    JOptionPane.showMessageDialog(window, "이미 닫힌 상태입니다!");
                } else {
                    if (ServerRun.userList != null && ServerRun.userList.size() != 0) {
                        try {
                            new ServerEnter(ServerRun.userList, "", "5", "");
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                    }
                    try {
                        start.setText("시작");
                        exit.setText("닫기");
                        ServerRun.s_socket.close();
                        ServerRun.s_socket = null;
                        ServerRun.userList = null;
                        ServerRun.flag = false;   // important
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });



        message.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    sendMsg();
                }
            }
        });

        enter.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMsg();
            }
        });
    }

    public void sendMsg() {
        String messages = message.getText();
        if ("".equals(messages)) {
            JOptionPane.showMessageDialog(window, "보낼 메시지가 없습니다!");
        } else if (ServerRun.userList == null || ServerRun.userList.size() == 0) {
            JOptionPane.showMessageDialog(window, "유저가 없습니다!");
        } else {
            try {
                new ServerEnter(ServerRun.userList, getServerName() + ": " + messages, "1", "");
                message.setText(null);
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(window, "제출에 실패했습니다!");
            }
        }
    }

    private int getPort() {
        String port = usingPort.getText();
        String name = serverName.getText();
        if ("".equals(port) || "".equals(name)) {
            JOptionPane.showMessageDialog(window, "포트 또는 운영자 이름이 없습니다!");
            return 0;
        } else {
            return Integer.parseInt(port);
        }
    }

    private String getServerName() {
        return serverName.getText();
    }
}