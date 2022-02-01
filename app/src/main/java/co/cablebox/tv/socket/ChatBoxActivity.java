package co.cablebox.tv.socket;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

import co.cablebox.tv.R;

//Clase Temporal Ejemplo para uso de Sockets
public class ChatBoxActivity extends Activity {

    public RecyclerView myRecylerView ;
    public List<Notificaciones> MessageList ;
    public ChatBoxAdapter chatBoxAdapter;
    public EditText messagetxt ;
    public Button send ;
    //declare socket object
    private Socket socket;

    public String Nickname;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ejem_chat);

        messagetxt = (EditText) findViewById(R.id.message) ;
        send = (Button)findViewById(R.id.send);
        // get the nickame of the user
        Nickname= (String)getIntent().getExtras().getString(SocketApp.NICKNAME);
        //connect you socket client to the server
        try {
            socket = IO.socket("http://iptv02.cablebox.co:3000/");
            socket.connect();
            socket.emit("join", Nickname);
        } catch (URISyntaxException e) {
            e.printStackTrace();

        }
        //setting up recyler
        MessageList = new ArrayList<>();
        myRecylerView = (RecyclerView) findViewById(R.id.messagelist);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
            myRecylerView.setLayoutManager(mLayoutManager);
            myRecylerView.setItemAnimator(new DefaultItemAnimator());



        // message send action
        send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //retrieve the nickname and the message content and fire the event messagedetection
                if(!messagetxt.getText().toString().isEmpty()){
                    socket.emit("messagedetection",Nickname,messagetxt.getText().toString());

                    messagetxt.setText(" ");
                }
            }
        });

        //implementing socket listeners
        socket.on("userjoinedthechat", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = (String) args[0];

                    Toast.makeText(ChatBoxActivity.this,data,Toast.LENGTH_SHORT).show();
                }
                });
            }
        });

        socket.on("userdisconnect", new Emitter.Listener() {
            @Override
            public void call(final Object... args) {
                    runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    String data = (String) args[0];

                    Toast.makeText(ChatBoxActivity.this,data,Toast.LENGTH_SHORT).show();
                }
                });
            }
        });

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

                    // make instance of message

                    Notificaciones m = new Notificaciones(nickname,message);


                    //add the message to the messageList

                    MessageList.add(m);

                    // add the new updated list to the dapter
                    chatBoxAdapter = new ChatBoxAdapter(MessageList);

                    // notify the adapter to update the recycler view

                    chatBoxAdapter.notifyDataSetChanged();

                    //set the adapter for the recycler view

                    myRecylerView.setAdapter(chatBoxAdapter);

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