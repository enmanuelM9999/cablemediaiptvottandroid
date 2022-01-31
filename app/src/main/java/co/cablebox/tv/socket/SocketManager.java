package co.cablebox.tv.socket;
import android.os.Build;

import android.util.Log;


import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

public class SocketManager{
    private static WebSocketClient mWebSocketClient;

    
    public static void connectWebSocket(String ipmuxSocketServer) {
        URI uri;
        try {
            uri = new URI(ipmuxSocketServer);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;

                switch (message){
                    case "nuevoplan":
                        break;
                }
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.e("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

    public static void sendMessage(String eventToSend) {
        mWebSocketClient.send(eventToSend);
    }
}
