package proxy;

import Utils.UIHelper;
import burp.BurpException;
import burp.BurpExtender;
import burp.IHttpService;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class ChangeHost extends ProxyComponent{

    private String destinationProtocol;
    private String destinationHost;
    private int destinationPort;

    private JComboBox destinationProtocolDropDown;
    private JTextField destinationHostField;
    private JTextField destinationPortField;
    private JButton pasteDestUrlButton;

    public ChangeHost(BurpExtender ext) {
        super(ext);
    }

    public String getName() {
        return "Destination Change";
    }

    @Override
    public void setEnabled(boolean enable) {
        if (enable) {
            // set filter
            enable = this.verifyAndSetFilter();
        }
        // destination
        if (enable) {
            this.destinationProtocol = (String) this.destinationProtocolDropDown.getSelectedItem();
            this.destinationHost = this.destinationHostField.getText();
            if ("".equals(destinationHost)) {
                JOptionPane.showMessageDialog(null,
                        "Destination host is not valid.",
                        "Invalid Host", JOptionPane.ERROR_MESSAGE);
                this.enableCheckBox.setSelected(false);
                enable = false;
            }
        }
        if (enable) {
            try {
                this.destinationPort = Integer.parseInt(this.destinationPortField.getText());
                if (destinationPort < 1 || destinationPort > 65535) {
                    throw new NumberFormatException();
                }
            } catch (NumberFormatException e) {
                JOptionPane.showMessageDialog(null,
                        "Destination port is not valid.",
                        "Invalid Port", JOptionPane.ERROR_MESSAGE);
                this.ext.stderr.println(Arrays.toString(e.getStackTrace()));
                this.enableCheckBox.setSelected(false);
                enable = false;
            }
        }
        this.setFilterEditable(!enable);
        this.destinationProtocolDropDown.setEnabled(!enable);
        this.destinationHostField.setEditable(!enable);
        this.destinationPortField.setEditable(!enable);
        this.pasteDestUrlButton.setEnabled(!enable);
        this.enabled = enable;
    }

    /**
     * @param pmc
     * @return true if the request should be processed further, else false.
     */
    @Override
    public boolean processRequest(ProxyMessageContainer pmc) {
        if (this.inFilter(pmc)) {
            IHttpService httpService = new IHttpService() {
                @Override
                public String getHost() {
                    return destinationHost;
                }

                @Override
                public int getPort() {
                    return destinationPort;
                }

                @Override
                public String getProtocol() {
                    return destinationProtocol;
                }
            };
            pmc.message.getMessageInfo().setHttpService(httpService);
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
        this.destinationProtocolDropDown = UIHelper.getProtocolDropDown();
        this.destinationProtocolDropDown.setSelectedIndex(0);
        this.destinationHostField = new JTextField("127.0.0.1");
        this.destinationPortField = new JTextField("80");
        UIHelper.setMonospaceFont(destinationHostField, destinationPortField);

        JPanel pane = new JPanel();
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = this.getDefaultGBC();

        // Filter Panel
        c.anchor = GridBagConstraints.LINE_START;
        c.weightx = 1;
        c.gridwidth = 6;
        c.gridx = 0;
        c.gridy = 0;
        pane.add(this.getFilterPanel(), c);

        //  button panel ... [Parse and paste URL (destination)]
        this.pasteDestUrlButton = new JButton("Parse and paste URL (destination)");
        this.pasteDestUrlButton.addActionListener(actionEvent -> {
            try {
                UIHelper.pasteUrlFromClipboard(this.destinationProtocolDropDown,
                        this.destinationHostField, this.destinationPortField, null);
            } catch (BurpException e) {
                JOptionPane.showMessageDialog(null,
                        e.getShowMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                this.ext.stderr.println(Arrays.toString(e.getStackTrace()));
            }
        });
        JPanel buttonPane2 = new JPanel();
        buttonPane2.setLayout(new BoxLayout(buttonPane2, BoxLayout.X_AXIS));
        buttonPane2.add(this.pasteDestUrlButton);
        c.gridy = c.gridy + 1;
        pane.add(buttonPane2, c);

        // Scheme [HTTP v]  Hostname (.*) [________]  Port (.*) [___]
        c.gridwidth = 1;
        c.gridy = c.gridy + 1;
        // method
        c.weightx = 0;
        pane.add(new JLabel("Destination Protocol"), c);
        c.gridx = c.gridx + 1;
        c.weightx = 0.2;
        pane.add(this.destinationProtocolDropDown, c);
        // hostname
        c.gridx = c.gridx + 1;
        c.weightx = 0;
        pane.add(new JLabel("Destination Hostname"), c);
        c.gridx = c.gridx + 1;
        c.weightx = 1;
        pane.add(this.destinationHostField, c);
        // port
        c.gridx = c.gridx + 1;
        c.weightx = 0;
        pane.add(new JLabel("Destination Port"), c);
        c.gridx = c.gridx + 1;
        c.weightx = 0.2;
        pane.add(this.destinationPortField, c);

        this.setEnabled(false);
        return pane;
    }

}
