package proxy;

import Utils.MessageUtils;
import Utils.UIHelper;
import burp.BurpException;
import burp.BurpExtender;
import burp.IInterceptedProxyMessage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

public class Drop extends ProxyComponent {

    private Pattern methodPattern;
    private Pattern hostPattern;
    private Pattern portPattern;
    private Pattern pathPattern;

    private JCheckBox enableCheckBox;
    private JLabel enableLabel;
    private JTextField hostnameField;
    private JTextField portField;
    private JTextField pathField;
    private JTextField methodField;
    private JButton pasteUrlButton;

    public Drop(BurpExtender ext) {
        super(ext);
    }

    public String getName() {
        return "Drop Requests";
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
                return;
            }
        }
        this.methodField.setEditable(!enable);
        this.hostnameField.setEditable(!enable);
        this.portField.setEditable(!enable);
        this.pathField.setEditable(!enable);
        this.pasteUrlButton.setEnabled(!enable);
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
        this.enableCheckBox = new JCheckBox("Drop requests");
        this.enableLabel = new JLabel("<html>Drop enabled and configs blocked. Disable to configure again " +
                "<span style=\"color:green\">⬤</span></html>", SwingConstants.RIGHT);
        this.methodField = new JTextField(".*", 8);
        this.hostnameField = new JTextField("", 36);
        this.portField = new JTextField(".*", 8);
        this.pathField = new JTextField(".*");
        UIHelper.setMonospaceFont(methodField, hostnameField, portField, pathField);
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
        this.pasteUrlButton = new JButton("Parse and paste URL");
        this.pasteUrlButton.addActionListener(actionEvent -> {
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
        buttonPane.add(this.pasteUrlButton);
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

        this.setEnabled(false);
        return pane;
    }
}
