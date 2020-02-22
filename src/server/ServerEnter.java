package server;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.ArrayList;

public class ServerEnter {
    ServerEnter(ArrayList<Socket> userList, Object message, String info, String name) throws IOException {
        String messages = info + "." + message + "." + name;
        PrintWriter writeOut = null;
        for (Socket s : userList) {
            writeOut = new PrintWriter(s.getOutputStream(), true);
            writeOut.println(messages);
        }
    }

    ServerEnter(Socket s, Object message, String info) throws IOException {
        String messages = info + "." + message;
        PrintWriter writeOut = new PrintWriter(s.getOutputStream(), true);
        writeOut.println(messages);
    }
}
