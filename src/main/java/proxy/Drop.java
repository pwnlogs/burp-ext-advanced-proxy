package proxy;

import Utils.UIHelper;
import burp.BurpException;
import burp.BurpExtender;
import burp.IInterceptedProxyMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Arrays;

public class Drop extends ProxyComponent {

    public Drop(BurpExtender ext) {
        super(ext);
    }

    public String getName() {
        return "Drop Requests";
    }

    @Override
    public void setEnabled(boolean enable) {
        if (enable) {
            enable = this.verifyAndSetFilter();
        }
        this.setFilterEditable(!enable);
        this.enabled = enable;
    }

    /**
     * @param pmc
     * @return true if the request should be processed further, else false.
     */
    @Override
    public boolean processRequest(ProxyMessageContainer pmc) {
        if (this.inFilter(pmc)) {
            pmc.message.setInterceptAction(IInterceptedProxyMessage.ACTION_DROP);
            return false;
        }
        return true;
    }

    /**
     * @param pmc
     * @return true if the response should be processed further, else false.
     */
    @Override
    public boolean processResponse(ProxyMessageContainer pmc) {
        return true;
    }

    /**
     * @return panel (BoxLayout) to be added into the UI tab, return null if there is no UI component
     */
    @Override
    public JPanel getPanel() {
        JPanel pane = new JPanel();
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = this.getDefaultGBC();

        // Title   ... Drop Requests ...
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx = 1;
        c.gridwidth = 6;
        c.gridx = 0;
        c.gridy = 0;
        pane.add(this.getFilterPanel(), c);

        this.setEnabled(false);
        return pane;
    }
}
