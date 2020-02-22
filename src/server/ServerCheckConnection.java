package server;

import java.net.Socket;
import java.util.ArrayList;
import java.util.TimerTask;

import static server.ServerRun.*;

public class ServerCheckConnection extends TimerTask{

    public static ArrayList<Socket> List;

    @Override
    public void run() {
        // 여기서 반복적으로 연결 확인
        System.out.println("good");
        this.List = userList;
        for (Socket s : List) {
            try {
                if (!s.getKeepAlive()) {
                    new ServerRecord(s, userList, userName, map, true);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }


    }
}
