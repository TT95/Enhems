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
import org.jnativehook.GlobalScreen;


public class Enhems extends JFrame {

	private static final long serialVersionUID = 1L;
	private static final String pathToLoadingIcon = "res/icons/loading.gif";
	private static final String pathToLoadTrayIcon = "res/icons/enhems16.png";
	private EnhemsDataModel dataModel;
	private Timer refreshDataModelTimer = new Timer();
	

	public Enhems() {
		setTitle("EnhemsApp");
		Utilities.setEnhemsIconToFrame(this);
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
		
		JPanel centerPanel = new JPanel(new GridLayout(1,0));
		GraphPanel graphPanel = new GraphPanel("Graf", dataModel);
		centerPanel.add(graphPanel);

		//put in system tray if possible
		if(SystemTray.isSupported()) {
			createSystemTray();
		} else {
			Utilities.showErrorDialog("Warning", "System tray not supported", this, null);
		}

		//refresh GUI for main interface
		getContentPane().removeAll();
		getContentPane().repaint();

		setLayout(new BorderLayout());
		JPanel upperPanel = new JPanel(new GridBagLayout());
		JPanel leftPanel = new JPanel(new GridBagLayout());
		
		ControlPanel controlPanel = new ControlPanel(
				"Kontrole", 10, BoxLayout.Y_AXIS, TitledBorder.CENTER, dataModel);
		MeasuredUnitPanel humidityPanel = new MeasuredUnitPanel(
				"Vlažnost", "res/icons/humidity.png", dataModel, GraphCodes.humidity);
		MeasuredUnitPanel co2Panel = new MeasuredUnitPanel(
				"CO2", "res/icons/co2.png", dataModel, GraphCodes.co2);
		MeasuredUnitPanel temperaturePanel = new MeasuredUnitPanel(
				"Temperatura", "res/icons/temperature.png", dataModel, GraphCodes.temperature);
		
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
							Utilities.class.getResource(pathToLoadingIcon));
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
		Utilities.putFrameInScreenCenter(this);
		
		
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

	private void createSystemTray() {
		TrayIcon trayIcon;
		SystemTray tray = SystemTray.getSystemTray();
		BufferedImage trayIconImage = null;
		try {
			trayIconImage = ImageIO.read(getClass().getResource(pathToLoadTrayIcon));
		} catch (IOException e) {
			e.printStackTrace();
		}
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


		addWindowStateListener(e -> {
			System.out.println(e.getNewState());
			if(e.getNewState()==ICONIFIED){
				toSystemTray(tray,trayIcon);
			}
		});

		toSystemTray(tray,trayIcon);

	}

	private void toSystemTray(SystemTray tray, TrayIcon icon) {
		try {
			if(!Arrays.asList(tray.getTrayIcons()).contains(icon)) {
				tray.add(icon);
			}
			dispose();
		} catch (AWTException ex) {
			Utilities.showErrorDialog("Error", "Program cannot be added to system tray!", this, ex);
		}
	}

	private void fromSystemTray() {
		setState(0);
		setVisible(true);
	}

}
