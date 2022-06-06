package co.cablebox.tv;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;

import co.cablebox.tv.activity.ErrorActivity;
import co.cablebox.tv.activity.VideoPlayerActivityBox;
import co.cablebox.tv.bean.Channels;
import co.cablebox.tv.bean.MensajeBean;
import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

public class SocketConn  {


    public static int count=0;
    private Socket socket;

    SocketConn(){
        //connect you socket client to the server
        try {
            System.out.println("Try Socket Connection ");
            String socketUri= AppState.getUrlService().generateAndReturnSocketUri();
            IO.Options options = IO.Options.builder()
                    .setForceNew(true)
                    .setReconnection(true)
                    .build();
            socket = IO.socket(socketUri,options);
            socketEmitConnect();

            socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    try{
                        loginWithDeviceId("");
                    }
                    catch(Exception e){
                        Log.d("error connect ", ""+e.toString());
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
                        socketEmitConnect();
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
                        /**

                         if (!canReveiveErrors) {
                         canReveiveErrors=true;
                         throw new Exception("Ignore first Error alert. HARDCODING");
                         }
                         */

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
                }
            });

            socket.on("mostrar_mensaje", new Emitter.Listener() {
                @Override
                public void call(final Object... args) {
                    try {
                        /* Extract array of messages from server*/
                        //JSONArray msgs = (JSONArray) args[0];

                        /* Convert json from server into a java object*/
                        MensajeBean mensajeBean= new Gson().fromJson("{data:[]}", MensajeBean.class);

                        /* Show msgs*/
                        //Insert method to show msgs in current activity
                    } catch (Exception e) {
                        Log.d("error mostrar_mensaje ", ""+e.toString());
                    }
                }
            });

        } catch (Exception e) {
            Log.d("error socket2 ", ""+e.toString());
            //e.printStackTrace();
        }

    }

    public void socketEmitConnect(){
        socket.connect();
        //loginWithDeviceId("");
    }

    public void socketEmitPlayingChannel(Channels channels, int channelIndex){
        String channelName= channels.getChannels().get(channelIndex).getName();
        String channelNum= channels.getChannels().get(channelIndex).getNum();
        socket.emit("vivo_channel",channelName,channelNum);
    }

    /**
     * Notify an error. Then, show the Error Activity
     * @param errorType Check static vars in this class, looks like "ERROR_TYPE_INCORRECT_CREDENTIALS"
     * @param context Activity context
     */
    public void notifyError(int errorType,Context context){
        Intent i= new Intent(context, ErrorActivity.class);
        i.putExtra("errorType",errorType); //pass props to the activity
        context.startActivity(i);
    }

    public void loginWithUserAndPass(String user, String pass){
        /**
         * user="Ani001111";
         *         pass="vaflipepru";
         */
        socket.emit("join", user,pass);
    }

    public void loginWithDeviceId(String deviceId){
        deviceId="eb329baf1";
        socket.emit("join",deviceId);
    }

    public void socketEmitJoin(){
        boolean isSmartphone= AppState.getUser().getDeviceType()==User.DEVICE_SMARTPHONE;
        boolean isTvbox= AppState.getUser().getDeviceType()==User.DEVICE_TVBOX;

        if (isSmartphone){

        }

    }

    private void openVideoPlayer(Channels channels){
        try {
            Context context= AppState.getAppContext();
            String imei= AppState.getUser().getUserId();
            String ipmuxIP= AppState.getUrlService().getIpmuxIP();
            String ipmuxPort= AppState.getUrlService().getIpmuxPort();
            boolean isSmartphoneMode=false;

            Intent i= new Intent(context, VideoPlayerActivityBox.class);
            i.putExtra("channels", channels); //pass props to the activity
            i.putExtra("isSmartphoneMode",isSmartphoneMode); //pass props to the activity
            context.startActivity(i);

        /*
        if (imei != null) { //Smartphone mode
            isSmartphoneMode=true;
            VideoPlayerActivityBox.openLive(this, channels, mensajeBean, imei, ipmuxIP,ipmuxPort, isSmartphoneMode);

        }else{  //TvBox mode
            imei = getSerialNumber();
            isSmartphoneMode=false;
            VideoPlayerActivityBox.openLive(this, channels, mensajeBean, imei, ipmuxIP,ipmuxPort, isSmartphoneMode);
        }
        * */
        } catch(Exception e){
            e.printStackTrace();
        }
    }

    public void openErrorActivity(String errorType,String errorMsg){
        Context context= AppState.getAppContext();
        Intent i= new Intent(context, ErrorActivity.class);
        i.putExtra("errorType",errorType); //pass props to the activity
        i.putExtra("errorMsg",errorMsg); //pass props to the activity
        context.startActivity(i);
    }

    /**
     * Gets all channels from server and launchs VideoPlayerActivity
     * @param channelsFromServer
     */
    private void loadChannels( JSONArray channelsFromServer){
        try {
            System.out.println("-----------------------------------------loadChannels");
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
            System.out.println("---------------------------------------Checking livebean:"+ channels.toString());

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
