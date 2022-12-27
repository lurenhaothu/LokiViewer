package LokiViewer.Object;

import java.awt.Dimension;
import java.awt.Graphics;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

/**
 * The object class to draw
 * 
 * @author Renhao Lu
 *
 */
public class Object3D {

	/**
	 * Hashmap to store id and vertices
	 */
	private HashMap<Integer, Vertex> vertices;
	/**
	 * ArrayList of all faces
	 */
	private ArrayList<Face> faces;
	/**
	 * ArrayList of all edges
	 */
	private ArrayList<Edge> edges;
	/**
	 * The rotation origin when mouse dragged
	 */
	private double[] rotateOrigin;
	/**
	 * Whether draw VertexAndEdge
	 */
	private boolean drawVertexAndEdge;
	/**
	 * Whether draw Faces
	 */
	private boolean drawFaces;

	/**
	 * Use naive z sorting (false) or topological sorting (true)
	 */
	private boolean sortAlgorithm;

	/**
	 * Constructor. Many faces may share same edges, hence use a HashSet to prevent
	 * repeating.
	 * 
	 * @param vertices vertices
	 * @param faces    faces
	 */
	public Object3D(HashMap<Integer, Vertex> vertices, ArrayList<Face> faces) {
		this.vertices = vertices;
		this.faces = faces;
		this.rotateOrigin = null;
		this.edges = new ArrayList<>();
		drawVertexAndEdge = true;
		drawFaces = true;
		sortAlgorithm = false;
		HashSet<String> edgeSet = new HashSet<>();
		for (Face f : faces) {
			var v = f.getVertices();
			String k1 = Integer.toString(v[0].getID()) + ' ' + Integer.toString(v[1].getID());
			if (!edgeSet.contains(k1)) {
				edges.add(new Edge(v[0], v[1]));
				edgeSet.add(k1);
			}
			String k2 = Integer.toString(v[0].getID()) + ' ' + Integer.toString(v[2].getID());
			if (!edgeSet.contains(k2)) {
				edges.add(new Edge(v[0], v[2]));
				edgeSet.add(k2);
			}
			String k3 = Integer.toString(v[1].getID()) + ' ' + Integer.toString(v[2].getID());
			if (!edgeSet.contains(k3)) {
				edges.add(new Edge(v[1], v[2]));
				edgeSet.add(k3);
			}
		}
		return;
	}

	/**
	 * Get the original border of the object, in order to rescale it to fill half of
	 * the window
	 * 
	 * @return border coordinate in x-y plain in the order of: x+, y+, x- y-
	 */
	private double[] getOriginBorder() {
		double[] res = new double[4]; // border coordinates for right, up, left, down
		res[0] = Double.NEGATIVE_INFINITY;
		res[1] = Double.NEGATIVE_INFINITY;
		res[2] = Double.POSITIVE_INFINITY;
		res[3] = Double.POSITIVE_INFINITY;

		for (Vertex v : vertices.values()) {
			res[0] = Double.max(res[0], v.getVisualCoord()[0]);
			res[1] = Double.max(res[1], v.getVisualCoord()[1]);
			res[2] = Double.min(res[2], v.getVisualCoord()[0]);
			res[3] = Double.min(res[3], v.getVisualCoord()[1]);
		}
		return res;
	}

	/**
	 * Given the target window dimension, return the zoom factor and shift
	 * parameters to make object fill half the window and in the center
	 * 
	 * @param d window dimension
	 * @return zoom and shift parameters, in the order of: zoom factor, x-shift,
	 *         y-shift
	 */
	private double[] getZoomAndShift(Dimension d) {
		double[] res = new double[3]; // zoom factor, x-shift, y-shift
		double[] border = getOriginBorder();
		res[1] = -(border[0] + border[2]) / 2;
		res[2] = -(border[1] + border[3]) / 2;
		double[] size = { border[0] - border[2], border[1] - border[3] };
		double h = d.height / 2;
		double w = d.width / 2;
		if (size[0] != 0 && size[1] != 0) {
			res[0] = Double.min(w / size[0], h / size[1]);
		} else if (size[0] == 0 && size[1] == 0) {
			res[0] = 1;
		} else if (size[0] != 0) {
			res[0] = h / size[1];
		} else {
			res[0] = w / size[0];
		}
		return res;
	}

