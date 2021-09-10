package proxy;

import Utils.Utils;
import burp.IInterceptedProxyMessage;

/**
 * Container to optimize message analysis
 */
public class ProxyMessageContainer {
    public final IInterceptedProxyMessage message;
    private byte[] request = null;
    private String hostname = null;
    private String path = null;
    private String method = null;

    public ProxyMessageContainer(IInterceptedProxyMessage message) {
        this.message = message;
    }

    public String getHostname() {
        if (this.hostname == null) {
            this.hostname = this.message.getMessageInfo().getHttpService().getHost();
        }
        return this.hostname;
    }

    public byte[] getRequest() {
        if (this.request == null) {
            this.request = message.getMessageInfo().getRequest();
        }
        return this.request;
    }

    public String getPath() {
        if (this.path == null) {
            this.path = Utils.getResourcePath(this.getRequest());
        }
        return this.path;
    }

    public String getMethod() {
        if (this.method == null) {
            this.method = Utils.getMethod(this.getRequest());
        }
        return method;
    }

}
