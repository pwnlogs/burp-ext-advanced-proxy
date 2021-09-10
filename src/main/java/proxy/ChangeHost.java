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
        return "Host Change";
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
        this.enableCheckBox = new JCheckBox("Change destination host");
        this.enableLabel = new JLabel("<html>Host change enabled and configs blocked. Disable to configure again " +
                "<span style=\"color:green\">⬤</span></html>", SwingConstants.RIGHT);
        this.hostnameField = new JTextField("", 72);
        this.pathField = new JTextField(".*", 72);
        this.methodField = new JTextField(".*", 72);
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
        c.gridwidth = 2;
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
                Utils.pasteUrlFromClipboard(this.hostnameField, this.pathField);
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

        // Hostname (regex) [______]
        c.gridwidth = 1;
        c.gridy = c.gridy + 1;
        c.weightx = 0;
        pane.add(new JLabel("Hostname (regex)"), c);
        c.gridx = 1;
        c.weightx = 1;
        pane.add(this.hostnameField, c);

        // Path (regex) [______]
        c.gridx = 0;
        c.gridy = c.gridy + 1;
        c.weightx = 0;
        pane.add(new JLabel("Path (regex)"), c);
        c.gridx = 1;
        c.weightx = 1;
        pane.add(this.pathField, c);

        // Method (regex) [______]
        c.gridx = 0;
        c.gridy = c.gridy + 1;
        c.weightx = 0;
        pane.add(new JLabel("Method (regex)"), c);
        c.gridx = 1;
        c.weightx = 1;
        pane.add(this.methodField, c);

        this.setEnabled(false);
        return pane;
    }

}
