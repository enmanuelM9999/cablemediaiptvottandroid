package co.cablebox.tv.utils;

import android.app.Activity;

import com.github.nkzawa.emitter.Emitter;
import com.github.nkzawa.socketio.client.IO;
import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;

import co.cablebox.tv.socket.Notificaciones;

public class SocketNotificaciones extends Activity {

    private Socket socket;

    public String Nickname;

    public SocketNotificaciones(){

        //connect you socket client to the server
        try {
            socket = IO.socket("http://179.1.84.51:3000/");
            socket.connect();
            socket.emit("join", Nickname);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.on("message", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        JSONObject data = (JSONObject) args[0];
                        try {
                            //extract data from fired event

                            String nickname = data.getString("senderNickname");
                            String message = data.getString("message");

                            Notificaciones m = new Notificaciones(nickname,message);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                });
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        socket.disconnect();
    }
}
