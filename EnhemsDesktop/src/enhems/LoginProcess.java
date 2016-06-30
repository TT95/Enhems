package enhems;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.AbstractAction;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.border.Border;

import enhems.components.ElementsCustomPanel;
import enhems.components.ImagePanel;

public class LoginProcess {

	private static final String pathToLoadingIcon = "res/icons/loading.gif";
	private static JTextField usernameField;
	private static JPasswordField passwordField;
	private static JButton button;
	private static AbstractAction actionAfterLogin;
	private static JLabel error;
	private static JFrame loadingFrame;
	private static ElementsCustomPanel loginPanel;
	
	public static void tokenLogin(Enhems enhems, AbstractAction actionAfterLogin) {
		LoginProcess.actionAfterLogin = actionAfterLogin;
		if(Token.isEmpty()) {
			enhems.panelLogin();
		} else {
			createLoadingFrame();
			ServerService.executeRequest(new ServerRequest() {
				private int response = -1;
				public void execute() {
						response = ServerService.Login(null);
				}
				public void afterExecution() {
					removeLoadingFrame();
					if(response == 200) {
						//must be changed, when token is activated user is unknown
						actionAfterLogin.actionPerformed(null);
					} else {
						enhems.panelLogin();
					}
				}
			});
		}
	}
	
	private static void removeLoadingFrame() {
		loadingFrame.dispose();
	}


	private static void createLoadingFrame() {
		loadingFrame = new JFrame();
		loadingFrame.dispose();
		loadingFrame.setUndecorated(true);
		loadingFrame.setVisible(true);
		ImageIcon enhemsIcon = new ImageIcon(
				Utilities.class.getResource("res/icons/enhemsGIF.gif"));
		ImagePanel enhemsPanel = new ImagePanel(enhemsIcon.getImage());
		enhemsPanel.setPreferredSize(new Dimension(400, 220));
		loadingFrame.setLayout(new GridBagLayout());
		loadingFrame.add(enhemsPanel, new GridBagConstraints());
		loadingFrame.pack();
		Utilities.putFrameInScreenCenter(loadingFrame);

	}


	public static void createLoginGUI(JFrame appFrame) {

		loginPanel = new ElementsCustomPanel("Log in", 10);
		JPanel usernamePanel = new JPanel(new GridLayout(1, 0));
		JPanel passwordPanel = new JPanel(new GridLayout(1, 0));
		JPanel buttonPanel = new JPanel();
		button = new JButton("Prijava");
		usernameField = new JTextField(12);
		passwordField = new JPasswordField();
		
		loginPanel.addComponentToPanel(usernamePanel);
		loginPanel.addComponentToPanel(passwordPanel);
		loginPanel.addComponentToPanel(buttonPanel);

		Border titledBorder = BorderFactory.createTitledBorder("Prijava");
		Border emptyBorder = BorderFactory.createEmptyBorder(15, 15, 15, 15);
		loginPanel.setBorder(BorderFactory.createCompoundBorder(titledBorder, emptyBorder));
		usernamePanel.add(new JLabel("Korisničko ime:"));
		usernamePanel.add(usernameField);
		passwordPanel.add(new JLabel("Lozinka:"));
		passwordPanel.add(passwordField);
		buttonPanel.add(button);
		appFrame.getContentPane().removeAll();
		appFrame.revalidate();
		appFrame.setVisible(true);
		appFrame.getContentPane().setLayout(new GridBagLayout());
		appFrame.getContentPane().add(loginPanel, new GridBagConstraints());
		appFrame.pack();
		appFrame.setSize(appFrame.getPreferredSize().width*2,appFrame.getPreferredSize().height*2);
		Utilities.putFrameInScreenCenter(appFrame);
		
		button.addActionListener((l)-> {
			removeErrorLabel();
			showLoadingIcon();
			//DISABLE BUTTON?
			ServerService.executeRequest(new ServerRequest() {
				private int response;
				public void execute() {
					response = ServerService.Login(new String[]
							{usernameField.getText(),new String(passwordField.getPassword())});
				}
				public void afterExecution() {
					switch (response) {
					case 200:
						//login success
						actionAfterLogin.actionPerformed(null);
						break;
					case 400:
						//bad credencials
						showErrorLabel("Pogrešno korisničko ime ili lozinka!");
						break;
					case 403:
						//invalid token or expired
						removeLoadingIcon();
						break;						
					case -1:
						//invalid token or expired
						showErrorLabel("Nepoznata pogreška!");
						break;
					default:
						//timeout or other error
						showErrorLabel("Pogreška sa poslužiteljske strane! (Error "+response+")");
						break;
					}
				}
			});

		});
		
		passwordField.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				if(e.getKeyCode() == KeyEvent.VK_ENTER) {
					button.doClick();
				}
			}
		});

	}
	
	private static void showLoadingIcon() {
		ImageIcon loadingIcon = new ImageIcon(
				Utilities.class.getResource(pathToLoadingIcon));
		button.setHorizontalTextPosition(JButton.LEFT);
		button.setIcon(loadingIcon);
		button.setEnabled(false);
	}
	
	private static void removeLoadingIcon() {
		button.setIcon(null);
		button.setEnabled(true);
	}
	
	private static void showErrorLabel(String text) {
		removeLoadingIcon();
		error = new JLabel(text);
		error.setForeground(Color.RED);
		error.setAlignmentX(Component.CENTER_ALIGNMENT);
		loginPanel.add(error);
	}
	
	private static void removeErrorLabel() {
		if(error==null) {
			return;
		}
		loginPanel.remove(error);
		error = null;
	}
	
}
