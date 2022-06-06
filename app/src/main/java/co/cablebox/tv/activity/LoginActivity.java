package co.cablebox.tv.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import co.cablebox.tv.AppState;
import co.cablebox.tv.IpmuxActivity;
import co.cablebox.tv.R;
import co.cablebox.tv.utils.PreUtils;

public class LoginActivity extends AppCompatActivity implements IpmuxActivity {
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

        /* Recover props */
        // props are in AppState.User

        /* Set onClick events*/
        initOnClickEvents();
    }

    void initOnClickEvents(){
        btnLogin.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                /*Get username and password*/
                String username= etUsername.getText().toString().trim();
                String password= etPassword.getText().toString().trim();

                /*Use User method*/
                AppState.getSocketConn().loginWithUserAndPass(username,password);

                /*Show loading animation*/
                Toast.makeText(LoginActivity.this,"Loggin...",Toast.LENGTH_LONG).show();
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