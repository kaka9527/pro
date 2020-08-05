package com.tin.mqtt;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class SocketDemo {

    public static void main(String[] args) {
        try {
            String msg = "testddddddddddddddd";
            Socket socket = new Socket("192.168.10.80", 61616);
            PrintStream ps = new PrintStream(new BufferedOutputStream(socket.getOutputStream()));
            //BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            ps.println(msg.getBytes());
            ps.flush();

            //String info = br.readLine();
            //System.out.println(info);
            ps.close();
            //br.close();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}
