package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Vector;

import static server.ServerRun.t;

public class ServerRecord implements Runnable {
    private Socket socket;
    private ArrayList<Socket> userList;
    private Vector<String> userName;
    private Map<String, Socket> map;
    private boolean c_f;

    public ServerRecord(Socket s, ArrayList<Socket> userList, Vector<String> userName, Map<String, Socket> map, boolean tf) {
        this.socket = s;
        this.userList = userList;
        this.userName = userName;
        this.map = map;
        this.c_f = tf;
    }

    public void run() {
        try {
            BufferedReader bufferedIn = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            while (true) {
                String s = bufferedIn.readLine();
                String[] str = s.split(",,");
                String info = str[0];  //judge the kind of info
                String line = str[1];
                String name = "";
                if (str.length == 3)
                    name = str[2];

                if (info.equals("1")) {
                    Server.console.append("새 메시지>> " + line + "\r\n");
                    Server.console.setCaretPosition(Server.console.getText().length());
                    new ServerEnter(userList, line, "1", "");
                } else if (info.equals("2")) {  // 2 : login
                    if (!userName.contains(line)) {
                        Server.console.append("새로운 유저가 들어왔습니다>> " + line + "\r\n");
                        Server.console.setCaretPosition(Server.console.getText().length());
                        userName.add(line);
                        map.put(line, socket);
                        Server.user.setListData(userName);
                        new ServerEnter(userList, userName, "2", line);
                    } else {
                        Server.console.append("중복된 이름이 있습니다.>> " + line + "\r\n");
                        Server.console.setCaretPosition(Server.console.getText().length());
                        userList.remove(socket);
                        new ServerEnter(socket, "", "4");
                    }
                } else if (info.equals("3")) {  // 3 : 입장
                    Server.console.append("유저가 퇴장했습니다.>> " + line + "\r\n");
                    Server.console.setCaretPosition(Server.console.getText().length());
                    userName.remove(line);
                    userList.remove(socket);
                    map.remove(line);
                    Server.user.setListData(userName);
                    new ServerEnter(userList, userName, "3", line);
                    socket.close();
                    break;
                }
            }
        } catch (NullPointerException e) {
            e.printStackTrace();
            // 만약 유저의 연결이 끊겼을 경우
            String name = "";
            Iterator<String> mapIter = map.keySet().iterator();
            while (mapIter.hasNext()) {
                String n = mapIter.next();
                Socket s = map.get(n);
                if (s == socket) {
                    name = n;
                }
            }

            Server.console.append("유저와의 연결이 끊겼습니다... \r\n");
            Server.console.setCaretPosition(Server.console.getText().length());

            userName.remove(name);
            userList.remove(socket);
            map.remove(name);
            try {
                Server.user.setListData(userName);
                //new ServerEnter(userList, userName, "4", name);
                socket.close();
            } catch (Exception e1) {
                e1.printStackTrace();
            }

            System.out.println("!!!!");
            t.cancel();

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

}
