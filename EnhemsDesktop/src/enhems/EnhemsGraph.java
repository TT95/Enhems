package enhems;

import java.awt.Image;

import enhems.components.ImagePanel;

public class EnhemsGraph {
	private String name;
	private ImagePanel imagePanel;
	public EnhemsGraph(String name, Image image) {
		super();
		this.name = name;
		this.imagePanel = new ImagePanel(image);
	}
	
	public ImagePanel getImagePanel() {
		return imagePanel;
	}

	public void setImagePanel(ImagePanel imagePanel) {
		this.imagePanel = imagePanel;
	}



	public String getName() {
		return name;
	}
}