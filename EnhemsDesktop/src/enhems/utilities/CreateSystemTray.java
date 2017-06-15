package enhems.utilities;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;

/**
 * Created by TeoLenovo on 3/26/2017.
 */
public class CreateSystemTray {

    private static JFrame frame;

    public static void create(JFrame frame) {
        CreateSystemTray.frame = frame;
        if(!SystemTray.isSupported()) {
            CommonUtilities.showDialog("Warning", "System tray not supported", frame,
                    null, JOptionPane.ERROR_MESSAGE);
            return;
        }
        TrayIcon trayIcon;
        SystemTray tray = SystemTray.getSystemTray();
        Image trayIconImage = CommonUtilities.getImageByName("enhems16.png");
        int trayIconWidth = new TrayIcon(trayIconImage).getSize().width;
        Image image = trayIconImage.getScaledInstance(trayIconWidth, -1, Image.SCALE_SMOOTH);
        ActionListener exitListener= e -> {
            System.exit(0);
        };
        PopupMenu popup=new PopupMenu();
        MenuItem defaultItem=new MenuItem("Exit");
        defaultItem.addActionListener(exitListener);
        popup.add(defaultItem);
        defaultItem=new MenuItem("Open");
        trayIcon = new TrayIcon(image,"Tray Icon", popup);
        trayIcon.setImageAutoSize(true);
        defaultItem.addActionListener(e -> fromSystemTray());
        popup.add(defaultItem);


        frame.addWindowStateListener(e -> {
            System.out.println(e.getNewState());
            if(e.getNewState()==frame.ICONIFIED){
                toSystemTray(tray,trayIcon);
            }
        });

        toSystemTray(tray,trayIcon);
    }

    private static void fromSystemTray() {
        frame.setState(0);
        frame.setVisible(true);
    }


    private static void toSystemTray(SystemTray tray, TrayIcon icon) {
        try {
            if(!Arrays.asList(tray.getTrayIcons()).contains(icon)) {
                tray.add(icon);
            }
            frame.dispose();
        } catch (AWTException ex) {
            CommonUtilities.showDialog("Error", "Program cannot be added to system tray!",
                    frame, ex, JOptionPane.ERROR_MESSAGE);
        }
    }
}
