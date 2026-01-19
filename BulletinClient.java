import java.io.*;
import java.net.*;


public class BulletinClient implements Runnable {

    private Socket clientSocket = null;
    private String clientIP = null;

    public BulletinClient(Socket clientSocket, String clientIP) {
        this.clientSocket = clientSocket;
        this.clientIP = clientIP;
    }


    @Override
    public void run() {
        // TODO: Implement client logic

    }


    



}
