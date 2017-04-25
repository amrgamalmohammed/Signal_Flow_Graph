package eg.edu.alexu.csd.control.sfg;

import java.awt.Point;
import java.awt.geom.Ellipse2D;

public class Nodes {
	
	private String name;
	private int index;
	private Ellipse2D.Float ellipse;
	private Point center;
	
	public Nodes (int number) {
		this.index = number-1;
		this.name = "X"+number;
	}
	
	public void makeEllipse(int x1, int y1, int x2, int y2) {
        int x = Math.min(x1, x2);
        int y = Math.min(y1, y2);
        int width = Math.abs(x1 - x2);
        int height = Math.abs(y1 - y2);
        center = new Point(x1+width/2, y1+height/2);
        this.ellipse = new Ellipse2D.Float(x, y, width, height);
    }
	
	public Ellipse2D.Float getShape () {
		return ellipse;
	}
	
	public String getName () {
		return name;
	}
	
	public Point getCenter() {
		return center;
	}
	
	public int getIndex() {
		return index;
	}

}
