package co.cablebox.tv.factory;

import co.cablebox.tv.activity.error.ErrorActivity;
import co.cablebox.tv.activity.error.SmartphoneErrorActivity;
import co.cablebox.tv.activity.error.TvboxErrorActivity;
import co.cablebox.tv.activity.login.LoginActivity;
import co.cablebox.tv.activity.login.TvboxLoginActivity;
import co.cablebox.tv.socket.SocketConnection;
import co.cablebox.tv.socket.TvboxSocketConnection;
import co.cablebox.tv.user.TvboxUser;
import co.cablebox.tv.user.User;

public class TvboxAppFactory implements AppFactory{
    @Override
    public SocketConnection getSocketConnection() {
        return new TvboxSocketConnection();
    }

    @Override
    public User getUser() {
        return new TvboxUser();
    }

    @Override
    public Class<?>  getLoginActivity() {
        return TvboxLoginActivity.class;
    }

    @Override
    public Class<?> getErrorActivity() {
        return TvboxErrorActivity.class;
    }
}
