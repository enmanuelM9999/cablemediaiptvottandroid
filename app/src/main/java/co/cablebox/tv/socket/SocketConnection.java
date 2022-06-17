package co.cablebox.tv.socket;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import co.cablebox.tv.ActivityLauncher;
import co.cablebox.tv.AppState;
import co.cablebox.tv.ToastManager;
import co.cablebox.tv.bean.Channels;
import co.cablebox.tv.bean.MensajeBean;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public abstract class SocketConnection {

    public static int count=0;
    public Socket socket;

    public SocketConnection(){
        //connect you socket client to the server
        try {
            System.out.println("Try SocketConnection Connection ");
            String socketUri= AppState.getUrlService().generateAndReturnSocketUri();
            IO.Options options = IO.Options.builder()
                    .setForceNew(true)
                    .setReconnection(true)
                    .build();
            socket = IO.socket(socketUri,options);
            initSocketEvents();
            //socketEmitConnect();

        } catch (Exception e) {
            Log.d("error SocketConstructor", ""+e.toString());
            //e.printStackTrace();
        }
    }


    private void initSocketEvents(){
        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                try{
                    socketEmitJoin();
                }
                catch(Exception e){
                    Log.d("error connect ", ""+e.toString());
                }
            }
        });

        socket.on("disconnect", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                try{
                    //Toast.makeText(VideoPlayerActivityBox.this, "SKT OFFLINE",Toast.LENGTH_SHORT).show();
                    socketEmitConnect();
                }
                catch(Exception e){
                    Log.d("error socket ", ""+e.toString());
                }
            }
        });

        socket.on("reconnect", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                try{
                    //socketEmitConnect();
                }
                catch(Exception e){
                    Log.d("error socket ", ""+e.toString());
                }
            }
        });

        /*Set events to receive, emits from server*/
        socket.on("cargar_plan_canales", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                try{
                    /* Extract channels from server*/
                    System.out.println("-------------------------------channelsFromServer");
                    JSONArray channelsFromServer = (JSONArray) args[0];
                    System.out.println("-------------------------------channelsFromServer"+channelsFromServer);

                    /* Take all channels and start VideoPlayerActivity*/
                    loadChannels(channelsFromServer);
                }
                catch(Exception e){
                    Log.d("cargar_plan_canales", ""+e.toString());
                }
            }
        });

        socket.on("recargar_plan", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                try{
                    /* Extract channels from server*/
                    JSONArray channelsFromServer = (JSONArray) args[0];

                    /* Take all channels and start VideoPlayerActivity*/
                    loadChannels(channelsFromServer);
                }
                catch(Exception e){
                    Log.d("error socket ", ""+e.toString());
                }
            }
        });

        socket.on("mensaje_error", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                try {
                    /* Extract data from server*/
                    String msgType = (String) args[0];
                    String msg = (String) args[1];

                    /*Open Error Activity*/
                    String errorType=msgType;
                    String errorMsg=msg;
                    openErrorActivity(errorType, errorMsg);
                } catch (Exception e) {
                    Log.d("e at mensaje_error ", ""+e.toString());
                    e.printStackTrace();
                }
            ;}
        });


        socket.on("ping_deco", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                try {
                    /* Extract data from server*/
                    String msg = (String) args[0];

                    /*Print message*/
                    ToastManager.toast(msg);

                } catch (Exception e) {
                    Log.d("error at ping_deco ", ""+e.toString());
                    e.printStackTrace();
                }
                ;}
        });

        socket.on("mostrar_mensaje", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                try {
                    /* Extract array of messages from server*/
                    JSONArray messages = (JSONArray) args[0];

                    /*Iterate each message*/
                    for (int i = 0; i < messages.length(); i++) {
                        String msg= messages.getString(i);
                        ToastManager.toast(msg);
                    }
                } catch (Exception e) {
                    Log.d("error mostrar_mensaje ", ""+e.toString());
                }
            }
        });


    }

    public void socketEmitConnect(){
        socket.connect();
    }

    public void socketEmitPlayingChannel(Channels channels, int channelIndex){
        String channelName= channels.getChannels().get(channelIndex).getName();
        String channelNum= channels.getChannels().get(channelIndex).getNum();
        socket.emit("vivo_channel",channelName,channelNum);
    }

    public abstract void socketEmitJoin();

    private void openVideoPlayer(Channels channels){
        ActivityLauncher.launchVideoPlayer(channels);
    }

    /**
     * Notify an error. Then, show the Error Activity
     */
    public void openErrorActivity(String errorType,String errorMsg){
        ActivityLauncher.launchErrorActivity(errorType,errorMsg);
    }

    /**
     * Gets all channels from server and launchs VideoPlayerActivity
     * @param channelsFromServer looks like:
     *                           {
     *                              [{name:"channel1"},
     *                               {name:"channel2"}
     *                              ]
     *                           }
     */
    private void loadChannels( JSONArray channelsFromServer){
        try {
            /* Convert Json received into a Java Object
            *
            *  The structure of json to convert into java object must looks like:
            *
            *  {
            *    channels: [...channels]
            *  }
            * */
            JSONObject json= new JSONObject(); // {} an empty json
            json.put("channels",channelsFromServer); // {channels: [...channels]} a json with channels property

            Channels channels = new Gson().fromJson(json.toString(), Channels.class); //convert json string into a java object
            System.out.println("---------------------------------------Checking channels from server:"+ channels.toString());

            /* Open VideoPlayer*/
            openVideoPlayer(channels);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public void disconnect(){
        socket.disconnect();
        socket.off();
        socket=null;
    }

}
