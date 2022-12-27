package LokiViewer.Object;

/**
 * Calculate the rotation matrix and apply to the vertices
 * 
 * @author Renhao Lu
 *
 */
public class RotationMatrix {
	/**
	 * Unit Axis vector
	 */
	private double[] unitAxisVector;

	/**
	 * Angle to rotate (in degrees)
	 */
	private double angle;

	/**
	 * The rotation matrix
	 */
	private double[][] matrix;

	/**
	 * Calculate the rotation matrix. See:
	 * <a href="https://en.wikipedia.org/wiki/Rotation_matrix">Rotation Matrix</a>
	 * 
	 * @param x rotation angle in x component
	 * @param y rotation angle in y component
	 */
	public RotationMatrix(double x, double y) {
		angle = Math.sqrt(x * x + y * y);
		unitAxisVector = new double[] { -y / angle, x / angle, 0 };

		matrix = new double[3][3];

		double cos = Math.cos(angle / 180 * Math.PI);
		double sin = Math.sin(angle / 180 * Math.PI);

		matrix[0][0] = cos + (1 - cos) * unitAxisVector[0] * unitAxisVector[0];
		matrix[0][1] = (1 - cos) * unitAxisVector[0] * unitAxisVector[1];
		matrix[0][2] = sin * unitAxisVector[1];

		matrix[1][0] = (1 - cos) * unitAxisVector[0] * unitAxisVector[1];
		matrix[1][1] = cos + (1 - cos) * unitAxisVector[1] * unitAxisVector[1];
		matrix[1][2] = -sin * unitAxisVector[0];

		matrix[2][0] = -sin * unitAxisVector[1];
		matrix[2][1] = sin * unitAxisVector[0];
		matrix[2][2] = cos;
	}

	/**
	 * Apply the rotation matrix to vertex
	 * 
	 * @param coord        coordinates before rotation
	 * @param rotateOrigin rotation origin
	 * @return coordinates after rotation
	 */
	public double[] rotate(double[] coord, double[] rotateOrigin) {
		double[] vector = new double[] { coord[0] - rotateOrigin[0], coord[1] - rotateOrigin[1],
				coord[2] - rotateOrigin[2] };
		double[] res = new double[3];
		res[0] = matrix[0][0] * vector[0] + matrix[0][1] * vector[1] + matrix[0][2] * vector[2] + rotateOrigin[0];
		res[1] = matrix[1][0] * vector[0] + matrix[1][1] * vector[1] + matrix[1][2] * vector[2] + rotateOrigin[1];
		res[2] = matrix[2][0] * vector[0] + matrix[2][1] * vector[1] + matrix[2][2] * vector[2] + rotateOrigin[2];
		return res;
	}

}
