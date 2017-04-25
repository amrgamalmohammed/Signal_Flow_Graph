package eg.edu.alexu.csd.control.sfg;

import java.awt.Font;

import javax.swing.JFrame;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

@SuppressWarnings("serial")
public class ResultView extends JFrame{
	
	private String[] forwardPaths;
	private Double[] forwardPathsGains;
	private String[] loops;
	private Double[] loopsGains;
	private String[] nonTouchingLoops;
	private Double[] nonTouchingGains;
	private double[] smallDeltas;
	private Double overallTF;
	
	public ResultView(String[] forwardPaths, Double[] forwardPathsGains, String[] loops, Double[] loopsGains,
			String[] nonTouchingLoops, Double[] nonTouchingGains, double[] smallDeltas, Double transfareFunction) {
		
		this.forwardPaths = forwardPaths;
		this.forwardPathsGains = forwardPathsGains;
		this.loops = loops;
		this.loopsGains = loopsGains;
		this.nonTouchingLoops = nonTouchingLoops;
		this.nonTouchingGains = nonTouchingGains;
		this.smallDeltas = smallDeltas;
		this.overallTF = transfareFunction;
		initialize();
	}
	
	private void initialize () {
		setSize(400,500);
		setResizable(false );
		setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		getContentPane().setLayout(null);
		setLocationRelativeTo(null);
		JTextArea results = new JTextArea();
		results.setBounds(0, 0, 400, 500);
		results.setFont(new Font("Serif",  Font.BOLD, 16));
		results.setEditable(false);
		
		JScrollPane scrol1 = new JScrollPane(results);
		scrol1.setBounds(0, 0, 400, 500);
		scrol1.setAutoscrolls(true);
		scrol1.setVisible(true);
		scrol1.setWheelScrollingEnabled(true);
		
		getContentPane().add(scrol1);
		results.append("Forward Paths:\n-----------------------\n");
		for (String s : forwardPaths) {
			results.append(s+"\n");
		}
		results.append("\nForward Paths Gains:\n-----------------------\n");
		for (Double d : forwardPathsGains) {
			results.append(d+"\n");
		}
		results.append("\nLoops:\n-----------------------\n");
		for (String s : loops) {
			results.append(s+"\n");
		}
		results.append("\nLoops Gains:\n-----------------------\n");
		for (Double d : loopsGains) {
			results.append(d+"\n");
		}
		results.append("\nNontouching Loops:\n-----------------------\n");
		for (String s : nonTouchingLoops) {
			results.append(s+"\n");
		}
		results.append("\nNontouching Loops Gains:\n-----------------------\n");
		for (double d : nonTouchingGains) {
			results.append(d+"\n");
		}
		results.append("\nSmall Deltas:\n-----------------------\n");
		int i = 1;
		for (double d : smallDeltas) {
			results.append("Delta"+i+" = "+d+"\n");
			i++;
		}
		results.append("\nOverall Transfare Function:\n-----------------------\n");
		results.append(overallTF+"\n\n");
	}
	
}
