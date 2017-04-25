package eg.edu.alexu.csd.control.sfg;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.geom.Path2D;

public class Edge {

	private Nodes from, to;
	private double gain; 
	private Point startPoint, midPoint, endPoint;
	private Path2D.Double edge;
	private Path2D.Double arrow;
	private boolean isLoop;
	
	public Edge (Nodes from, Nodes to, double gain) {
		this.from = from;
		this.to = to;
		this.gain = gain;
		this.isLoop = false;
	}
	
	public Point getStart() {
		return startPoint;
	}
	
	public Point getEnd() {
		return endPoint;
	}
	
	public Point getMid() {
		return midPoint;
	}
	
	public Nodes getFrom() {
		return from;
	}
	
	public Nodes getTo() {
		return to;
	}
	
	public double getGain() {
		return gain;
	}
	
	public boolean isLoop () {
		return isLoop;
	}
	
	public void setStart (Point num) {
		startPoint = num;
	}
	
	public void setEnd (Point num) {
		endPoint = num;
	}
	
	public void setMid (Point num) {
		midPoint = num;
	}
	
	public void makeArc(String gain, Edge edge) {
		Rectangle container1 = from.getShape().getBounds();
		Rectangle container2 = to.getShape().getBounds();
		Path2D.Double path = new Path2D.Double();
		Path2D.Double arrow = new Path2D.Double();
		Point center1 = new Point();
		center1.x = container1.width/2+container1.x;
		center1.y = container1.height/2+container1.y;
		Point center2 = new Point();
		center2.x = container2.width/2+container2.x;
		center2.y = container2.height/2+container2.y;
		int x1, y1;
		if (from.getName().equals(to.getName())) {
			this.isLoop = true;
			edge.setStart(new Point(center1.x,center1.y));
			edge.setEnd(new Point(center1.x+2*30, center1.y+3*30));
			edge.setMid(new Point(center1.x+2*30, center1.y-3*30));
			path.moveTo(center1.x,center1.y);
			path.curveTo(center1.x+2*30, center1.y+3*30, center1.x+2*30, center1.y-3*30, center2.x,center2.y);
			double x = 0.125*startPoint.x+0.375*midPoint.x+0.375*endPoint.x+0.125*startPoint.x;
			double y = 0.125*startPoint.y+0.375*midPoint.y+0.375*endPoint.y+0.125*startPoint.y;
			arrow.moveTo(x+6, y-10);
			arrow.lineTo(x, y);
			arrow.lineTo(x-6, y-10);
		}
		else {
			x1 = (container2.x+container1.x)/2;
			y1 = -(container2.x-container1.x)/2+Math.max(container1.y, container2.y);
			if (container1.x < container2.x) {
				edge.setStart(new Point(center1.x,center1.y));
				edge.setEnd(new Point(center2.x, center2.y));
				edge.setMid(new Point(x1, y1));
				double x = 0.25*startPoint.x+0.5*midPoint.x+0.25*endPoint.x;
				double y = 0.25*startPoint.y+0.5*midPoint.y+0.25*endPoint.y;
				path.moveTo(center1.x,center1.y);
				path.quadTo(x1, y1, center2.x, center2.y);
				arrow.moveTo(x-10, y+6);
				arrow.lineTo(x, y);
				arrow.lineTo(x-10, y-6);
			}
			else {
				edge.setStart(new Point(center2.x,center2.y));
				edge.setEnd(new Point(center1.x, center1.y));
				edge.setMid(new Point(x1, y1));
				double x = 0.25*startPoint.x+0.5*midPoint.x+0.25*endPoint.x;
				double y = 0.25*startPoint.y+0.5*midPoint.y+0.25*endPoint.y;
				path.moveTo(center2.x,center2.y);
				path.quadTo(x1, y1, center1.x, center1.y);
				arrow.moveTo(x+10, y+6);
				arrow.lineTo(x, y);
				arrow.lineTo(x+10, y-6);
			}
		}
		this.edge = path;
		this.arrow = arrow;
	}
	
	public Path2D.Double getShape() {
		return edge;
	}
	
	public Path2D.Double getArrow() {
		return arrow;
	}
}
