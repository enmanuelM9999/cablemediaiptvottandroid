package co.cablebox.tv.socket;

import co.cablebox.tv.AppState;

public class SmartphoneSocketConnection extends SocketConnection {
    public SmartphoneSocketConnection(){
        super();
    }

    @Override
    public void socketEmitJoin() {
        String[] userCredentials= AppState.getUser().getUserCredentials();

        /*User is in first position when User is a SmartphoneUser */
        String user= userCredentials[0];
        /*Password is in second position when User is a SmartphoneUser */
        String password= userCredentials[1];

        System.out.println("--------Login"+user+password);
        socket.emit("join",user,password);
    }
}
