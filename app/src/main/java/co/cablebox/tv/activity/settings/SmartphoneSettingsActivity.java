package co.cablebox.tv.activity.settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import co.cablebox.tv.AppState;
import co.cablebox.tv.R;
import co.cablebox.tv.activity.helpers.SettingsGridViewItem;

public class SmartphoneSettingsActivity extends SettingsActivity {
    private void showChangeIpDialog(){
        Context context= AppState.getAppContext();
        if(llDescarga.getVisibility() == View.INVISIBLE && !isUpdatingApp){

            EditText inputNewIp;
            AlertDialog.Builder builder= new AlertDialog.Builder(context);
            builder.setTitle("Cambiar CAS");
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
                        Toast.makeText(context, "El CAS ha cambiado", Toast.LENGTH_SHORT).show();

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

    /**
     * Remove "Actualizar" button
     */
    @Override
    public void loadConfigurationsToArrayList(){
        Drawable icon;
        String text;
        String actionType= SettingsGridViewItem.ACTION_TYPE_START_CONFIGURATION;
        String action;
        String bgColor;
        String bgColorAlpha;

        //verCanales
        icon = getResources().getDrawable(R.drawable.watch_tv_3d);
        text="Canales";
        action= SettingsGridViewItem.ACTION_START_CONFIGURATION_CHANNELS;
        bgColor= SettingsGridViewItem.DEFAULT_BG_COLOR;
        bgColorAlpha= SettingsGridViewItem.DEFAULT_BG_COLOR;
        gridViewItems.add(new SettingsGridViewItem(icon,text,actionType,action,bgColor,bgColorAlpha));

        //net
        icon = getResources().getDrawable(R.drawable.wifi);
        text="Red";
        action= SettingsGridViewItem.ACTION_START_CONFIGURATION_RED;
        gridViewItems.add(new SettingsGridViewItem(icon,text,actionType,action,bgColor,bgColorAlpha));

        //fix para saber si la activity necesita mostrar ajustes importantes o no. Los ajustes importantes o delicados, son los que pueden causar un mal funcionamiento de la app si no se saben usar
        if (SettingsActivity.needsImportantSettings){
            //cambiarIp
            icon = getResources().getDrawable(R.drawable.icon_ip_3d);
            text="Cambiar CAS";
            action= SettingsGridViewItem.ACTION_START_CONFIGURATION_CHANGE_IP;
            gridViewItems.add(new SettingsGridViewItem(icon,text,actionType,action,bgColor,bgColorAlpha));
        }

        loadMoreConfigurations();
    }

}
