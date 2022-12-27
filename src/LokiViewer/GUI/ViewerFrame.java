package LokiViewer.GUI;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.ButtonGroup;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JRadioButtonMenuItem;

import LokiViewer.Object.Object3D;

/**
 * Main frame of the viewer, containing a simple menubar and a ViewerComponent
 * to draw the object
 * 
 * @author Renhao Lu
 */

public class ViewerFrame extends JFrame {

	private static final long serialVersionUID = 1L;

	/**
	 * Vertex circle size
	 */
	public static final int vertexDrawSize = 5;
	/**
	 * Edge width
	 */
	public static final int edgeDrawSize = 2;
	/**
	 * Vertex and Edge color
	 */
	public static final Color color = Color.BLUE;
	/**
	 * A coefficient for calculating rotation angle when dragging
	 */
	public static final double rotateFactor = 0.5;
	/**
	 * The Object to draw in the window
	 */
	private Object3D object3D;
	/**
	 * The component to draw the Object on
	 */
	private ViewerComponent drawComponent;

	/**
	 * Initialize the window title and size and then initialize a simple menubar
	 */
	public ViewerFrame() {
		setTitle("LokiViewer");
		setSize(800, 800);

		this.object3D = null;

		var openAction = new OpenAction(this);

		var openMenu = new JMenu("File");

		openMenu.add(openAction);

		var viewMenu = new JMenu("View");

		var showVertexAndEdgeItem = new JCheckBoxMenuItem("show vertices and edges");
		showVertexAndEdgeItem.setSelected(true);
		showVertexAndEdgeItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean flag = showVertexAndEdgeItem.isSelected();
				if (object3D != null) {
					object3D.setDrawVertexAndEdge(flag);
				}
				if (drawComponent != null) {
					drawComponent.repaint();
				}
			}
		});

		var showFaceItem = new JCheckBoxMenuItem("show faces");
		showFaceItem.setSelected(true);
		showFaceItem.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean flag = showFaceItem.isSelected();
				if (object3D != null) {
					object3D.setDrawFace(flag);
				}
				if (drawComponent != null) {
					drawComponent.repaint();
				}
			}
		});

		viewMenu.add(showVertexAndEdgeItem);
		viewMenu.add(showFaceItem);

		var optionMenu = new JMenu("Options");

		var group = new ButtonGroup();

		var naiveZSorting = new JRadioButtonMenuItem("Naive Z Sorting");
		naiveZSorting.setSelected(true);
		naiveZSorting.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean flag = naiveZSorting.isSelected();
				if (object3D != null) {
					object3D.setPainterAlgo(!flag);
				}
				if (drawComponent != null) {
					drawComponent.repaint();
				}
			}
		});

		var topologicalSoring = new JRadioButtonMenuItem("Topological Z Sorting");
		topologicalSoring.setSelected(false);
		topologicalSoring.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				boolean flag = topologicalSoring.isSelected();
				if (object3D != null) {
					object3D.setPainterAlgo(flag);
				}
				if (drawComponent != null) {
					drawComponent.repaint();
				}
			}
		});

		group.add(naiveZSorting);
		group.add(topologicalSoring);

		optionMenu.add(naiveZSorting);
		optionMenu.add(topologicalSoring);

		var menuBar = new JMenuBar();

		setJMenuBar(menuBar);

		menuBar.add(openMenu);
		menuBar.add(viewMenu);
		menuBar.add(optionMenu);

		drawComponent = new ViewerComponent(this);

		add(drawComponent);
	}

	/**
	 * set the object to draw
	 * 
	 * @param object3D object to draw
	 */
	public void setObject(Object3D object3D) {
		this.object3D = object3D;
		this.drawComponent.setInitial();
		this.drawComponent.repaint();
	}

	/**
	 * get the object
	 * 
	 * @return the object to draw
	 */
	public Object3D getObject() {
		return object3D;
	}
}