	/**
	 * Get the rendering order of the faces in their z-order. Because not all face
	 * are comparable, I implemented a topological sorting there.
	 * 
	 * @return a ArrayList of the rendering order
	 */
	private ArrayList<Integer> topologicalSortFacesRenderOrder() {
		var comp = new FaceComp();
		int n = faces.size();
		ArrayList<ArrayList<Integer>> compRes = new ArrayList<>();
		ArrayList<Integer> deg = new ArrayList<>();
		for (int i = 0; i < n; i++) {
			compRes.add(new ArrayList<>());
			deg.add(0);
		}
		for (int i = 0; i < n; i++) {
			for (int j = i + 1; j < n; j++) {
				int res = comp.compare(faces.get(i), faces.get(j));
				if (res == -1) {
					compRes.get(i).add(j);
					deg.set(j, deg.get(j) + 1);
				} else if (res == 1) {
					compRes.get(j).add(i);
					deg.set(i, deg.get(i) + 1);
				}
			}
		}
		ArrayDeque<Integer> que = new ArrayDeque<>();
		for (int i = 0; i < n; i++) {
			if (deg.get(i) == 0) {
				que.push(i);
			}
		}
		ArrayList<Integer> sortRes = new ArrayList<>();
		while (que.size() != 0) {
			int head = que.poll();
			sortRes.add(head);
			for (int dest : compRes.get(head)) {
				deg.set(dest, deg.get(dest) - 1);
				if (deg.get(dest) == 0) {
					que.push(dest);
				}
			}
		}
		return sortRes;
	}

	/**
	 * Sort faces according to their average z value
	 * 
	 * @return a ArrayList of the rendering order
	 */
	private ArrayList<Integer> naiveSortFacesRenderOrder() {
		ArrayList<Entry<Integer, Double>> pairs = new ArrayList<>();
		for (int i = 0; i < faces.size(); i++) {
			pairs.add(Map.entry(i, faces.get(i).getAverageZVale()));
		}
		Collections.sort(pairs, (i, j) -> {
			double r = i.getValue() - j.getValue();
			if (r > 0)
				return 1;
			else if (r < 0)
				return -1;
			else
				return 0;
		});
		ArrayList<Integer> res = new ArrayList<>();
		for (var p : pairs) {
			res.add(p.getKey());
		}
		return res;
	}

	/**
	 * Draw the object in window. If need to rescale, first rescale all vertices.
	 * Then draw all faces, vertices, and edges
	 * 
	 * @param g       the Graphics context in which to paint
	 * @param d       window dimension
	 * @param reScale Whether rescale is needed (when load the file or resize the
	 *                window)
	 */
	public void draw(Graphics g, Dimension d, boolean reScale) {
		double[] zoomAndShift = getZoomAndShift(d);
		if (reScale) {
			for (Vertex v : vertices.values()) {
				v.applyZoomAndShift(zoomAndShift);
			}
		}

		if (drawFaces) {

			ArrayList<Integer> sortRes;

			if (sortAlgorithm) {
				sortRes = topologicalSortFacesRenderOrder();
			} else {
				sortRes = naiveSortFacesRenderOrder();
			}

			for (int index : sortRes) {
				faces.get(index).draw(g, d);
			}
		}

		if (drawVertexAndEdge) {

			for (Vertex v : vertices.values()) {
				v.draw(g, d);
			}

			for (Edge e : edges) {
				e.draw(g, d);
			}
		}

	}

