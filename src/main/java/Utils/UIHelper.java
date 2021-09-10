package Utils;

import burp.BurpException;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.net.URL;
import java.util.regex.Pattern;

public class UIHelper {

    public static final int HTTP_INDEX = 0;
    public static final int HTTPS_INDEX = 1;

    public static void pasteUrlFromClipboard(
            JComboBox protocolDropDown, JTextComponent hostnameField,
            JTextComponent portField, JTextComponent pathField) throws BurpException {
        try {
            String urlStr = (String) Toolkit.getDefaultToolkit()
                    .getSystemClipboard().getData(DataFlavor.stringFlavor);
            URL url = new URL(urlStr);
            // Hostname
            hostnameField.setText(Pattern.quote(url.getHost()));
            // Port
            int port = url.getPort();
            if (port == -1) {
                port = "HTTP".equals(url.getProtocol()) ? 80 : 443;
            }
            portField.setText(Pattern.quote(String.valueOf(port)));
            // Path
            if (pathField != null) {
                String path = url.getPath();
                pathField.setText(Pattern.quote(
                        path.endsWith("/") ? path.substring(0, path.length()-1) : path
                ) + "/?");
            }
            // Protocol
            if (protocolDropDown != null) {
                protocolDropDown.setSelectedIndex("HTTP".equals(url.getProtocol()) ? UIHelper.HTTP_INDEX : UIHelper.HTTPS_INDEX);
            }
        } catch (UnsupportedFlavorException | IOException e) {
            throw new BurpException("Failed to read/parse clipboard content. Clipboard data might be invalid.", e);
        }
    }

    public static JComboBox getProtocolDropDown() {
        return new JComboBox(new String[]{"http", "https"});
    }

    public static void setMonospaceFont(JTextComponent... components) {
        Font monospaceFont = new Font("monospaced", Font.PLAIN, components[0].getFont().getSize()-2);
        for (JTextComponent c: components) {
            c.setFont(monospaceFont);
        }
    }

}
