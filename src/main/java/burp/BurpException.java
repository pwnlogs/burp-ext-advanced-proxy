package burp;

public class BurpException extends Exception {

    public String getShowMessage() {
        return showMessage;
    }

    public void setShowMessage(String showMessage) {
        this.showMessage = showMessage;
    }

    String showMessage;

    public BurpException(String msg, Throwable err) {
        super(err.getMessage(), err);
        this.showMessage = msg;
    }

}
