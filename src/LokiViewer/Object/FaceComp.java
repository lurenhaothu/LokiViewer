package LokiViewer.Object;

import java.util.Comparator;

/**
 * Compare two faces' rendering order, based on whether they are overlapping
 * 
 * @author Renhao Lu
 *
 */
public class FaceComp implements Comparator<Face> {

	/**
	 * Return if two edges have intersection on x-y plane, and if they have, which
	 * edge is nearer to the observer on the intersection point
	 * 
	 * Algorithm: <br>
	 * Assume intersection is v<br>
	 * Assume v = v1 + alpha * (v2 - v1), 0 < alpha < 1 (1) <br>
	 * Assume v = v3 + beta * (v4 - v3), 0 < beta < 1 (2) <br>
	 * Hence, (1) - (2): (v2 - v1) * alpha + (v3 - v4) * beta + (v1 - v3) = 0<br>
	 * Which is: <br>
	 * (x2 - x1) * alpha + (x3 - x4) * beta + (x1 - x3) = 0<br>
	 * (y2 - y1) * alpha + (y3 - y4) * beta + (y1 - y3) = 0<br>
	 * I simplified the above equation to: a1 * alpha + b1* beta + c1 = 0<br>
	 * a2 * alpha + b2 * beta + c3 = 0<br>
	 * And further solved it
	 * 
	 * @param v1 v1
	 * @param v2 v2
	 * @param v3 v3
	 * @param v4 v4
	 * @return
	 */
	private int lineIntersection(Vertex v1, Vertex v2, Vertex v3, Vertex v4) {
		double a1 = v2.getVisualCoord()[0] - v1.getVisualCoord()[0];
		double b1 = v3.getVisualCoord()[0] - v4.getVisualCoord()[0];
		double c1 = v1.getVisualCoord()[0] - v3.getVisualCoord()[0];
		double a2 = v2.getVisualCoord()[1] - v1.getVisualCoord()[1];
		double b2 = v3.getVisualCoord()[1] - v4.getVisualCoord()[1];
		double c2 = v1.getVisualCoord()[1] - v3.getVisualCoord()[1];
		double dom = a1 * b2 - a2 * b1;
		if (dom == 0)
			return 0;
		double alpha = (b1 * c2 - b2 * c1) / dom;
		double beta = (a2 * c1 - a1 * c2) / dom;
		if (alpha >= 1 || alpha <= 0 || beta >= 1 || beta <= 0) {
			return 0;
		}
		double z1 = v1.getVisualCoord()[2] + alpha * (v2.getVisualCoord()[2] - v1.getVisualCoord()[2]);
		double z2 = v3.getVisualCoord()[2] + beta * (v4.getVisualCoord()[2] - v3.getVisualCoord()[2]);

		if (z1 < z2 - 1e-5) {
			return -1;
		} else if (z1 > z2 + 1e-5) {
			return 1;
		} else {
			return 0;
		}
	}

