
package co.cablebox.tv.activity;

import android.app.Dialog;
import android.content.Context;
import android.net.wifi.ScanResult;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;

import co.cablebox.tv.R;
import co.cablebox.tv.utils.config.wifi.wificonnector.WifiConnector;

public class ConnectToWifiDialog extends Dialog implements View.OnClickListener {

    private TextView wifiName;
    private TextView wifiSecurity;
    private EditText pass;
    private Button connect;
    private ScanResult scanResult;

    public DialogListener dialogListener;

    public ConnectToWifiDialog(@NonNull Context context, ScanResult scanResult) {
        super(context);
        this.scanResult = scanResult;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_connectwifi);
        wifiName = findViewById(R.id.dialog_wifiname);
        wifiSecurity = findViewById(R.id.dialog_security);
        pass = findViewById(R.id.dialog_et);
        connect = findViewById(R.id.dialog_btn);
        connect.setOnClickListener(this);
        fillData();

        pass.setOnEditorActionListener(new TextView.OnEditorActionListener(){
           @Override
           public boolean onEditorAction(TextView v, int actionId, KeyEvent event){
               if((actionId & EditorInfo.IME_MASK_ACTION) != 0){
                   System.out.println("Oprimio Enter 3");
                   dialogListener.onConnectClicked(scanResult, pass.getText().toString());
                   dismiss();
                   return true;
               }else
                   return false;
           }
        });
    }

    private void fillData() {
        wifiName.setText(scanResult.SSID);
        String sec = WifiConnector.getWifiSecurityType(scanResult);

        if (WifiConnector.SECURITY_NONE.equals(sec)) {
            pass.setVisibility(View.GONE);
        } else {
            pass.setVisibility(View.VISIBLE);
        }
        wifiSecurity.setText(sec);
    }

    public void setConnectButtonListener(DialogListener listener) {
        this.dialogListener = listener;
    }

    @Override
    public void onClick(View v) {
        this.dialogListener.onConnectClicked(scanResult, pass.getText().toString());
        dismiss();
    }

    interface DialogListener {
        void onConnectClicked(ScanResult scanResult, String password);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.FLAG_EDITOR_ACTION:
                System.out.println("Oprimio Enter 2");
                break;
        }
        return super.onKeyDown(keyCode, event);
    }
}
