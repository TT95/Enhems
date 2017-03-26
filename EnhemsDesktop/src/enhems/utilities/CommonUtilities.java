package enhems.utilities;

import enhems.MyLogger;

import java.awt.*;
import java.util.*;

import javax.swing.*;

/**
 * This class provides some tools needed throughout application
 */
public class CommonUtilities {

	public static void setEnhemsIconToFrame(JFrame frame) {
		java.util.List<Image> icons = new ArrayList<>();
		icons.add(getImageByName("enhems32.png"));
		icons.add(getImageByName("enhems16.png"));
		frame.setIconImages(icons);
	}

	public static Image getImageByName(String name) {
		return new ImageIcon(CommonUtilities.class.getResource("../res/icons/" + name)).getImage();
	}

	
	public static void putFrameInScreenCenter(JFrame frame) {
		Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		frame.setLocation(
				dim.width/2-frame.getSize().width/2,
				dim.height/2-frame.getSize().height/2);
	}
	
	/**
	 * Shows error to user and writes given desc to logger. If there is no exception just give null,
	 * this means that program is only talking to user (no logging).
	 */
	public static void showErrorDialog(String title, String desc, Component frame, Exception ex) {
		JOptionPane.showMessageDialog(frame,
				desc,
				title,
			    JOptionPane.ERROR_MESSAGE);
		if(ex != null) {
			MyLogger.log(desc, ex);
		}
	}

}
