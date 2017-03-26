package enhems;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.*;

import javax.imageio.ImageIO;
import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.WindowConstants;
import javax.swing.border.TitledBorder;

import enhems.components.ControlPanel;
import enhems.components.GraphPanel;
import enhems.components.MeasuredUnitPanel;
import enhems.utilities.CommonUtilities;
import enhems.utilities.CreateSystemTray;
import org.jnativehook.GlobalScreen;


public class Enhems extends JFrame {

	private static final long serialVersionUID = 1L;
	private EnhemsDataModel dataModel;
	private Timer refreshDataModelTimer = new Timer();
	

	public Enhems() {
		setTitle("EnhemsApp");
		CommonUtilities.setEnhemsIconToFrame(this);
		setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
		addWindowListener(new WindowAdapter() {
			@Override
			public void windowClosing(WindowEvent e) {
				//close timer thread
				refreshDataModelTimer.cancel();
				refreshDataModelTimer.purge();
				try {
					//close hook thread
				GlobalScreen.unregisterNativeHook();
				} catch (Exception ex) {
					MyLogger.log("Problem closing some context thread with application", ex);
				}
				System.runFinalization();
			}
		});
		loginGUI();
	}

	/**
	 * starts login process and if connection to server is made
	 * method mainGUI is started which builds GUI
	 */
	private void loginGUI() {
		LoginProcess.tokenLogin(this, new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				ServerService.executeRequest(new ServerRequest() {
					String[] units = null;
					public void execute() {
						units = ServerService.GetAssignedUnits();
					}
					public void afterExecution() {
						mainGUI(units);
					}
				});
			}
		});
	}

	/**
	 * Used if there is no token for login
	 */
	public void panelLogin() {
		LoginProcess.createLoginGUI(this);
	}


	/**
	 * This GUI is loaded when authorization of user was made
	 * @param units room - units
	 */
	private void mainGUI(String[] units) {
		
		dataModel = new EnhemsDataModel(units);

		//refresh data periodically
		initTimerRefresh(dataModel);

		//put in system tray if possible
		CreateSystemTray.create(this);

		//refresh GUI for main interface
		getContentPane().removeAll();
		getContentPane().repaint();

		JPanel centerPanel = new JPanel(new GridLayout(1,0));
		GraphPanel graphPanel = new GraphPanel("Graf", dataModel);
		centerPanel.add(graphPanel);


		setLayout(new BorderLayout());
		JPanel upperPanel = new JPanel(new GridBagLayout());
		JPanel leftPanel = new JPanel(new GridBagLayout());
		
		ControlPanel controlPanel = new ControlPanel(
				"Kontrole", 10, BoxLayout.Y_AXIS, TitledBorder.CENTER, dataModel);
		MeasuredUnitPanel humidityPanel = new MeasuredUnitPanel(
				"Vlažnost", "humidity.png", dataModel, GraphCodes.humidity);
		MeasuredUnitPanel co2Panel = new MeasuredUnitPanel(
				"CO2", "co2.png", dataModel, GraphCodes.co2);
		MeasuredUnitPanel temperaturePanel = new MeasuredUnitPanel(
				"Temperatura", "temperature.png", dataModel, GraphCodes.temperature);
		
		leftPanel.setBorder(BorderFactory.createEmptyBorder(0,10,10,10));
		GridBagConstraints c = new GridBagConstraints();
		c.weighty = 1.0;
		c.fill=GridBagConstraints.HORIZONTAL;
		c.anchor = GridBagConstraints.PAGE_START;
		leftPanel.add(controlPanel,c);
		c.fill=GridBagConstraints.NONE;
		c.weighty = 0.3;
		c.anchor = GridBagConstraints.FIRST_LINE_START;
		c.gridy=1;
		leftPanel.add(temperaturePanel,c);
		c.gridy=2;
		leftPanel.add(co2Panel,c);
		c.gridy=3;
		leftPanel.add(humidityPanel, c);

		Set<String> rooms = dataModel.getRooms();
		JComboBox roomSelected = new JComboBox<>(rooms.toArray(new String[rooms.size()]));
		roomSelected.setEnabled(true);
		DefaultListCellRenderer dlcr = new DefaultListCellRenderer(); 
		dlcr.setHorizontalAlignment(DefaultListCellRenderer.CENTER); 
		roomSelected.setRenderer(dlcr);
		roomSelected.setPreferredSize(new Dimension(150, roomSelected.getPreferredSize().height));
		roomSelected.addActionListener(e -> {
			@SuppressWarnings("unchecked")
			String selected = (String)roomSelected.getSelectedItem();
			dataModel.setSelectedRoom(selected);
		});
		JLabel usernameLabel = new JLabel("Prijavljen: " + Token.getUsername());
		usernameLabel.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		JButton logoutButton = new JButton("Odjava");
		logoutButton.addActionListener((l)-> {
			panelLogin();
			Token.removeToken();
		});
		JButton refreshButton = new JButton("Osvježi");
		refreshButton.addActionListener((l)-> {
			ServerService.executeRequest(new ServerRequest() {
				public void execute() {
					ImageIcon loadingIcon = new ImageIcon(
							CommonUtilities.getImageByName("loading.gif"));
					refreshButton.setHorizontalTextPosition(JButton.LEFT);
					refreshButton.setIcon(loadingIcon);
					refreshButton.setEnabled(false);
					dataModel.refreshData();
					
					/**
					 * using this sleep to show user that button was pressed
					 * in case of fast refreshing, also removes possibility
					 * for user to send to many requests at time by spamming
					 * button
					 */
					try {
						Thread.sleep(500);
					} catch (InterruptedException e) {
						MyLogger.log("Trouble with thread sleep", e);
					}
				}
				public void afterExecution() {
					refreshButton.setIcon(null);
					refreshButton.setEnabled(true);
				}
			});
		});

		c = new GridBagConstraints();
		c.insets = new Insets(10, 10, 15, 0);
		c.weightx = 1;
		c.gridx = 0;
		c.anchor = GridBagConstraints.LINE_START;
		upperPanel.add(usernameLabel, c);
		c.gridx = 1;
		c.anchor = GridBagConstraints.LINE_END;
		upperPanel.add(new JLabel("Prostorija:"), c);
		c.gridx = 2;
		c.anchor = GridBagConstraints.LINE_START;
		upperPanel.add(roomSelected, c);
		c.anchor = GridBagConstraints.LINE_END;
		c.gridx = 3;
		upperPanel.add(refreshButton, c);
		c.gridx = 4;
		c.anchor = GridBagConstraints.LINE_START;
		upperPanel.add(logoutButton, c);

		getContentPane().add(leftPanel, BorderLayout.LINE_START);
		getContentPane().add(centerPanel, BorderLayout.CENTER);
		getContentPane().add(upperPanel, BorderLayout.PAGE_START);	
		getContentPane().revalidate();
		
		//or 1024x620
		setSize(930, 580);
		CommonUtilities.putFrameInScreenCenter(this);
		
		
		dataModel.setSelectedRoom((String)roomSelected.getSelectedItem());

		//this activates activity listener
		new ActivityListener();

	}
	

	private void initTimerRefresh(EnhemsDataModel dataModel) {
		
        TimerTask userTask = new TimerTask() {
            @Override
            public void run() {
            	dataModel.refreshData();
            }
        };
        
		int refreshPeriod = 1200000;
        refreshDataModelTimer.schedule(userTask, refreshPeriod, refreshPeriod);
	}

	public static void main(String[] args) {
		SwingUtilities.invokeLater(()-> {
			try {
				UIManager.setLookAndFeel(
						"com.sun.java.swing.plaf.windows.WindowsLookAndFeel");
			} catch (Exception ignorable) { }
			new Enhems();
			
		});
	}

	public EnhemsDataModel getDataModel() {
		return dataModel;
	}



}
