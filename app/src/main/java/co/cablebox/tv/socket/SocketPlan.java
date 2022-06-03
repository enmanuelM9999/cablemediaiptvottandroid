package co.cablebox.tv.socket;

import com.google.gson.Gson;

import co.cablebox.tv.bean.Channels;

public class SocketPlan {
    private Gson gson;
    private Channels channels;

    public SocketPlan(){

    }

    /*private void initData() {
        gson = new Gson();
        channels = new Channels();

        String str = getServiceListFromFile(LIVE_DIR);
        System.out.println("STR: "+str);
        if (!TextUtils.isEmpty(str)) {
            channels = gson.fromJson(str, Channels.class);
        }

        if (!NetWorkUtils.getNetState(this)) {
            handler.sendEmptyMessage(CODE_NETWORK_ERROR);
            return;
        }

        getServiceListFromServer();
    }*/
}
