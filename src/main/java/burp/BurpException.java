package burp;

public class BurpException extends Exception {

    public String getShowMessage() {
        return showMessage;
    }

    String showMessage;

    public BurpException(String msg, Throwable err) {
        super(err.getMessage(), err);
        this.showMessage = msg;
    }

}
