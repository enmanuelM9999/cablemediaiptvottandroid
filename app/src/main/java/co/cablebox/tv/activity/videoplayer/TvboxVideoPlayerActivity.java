package co.cablebox.tv.activity.videoplayer;

import android.graphics.drawable.Drawable;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import co.cablebox.tv.AppState;
import co.cablebox.tv.R;
import co.cablebox.tv.utils.OnSwipeTouchListener;

public class TvboxVideoPlayerActivity extends VideoplayerActivity{

    @Override
    public void configTopButtons(){
        showTopButtons();

        ivLogout.setVisibility(View.VISIBLE);
        ivTypeNum.setVisibility(View.VISIBLE);

        ivExitApp.setVisibility(View.GONE);
        ivList.setVisibility(View.GONE);
        ivLock.setVisibility(View.GONE);
        ivUnLock.setVisibility(View.GONE);
        ivLogout.setVisibility(View.GONE);
    }
    public void hideTopButtons(){
        llSmartphoneButtons.setVisibility(View.INVISIBLE);
    }
    public void showTopButtons(){
        llSmartphoneButtons.setVisibility(View.VISIBLE);

    }

    @Override
    void onActionTouch(){

        //otro boton que abre lista de canales
        rlOpciones.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        break;
                    case MotionEvent.ACTION_UP:
                        pressOptionButton();
                        break;
                }
                return true;
            }

        });
        rlOpciones.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pressOptionButton();
            }
        });


        // Boton para cerrar sesion
        ivLogout.setOnTouchListener(new View.OnTouchListener() {
            Drawable originalBackground = ivLogout.getBackground();
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:

                        ivLogout.setBackground(getDrawable(R.drawable.bordes_suave_act));

                        break;
                    case MotionEvent.ACTION_UP:
                        ivLogout.setBackground(originalBackground);
                        pressLogoutButton();
                        break;
                }
                return true;
            }

        });
        ivLogout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pressLogoutButton();
            }
        });



        // Boton para cerra la app por completo
        ivExitApp.setOnTouchListener(new View.OnTouchListener() {
            Drawable originalBackground = ivExitApp.getBackground();
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        ivExitApp.setBackground(getDrawable(R.drawable.bordes_suave_act));

                        break;
                    case MotionEvent.ACTION_UP:
                        ivExitApp.setBackground(originalBackground);
                        pressExitAppButton();

                        break;
                }
                return true;
            }

        });
        ivExitApp.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pressExitAppButton();
            }
        });


        // Boton para ver la lista de todos los canales en un panel izquierdo
        ivList.setOnTouchListener(new View.OnTouchListener() {
            Drawable originalBackground = ivList.getBackground();
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        ivList.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        break;
                    case MotionEvent.ACTION_UP:
                        ivList.setBackground(originalBackground);
                        pressOptionButton();
                        break;
                }
                return true;
            }

        });
        ivList.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pressOptionButton();
            }
        });



        // Boton Bloquear Pantalla
        ivLock.setOnTouchListener(new View.OnTouchListener() {
            Drawable originalBackground = ivLock.getBackground();
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                        ivLock.setBackground(getDrawable(R.drawable.bordes_suave_act));

                    case MotionEvent.ACTION_UP:
                        ivLock.setBackground(originalBackground);
                        pressLockScreenButton();

                        break;
                }
                return true;
            }

        });
        ivLock.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pressLockScreenButton();
            }
        });


        // Boton Desbloquear Pantalla
        ivUnLock.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                    case MotionEvent.ACTION_UP:
                        pressUnlockScreenButton();
                        break;
                }
                return true;
            }

        });
        ivUnLock.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pressUnlockScreenButton();
            }
        });

        // Button to launch settings
        ivSettings.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                    case MotionEvent.ACTION_UP:
                        pressSettingsButton();
                        break;
                }
                return true;
            }

        });
        ivSettings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pressSettingsButton();
            }
        });

        // Button to launch settings as technician
        ivAdvanceSettings.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:

                    case MotionEvent.ACTION_UP:
                        pressAdvanceSettingsButton();
                        break;
                }
                return true;
            }

        });
        ivAdvanceSettings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pressAdvanceSettingsButton();
            }
        });

        // Boton para expandir el panel de numeros para digitar el numero de un canal
        ivTypeNum.setOnTouchListener(new View.OnTouchListener() {
            Drawable originalBackground = ivTypeNum.getBackground();
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        ivTypeNum.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        break;
                    case MotionEvent.ACTION_UP:
                        ivTypeNum.setBackground(originalBackground);
                        pressTypeNumberButton();
                        break;
                }
                return true;
            }

        });
        ivTypeNum.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pressTypeNumberButton();
            }
        });


        // Cada uno de los numeros del panel para digitar el numero del canal buscado
        numOne.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        numOne.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        pressNumber("1");
                        break;
                    case MotionEvent.ACTION_UP:
                        numOne.setBackground(null);
                        break;
                }
                return true;
            }

        });
        numOne.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pressNumber("1");
            }
        });
        numTwo.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        numTwo.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        pressNumber("2");
                        break;
                    case MotionEvent.ACTION_UP:
                        numTwo.setBackground(null);
                        break;
                }
                return true;
            }

        });
        numTwo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pressNumber("2");
            }
        });
        numThree.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        numThree.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        pressNumber("3");
                        break;
                    case MotionEvent.ACTION_UP:
                        numThree.setBackground(null);
                        break;
                }
                return true;
            }

        });
        numThree.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pressNumber("3");
            }
        });
        numFour.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        numFour.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        pressNumber("4");
                        break;
                    case MotionEvent.ACTION_UP:
                        numFour.setBackground(null);
                        break;
                }
                return true;
            }

        });
        numFour.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pressNumber("4");
            }
        });
        numFive.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        numFive.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        pressNumber("5");
                        break;
                    case MotionEvent.ACTION_UP:
                        numFive.setBackground(null);
                        break;
                }
                return true;
            }

        });
        numFive.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pressNumber("5");
            }
        });
        numSix.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        numSix.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        pressNumber("6");
                        break;
                    case MotionEvent.ACTION_UP:
                        numSix.setBackground(null);
                        break;
                }
                return true;
            }

        });
        numSix.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pressNumber("6");
            }
        });
        numSeven.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        numSeven.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        pressNumber("7");
                        break;
                    case MotionEvent.ACTION_UP:
                        numSeven.setBackground(null);
                        break;
                }
                return true;
            }

        });
        numSeven.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pressNumber("7");
            }
        });
        numEight.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        numEight.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        pressNumber("8");
                        break;
                    case MotionEvent.ACTION_UP:
                        numEight.setBackground(null);
                        break;
                }
                return true;
            }

        });
        numEight.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pressNumber("8");
            }
        });
        numNine.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        numNine.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        pressNumber("9");
                        break;
                    case MotionEvent.ACTION_UP:
                        numNine.setBackground(null);
                        break;
                }
                return true;
            }

        });
        numNine.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pressNumber("9");
            }
        });
        numZero.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        numZero.setBackground(getDrawable(R.drawable.bordes_suave_act));
                        pressNumber("0");
                        break;
                    case MotionEvent.ACTION_UP:
                        numZero.setBackground(null);
                        break;
                }
                return true;
            }

        });
        numZero.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                pressNumber("0");
            }
        });
        ivEnterNum.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        ivEnterNum.setBackground(getDrawable(R.drawable.bordes_suave_act));

                        if (writingNum) {
                            delayBusNum = 0;
                            handler.sendEmptyMessageDelayed(CODE_CHANGE_BY_NUM, delayBusNum);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        ivEnterNum.setBackground(null);
                        break;
                }
                return true;
            }

        });
        ivEnterNum.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                if (writingNum) {
                    delayBusNum = 0;
                    handler.sendEmptyMessageDelayed(CODE_CHANGE_BY_NUM, delayBusNum);
                }
            }
        });
    }

    @Override
    public void pressNumber(String number){
        claveExit(number);
        writingNum = true;
        canalNum(number);
        extendClearScreenTimeout();

        //extender el tiempo en que se oculta el textview del canal en marcación
        handler.removeMessages(CODE_HIDE_CHANNEL_NUMBER_TEXT_VIEW);
        handler.sendEmptyMessageDelayed(CODE_HIDE_CHANNEL_NUMBER_TEXT_VIEW,HUD_HIDE_TIME);
    }

    @Override
    public void extendClearScreenTimeout(){
        handler.removeMessages(CODE_CLEAR_SCREEN);
        handler.sendEmptyMessageDelayed(CODE_CLEAR_SCREEN,CHANNEL_HIDE_TIME);
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {

            case KeyEvent.KEYCODE_ENTER:
                if (writingNum) {
                    System.out.println("Escribiendo Enter");
                    delayBusNum = 0;
                    handler.sendEmptyMessageDelayed(CODE_CHANGE_BY_NUM, delayBusNum);
                } else if(llOptions.getVisibility() == View.VISIBLE){
                    System.out.println("Opcion Enter");
                    selecOpcion();
                }else {
                    System.out.println("Informacion Enter");
                    if(rlDisplayDown.getVisibility() == View.INVISIBLE){
                        clearAndShowChannelInfo();
                    }else{
                        clearScreen();
                    }
                    setIdProgramaActual();
                    showProgramInfo();

                    //Pausar Canal Actual y Reanudar al presente
                    /*if(mediaPlayer.isPlaying()){
                        mediaPlayer.pause();
                        controlError = true;
                    } else
                        playerInterface.seekTo(0);*/
                }
                break;

            case KeyEvent.KEYCODE_BOOKMARK:
                if (isSomeHudActive()){
                    clearScreen();
                }
                else if(!isSomeHudActive()){
                    clearAndShowChannelInfo();
                }
                break;

            case KeyEvent.KEYCODE_DPAD_CENTER:

            case KeyEvent.KEYCODE_DPAD_UP:

            case KeyEvent.KEYCODE_DPAD_DOWN:

            case KeyEvent.KEYCODE_DPAD_RIGHT:

            case KeyEvent.KEYCODE_DPAD_LEFT:

                if(isSomeHudActive()){

                }
                else{
                    extendClearScreenTimeout();
                }
                break;

            case KeyEvent.KEYCODE_MENU:
                /*
                if(isOptionsActive() || isChannelListActive()){
                    clearScreen();
                }
                else{
                    rlOpciones.setBackground(getDrawable(R.drawable.bordes_suave_act));
                    clearAndShowOptionsAndChannelInfo();
                }
                return true;
                //break;
                 */
                if(isOptionsActive() || isChannelListActive()){
                    clearScreen();
                }
                else{
                    clearAndShowChannelList();
                }
                return true;

            case KeyEvent.KEYCODE_0:
                pressNumber("0");
                break;

            case KeyEvent.KEYCODE_1:
                pressNumber("1");
                break;

            case KeyEvent.KEYCODE_2:
                pressNumber("2");
                break;

            case KeyEvent.KEYCODE_3:
                pressNumber("3");
                break;

            case KeyEvent.KEYCODE_4:
                pressNumber("4");
                break;

            case KeyEvent.KEYCODE_5:
                pressNumber("5");
                break;

            case KeyEvent.KEYCODE_6:
                pressNumber("6");
                break;

            case KeyEvent.KEYCODE_7:
                pressNumber("7");;
                break;

            case KeyEvent.KEYCODE_8:
                pressNumber("8");
                break;

            case KeyEvent.KEYCODE_9:
                pressNumber("9");
                break;

            case KeyEvent.KEYCODE_VOLUME_UP:
                break;

            case KeyEvent.KEYCODE_VOLUME_DOWN:
                break;

            case KeyEvent.KEYCODE_VOLUME_MUTE:
                if(ivMute.getVisibility() == View.INVISIBLE){
                    ivMute.setVisibility(View.VISIBLE);

                    rlVolumenA.setVisibility(View.INVISIBLE);
                }else{
                    ivMute.setVisibility(View.INVISIBLE);
                }
                break;

            case KeyEvent.KEYCODE_BACK:
                int tempLastChannelIndex= lastChannelIndex;
                lastChannelIndex=channelIndex;
                channelIndex=tempLastChannelIndex;
                changeChannelInScreen();

                return true;
            case KeyEvent.KEYCODE_SETTINGS:
                Toast.makeText(this, "Settings", Toast.LENGTH_LONG).show();
                System.out.println("++++++++++++++++++++++++SETTINGS");
                return true;

            case KeyEvent.KEYCODE_W:
                try {
                    releaseResources();
                    Process proc = Runtime.getRuntime()
                            .exec(new String[]{ "su", "-c", "reboot -p" });
                    proc.waitFor();

                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;

            case KeyEvent.KEYCODE_Q:
                try {
                    openSettingsActivityAsNormalUser();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                break;

        }
        return super.onKeyDown(keyCode, event);
    }


    /*Milliseconds to hide channel info and top buttons*/
    public int HUD_HIDE_TIME=20000;
    public int CHANNEL_HIDE_TIME=HUD_HIDE_TIME;

    @Override
    public void clearScreen(){
        ivSettings.requestFocus();
        removeHudDelayedMessages();
        hideOptions();
        hideChannelInfo();
        hideChannelList();
        hidePanelNum();
        //tvChannelNumberChange.setVisibility(View.INVISIBLE);
    }

    @Override
    public void clearAndShowChannelInfo(){
        clearAndShowListAndChannelInfo();
    }

    @Override
    public void clearAndShowOptionsAndChannelInfo(){
        clearScreen();
        showChannelInfo();
        showOptions();
        clearScreen(CHANNEL_HIDE_TIME);

    }

    @Override
    public void clearAndShowListAndChannelInfo(){
        clearScreen();
        showChannelInfo();
        showChannelList();
        clearScreen(CHANNEL_HIDE_TIME);

    }

    @Override
    public void clearAndShowChannelList(){
        clearScreen();
        showChannelList();
        clearScreen(CHANNEL_HIDE_TIME);
    }

}
