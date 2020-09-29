package com.tg.sbootshrio.websoket;

import org.java_websocket.WebSocket;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.URISyntaxException;

public class MyWebSocketClient extends WebSocketClient {


    public MyWebSocketClient(URI serverUri) {
        super(serverUri);
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        System.out.println("握手...");
    }

    @Override
    public void onMessage(String s) {
        System.out.println("client 接收到消息：" + s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        System.out.println("关闭...");
    }

    @Override
    public void onError(Exception e) {
        System.out.println("异常" + e);
    }

    public static void main(String[] args) {
        try {
            MyWebSocketClient client = new MyWebSocketClient(new URI("ws://127.0.0.1:8070/websocketServer"));
            client.connect();
            System.out.println(client.getReadyState() + "========");
            while (!client.getReadyState().equals(WebSocket.READYSTATE.OPEN)) {
                System.out.println("还没有打开");
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("建立websocket连接");

            client.send("client fasong  shuju");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
