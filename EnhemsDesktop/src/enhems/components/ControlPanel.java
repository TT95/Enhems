package enhems.components;

import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.math.RoundingMode;
import java.text.DecimalFormat;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JSpinner;
import javax.swing.JTextField;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import enhems.DataListener;
import enhems.EnhemsDataModel;
import enhems.ServerRequest;
import enhems.ServerService;
import enhems.utilities.CommonUtilities;

public class ControlPanel extends ElementsCustomPanel implements DataListener {

	private static final long serialVersionUID = 1L;
	
	private static Dimension changeDialogSize = new Dimension(350, 150);
	private JTextField setpointField;
	private JTextField fcspeedField;
	private JSpinner setPointSpinner;
	private JButton setValues;
	private String setPointToolTipText="Postavna vrijednost temperature za regulaciju";
	private String FCSpeedToolTipTeext="Brzina vrtnje ventilatora";
	private EnhemsDataModel dataModel;
	private final static int defaultSetpoint = 24;
	private final static int defaultFCspeed = 1;
	
	
	public ControlPanel(String title, int paddings, int axis,
			int titleJustification, EnhemsDataModel dataModel) {
		super(title, paddings, axis, titleJustification);
		this.dataModel=dataModel;
		dataModel.addListener(this);
		
		JPanel paramPanel = new JPanel(new GridBagLayout());
		JLabel setpointLabel = new JLabel("Temperatura ");
		JLabel fcvalveLabel = new JLabel("<html><div style=\"text-align: center;\">"
				+ "Brzina<br>ventilatora</html>");
		fcvalveLabel.setVerticalAlignment(SwingConstants.CENTER);
		setpointField = new JTextField(4);
		fcspeedField =  new JTextField(4);
		setpointLabel.setToolTipText(setPointToolTipText);
		setpointField.setToolTipText(setPointToolTipText);
		fcvalveLabel.setToolTipText(FCSpeedToolTipTeext);
		fcspeedField.setToolTipText(FCSpeedToolTipTeext);
		setValues = new JButton("Promijeni");
		setValues.setEnabled(false); // must be approved by room setting
		setpointLabel.setHorizontalAlignment(JLabel.CENTER);
		fcvalveLabel.setHorizontalAlignment(JLabel.CENTER);
		GridBagConstraints c = new GridBagConstraints();
		paramPanel.add(setpointLabel,c);
		paramPanel.add(setpointField,c);
		c.gridy=1;
		c.insets = new Insets(9, 0, 0, 0);
		paramPanel.add(fcvalveLabel,c);
		paramPanel.add(fcspeedField,c);

    	setValues.addActionListener((l)-> {
    		try {
    			showChangeDialog( getSetPointValue(),getFCSpeedValue()); 
    		} catch (Exception e) {
    			CommonUtilities.showDialog("Info", "Vrijednosti se postavljaju prvi puta: \n "
    					+ "predocene su uobicajene postavke koje trenutno ne vrijede \n"
    					+ "(setpoint: "+defaultSetpoint+"°C, brzina ventilatora: "+defaultFCspeed+" )",
						this, e, JOptionPane.INFORMATION_MESSAGE);
    			showChangeDialog(defaultSetpoint,defaultFCspeed); 
    		}
    		});
    	
		setpointField.setEnabled(false);
		setpointField.setText("--°C");
		setpointField.setHorizontalAlignment(JTextField.CENTER);
		fcspeedField.setEnabled(false);
		fcspeedField.setText("--");
		fcspeedField.setHorizontalAlignment(JTextField.CENTER);
		
		addComponentToPanel(paramPanel);
		addComponentToPanel(setValues);
		
	}
	
