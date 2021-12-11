package proxy;

import Utils.MessageUtils;
import burp.IInterceptedProxyMessage;

/**
 * Container to optimize message analysis
 */
public class ProxyMessageContainer {
    public final IInterceptedProxyMessage message;
    private byte[] request = null;
    private byte[] response = null;
    private String method = null;
    private String hostname = null;
    private String port = null;
    private String path = null;

    public ProxyMessageContainer(IInterceptedProxyMessage message) {
        this.message = message;
    }

    public byte[] getRequest() {
        if (this.request == null) {
            this.request = message.getMessageInfo().getRequest();
        }
        return this.request;
    }

    public byte[] getResponse() {
        if (this.response == null) {
            this.response = message.getMessageInfo().getResponse();
        }
        return this.request;
    }

    public String getMethod() {
        if (this.method == null) {
            this.method = MessageUtils.getMethod(this.getRequest());
        }
        return method;
    }

    public String getHostname() {
        if (this.hostname == null) {
            this.hostname = this.message.getMessageInfo().getHttpService().getHost();
        }
        return this.hostname;
    }

    public String getPort() {
        if (this.port == null) {
            this.port = String.valueOf(this.message.getMessageInfo().getHttpService().getPort());
        }
        return this.port;
    }

    public String getPath() {
        if (this.path == null) {
            this.path = MessageUtils.getResourcePath(this.getRequest());
        }
        return this.path;
    }
}
