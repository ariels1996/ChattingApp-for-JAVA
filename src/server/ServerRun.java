package server;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.Map;

import javax.swing.JOptionPane;

import java.util.Timer;

public class ServerRun implements Runnable {
    private int port;
    public static ArrayList<Socket> userList = null;
    public static Vector<String> userName = null;    // thread security
    public static Map<String, Socket> map = null;
    public static ServerSocket s_socket = null;
    public static boolean flag = true;
    public static Timer t;

    public ServerRun(int port) throws IOException {
        this.port = port;
    }

    public void run() {

        Socket s = null;
        userList = new ArrayList<Socket>();
        userName = new Vector<String>();
        map = new HashMap<String, Socket>();   //name to socket one on one map

        System.out.println("서버 구동중!");

        try {
            s_socket = new ServerSocket(port);
        } catch (IOException e1) {
            e1.printStackTrace();
        }

        while (flag) {
            try {
                s = s_socket.accept();
                // setKeepAlive
                s.setKeepAlive(true);

                // 연결 요청이 오면 연결이 되었다는 메시지 출력(console)
                InetSocketAddress clientSocketAddress = (InetSocketAddress)s.getRemoteSocketAddress();
                String clientHostName = clientSocketAddress.getAddress().getHostName();
                int clientHostPort = clientSocketAddress.getPort();
                System.out.println("[System] 연결됨!" + clientHostName + ", 포트 번호: " + clientHostPort);

                userList.add(s);

                // 연결 요청이 오면 지속적으로 연결 확인
                t = new Timer();
                t.schedule(new ServerCheckConnection(), 2000, 2000);

                new Thread(new ServerRecord(s, userList, userName, map, false)).start();
            } catch (IOException e) {
                JOptionPane.showMessageDialog(Server.window, "서버를 닫았습니다！");
            }
        }
    }


}
