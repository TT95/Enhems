package enhems;

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JOptionPane;

public class Utilities {
	
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
