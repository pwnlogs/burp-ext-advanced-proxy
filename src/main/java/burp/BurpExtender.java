package burp;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


public class BurpExtender implements IBurpExtender, ITab, IProxyListener {

    private final String name = "Auto Drop";

    private boolean dropEnabled;
    private JCheckBox enableDropCheckBox;
    private JLabel enableDropLabel;
    private JTextField hostnameField;
    private JTextField pathField;
    private JTextField methodField;
    private JButton pasteUrlButton;
    private Pattern hostPattern;
    private Pattern pathPattern;
    private Pattern methodPattern;

    private JPanel tabUi;
    public PrintWriter stdout;
    public PrintWriter stderr;
    private IBurpExtenderCallbacks callbacks;
    private IExtensionHelpers helpers;

    public BurpExtender() { }

    public void registerExtenderCallbacks(IBurpExtenderCallbacks callbacks) {
        this.callbacks = callbacks;
        this.stdout = new PrintWriter(callbacks.getStdout(), true);
        this.stderr = new PrintWriter(callbacks.getStderr(), true);
        callbacks.setExtensionName(this.name);
        this.createTabUi();
        callbacks.addSuiteTab(this);
        this.helpers = callbacks.getHelpers();
        callbacks.registerProxyListener(this);
        this.stdout.println("Loaded" + this.name + "Extension");
    }

    @Override
    public void processProxyMessage(boolean messageIsRequest, IInterceptedProxyMessage message) {
        if (messageIsRequest) {
            if (this.dropEnabled &&
                    this.hostPattern.matcher(message.getMessageInfo().getHttpService().getHost()).matches()) {
                processRequest(message);
            }
        }
    }

    private void processRequest(IInterceptedProxyMessage message) {
        byte[] request = message.getMessageInfo().getRequest();
        IRequestInfo requestInfo = this.helpers.analyzeRequest(request);
        if (this.methodPattern.matcher(requestInfo.getMethod()).matches()) {
            String path = BurpExtender.getResourcePath(request);
            if (this.pathPattern.matcher(path).matches()) {
                message.setInterceptAction(IInterceptedProxyMessage.ACTION_DROP);
            }
        }
    }

    /**
     * Return the resource path from the request.
     * If the request is malformed, array of 0 elements will be returned.
     *
     * @param request request as byte array
     * @return resource path as string
     */
    public static String getResourcePath(byte[] request) {
        int i = 0;
        // skip HTTP method
        for ( ; i < request.length-1 && request[i] != ' '; i++) ;
        // Start copying resource path
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        i++; // start at (method + ' ')
        for ( ; i < request.length-1; i++) {
            // if ' ' or '?' or '/ ' or '/?' is encountered, break
            if (request[i] == (byte)' ' || request[i] == (byte)'?' ||
                    (request[i] == (byte)'/' && (request[i+1] == (byte)' ' || request[i+1] == (byte)'?'))) {
                break;
            }
            ba.write(request[i]);
        }
        return ba.toString();
    }

    private void enableDrop() {
        // Hostname
        String hostname = this.hostnameField.getText().trim();
        if ("".equals(hostname)) {
            JOptionPane.showMessageDialog(null,
                    "Hostname is empty.",
                    "Invalid Hostname", JOptionPane.ERROR_MESSAGE);
            return;
        }
        try {
            this.hostPattern = Pattern.compile(hostname);
        } catch (PatternSyntaxException e) {
            JOptionPane.showMessageDialog(null,
                    "Hostname is not valid regex.",
                    "Invalid Hostname", JOptionPane.ERROR_MESSAGE);
            this.enableDropCheckBox.setSelected(false);
            return;
        }
        // URL
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
        // Method
        String method = this.methodField.getText().trim();
        if ("".equals(method)) {
            this.methodField.setText(".*");
        }
        try {
            this.methodPattern = Pattern.compile(method);
        } catch (PatternSyntaxException e) {
            JOptionPane.showMessageDialog(null,
                    "Method is not valid regex.",
                    "Invalid Method", JOptionPane.ERROR_MESSAGE);
            return;
        }
        this.hostnameField.setEditable(false);
        this.pathField.setEditable(false);
        this.methodField.setEditable(false);
        this.pasteUrlButton.setEnabled(false);
        this.enableDropLabel.setVisible(true);
        this.dropEnabled = true;
    }