	/**
	 * Set drawVertexAndEdge
	 * 
	 * @param flag target value
	 */
	public void setDrawVertexAndEdge(boolean flag) {
		this.drawVertexAndEdge = flag;
	}

	/**
	 * Set drawFaces
	 * 
	 * @param flag target value
	 */
	public void setDrawFace(boolean flag) {
		this.drawFaces = flag;
	}

	public void setPainterAlgo(boolean flag) {
		this.sortAlgorithm = flag;
	}

	/**
	 * Find the nearest point to the observer, set the rotation origin's x, y
	 * coordinates the same as that point. Find the farthest point to the observer,
	 * and set the z coordinate of rotation origin the average of nearest and
	 * farthest.
	 */
	public void generateRotateOrigin() {
		Vertex v = null;
		double maxz = Double.NEGATIVE_INFINITY;
		double minz = Double.POSITIVE_INFINITY;

		for (Vertex vertex : vertices.values()) {
			if (vertex.getRescaleCoord()[2] > maxz) {
				maxz = vertex.getRescaleCoord()[2];
				v = vertex;
			}
			if (vertex.getRescaleCoord()[2] < minz) {
				minz = vertex.getRescaleCoord()[2];
			}
		}
		rotateOrigin = new double[] { v.getRescaleCoord()[0], v.getRescaleCoord()[1], (maxz + minz) / 2 };
		return;
	}

	/**
	 * remove rotation origin after mouse released
	 */
	public void removeRotateOrigin() {
		rotateOrigin = null;
	}

	/**
	 * Rotate all vertex
	 * 
	 * @param x angle in x component
	 * @param y angle in y component
	 */
	public void rotate(double x, double y) {
		var matrix = new RotationMatrix(x, y);
		for (Vertex v : vertices.values()) {
			v.rotate(matrix, rotateOrigin);
		}
	}

	/**
	 * After rotation, update the vertex
	 */
	public void updateVertex() {
		for (Vertex v : vertices.values()) {
			v.updateRescaleCoord();
		}
	}

	/**
	 * Parse the input file
	 * 
	 * @param file input file
	 * @return new object3D
	 * @throws ObjectParseException when any file reading or parsing problem happens
	 */
	public static Object3D parse(File file) throws ObjectParseException {
		HashMap<Integer, Vertex> vertices = new HashMap<>();
		ArrayList<Face> faces = new ArrayList<>();
		try (BufferedReader bf = new BufferedReader(new FileReader(file))) {
			String str = bf.readLine();
			String[] s = str.split(",");
			if (s.length != 2)
				throw new ObjectParseException();
			int n = Integer.parseInt(s[0]);
			int m = Integer.parseInt(s[1]);

			for (int i = 0; i < n; i++) {
				str = bf.readLine();
				s = str.split(",");
				if (s.length != 4)
					throw new ObjectParseException();
				int id = Integer.parseInt(s[0]);
				double x = Double.parseDouble(s[1]);
				double y = Double.parseDouble(s[2]);
				double z = Double.parseDouble(s[3]);
				vertices.put(id, new Vertex(id, x, y, z));
			}

			for (int i = 0; i < m; i++) {
				str = bf.readLine();
				s = str.split(",");
				if (s.length != 3)
					throw new ObjectParseException();
				int v1 = Integer.parseInt(s[0]);
				int v2 = Integer.parseInt(s[1]);
				int v3 = Integer.parseInt(s[2]);
				if (vertices.get(v1) == null || vertices.get(v2) == null || vertices.get(v1) == null)
					throw new ObjectParseException();
				faces.add(new Face(vertices.get(v1), vertices.get(v2), vertices.get(v3)));
			}
			bf.close();
			return new Object3D(vertices, faces);
		} catch (NumberFormatException e) {
			throw new ObjectParseException();
		} catch (FileNotFoundException e) {
			throw new ObjectParseException();
		} catch (IOException e) {
			throw new ObjectParseException();
		} catch (ObjectParseException e) {
			throw new ObjectParseException();
		}
	}

}
