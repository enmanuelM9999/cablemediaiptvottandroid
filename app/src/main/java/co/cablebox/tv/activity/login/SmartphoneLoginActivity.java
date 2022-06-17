package co.cablebox.tv.activity.login;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.cablebox.tv.AppState;
import co.cablebox.tv.activity.IpmuxActivity;
import co.cablebox.tv.R;
import co.cablebox.tv.utils.PreUtils;

public class SmartphoneLoginActivity extends AppCompatActivity implements IpmuxActivity,LoginActivity {
    @BindView(R.id.username)
    EditText etUsername;
    @BindView(R.id.password)
    EditText etPassword;
    @BindView(R.id.login)
    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        /* App state configs */
        setContextOnAppState();
        setMessageOnAppState();

        /* Set onClick events*/
        initOnClickEvents();

        /*Check if user already logged in*/
        if (AppState.getUser().isLoggedIn())
            AppState.getSocketConnection().socketEmitConnect();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    void initOnClickEvents(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*Get username and password*/
                String username= etUsername.getText().toString().trim();
                String password= etPassword.getText().toString().trim();

                /*Set username and pass in User object*/
                AppState.getUser().setUserCredentials(new String[]{username,password});

                /*Connect app with server usign socket*/
                AppState.getSocketConnection().socketEmitConnect();

                /*Show loading animation*/
                Toast.makeText(SmartphoneLoginActivity.this,"Loggin...",Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void setContextOnAppState() {
        AppState.setAppContext(this);
    }

    @Override
    public void setMessageOnAppState() {

    }
}