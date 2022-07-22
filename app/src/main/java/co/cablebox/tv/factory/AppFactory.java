package co.cablebox.tv.factory;

import co.cablebox.tv.activity.error.ErrorActivity;
import co.cablebox.tv.activity.error.TvboxErrorActivity;
import co.cablebox.tv.activity.login.LoginActivity;
import co.cablebox.tv.socket.SocketConnection;
import co.cablebox.tv.user.User;

public interface AppFactory {
    SocketConnection getSocketConnection();
    User getUser();
    Class<?>  getLoginActivity();
    Class<?>  getErrorActivity();
    Class<?> getSettingsActivity();
    Class<?> getVideoPlayerActivity();

}
