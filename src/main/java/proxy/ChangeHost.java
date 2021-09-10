package proxy;

import Utils.MessageUtils;
import Utils.UIHelper;
import burp.*;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class ChangeHost extends ProxyComponent{

    private Pattern methodPattern;
    private Pattern hostPattern;
    private Pattern portPattern;
    private Pattern pathPattern;
    private String destinationProtocol;
    private String destinationHost;
    private int destinationPort;

    private JCheckBox enableCheckBox;
    private JLabel enableLabel;
    private JTextField methodField;
    private JTextField hostnameField;
    private JTextField portField;
    private JTextField pathField;
    private JComboBox destinationProtocolDropDown;
    private JTextField destinationHostField;
    private JTextField destinationPortField;
    private JButton pasteSrcUrlButton;
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
            // Method
            String method = this.methodField.getText().trim();
            if ("".equals(method)) {
                this.methodField.setText(".*");
            }
            try {
                this.methodPattern = Pattern.compile("".equals(method) ? method : ".*");
            } catch (PatternSyntaxException e) {
                JOptionPane.showMessageDialog(null,
                        "Method is not valid regex.",
                        "Invalid Method", JOptionPane.ERROR_MESSAGE);
                this.ext.stderr.println(Arrays.toString(e.getStackTrace()));
                return;
            }
            // Check hostname
            String hostname = this.hostnameField.getText().trim();
            if ("".equals(hostname)) {
                JOptionPane.showMessageDialog(null,
                        "Hostname is empty.",
                        "Invalid Hostname", JOptionPane.ERROR_MESSAGE);
                this.enableCheckBox.setSelected(false);
                return;
            }
            try {
                this.hostPattern = Pattern.compile(hostname);
            } catch (PatternSyntaxException e) {
                JOptionPane.showMessageDialog(null,
                        "Hostname is not valid regex.",
                        "Invalid Hostname", JOptionPane.ERROR_MESSAGE);
                this.enableCheckBox.setSelected(false);
                return;
            }
            // Port
            String port = this.portField.getText().trim();
            if ("".equals(method)) {
                this.portField.setText(".*");
            }
            try {
                this.portPattern = Pattern.compile("".equals(method) ? method : ".*");
            } catch (PatternSyntaxException e) {
                JOptionPane.showMessageDialog(null,
                        "Port is not valid regex.",
                        "Invalid Port", JOptionPane.ERROR_MESSAGE);
                this.enableCheckBox.setSelected(false);
                return;
            }
            // check path
            String url = this.pathField.getText().trim();
            if ("".equals(url)) {
                this.pathField.setText(".*");
            }
            try {
                this.pathPattern = Pattern.compile(url);
            } catch (PatternSyntaxException e) {
                JOptionPane.showMessageDialog(null,
                        "URL is not valid regex.",
                        "Invalid URL", JOptionPane.ERROR_MESSAGE);
                this.enableCheckBox.setSelected(false);
                return;
            }
            // destination
            this.destinationProtocol = (String) this.destinationProtocolDropDown.getSelectedItem();
            this.destinationHost = this.destinationHostField.getText();
            if ("".equals(destinationHost)) {
                JOptionPane.showMessageDialog(null,
                        "Destination host is not valid.",
                        "Invalid Host", JOptionPane.ERROR_MESSAGE);
                this.enableCheckBox.setSelected(false);
                return;
            }
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
                return;
            }
        }
        this.methodField.setEditable(!enable);
        this.hostnameField.setEditable(!enable);
        this.portField.setEditable(!enable);
        this.pathField.setEditable(!enable);
        this.pasteSrcUrlButton.setEnabled(!enable);
        this.destinationProtocolDropDown.setEditable(!enable);
        this.destinationHostField.setEditable(!enable);
        this.destinationPortField.setEditable(!enable);
        this.pasteDestUrlButton.setEnabled(!enable);
        this.enableLabel.setVisible(enable);
        this.enabled = enable;
    }

    /**
     * @param pmc
     * @return true if the request should be processed further, else false.
     */
    @Override
    public boolean processRequest(ProxyMessageContainer pmc) {
        if (this.enabled &&
                this.methodPattern.matcher(pmc.getMethod()).matches() &&
                this.hostPattern.matcher(pmc.getHostname()).matches() &&
                this.portPattern.matcher(pmc.getPort()).matches() &&
                this.pathPattern.matcher(pmc.getPath()).matches()) {
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
        this.enableCheckBox = new JCheckBox("Change Destination");
        this.enableLabel = new JLabel("<html>Destination change enabled and configs blocked." +
                " Disable to configure again <span style=\"color:green\">⬤</span></html>", SwingConstants.RIGHT);
        this.methodField = new JTextField(".*", 8);
        this.hostnameField = new JTextField("", 36);
        this.portField = new JTextField(".*", 8);
        this.pathField = new JTextField(".*");
        this.destinationProtocolDropDown = UIHelper.getProtocolDropDown();
        this.destinationProtocolDropDown.setSelectedIndex(1);
        this.destinationHostField = new JTextField("");
        this.destinationPortField = new JTextField("");
        UIHelper.setMonospaceFont(methodField, hostnameField, portField, pathField, destinationHostField, destinationPortField);
        this.enableCheckBox.addItemListener(e -> {
            setEnabled(e.getStateChange() == ItemEvent.SELECTED);
        });

        JPanel pane = new JPanel();
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = this.getDefaultGBC();

        // Title   ... Drop Requests ...
        JLabel titlePane = new JLabel(this.getName(), SwingConstants.CENTER);
        Font font = titlePane.getFont();
        titlePane.setFont(font.deriveFont(font.getStyle() | Font.BOLD));
        c.anchor = GridBagConstraints.CENTER;
        c.weightx = 1;
        c.gridwidth = 6;
        pane.add(titlePane, c);

        // [ ] Drop request with ...     ... Drop enabled
        JPanel checkBoxPane1 = new JPanel();
        checkBoxPane1.setLayout(new BoxLayout(checkBoxPane1, BoxLayout.X_AXIS));
        checkBoxPane1.add(this.enableCheckBox);
        checkBoxPane1.add(Box.createHorizontalGlue());
        checkBoxPane1.add(this.enableLabel);
        c.anchor = GridBagConstraints.LINE_START;
        c.gridy = c.gridy + 1;
        pane.add(checkBoxPane1, c);

        //  button panel ... [Parse and paste URL]
        this.pasteSrcUrlButton = new JButton("Parse and paste URL");
        this.pasteSrcUrlButton.addActionListener(actionEvent -> {
            try {
                UIHelper.pasteUrlFromClipboard(null, this.hostnameField, this.portField, this.pathField);
            } catch (BurpException e) {
                JOptionPane.showMessageDialog(null,
                        e.getShowMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                this.ext.stderr.println(Arrays.toString(e.getStackTrace()));
            }
        });
        JPanel buttonPane = new JPanel();
        buttonPane.setLayout(new BoxLayout(buttonPane, BoxLayout.X_AXIS));
        buttonPane.add(this.pasteSrcUrlButton);
        c.gridy = c.gridy + 1;
        pane.add(buttonPane, c);

        // Method (.*) [___] Hostname (.*) [________]  Port (.*) [___]
        c.gridwidth = 1;
        c.gridy = c.gridy + 1;
        // method
        c.weightx = 0;
        pane.add(new JLabel("Method (.*)"), c);
        c.gridx = c.gridx + 1;
        c.weightx = 0.2;
        pane.add(this.methodField, c);
        // hostname
        c.gridx = c.gridx + 1;
        c.weightx = 0;
        pane.add(new JLabel("Hostname (.*)"), c);
        c.gridx = c.gridx + 1;
        c.weightx = 1;
        pane.add(this.hostnameField, c);
        // port
        c.gridx = c.gridx + 1;
        c.weightx = 0;
        pane.add(new JLabel("Port (.*)"), c);
        c.gridx = c.gridx + 1;
        c.weightx = 0.2;
        pane.add(this.portField, c);

        // Path (regex) [______]
        c.gridx = 0;
        c.gridy = c.gridy + 1;
        c.weightx = 0;
        pane.add(new JLabel("Path (.*)"), c);
        c.gridx = c.gridx + 1;
        c.gridwidth = 5;
        c.weightx = 1;
        pane.add(this.pathField, c);

        //  button panel ... [Parse and paste URL]
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
        c.gridx = 0;
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
