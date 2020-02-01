package httpserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket serverSocket;
    private int cntClient;

    public Server() {
        this(8080);
    }

    public Server(int port) {
        cntClient = 0;
        try {
            serverSocket = new ServerSocket(port);
            start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void start() {
        while (true) {
            try {
                Socket client = serverSocket.accept();
                cntClient++;
                System.out.println("have " + cntClient + " Client link");
                new Thread(new ServerFactory(client)).start();

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
