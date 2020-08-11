package com.tin.mq;

import java.io.*;
import java.net.Socket;

public class SocketDemo {

    public static void main(String[] args) {
        try {
            String msg = "FF";
            Socket socket = new Socket("192.168.10.80", 8899);
            PrintStream ps = new PrintStream(new BufferedOutputStream(socket.getOutputStream()));
            //BufferedReader br = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            ps.println(msg.getBytes("UTF-8"));
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
