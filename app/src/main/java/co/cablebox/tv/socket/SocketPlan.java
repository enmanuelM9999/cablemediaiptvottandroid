package co.cablebox.tv.socket;

import android.text.TextUtils;

import com.google.gson.Gson;

import co.cablebox.tv.bean.LiveBean;
import co.cablebox.tv.utils.NetWorkUtils;

public class SocketPlan {
    private Gson gson;
    private LiveBean liveBean;

    public SocketPlan(){

    }

    /*private void initData() {
        gson = new Gson();
        liveBean = new LiveBean();

        String str = getServiceListFromFile(LIVE_DIR);
        System.out.println("STR: "+str);
        if (!TextUtils.isEmpty(str)) {
            liveBean = gson.fromJson(str, LiveBean.class);
        }

        if (!NetWorkUtils.getNetState(this)) {
            handler.sendEmptyMessage(CODE_NETWORK_ERROR);
            return;
        }

        getServiceListFromServer();
    }*/
}
