package co.cablebox.tv.activity.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import co.cablebox.tv.AppState;

public class SmartphoneSettingsActivity extends SettingsActivity {
    private void showChangeIpDialog(){
        Context context= AppState.getAppContext();
        if(llDescarga.getVisibility() == View.INVISIBLE && !isUpdatingApp){

            EditText inputNewIp;
            AlertDialog.Builder builder= new AlertDialog.Builder(context);
            builder.setTitle("Cambiar IP");
            //builder.setMessage(""); //Mensaje además del titulo
            inputNewIp= new EditText(context);

            //Pintar la ip configurada en el EditText
            inputNewIp.setText(AppState.getUrlService().generateAndReturnSocketUriWithoutProtocol());
            inputNewIp.setMaxLines(1);
            inputNewIp.setPadding(20,10,20,10);
            builder.setView(inputNewIp);

            //Set positive button
            builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String txt= inputNewIp.getText().toString();

                    if(llDescarga.getVisibility() == View.INVISIBLE && !isUpdatingApp){

                        String myarray []=  getIpAndPortByText(txt);
                        String ipmuxIP=myarray[0];
                        String ipmuxPort=myarray[1];

                        AppState.getUrlService().setSocketIP(ipmuxIP);
                        AppState.getUrlService().setSocketPort(ipmuxPort);

                        llIpNueva.setVisibility(View.INVISIBLE);
                        Toast.makeText(context, "La Ip ha cambiado", Toast.LENGTH_SHORT).show();

                        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                        imm.hideSoftInputFromWindow(inputNewIp.getWindowToken(), 0);
                    }

                }
            });

            //Set negative button
            builder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    String txt= inputNewIp.getText().toString();
                    dialog.dismiss();
                }
            });


            //Create Dialog
            AlertDialog ad= builder.create();
            ad.show();

            //Only for smartphones
            ad.getWindow().setLayout(300, 180); //Controlling width and height.
        }
    }

}
