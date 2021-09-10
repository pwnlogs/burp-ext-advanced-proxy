package proxy;

import burp.BurpExtender;

import java.awt.*;

abstract class ProxyComponent implements IProxyComponent{

    boolean enabled = false;
    final BurpExtender ext;

    public ProxyComponent(BurpExtender ext) {
        this.ext = ext;
    }

    @Override
    public boolean isEnabled() {
        return this.enabled;
    }

    GridBagConstraints getDefaultGBC() {
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(7, 10, 7, 10);  // margin
        c.ipady = 5; // padding
        c.weightx = 0;
        c.gridy = 0;
        c.gridx = 0;
        return c;
    }

}
