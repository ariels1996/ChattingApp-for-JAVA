package client;

import javax.swing.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class ClientReceive implements Runnable {
    private Socket s;

    public ClientReceive(Socket s) {
        this.s = s;
    }

    public void run() {
        try {
            BufferedReader brIn = new BufferedReader(new InputStreamReader(s.getInputStream()));
            while (true) {
                String s = brIn.readLine();
                String[] strs = s.split("\\.");
                String info = strs[0];     
                String name = "", line = "";
                if (strs.length == 2)
                    line = strs[1];
                else if (strs.length == 3) {
                    line = strs[1];
                    name = strs[2];
                }

                if (info.equals("1")) {  // 1은 메시지
                    Client.textMessage.append(line + "\r\n");
                    Client.textMessage.setCaretPosition(Client.textMessage.getText().length());
                } else if (info.equals("2") || info.equals("3")) { // 2 는 입장, 3은 퇴장
                    if (info.equals("2")) {
                        Client.textMessage.append("(System) " + name + "님 입장하셨습니다!" + "\r\n");
                        Client.textMessage.setCaretPosition(Client.textMessage.getText().length());
                    } else {
                        Client.textMessage.append("(System) " + name + " 안녕히 가세요!" + "\r\n");
                        Client.textMessage.setCaretPosition(Client.textMessage.getText().length());
                    }
                    String list = line.substring(1, line.length() - 1);
                    String[] data = list.split(",");
                } else if (info.equals("4")) {  // 4 는 알림
                    Client.connect.setText("입장");
                    Client.exit.setText("퇴장");
                    Client.socket.close();
                    Client.socket = null;
                    JOptionPane.showMessageDialog(Client.window, "이 아이디는 이미 누군가 사용중입니다...");
                    break;
                } else if (info.equals("5")) {   // 5 는 서버 종료!
                    Client.connect.setText("입장");
                    Client.exit.setText("퇴장");
                    Client.socket.close();
                    Client.socket = null;
                    break;
                } else if (info.equals("7")) {
                    JOptionPane.showMessageDialog(Client.window, "온라인 상태입니다!");
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(Client.window, "안녕...");
        }
    }
}
