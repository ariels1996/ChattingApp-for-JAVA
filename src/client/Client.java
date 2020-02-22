package client;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class Client {

    public static JFrame window;
    public static JButton connect;
    public static JButton exit;
    public static JTextArea textMessage;
    public static Socket socket = null;
    public static JList<String> user;

    JTextField userID;
    JTextField port;
    JTextField msg;

    JButton send;

    public static void main(String[] args) {
        new Client();
    }

    public Client() {
        init();
    }

    public void init() {
        window = new JFrame("채팅창 | Yonsei Chat");
        window.setLayout(null);
        window.setBounds(400, 400, 525, 420);
        window.setResizable(true);

        JLabel label_userID = new JLabel("ID:");
        label_userID.setBounds(40, 28, 50, 30);
        window.add(label_userID);

        userID = new JTextField();
        userID.setBounds(60, 30, 100, 25);
        window.add(userID);

        JLabel label_port = new JLabel("포트번호:");
        label_port.setBounds(180, 28, 50, 30);
        window.add(label_port);

        port = new JTextField();
        port.setBounds(230, 30, 50, 25);
        window.add(port);

        connect = new JButton("입장");
        connect.setBounds(300, 28, 90, 30);
        window.add(connect);

        exit = new JButton("나가기");
        exit.setBounds(400, 28, 90, 30);
        window.add(exit);

        textMessage = new JTextArea();
        textMessage.setBounds(10, 70, 340, 220);
        textMessage.setEditable(false);

        textMessage.setLineWrap(true);
        textMessage.setWrapStyleWord(true);

        JLabel label_text = new JLabel("채팅창");
        label_text.setBounds(10, 58, 200, 50);
        window.add(label_text);

        JScrollPane paneText = new JScrollPane(textMessage);
        paneText.setBounds(10, 90, 360, 240);
        window.add(paneText);

        JLabel label_Alert = new JLabel("메시지를 입력하세요");
        label_Alert.setBounds(10, 320, 180, 50);
        window.add(label_Alert);

        msg = new JTextField();
        msg.setBounds(10, 355, 188, 30);
        msg.setText(null);
        window.add(msg);

        send = new JButton("전송");
        send.setBounds(190, 355, 77, 30);
        window.add(send);

        myEvent();
        window.setVisible(true);
    }

    public void myEvent() {
        window.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {
                if (socket != null && socket.isConnected()) {
                    try {
                        new ClientSend(socket, getUserID(), "3", "");
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
                System.exit(0);
            }
        });

        exit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (socket == null) {
                    JOptionPane.showMessageDialog(window, "접속이 종료되었습니다!");
                } else if (socket != null && socket.isConnected()) {
                    try {
                        new ClientSend(socket, getUserID(), "3", "");
                        connect.setText("입장");
                        exit.setText("(퇴장)");
                        socket.close();
                        socket = null;
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });

        connect.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (socket != null && socket.isConnected()) {
                    JOptionPane.showMessageDialog(window, "연결 되었습니다!");
                } else {
                    // 여기 ip 주소를 서버 주소에 맞게 바꿔야함!!!
                    String ipString = "172.24.211.16";
                    String clientPort = port.getText();
                    String name = userID.getText();

                    if ("".equals(name) || "".equals(clientPort)) {
                        JOptionPane.showMessageDialog(window, "사용자명 / 포트번호를 입력해주세요!");
                    } else {
                        try {
                            int port2 = Integer.parseInt(clientPort);
                            socket = new Socket();
                            //socket = new Socket(ipString, port2);
                            SocketAddress socketAddress = new InetSocketAddress(ipString,port2);
                            // 연결 시 timeout 설정 - 5 초 동안 연결 안 되면 알림 뜸
                            socket.connect(socketAddress, 5000);
                            connect.setText("입장");
                            exit.setText("퇴장");
                            new ClientSend(socket, getUserID(), "2", "");
                            new Thread(new ClientReceive(socket)).start();
                        } catch (Exception e2) {
                            JOptionPane.showMessageDialog(window, "연결 실패.. 포트 번호를 확인해주세요!");
                        }
                    }
                }
            }
        });

        send.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                sendMsg();
            }
        });
    }

    public void sendMsg() {
        String messages = msg.getText();
        if ("".equals(messages)) {
            JOptionPane.showMessageDialog(window, "내용을 입력해주세요!");
        } else if (socket == null || !socket.isConnected()) {
            JOptionPane.showMessageDialog(window, "채팅방이 닫혀 있습니다");
        } else {
            try {
                new ClientSend(socket, getUserID() + ": " + messages, "1", "");
                msg.setText(null);
            } catch (IOException e1) {
                JOptionPane.showMessageDialog(window, "전송에 실패했습니다...");
            }
        }
    }

    public String getUserID() {
        return userID.getText();
    }
}
