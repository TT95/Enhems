package enhems.components;

import java.awt.Component;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.border.TitledBorder;

/**
 * Panel which groups components under titled border in way
 * specific for this application. Components are added through
 * createPanel() method.
 *
 */

public class ElementsCustomPanel extends JPanel{
	
	private static final long serialVersionUID = 1L;
	private String title;
	private int paddings;
	private int axis;
	private int titleJustification;
	
	public ElementsCustomPanel(String title, int paddings) {
		super();
		this.title = title;
		this.paddings = paddings;
		this.axis = BoxLayout.Y_AXIS;
		this.titleJustification = TitledBorder.LEFT;
		createPanel();
	}
	
	public ElementsCustomPanel(String title, int paddings, int axis) {
		super();
		this.title = title;
		this.paddings = paddings;
		this.axis = axis;
		this.titleJustification = TitledBorder.LEFT;
		createPanel();
	}
	
	public ElementsCustomPanel(String title, int paddings, int axis, int titleJustification) {
		super();
		this.title = title;
		this.paddings = paddings;
		this.axis = axis;
		this.titleJustification = titleJustification;
		createPanel();
	}
	

	private void createPanel() {
		setLayout(new BoxLayout(this, axis));
		TitledBorder border = BorderFactory.createTitledBorder(title);
		border.setTitleJustification(titleJustification);
		setBorder(border);
		addPadding();
	}

	public void addComponentToPanel(JComponent c) {
		c.setAlignmentX(Component.CENTER_ALIGNMENT);
		add(c);
		addPadding();
	}
	
	public void removeComponentFromPanel(Component c) {
		remove(c);
	}
	
	private void addPadding() {
		if(axis == BoxLayout.X_AXIS) {
			add(Box.createHorizontalStrut(paddings));
		} else {
			add(Box.createVerticalStrut(paddings));
		}
	}
	
}
