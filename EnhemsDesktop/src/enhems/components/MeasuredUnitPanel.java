package enhems.components;

import java.awt.BorderLayout;
import java.awt.GridBagLayout;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import enhems.DataListener;
import enhems.EnhemsDataModel;
import enhems.GraphCodes;
import enhems.utilities.CommonUtilities;

public class MeasuredUnitPanel extends JPanel implements DataListener{

	private static final long serialVersionUID = 1L;
	private static final String pathToArrowIcon = "../res/icons/arrow.png";
	private static List<MeasuredUnitPanel> panelsList = new ArrayList<>();
	private ImageIcon arrowIcon;
	private JButton button;
	private JLabel arrowLabel;
	private String value;
	private boolean selected;
	private EnhemsDataModel dataModel;
	private String graphCode;
	

	/**
	 * 
	 * @param title
	 * @param pathToIconRes
	 * @param dataModel
	 * @param graphCode code from class {@link GraphCodes}
	 */
	public MeasuredUnitPanel(String title, String nameOfIcon,
			EnhemsDataModel dataModel, String graphCode) {
		
		this.dataModel=dataModel;
		this.graphCode=graphCode;
		dataModel.addListener(this);
		
		arrowIcon = new ImageIcon(CommonUtilities.getImageByName("arrow.png"));
		selected = false;
		button = new JButton("---",new ImageIcon(CommonUtilities.getImageByName(nameOfIcon)));
		arrowLabel = new JLabel();
		JPanel arrowPanel = new JPanel(new GridBagLayout());
		JPanel iconPanel = new JPanel(new GridBagLayout());
		iconPanel.setBorder(BorderFactory.createTitledBorder(title));
		iconPanel.add(button);
		arrowPanel.add(arrowLabel);
		setLayout(new BorderLayout());
		add(iconPanel, BorderLayout.CENTER);
		add(arrowPanel, BorderLayout.LINE_END);
		panelsList.add(this);
		button.setFocusable(false);
		button.setVerticalTextPosition   ( SwingConstants.BOTTOM ) ;
		button.setHorizontalTextPosition ( SwingConstants.CENTER ) ;
		button.addActionListener((l)-> {
			dataModel.setCurrentMeassUnit(graphCode);
			selectOnlyThis();
		});
		selectOnlyThis();
	}
	
	public static String getCurrentSelectedValue() {
		for(MeasuredUnitPanel panel : panelsList) {
			if(!panel.getButton().isEnabled()) {
				return panel.getValue();
			}
		}
		return "none_selected_error";
	}
	
	public void select() {
		selected = true;
		arrowLabel.setIcon(arrowIcon);
		button.setEnabled(false);
	}
	
	public void deselect() {
		selected = false;
		arrowLabel.setIcon(null);
		button.setEnabled(true);
	}
	
	private void selectOnlyThis() {
		for(MeasuredUnitPanel panel : panelsList) {
			if(!selected) {
				panel.deselect();
			}
		}
		select();
	}

	public String getValue() {
		return value;
	}

	
	public void setValue(String value) {
		this.value = value;
		button.setText(value);
	}

	public JButton getButton() {
		return button;
	}

	@Override
	public void dataChanged() {
		String value="";
		switch (graphCode) {
		case GraphCodes.temperature:
			value=dataModel.getTemperature();
			break;
		case GraphCodes.humidity:
			value=dataModel.getHumidity();
			break;
		case GraphCodes.co2:
			value=dataModel.getCo2();
			break;
		default:
			break;
		}
		button.setText(value);
	}
	
}
