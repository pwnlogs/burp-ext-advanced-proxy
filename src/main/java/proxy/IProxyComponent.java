package proxy;

import burp.IInterceptedProxyMessage;

import javax.swing.*;

/**
 * Interface for different components of Proxy
 */
public interface IProxyComponent {

    public boolean isEnabled();

    public void setEnabled(boolean enable);

    public String getName();

    /**
     * @return true if the request should be processed further, else false.
     */
    public boolean processRequest(ProxyMessageContainer pmc);

    /**
     * @return true if the response should be processed further, else false.
     */
    public boolean processResponse(ProxyMessageContainer pmc);

    /**
     * @return panel (BoxLayout) to be added into the UI tab, return null if there is no UI component
     */
    public JPanel getPanel();
}
