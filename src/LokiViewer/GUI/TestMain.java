package LokiViewer.GUI;

import java.awt.EventQueue;

import javax.swing.JFrame;

/**
 * A TestMain class containing a main method to enter the GUI
 * 
 * @author Renhao Lu
 */
public class TestMain {

	/**
	 * Main method to enter viewer GUI
	 * 
	 * @param args main method argument
	 */

	public static void main(String[] args) {
		EventQueue.invokeLater(() -> {
			var frame = new ViewerFrame();
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
			frame.setLocationRelativeTo(null);
			frame.setVisible(true);
		});
	}

}
