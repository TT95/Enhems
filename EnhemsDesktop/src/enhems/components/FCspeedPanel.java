package enhems.components;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JPanel;

import enhems.Utilities;

public class FCspeedPanel extends JPanel {

	private static final long serialVersionUID = 1L;
	private static final String pathToShutdownIcon = "res/icons/shutdown32.png";
	private static final String pathToSmallFanIcon = "res/icons/fan32.png";
	private static final String pathToMediumFanIcon = "res/icons/fan42.png";
	private static final String pathToBigFanIcon = "res/icons/fan52.png";


	private List<JButton> buttonsList;
	private int selectedButton;
	
	/**
	 * 
	 * @param title panel title
	 * @param pathToIconRes icons should be 64x64 pixels
	 */
	public FCspeedPanel(int startingFCspeed) {
		buttonsList = new ArrayList<>();
		JButton shutdownFan = new JButton(new ImageIcon(Utilities.class.getResource(pathToShutdownIcon)));
		JButton smallFan = new JButton(new ImageIcon(Utilities.class.getResource(pathToSmallFanIcon)));
		JButton mediumFan = new JButton(new ImageIcon(Utilities.class.getResource(pathToMediumFanIcon)));
		JButton bigFan = new JButton(new ImageIcon(Utilities.class.getResource(pathToBigFanIcon)));
		shutdownFan.addActionListener(getButtonAction());
		smallFan.addActionListener(getButtonAction());
		mediumFan.addActionListener(getButtonAction());
		bigFan.addActionListener(getButtonAction());
//		shutdownFan.setBorder(BorderFactory.createEmptyBorder());
//		smallFan.setBorder(BorderFactory.createEmptyBorder());
//		mediumFan.setBorder(BorderFactory.createEmptyBorder());
//		bigFan.setBorder(BorderFactory.createEmptyBorder());
		buttonsList.add(shutdownFan);
		buttonsList.add(smallFan);
		buttonsList.add(mediumFan);
		buttonsList.add(bigFan);
		
		setBorder(BorderFactory.createTitledBorder("Brzina ventilatora"));
		
		setLayout(new GridBagLayout());
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1;
		c.weighty = 1;
		c.anchor = GridBagConstraints.CENTER;
		add(shutdownFan,c);
		c.gridx = 1;
		add(smallFan,c);
		c.gridx = 2;
		add(mediumFan,c);
		c.gridx = 3;
		add(bigFan,c);
		
		buttonsList.get(startingFCspeed).doClick();
	}
	
	public int getFCspeed() {
		return selectedButton;
	}
	
	public void setFCspeed(int fcspeed) {
		buttonsList.get(fcspeed).doClick();
	}
	
	
	private ActionListener getButtonAction() {
		return new AbstractAction() {
			private static final long serialVersionUID = 1L;
			public void actionPerformed(ActionEvent e) {
				JButton b = (JButton)e.getSource();
				buttonsList.get(selectedButton).setEnabled(true);
				for(int i=0;i<buttonsList.size();i++) {
					if(buttonsList.get(i).equals(b)) {
						selectedButton = i;
						buttonsList.get(i).setEnabled(false);
					}
				}
			}
		};
	}
	
}
