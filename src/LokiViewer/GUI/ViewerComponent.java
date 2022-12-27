package LokiViewer.GUI;

import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;

/**
 * The JComponent to draw the object
 * 
 * @author Renhao Lu
 *
 */
public class ViewerComponent extends JComponent {

	private static final long serialVersionUID = 1L;

	/**
	 * The parent viewer frame
	 */
	private ViewerFrame parentFrame;

	/**
	 * Flag to mark if rescaling is needed
	 */
	private boolean initial;

	/**
	 * When dragging the object, mark the start point of mouse on screen
	 */
	private Point startPoint;

	/**
	 * Initialize parent frame, initial flag, and startpoint. Add mouse listener and
	 * window resize listener.
	 * 
	 * @param parentFrame parent viewer frame
	 */
	public ViewerComponent(ViewerFrame parentFrame) {
		this.parentFrame = parentFrame;
		initial = true;
		startPoint = null;
		addMouseListener(new MouseHandler());
		addMouseMotionListener(new MouseMotionHandler());

		addComponentListener(new ComponentAdapter() {
			@Override
			public void componentResized(ComponentEvent e) {
				setInitial();
				repaint();
			}

			@Override
			public void componentMoved(ComponentEvent e) {
			}
		});
	}

	/**
	 * Paint function of the component. If initial flag is set, rescale and draw the
	 * object to fill half of the window. Otherwise, just draw the object
	 */
	@Override
	public void paint(Graphics g) {
		if (parentFrame.getObject() != null) {
			if (initial) {
				System.out.println("repaint");
				parentFrame.getObject().draw(g, this.getSize(), true);
				initial = false;
			} else {
				parentFrame.getObject().draw(g, this.getSize(), false);
			}
		} else {
			initial = true;
		}
	}

	/**
	 * set the initial flag to true, to rescale the object in the window
	 */
	public void setInitial() {
		initial = true;
	}

	/**
	 * Handle mouse press and release action
	 * 
	 * @author Renhao Lu
	 *
	 */
	private class MouseHandler extends MouseAdapter {
		/**
		 * When mouse pressed, a rotation action starts. Remember the start point to
		 * generate rotation angle and direction later. Meanwhile, generate a rotate
		 * origin in object
		 */
		@Override
		public void mousePressed(MouseEvent event) {
			if (parentFrame.getObject() != null) {
				parentFrame.getObject().generateRotateOrigin();
				startPoint = event.getPoint();
			}
		}

		/**
		 * When mouse released, the rotation action ends. Update the coordinates of each
		 * vertex, and reset the start point and rotate origin.
		 */
		@Override
		public void mouseReleased(MouseEvent event) {
			if (parentFrame.getObject() != null) {
				parentFrame.getObject().updateVertex();
				parentFrame.getObject().removeRotateOrigin();
				startPoint = null;
			}
		}
	}

	/**
	 * Handle mouse dragging motion
	 * 
	 * @author Renhao Lu
	 *
	 */
	private class MouseMotionHandler implements MouseMotionListener {
		/**
		 * When mouse dragged, calculate the rotation angle and direction according to
		 * the difference between current mouse position and start point, pass it to
		 * object to rotate, and repaint the object.
		 */
		@Override
		public void mouseDragged(MouseEvent e) {
			if (startPoint != null) {
				double x = e.getX();
				double y = e.getY();
				parentFrame.getObject().rotate((x - startPoint.x) * ViewerFrame.rotateFactor,
						-(y - startPoint.y) * ViewerFrame.rotateFactor);
				repaint();
			}
		}

		@Override
		public void mouseMoved(MouseEvent e) {

		}

	}

}
