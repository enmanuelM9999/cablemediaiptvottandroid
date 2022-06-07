package co.cablebox.tv.socket;

import co.cablebox.tv.AppState;

public class TvboxSocketConnection extends SocketConnection {
    public TvboxSocketConnection(){
        super();
    }

    @Override
    public void socketEmitJoin() {
        String[] userCredentials= AppState.getUser().getUserCredentials();

        /*Serial number is in first position when User is a TvboxUser */
        String serialNumber= userCredentials[0];

        socket.emit("join",serialNumber);
    }
}