	/**
	 * If any two edges in two faces has intersection in x-y plain and has a Z value
	 * difference, then the deeper face should be rendered earlier
	 * 
	 * @param f1 face1
	 * @param f2 face2
	 * @return
	 */
	private int intersection(Face f1, Face f2) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 3; j++) {
				var v1 = f1.getVertices()[i];
				var v2 = f1.getVertices()[(i + 1) % 3];
				var v3 = f2.getVertices()[j];
				var v4 = f2.getVertices()[(j + 1) % 3];
				int l = lineIntersection(v1, v2, v3, v4);
				if (l != 0) {
					return l;
				}
			}
		}
		return 0;
	}

	/**
	 * Get a vector v2 - v1
	 * 
	 * @param v1 start point of vector
	 * @param v2 end point of vector
	 * @return double array showing the vector
	 */
	private double[] getVector(Vertex v1, Vertex v2) {
		return new double[] { v2.getVisualCoord()[0] - v1.getVisualCoord()[0],
				v2.getVisualCoord()[1] - v1.getVisualCoord()[1] };
	}

	/**
	 * Get the normal vector of a vector
	 * 
	 * @param vector vector
	 * @return normal vector
	 */
	private double[] getNormelVector(double[] vector) {
		return new double[] { vector[1], -vector[0] };
	}

	/**
	 * Get inner product of two vectors
	 * 
	 * @param vector1
	 * @param vector2
	 * @return inner product
	 */
	private double getInnerProduct(double[] vector1, double[] vector2) {
		return vector1[0] * vector2[0] + vector1[1] * vector2[1];
	}

	/**
	 * Return if a vertex is in the face in xy projection<br>
	 * Algorithm: Assume the vertex is v. If for each face edge (two vertices on
	 * edge), v and the third vertex is on the same side of the edge, then the v is
	 * inside the face.
	 * 
	 * @param v
	 * @param face
	 * @return if a vertex is in the face in xy projection
	 */
	private boolean pointInTriangle(Vertex v, Face face) {
		for (int index1 = 0; index1 < 3; index1++) {
			int index2 = (index1 + 1) % 3;
			int index3 = (index1 + 2) % 3;
			var vec = getVector(face.getVertices()[index1], face.getVertices()[index2]);
			var norm = getNormelVector(vec);
			var vec1 = getVector(face.getVertices()[index1], face.getVertices()[index3]);
			var vec2 = getVector(face.getVertices()[index1], v);
			if (getInnerProduct(norm, vec1) * getInnerProduct(norm, vec2) <= 0) {
				return false;
			}
		}
		return true;
	}

	/**
	 * If v is in a face's x-y projection, return the z valve of v's projection in
	 * face.<br>
	 * Algorithm:<bt> Assume the projection is v', and the three vertices on face is
	 * v1, v2, and v3. <br>
	 * Assume vec1 = v2 - v1, vec2 = v3 - v1, vec3 = v' - v1<br>
	 * Then there exist real number a and b that:<br>
	 * vec3 = a * vec1 + b * vec2<br>
	 * We know that v' has same x and y coordinates with vertex v, hence solving the
	 * equation give us the z value of v'
	 * 
	 * @param v
	 * @param face
	 * @return z valve of v's projection in face
	 */
	private double getZValue(Vertex v, Face face) {
		var vec1 = getVector(face.getVertices()[0], face.getVertices()[1]);
		var vec2 = getVector(face.getVertices()[0], face.getVertices()[2]);
		var vec3 = getVector(face.getVertices()[0], v);
		double denominator = vec1[0] * vec2[1] - vec1[1] * vec2[0];
		if (denominator == 0) {
			return Double.NaN;
		}
		double a = (vec3[0] * vec2[1] - vec2[0] * vec3[1]) / denominator;
		double b = (vec1[0] * vec3[1] - vec3[0] * vec1[1]) / denominator;
		return face.getVertices()[0].getVisualCoord()[2]
				+ a * (face.getVertices()[1].getVisualCoord()[2] - face.getVertices()[0].getVisualCoord()[2])
				+ b * (face.getVertices()[2].getVisualCoord()[2] - face.getVertices()[0].getVisualCoord()[2]);
	}

	/**
	 * Run after knowing that f1 and f2 has no intersecting edges in x-y plain.Then
	 * we know it is either f1 and f2 containing each other, other they are
	 * Separate. If any vertex of one face is in another face, then we know the face
	 * is in the another face.
	 * 
	 * @param f1
	 * @param f2
	 * @return 1 or -1 if f1 and f2 are containing and has a z-order. 0 if they are
	 *         separate.
	 */
	private int containing(Face f1, Face f2) {
		for (int i = 0; i < 3; i++) {
			if (pointInTriangle(f1.getVertices()[i], f2)) {
				double z = getZValue(f1.getVertices()[i], f2);
				double vz = f1.getVertices()[i].getVisualCoord()[2];
				if (z == Double.NaN) {
					return 0;
				}
				if (vz < z) {
					return -1;
				} else if (vz < z) {
					return 1;
				} else {
					return 0;
				}
			}
			if (pointInTriangle(f2.getVertices()[i], f1)) {
				double z = getZValue(f2.getVertices()[i], f1);
				double vz = f2.getVertices()[i].getVisualCoord()[2];
				if (z == Double.NaN) {
					return 0;
				}
				if (z < vz) {
					return -1;
				} else if (z > vz) {
					return 1;
				} else {
					return 0;
				}
			}
		}
		return 0;
	}

	/**
	 * Compare the z-ordering of two faces <br>
	 * compare their border first, if they are separate in x-y plain, then rendering
	 * order doesn't matter.If they areseperate in z axis, then dicide the order by
	 * their depth. <br>
	 * Then compare if the two triangle is intersecting or containing on x-y plain.
	 */
	@Override
	public int compare(Face f1, Face f2) {
		var b1 = f1.getBorder();
		var b2 = f2.getBorder();
		if (b1[0] <= b2[2] || b2[0] <= b1[2] || b1[1] <= b2[3] || b2[1] <= b1[3]) {
			return 0;
		} else if (b1[4] <= b2[5]) {
			return -1;
		} else if (b2[4] <= b1[5]) {
			return 1;
		}
		int i = intersection(f1, f2);
		if (i != 0) {
			return i;
		}
		int c = containing(f1, f2);
		if (c != 0) {
			return c;
		}
		return 0;
	}

}
