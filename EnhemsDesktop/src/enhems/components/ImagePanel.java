package enhems.components;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

/**
 * Panel used for showing image
 * @author Teo Toplak
 *
 */
public class ImagePanel extends JPanel {

	private static final long serialVersionUID = 1L;

	
	private Image image;
	public ImagePanel(String pathToImage) {
		super();
		try {                
			image = ImageIO.read(new File(pathToImage));
	       } catch (IOException ex) {
	            ex.printStackTrace();
	       }
	}
	
	public ImagePanel(Image image) {
		this.image = image;
	}
	
	
	public Image getImage() {
		return image;
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;
        Map<RenderingHints.Key, Object> map = new HashMap<RenderingHints.Key, Object>();
        map.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        map.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        map.put(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        map.put(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        map.put(RenderingHints.KEY_STROKE_CONTROL,RenderingHints.VALUE_STROKE_PURE);
        
        RenderingHints renderHints = new RenderingHints(map);
        g2d.setRenderingHints(renderHints);
		if(image!=null) {
			g2d.drawImage(image, 0, 0, getWidth(), getHeight(), this);
		}
	}

	public void setImage(Image image) {
		this.image = image;
		repaint();
		revalidate();
	}
}
