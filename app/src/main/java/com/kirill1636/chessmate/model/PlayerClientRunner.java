package com.kirill1636.chessmate.model;

import java.io.*;
import java.net.Socket;

public class PlayerClientRunner {
    public static void main(String[] args)  {
        String serverName = "localhost";
        int port = 7095;
        String playerId = args[0];
        System.out.println("Подключение к " + serverName + " на порт " + port);
        try (Socket client = new Socket(serverName, port)) {
            System.out.println("Просто подключается к " + client.getRemoteSocketAddress());
            OutputStream outToServer = client.getOutputStream();
            DataOutputStream out = new DataOutputStream(outToServer);
            out.writeUTF(playerId);

            DataInputStream in = new DataInputStream(client.getInputStream());
            while (true) {
                String inputLine = in.readUTF();
                System.out.println(inputLine);
                out.writeUTF("OK");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        System.out.println("Клиент закончил работу");
    }
}