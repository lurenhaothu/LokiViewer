package LokiViewer.Object;

import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.Arrays;

import LokiViewer.GUI.ViewerFrame;

/**
 * The edge class of the object
 * 
 * @author Renhao Lu
 *
 */
public class Edge {
	/**
	 * Vertex array storing two vertices
	 */
	private Vertex[] v;

	/**
	 * 
	 * @param v1 one vertex
	 * @param v2 the other vertex
	 */
	public Edge(Vertex v1, Vertex v2) {
		v = new Vertex[2];
		v[0] = v1;
		v[1] = v2;
	}

	/**
	 * Get vertex array
	 * 
	 * @return tow vertex array
	 */
	public Vertex[] getVertices() {
		return v;
	}

	/**
	 * draw the edge
	 * 
	 * @param g the Graphics context in which to paint
	 * @param d window dimension
	 */
	public void draw(Graphics g, Dimension d) {
		var g2 = (Graphics2D) g;

		g2.setStroke(new BasicStroke(ViewerFrame.edgeDrawSize));

		var line = new Line2D.Double(v[0].getVisualCoord()[0] + d.width / 2, -v[0].getVisualCoord()[1] + d.height / 2,
				v[1].getVisualCoord()[0] + d.width / 2, -v[1].getVisualCoord()[1] + d.height / 2);
		g2.setColor(ViewerFrame.color);
		g2.draw(line);
	}

	@Override
	public String toString() {
		return Arrays.toString(v);
	}
}
