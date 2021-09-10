package proxy;

import Utils.Utils;
import burp.BurpException;
import burp.BurpExtender;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.util.Arrays;
import java.util.regex.Pattern;

public class ChangeHost extends ProxyComponent{

    private Pattern hostPattern;
    private Pattern pathPattern;
    private Pattern methodPattern;

    private JCheckBox enableCheckBox;
    private JLabel enableLabel;
    private JTextField hostnameField;
    private JTextField pathField;
    private JTextField methodField;
    private JButton pasteUrlButton;

    public ChangeHost(BurpExtender ext) {
        super(ext);
    }

    public String getName() {
        return "Change destination host";
    }

    @Override
    public void setEnabled(boolean enable) {

    }

    /**
     * @param pmc
     * @return true if the request should be processed further, else false.
     */
    @Override
    public boolean processRequest(ProxyMessageContainer pmc) {
        return false;
    }

    /**
     * @param pmc
     * @return true if the response should be processed further, else false.
     */
    @Override
    public boolean processResponse(ProxyMessageContainer pmc) {
        return false;
    }

    /**
     * @return panel (BoxLayout) to be added into the UI tab, return null if there is no UI component
     */
    @Override
    public JPanel getPanel() {
        this.enableCheckBox = new JCheckBox("Drop requests with matching host");
        this.enableLabel = new JLabel("<html>Drop enabled and configs blocked. Disable to configure again " +
                "<span style=\"color:green\">⬤</span></html>", SwingConstants.RIGHT);
        this.hostnameField = new JTextField("", 72);
        this.pathField = new JTextField(".*", 72);
        this.methodField = new JTextField(".*", 72);
        this.enableCheckBox.addItemListener(e -> {
            setEnabled(e.getStateChange() == ItemEvent.SELECTED);
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
        pane1.add(this.enableCheckBox);
        pane1.add(Box.createHorizontalGlue());
        pane1.add(this.enableLabel);

        c.gridwidth = 2;
        c.gridy = 0;
        c.gridx = 0;
        pane.add(pane1, c);

        c.gridwidth = 1;
        c.gridy = c.gridy + 1;
        this.pasteUrlButton = new JButton("Parse and paste URL");
        this.pasteUrlButton.addActionListener(actionEvent -> {
            try {
                Utils.pasteUrlFromClipboard(this.hostnameField, this.pathField);
            } catch (BurpException e) {
                JOptionPane.showMessageDialog(null,
                        e.getShowMessage(),
                        "Error", JOptionPane.ERROR_MESSAGE);
                this.ext.stderr.println(Arrays.toString(e.getStackTrace()));
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

        return pane;
    }

}