    private void disableDrop() {
        this.hostnameField.setEditable(true);
        this.pathField.setEditable(true);
        this.methodField.setEditable(true);
        this.pasteUrlButton.setEnabled(true);
        this.enableDropLabel.setVisible(false);
        this.dropEnabled = false;
    }

    @Override
    public String getTabCaption() {
        return "Drop";
    }

    /**
     * Burp uses this method to obtain the component that should be used as the
     * contents of the custom tab when it is displayed.
     *
     * @return The component that should be used as the contents of the custom
     * tab when it is displayed.
     */
    @Override
    public Component getUiComponent() {
        return tabUi;
    }

    private void createTabUi() {
        this.tabUi = new JPanel();
        createLoaders();
        callbacks.customizeUiComponent(tabUi);
    }

    private void createLoaders() {
        this.enableDropCheckBox = new JCheckBox("Drop requests with matching host");
        this.enableDropLabel = new JLabel("<html>Drop enabled and configs blocked. Disable to configure again " +
                "<span style=\"color:green\">⬤</span></html>", SwingConstants.RIGHT);
        this.hostnameField = new JTextField("", 72);
        this.pathField = new JTextField(".*", 72);
        this.methodField = new JTextField(".*", 72);
        this.enableDropCheckBox.addItemListener(e -> {
            if (e.getStateChange() == ItemEvent.SELECTED) {
                enableDrop();
            } else {
                disableDrop();
            }
        });

        JPanel pane = new JPanel();
        pane.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.anchor = GridBagConstraints.LINE_START;
        c.insets = new Insets(10, 10, 10, 10);  // margin
        c.ipady = 5; // padding
        c.weightx = 1;

        JPanel pane1 = new JPanel();
        pane1.setLayout(new BoxLayout(pane1, BoxLayout.X_AXIS));
        pane1.add(this.enableDropCheckBox);
        pane1.add(Box.createHorizontalGlue());
        pane1.add(this.enableDropLabel);

        c.gridwidth = 2;
        c.gridy = 0;
        c.gridx = 0;
        pane.add(pane1, c);

        c.gridwidth = 1;
        c.gridy = c.gridy + 1;
        this.pasteUrlButton = new JButton("Parse and paste URL");
        this.pasteUrlButton.addActionListener(actionEvent -> {
            try {
                String urlStr = (String) Toolkit.getDefaultToolkit()
                        .getSystemClipboard().getData(DataFlavor.stringFlavor);
                URL url = new URL(urlStr);
                hostnameField.setText(url.getHost());
                pathField.setText(url.getPath());
            } catch (UnsupportedFlavorException | IOException e) {
                JOptionPane.showMessageDialog(null,
                        "Failed to read/parse clipboard content.",
                        "Error", JOptionPane.ERROR_MESSAGE);
                stderr.println(Arrays.toString(e.getStackTrace()));
            }
        });
        pane.add(this.pasteUrlButton, c);

        c.gridwidth = 1;
        c.gridy = c.gridy + 1;
        pane.add(new JLabel("Hostname (regex)"), c);
        c.gridx = 1;
        pane.add(this.hostnameField, c);

        c.gridx = 0;
        c.gridy = c.gridy + 1;
        pane.add(new JLabel("Path (regex)"), c);
        c.gridx = 1;
        pane.add(this.pathField, c);

        c.gridx = 0;
        c.gridy = c.gridy + 1;
        pane.add(new JLabel("Method (regex)"), c);
        c.gridx = 1;
        pane.add(this.methodField, c);

        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEADING));
        panel.add(pane);
        this.tabUi.setLayout(new BorderLayout());
        this.tabUi.add(panel, BorderLayout.PAGE_START);

        this.disableDrop();
        System.gc();
    }
}
