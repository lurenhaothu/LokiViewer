package LokiViewer.Object;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.util.Arrays;

/**
 * The face class of object
 * 
 * @author Renhao Lu
 *
 */
public class Face {
	/**
	 * A vertex array storing three vertex on face
	 */
	private Vertex[] v;

	/**
	 * Store the vertices in array, sort them according to their id
	 * 
	 * @param v1 vertex1
	 * @param v2 vertex2
	 * @param v3 vertex3
	 */
	public Face(Vertex v1, Vertex v2, Vertex v3) {
		v = new Vertex[3];
		v[0] = v1;
		v[1] = v2;
		v[2] = v3;
		Arrays.sort(v, new VertexComp());
	}

	/**
	 * Get vertices
	 * 
	 * @return vertices array
	 */
	public Vertex[] getVertices() {
		return v;
	}

	/**
	 * draw the face
	 * 
	 * @param g the Graphics context in which to paint
	 * @param d window dimension
	 */
	public void draw(Graphics g, Dimension d) {
		var g2 = (Graphics2D) g;
		g2.setColor(getColor());
		g2.fill(getPolygon(d));
	}

	/**
	 * Return the average z valve of three vertices, used for naive z sorting
	 * 
	 * @return average z valve
	 */
	public double getAverageZVale() {
		return (v[0].getVisualCoord()[2] + v[1].getVisualCoord()[2] + v[2].getVisualCoord()[2]) / 3;
	}

	/**
	 * Java swing cannot draw triangle directly, hence generate a polygon object
	 * that can be drawn in windows
	 * 
	 * @param d window dimension
	 * @return the polygon representing the face
	 */
	private Polygon getPolygon(Dimension d) {
		return new Polygon(
				new int[] { (int) Math.round(v[0].getVisualCoord()[0] + d.width / 2),
						(int) Math.round(v[1].getVisualCoord()[0] + d.width / 2),
						(int) Math.round(v[2].getVisualCoord()[0] + d.width / 2) },
				new int[] { (int) Math.round(-v[0].getVisualCoord()[1] + d.height / 2),
						(int) Math.round(-v[1].getVisualCoord()[1] + d.height / 2),
						(int) Math.round(-v[2].getVisualCoord()[1] + d.height / 2) },
				3);
	}

	/**
	 * Get the color according the face's angle to z-axis
	 * 
	 * @return face color
	 */
	public Color getColor() {
		double angle = angleWithZAxis();
		// return Color.green;
		return new Color(0, 0, (int) (95 + angle / Math.PI * 160 * 2));
	}

	/**
	 * Get the angle of the face with z-axis, calculated by complementary angle of
	 * the angle between z-axis and the face normal vector.
	 * 
	 * @return the angle of the face with z-axis
	 */
	private double angleWithZAxis() {
		double[] normal = getNormalVector();
		double innerProduct = Math.abs(normal[2]);
		double len = Math.sqrt(normal[0] * normal[0] + normal[1] * normal[1] + normal[2] * normal[2]);
		double cos = innerProduct / len;
		return Math.asin(cos);
	}

	/**
	 * Get the normal vector of the face, calculated by finding a vector that is
	 * perpendicular to two edges on the face
	 * 
	 * @return face normal vector
	 */
	private double[] getNormalVector() {
		double[] normal = new double[3];
		double[] edge1 = new double[] { v[1].getVisualCoord()[0] - v[0].getVisualCoord()[0],
				v[1].getVisualCoord()[1] - v[0].getVisualCoord()[1],
				v[1].getVisualCoord()[2] - v[0].getVisualCoord()[2] };
		double[] edge2 = new double[] { v[2].getVisualCoord()[0] - v[0].getVisualCoord()[0],
				v[2].getVisualCoord()[1] - v[0].getVisualCoord()[1],
				v[2].getVisualCoord()[2] - v[0].getVisualCoord()[2] };
		normal[0] = (edge1[1] * edge2[2] - edge2[1] * edge1[2]);
		normal[1] = -(edge1[0] * edge2[2] - edge2[0] * edge1[2]);
		normal[2] = (edge1[0] * edge2[1] - edge2[0] * edge1[1]);
		return normal;
	}

	/**
	 * Return the border value of the face, in order of: x+, y+, x-, y-, z+, z-
	 * 
	 * @return border values
	 */
	public double[] getBorder() {
		double[] res = new double[] { Double.NEGATIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY,
				Double.POSITIVE_INFINITY, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY };
		for (int i = 0; i < 3; i++) {
			res[0] = Double.max(res[0], v[i].getVisualCoord()[0]);
			res[1] = Double.max(res[1], v[i].getVisualCoord()[1]);
			res[2] = Double.min(res[2], v[i].getVisualCoord()[0]);
			res[3] = Double.min(res[3], v[i].getVisualCoord()[1]);
			res[4] = Double.max(res[4], v[i].getVisualCoord()[2]);
			res[5] = Double.min(res[5], v[i].getVisualCoord()[2]);
		}
		return res;
	}

	@Override
	public String toString() {
		return Arrays.deepToString(v);
	}
}