	private void showChangeDialog(int setpoint, int FCSpeed) {

		JPanel panel = new JPanel(new GridBagLayout());
		panel.setPreferredSize(changeDialogSize);
		JLabel setPointLabel = new JLabel("Postavljena temperatura: ");
		FCspeedPanel FCspeedPanel = new FCspeedPanel(FCSpeed);
		ImageIcon tempIcon = new ImageIcon(CommonUtilities.getImageByName("temperature25.png"));
		ImagePanel tempIconPanel = new ImagePanel(tempIcon.getImage());
		tempIconPanel.setPreferredSize(new Dimension(25, 25));
		
		SpinnerNumberModel model = new SpinnerNumberModel(setpoint, -10, 40, 1);
		setPointSpinner = new JSpinner(model);
		JComponent field = ((JSpinner.DefaultEditor) setPointSpinner.getEditor());
		Dimension prefSize = field.getPreferredSize();
		prefSize = new Dimension(20, prefSize.height);
		field.setPreferredSize(prefSize);
		setPointSpinner.setPreferredSize(
				new Dimension(45, setPointSpinner.getPreferredSize().height));
		GridBagConstraints c = new GridBagConstraints();
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.anchor = GridBagConstraints.LINE_END;
		panel.add(setPointLabel, c);
		c.weightx = 0.1;
		c.weighty = 0.1;
		c.fill = GridBagConstraints.HORIZONTAL;
		c.gridx = 1;
		c.anchor = GridBagConstraints.CENTER;
		panel.add(setPointSpinner, c);
		c.fill = GridBagConstraints.NONE;
		c.weightx = 1.0;
		c.weighty = 1.0;
		c.gridx = 2;
		c.anchor = GridBagConstraints.LINE_START;
		panel.add(tempIconPanel, c);
		c.fill = GridBagConstraints.BOTH;
		c.gridx = 0;
		c.gridy = 1;
		c.gridwidth = 3;
		panel.add(FCspeedPanel, c);

		int result = JOptionPane.showConfirmDialog(this, panel, 
				"Edit parameters",JOptionPane.OK_CANCEL_OPTION,
				JOptionPane.PLAIN_MESSAGE);
		if (result == JOptionPane.OK_OPTION) {
			ServerService.executeRequest(new ServerRequest() {
				private String setPointRes;
				private String FCspeedRes;
				public void execute() {
					setPointRes = ServerService.Setpoint(
							String.valueOf(setPointSpinner.getValue()),
							String.valueOf(setpoint),
							dataModel.getSelectedRoom());
					FCspeedRes = ServerService.FCspeed(
							String.valueOf(FCspeedPanel.getFCspeed()),
							String.valueOf(FCSpeed),
							dataModel.getSelectedRoom());
				}
				public void afterExecution() {
					
					if(setPointRes != null) {
						CommonUtilities.showDialog("Greška",
								"Greška tijekom postavljanja kontrolnih podataka"
								+ " (Postavljena temperatura)", null, null,
								JOptionPane.ERROR_MESSAGE);
						setpointField.setText(setPointRes);
					} else {
						//TU RECIMO MOZES ZATRAZIT REFRESH SVEGA
						DecimalFormat df = new DecimalFormat("##.##");
						df.setRoundingMode(RoundingMode.CEILING);
//						setpointField.setText(
//								df.format((Integer)setPointSpinner.getValue())  + "°C");
						dataModel.setSetPoint(df.format((Integer)setPointSpinner.getValue())+ "°C");
					}
					
					if(FCspeedRes != null) {
						CommonUtilities.showDialog("Greška",
								"Greška tijekom postavljanja kontrolnih podataka"
								+ " (Brzina ventilatora)", null, null,
								JOptionPane.ERROR_MESSAGE);
						fcspeedField.setText(FCspeedRes);
					} else {
						//TU RECIMO MOZES ZATRAZIT REFRESH SVEGA
//						fcspeedField.setText(
//								String.valueOf(FCspeedPanel.getFCspeed()));
						dataModel.setFCspeed(String.valueOf(FCspeedPanel.getFCspeed()));
					}
				}
			});
		}
	}
	
	public void controlEnabled() {
		setValues.setEnabled(true);
	}
	
	public void controlDisabled() {
		setValues.setEnabled(false);
	}
	
	public Integer getSetPointValue() throws Exception {
		String text = setpointField.getText();
		return Integer.parseInt(text.substring(0, text.length()-2));
	}

	public Integer getFCSpeedValue() throws Exception {
		String text = fcspeedField.getText();
		return Integer.parseInt(text);
	}

	@Override
	public void dataChanged() {
		fcspeedField.setText(dataModel.getFCspeed());
		setpointField.setText(dataModel.getSetPoint());
		setValues.setEnabled(dataModel.isOpMode());
	}
}
