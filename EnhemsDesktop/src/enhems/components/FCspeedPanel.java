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

import enhems.utilities.CommonUtilities;

public class FCspeedPanel extends JPanel {

	private static final long serialVersionUID = 1L;

	private List<JButton> buttonsList;
	private int selectedButton;
	
	/**
	 * 
	 * @param title panel title
	 * @param pathToIconRes icons should be 64x64 pixels
	 */
	public FCspeedPanel(int startingFCspeed) {
		buttonsList = new ArrayList<>();
		JButton shutdownFan = new JButton(new ImageIcon(CommonUtilities.getImageByName("shutdown32.png")));
		JButton smallFan = new JButton(new ImageIcon(CommonUtilities.getImageByName("fan32.png")));
		JButton mediumFan = new JButton(new ImageIcon(CommonUtilities.getImageByName("fan42.png")));
		JButton bigFan = new JButton(new ImageIcon(CommonUtilities.getImageByName("fan52.png")));
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
