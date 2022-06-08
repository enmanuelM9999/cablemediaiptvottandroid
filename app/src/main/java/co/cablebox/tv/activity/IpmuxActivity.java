package co.cablebox.tv.activity;

public interface IpmuxActivity {

    /**
     * Is necesary set the android context onCreate any activity, because the socket
     * can receive a emit anytime and it needs to launch a new activity if is necesary
     */
    void setContextOnAppState();

    /**
     * Is necesary set the (View)Message object onCreate any activity, because the socket
     * can receive a emit anytime and it needs to show a tooltip message if is necesary
     */
    void setMessageOnAppState();
}
