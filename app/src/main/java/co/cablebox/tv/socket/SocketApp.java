package co.cablebox.tv.socket;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import co.cablebox.tv.R;


public class SocketApp extends Activity {

    private Button btn;
    private EditText nickname;
    public static final String NICKNAME = "usernickname";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ejem_socket);

        //call UI components  by id
        btn = (Button)findViewById(R.id.enterchat) ;
        nickname = (EditText) findViewById(R.id.nickname);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //if the nickname is not empty go to chatbox activity and add the nickname to the intent extra


                if(!nickname.getText().toString().isEmpty()){

                    Intent i  = new Intent(SocketApp.this,ChatBoxActivity.class);

                    //retreive nickname from EditText and add it to intent extra
                    i.putExtra(NICKNAME,nickname.getText().toString());

                    startActivity(i);
                }
            }
        });

    }

    public static void openLive(Context context) {
        context.startActivity(new Intent(context, SocketApp.class));
    }
}