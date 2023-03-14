package kr.hs.dgsw.network.test01.n2202.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerMain {
    public static void main(String[] args) {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(5000);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        try {
            while(true){
                new ServerThread(serverSocket.accept());
            }

        } catch (IOException e) {

        }

    }
}
