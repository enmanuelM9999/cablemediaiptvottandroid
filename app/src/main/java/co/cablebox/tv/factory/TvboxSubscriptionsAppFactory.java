package co.cablebox.tv.factory;

import co.cablebox.tv.activity.error.SmartphoneErrorActivity;
import co.cablebox.tv.activity.error.TvboxErrorActivity;
import co.cablebox.tv.activity.login.SmartphoneLoginActivity;
import co.cablebox.tv.socket.SmartphoneSocketConnection;
import co.cablebox.tv.socket.SocketConnection;
import co.cablebox.tv.user.SmartphoneUser;
import co.cablebox.tv.user.User;

public class TvboxSubscriptionsAppFactory implements AppFactory{
    @Override
    public SocketConnection getSocketConnection() {
        return new SmartphoneSocketConnection();
    }

    @Override
    public User getUser() {
        return new SmartphoneUser();
    }

    @Override
    public Class<?>  getLoginActivity() {
        return SmartphoneLoginActivity.class;
    }

    @Override
    public Class<?>  getErrorActivity() {
        return  TvboxErrorActivity.class;
    }
}
