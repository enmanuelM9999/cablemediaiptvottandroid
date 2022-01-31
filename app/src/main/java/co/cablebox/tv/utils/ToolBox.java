package co.cablebox.tv.utils;

import android.content.Context;


public class ToolBox {
    public static  int convertDpToPx(int dp, Context context){
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
