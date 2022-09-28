package co.cablebox.tv.activity.error;

import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import butterknife.BindView;
import co.cablebox.tv.ActivityLauncher;
import co.cablebox.tv.AppState;
import co.cablebox.tv.R;

public class SmartphoneErrorActivity extends ErrorActivity{

    //Panel de Numeros
    @BindView(R.id.ll_type_num)
    LinearLayout llTypeNum;
    @BindView(R.id.rl_panel_num)
    RelativeLayout rlPanelNum;

    @BindView(R.id.tv_num_one)
    TextView numOne;
    @BindView(R.id.tv_num_two)
    TextView numTwo;
    @BindView(R.id.tv_num_three)
    TextView numThree;
    @BindView(R.id.tv_num_four)
    TextView numFour;
    @BindView(R.id.tv_num_five)
    TextView numFive;
    @BindView(R.id.tv_num_six)
    TextView numSix;
    @BindView(R.id.tv_num_seven)
    TextView numSeven;
    @BindView(R.id.tv_num_eight)
    TextView numEight;
    @BindView(R.id.tv_num_nine)
    TextView numNine;
    @BindView(R.id.tv_num_zero)
    TextView numZero;


    @Override
    public void loadComponents(){
        /*Button to login*/
        Button btnErrorLaunchLogin=findViewById(R.id.btnErrorLaunchLogin);
        btnErrorLaunchLogin.setVisibility(View.VISIBLE);
        btnErrorLaunchLogin.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                // TODO Auto-generated method stub
                ActivityLauncher.logout();
            }
        });

        /*Number typing*/
        llTypeNum.setVisibility(View.VISIBLE);
        numberTypingOnActionTouch();

    }

    /**
     * Acciones para el evento touch para todos los elementos que conforman el entorno del panel numérico
     */
    private void numberTypingOnActionTouch(){
        //boton que hace visible el panel numérico
        llTypeNum.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {

                    case MotionEvent.ACTION_DOWN:
                        llTypeNum.setBackground(getDrawable(R.drawable.bordes_suave_act));

                        if (rlPanelNum.getVisibility()==View.VISIBLE){
                            rlPanelNum.setVisibility(View.INVISIBLE);
                        }
                        else{
                            rlPanelNum.setVisibility(View.VISIBLE);
                        }
                        break;
                    case MotionEvent.ACTION_UP:
                        llTypeNum.setBackground(null);
                        break;
                }
                return true;
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
    }
}
