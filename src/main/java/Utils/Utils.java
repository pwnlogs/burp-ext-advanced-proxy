package Utils;

import burp.BurpException;

import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;

public class Utils {

    /**
     * Return the resource path from a request.
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
    /**
     * Return the method from a request.
     * Use this if you already have request as byte[] and don't want to analyse the entire request.
     * But if you need more analysis, use IExtensionHelpers.analyzeRequest(...) provided by Burp
     *
     * @param request request as byte array
     * @return method as String
     */
    public static String getMethod(byte[] request) {
        ByteArrayOutputStream ba = new ByteArrayOutputStream();
        for (int i=0; i < request.length && request[i] != ' '; i++) ba.write(request[i]);
        return ba.toString();
    }

    public static void pasteUrlFromClipboard(JTextComponent hostnameField, JTextComponent pathField) throws BurpException {
        try {
            String urlStr = (String) Toolkit.getDefaultToolkit()
                    .getSystemClipboard().getData(DataFlavor.stringFlavor);
            URL url = new URL(urlStr);
            hostnameField.setText(url.getHost());
            pathField.setText(url.getPath());
        } catch (UnsupportedFlavorException | IOException e) {
            throw new BurpException("Failed to read/parse clipboard content. Clipboard data might be invalid.", e);
        }
    }
}
