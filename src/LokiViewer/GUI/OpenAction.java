package LokiViewer.GUI;

import java.awt.event.ActionEvent;
import java.io.File;

import javax.swing.AbstractAction;
import javax.swing.JFileChooser;

import LokiViewer.Object.Object3D;
import LokiViewer.Object.ObjectParseException;

/**
 * The action of opening a new model file
 * 
 * @author Renhao Lu
 *
 */

public class OpenAction extends AbstractAction {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * The file chooser window
	 */
	private JFileChooser chooser;

	/**
	 * The parent viewer frame
	 */
	private ViewerFrame parent;

	/**
	 * Initialize the private members
	 * 
	 * @param parent the parent viewer frame
	 */
	public OpenAction(ViewerFrame parent) {
		super("Open");
		chooser = new JFileChooser();
		this.parent = parent;
	}

	/**
	 * get File object from the file chooser window and open parse with
	 * Object3D.parse(file)
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		System.out.println("Open action performed");
		int result = chooser.showOpenDialog(parent);
		if (result == JFileChooser.APPROVE_OPTION) {
			File file = chooser.getSelectedFile();
			try {
				var object = Object3D.parse(file);
				parent.setObject(object);
			} catch (ObjectParseException e2) {
				System.out.println("File parse failed");
			}
		} else {
			System.out.println("File not selected");
		}
	}

}
