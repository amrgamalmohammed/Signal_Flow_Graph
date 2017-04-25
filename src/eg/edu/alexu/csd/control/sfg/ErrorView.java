package eg.edu.alexu.csd.control.sfg;

import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JLabel;

@SuppressWarnings("serial")
public class ErrorView extends JFrame{
	
	private String message;
	private JLabel label;
	
	public ErrorView (String text) {
		message = text;
		initialize();
	}
	
	private void initialize() {
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		setBounds(400, 100, 400, 100);
		setTitle("Error ");
		setLayout(null);
		setResizable(false);

		label = new JLabel(message);
		label.setBounds(80, 0, 400, 100);
        Font font = new Font("Serif", Font.PLAIN, 24);
        label.setFont(font);
		getContentPane().add(label);
	}

}
