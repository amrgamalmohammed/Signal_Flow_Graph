package eg.edu.alexu.csd.control.sfg;

import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import java.awt.AlphaComposite;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Ellipse2D;
import java.util.ArrayList;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JComponent;

public class UIView {

	private static JFrame frame;
	private JButton vertix, edge, solve, delete;
	private static int currentAction;

	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			@SuppressWarnings("static-access")
			public void run() {
				try {
					UIView window = new UIView();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public UIView() {
		initialize();
	}

	private void initialize() {
		frame = new JFrame();
		frame.setTitle("Signal Flow Graph");
		frame.setBounds(100, 100, 1000, 600);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel buttonPanel = new JPanel();
		Box buttonBox = Box.createHorizontalBox();
		vertix = makeButton("Node", 1);
		edge = makeButton("Edge", 2);
		solve = new JButton();
		solve.setText("solve");
		solve.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				Solver solver = new Solver();
				solver.solve(DrawingArea.buildGraph());
				ResultView results = new ResultView(solver.getForwardPaths(), solver.getForwardPathsGains() ,
						solver.getLoops(), solver.getLoopsGains(), solver.getNonTouchingLoops(), 
						solver.getNonTouchingGains(), solver.getSmallDeltas(), solver.getTransfareFunction());
				results.setVisible(true);
			}
		});
		delete = new JButton();
		delete.setText("Delete");
		delete.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					String input = JOptionPane.showInputDialog(frame, "From Node :");
					int from = Integer.parseInt(input);
					input = JOptionPane.showInputDialog(frame, "To Node :");
					int to = Integer.parseInt(input);
					boolean deleted = DrawingArea.deleteEdge(from, to);
					if (!deleted) {
						ErrorView error = new ErrorView("Can't Delete This Edge !");
						error.setVisible(true);
					}
					frame.revalidate();
					frame.repaint();
				} catch (Exception ex) {
					ErrorView error = new ErrorView("Invalid Input !");
					error.setVisible(true);
				}
				
			}
		});
		buttonBox.add(vertix);
		buttonBox.add(edge);
		buttonBox.add(delete);
		buttonBox.add(solve);
		buttonPanel.add(buttonBox);
		frame.add(buttonPanel, BorderLayout.SOUTH);
		
		frame.add(new DrawingArea(), BorderLayout.CENTER);
	}
	
	private JButton makeButton(String label, int actionNumber) {
		JButton button = new JButton();
		button.setText(label);
		
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				currentAction = actionNumber;
			}
		});
		return button;
	}
	
	@SuppressWarnings("serial")
	private static class DrawingArea extends JComponent{
		
		private static ArrayList<Nodes> nodes;
		private static ArrayList<Edge> edges;
		private int[] edgeSides;
		private Point centerPoint;
		private int position;
		
		public DrawingArea() {
			nodes = new ArrayList<Nodes>();
			edges = new ArrayList<Edge>();
			edgeSides = new int[2];
			position = 0;
			this.addMouseListener(new MouseAdapter() {
				public void mousePressed(MouseEvent e) {
					centerPoint = new Point(e.getX(), e.getY());
					repaint();
				}
				
				public void mouseReleased(MouseEvent e) {
					int offset = 20;
					if (currentAction == 1){
						Nodes node = new Nodes(nodes.size()+1);
						node.makeEllipse(centerPoint.x-offset, centerPoint.y-offset, centerPoint.x+offset, centerPoint.y+offset);
						nodes.add(node);
					}
					else if (currentAction == 2) {
						int counter = 0;
						for (Nodes n : nodes) {
							if (n.getShape() instanceof Ellipse2D && n.getShape().contains(e.getPoint()) && position == 0) {
								edgeSides[0] = counter;
								position = 1;
							}
							else if (n.getShape() instanceof Ellipse2D && n.getShape().contains(e.getPoint()) && position == 1) {
								try {
									edgeSides[1] = counter;
									String input = JOptionPane.showInputDialog(frame, "Enter edge's gain :");
									double gain = Double.parseDouble(input);
									Edge newEdge = new Edge(nodes.get(edgeSides[0]), nodes.get(edgeSides[1]), gain);
									newEdge.makeArc(input, newEdge);
									edges.add(newEdge);
									position = 0;
									edgeSides = new int[2];
								} catch (Exception ex) {
									ErrorView error = new ErrorView("Invalid Input !");
									error.setVisible(true);
									position = 0;
									edgeSides = new int[2];
								}
							}
							counter++;
						}
					}
					repaint();
				}
			});
		}
		
		public void paint (Graphics g) {
			Graphics2D g2 = (Graphics2D) g;
			g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1.0f));
			for (Edge e : edges) {
				int start = 0, end = 0;
				if (e.isLoop()) {
					start = (int) (0.125*e.getStart().x+0.375*e.getMid().x+0.375*e.getEnd().x+0.125*e.getStart().x+7);
					end = (int) (0.125*e.getStart().y+0.375*e.getMid().y+0.375*e.getEnd().y+0.125*e.getStart().y);
				}
				else {
					start = (int) (0.25*e.getStart().x+0.5*e.getMid().x+0.25*e.getEnd().x);
					end = (int) (0.25*e.getStart().y+0.5*e.getMid().y+0.25*e.getEnd().y-10);
				}
				g2.setColor(Color.darkGray);
				Font font = new Font("Serif", Font.PLAIN, 18);
				g2.setFont(font);
				g2.drawString(e.getGain()+"", start, end);
				g2.draw(e.getShape());
				g2.fill(e.getArrow());
			}
			for (Nodes n : nodes) {
				g2.setColor(new Color(0x2196F3));
				g2.fill(n.getShape());
				g2.setColor(Color.white);
				Font font = new Font("Serif", Font.PLAIN, 18);
				g2.setFont(font);
				g2.drawString(n.getName(), n.getCenter().x-8, n.getCenter().y+5);
			}
		}
		
		private static double[][] buildGraph() {
			double[][] graph = new double[nodes.size()][nodes.size()];
			for (Edge e : edges) {
				graph[e.getFrom().getIndex()][e.getTo().getIndex()] = e.getGain();
			}
			return graph;
		}
		
		private static boolean deleteEdge(int from, int to) {
			for (Edge e : edges) {
				if (e.getFrom().getIndex() == from-1 && e.getTo().getIndex() == to-1) {
					edges.remove(e);
					return true;
				}
			}
			return false;
		}
	}
		
}
