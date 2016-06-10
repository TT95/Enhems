package enhems.components;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import enhems.DataListener;
import enhems.EnhemsDataModel;
import enhems.GraphCodes;
import enhems.Utilities;

public class GraphPanel extends JPanel implements DataListener{

	private static final long serialVersionUID = 1L;
	private static final int width = 640;
	private static final int height = 360;
	private static String currentValuePrefix = "Trenutna vrijednost = ";
	private static String currentDatePrefix = "Datum zadnjeg mjerenja: ";
	private JLabel currentMeass;
	private ImagePanel imagePanel;
	private JPanel graph;
	private JButton day;
	private JButton week;
	private JLabel dateLabel;
	private EnhemsDataModel dataModel;
	
	
	public GraphPanel(String meassTxt, EnhemsDataModel dataModel) {

		this.dataModel=dataModel;
		dataModel.addListener(this);
		setLayout(new BorderLayout());
		setBorder(BorderFactory.createTitledBorder(meassTxt));
		try {
			imagePanel = new ImagePanel(ImageIO.read(
					Utilities.class.getResource("res/icons/enhemsBig.png")));
		} catch (IOException e) {
			e.printStackTrace();
		}
		imagePanel.setBorder(BorderFactory.createEtchedBorder());
		graph = new JPanel(new GridBagLayout());
		ElementsCustomPanel intervalPanel = 
				new ElementsCustomPanel("Vremenski interval", 10, BoxLayout.X_AXIS);
		day = new JButton("24 sata");
		week = new JButton("tjedan");
		JPanel upperPanel = new JPanel(new GridBagLayout());
		currentMeass = new JLabel(currentValuePrefix+"---");
		dateLabel = new JLabel();
		refreshDate(); //setting date
		upperPanel.setBorder(BorderFactory.createEmptyBorder());
		intervalPanel.addComponentToPanel(day);
		intervalPanel.addComponentToPanel(week);
		graph.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
		graph.setPreferredSize(new Dimension(width, height));
		graph.setLayout(new BorderLayout());
		graph.add(imagePanel, BorderLayout.CENTER);
		graph.setBorder(BorderFactory.createLineBorder(Color.BLACK));
		currentMeass.setBorder(BorderFactory.createEmptyBorder(7,7,7,7));
		GridBagConstraints c = new GridBagConstraints();
		c.anchor = GridBagConstraints.CENTER;
		c.weightx = 1.0;
		upperPanel.add(currentMeass,c);
		upperPanel.add(dateLabel);
		upperPanel.add(intervalPanel,c);
		add(upperPanel, BorderLayout.PAGE_START);
		add(graph, BorderLayout.CENTER);
		addTimePeriodAction();
		
		//simulating click
		dataModel.setCurrentMeassPeriod(GraphCodes.dayInterval);
		day.setEnabled(false);
		week.setEnabled(true);
	}
	
	public void addTimePeriodAction() {
		
		day.addActionListener((l)-> {
			dataModel.setCurrentMeassPeriod(GraphCodes.dayInterval);
			day.setEnabled(false);
			week.setEnabled(true);
		});
		
		week.addActionListener((l)-> {
			dataModel.setCurrentMeassPeriod(GraphCodes.weekInterval);
			week.setEnabled(false);
			day.setEnabled(true);
		});
	}
	
	public void setCurrentValue(String text) {
		currentMeass.setText(currentValuePrefix+text);
	}
	
	
	private void setImagePanel(ImagePanel imagePanel) {
		graph.remove(this.imagePanel);
		this.imagePanel=imagePanel;
		imagePanel.setBorder(BorderFactory.createEtchedBorder());
		graph.add(imagePanel, BorderLayout.CENTER);
		repaint();
		revalidate();
	}
	
	public void refreshDate() {
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		dateLabel = new JLabel(currentDatePrefix+ dateFormat.format(new Date()));
	}

	@Override
	public void dataChanged() {
		setImagePanel(dataModel.getSelectedGraph());
		currentMeass.setText(currentValuePrefix+dataModel.getCurrentMeass());
		refreshDate();
	}
	
}
