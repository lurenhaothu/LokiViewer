package LokiViewer.Object;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.util.Arrays;
import java.util.Comparator;

import LokiViewer.GUI.ViewerFrame;

/**
 * Vertex class
 * 
 * @author Renhao Lu
 *
 */
public class Vertex {
	/**
	 * origin coordinates from input file
	 */
	private double[] coord;
	/**
	 * coordinates after rescale but before rotation
	 */
	private double[] rescaleCoord;
	/**
	 * coordinate after rotation, which is to draw in the window
	 */
	private double[] visualCoord;
	/**
	 * id
	 */
	private int id;

	/**
	 * Constructor
	 * 
	 * @param id id
	 * @param x  x coordinate
	 * @param y  y coordinate
	 * @param z  z coordinate
	 */
	public Vertex(int id, double x, double y, double z) {
		this.id = id;
		coord = new double[3];
		coord[0] = x;
		coord[1] = y;
		coord[2] = z;
		rescaleCoord = new double[3];
		rescaleCoord[0] = x;
		rescaleCoord[1] = y;
		rescaleCoord[2] = z;
		visualCoord = new double[3];
		visualCoord[0] = x;
		visualCoord[1] = y;
		visualCoord[2] = z;
	}

	/**
	 * getID
	 * 
	 * @return id
	 */
	public int getID() {
		return id;
	}

	/**
	 * Get original coordinate
	 * 
	 * @return original coordinate
	 */
	public double[] getCoord() {
		return coord;
	}

	/**
	 * Get rescaled coordinate
	 * 
	 * @return coordinate after rescaling
	 */
	public double[] getRescaleCoord() {
		return rescaleCoord;
	}

	/**
	 * Get visual coordinate after rotation
	 * 
	 * @return visual coordinate after rotation
	 */
	public double[] getVisualCoord() {
		return visualCoord;
	}

	/**
	 * Apply zoom factor and shift to vertex
	 * 
	 * @param zoomAndShift zoom factor, x-shift, y-shift
	 */
	public void applyZoomAndShift(double[] zoomAndShift) {
		rescaleCoord[0] += zoomAndShift[1];
		rescaleCoord[1] += zoomAndShift[2];
		rescaleCoord[0] *= zoomAndShift[0];
		rescaleCoord[1] *= zoomAndShift[0];
		rescaleCoord[2] *= zoomAndShift[0];

		visualCoord[0] = rescaleCoord[0];
		visualCoord[1] = rescaleCoord[1];
		visualCoord[2] = rescaleCoord[2];
	}

	/**
	 * draw the vertex
	 * 
	 * @param g the Graphics context in which to paint
	 * @param d window dimension
	 */
	public void draw(Graphics g, Dimension d) {
		var g2 = (Graphics2D) g;

		var dot = new Ellipse2D.Double(visualCoord[0] + d.width / 2 - ViewerFrame.vertexDrawSize / 2,
				-visualCoord[1] + d.height / 2 - ViewerFrame.vertexDrawSize / 2, ViewerFrame.vertexDrawSize,
				ViewerFrame.vertexDrawSize);
		g2.setPaint(ViewerFrame.color);
		g2.fill(dot);
	}

	/**
	 * After rotation, update the rescale coordinates with visual coordinates
	 */
	public void updateRescaleCoord() {
		rescaleCoord[0] = visualCoord[0];
		rescaleCoord[1] = visualCoord[1];
		rescaleCoord[2] = visualCoord[2];
	}

	/**
	 * apply rotation matrix to vertex
	 * 
	 * @param matrix       rotation matrix
	 * @param rotateOrigin rotation origin
	 */
	public void rotate(RotationMatrix matrix, double[] rotateOrigin) {
		visualCoord = matrix.rotate(rescaleCoord, rotateOrigin);
	}

	@Override
	public String toString() {
		return Integer.toString(id) + ' ' + Arrays.toString(coord);
	}
}

/**
 * Sort vertex according to their id
 * 
 * @author Renhao Lu
 *
 */
class VertexComp implements Comparator<Vertex> {
	@Override
	public int compare(Vertex v1, Vertex v2) {
		return v1.getID() - v2.getID();
	}
}