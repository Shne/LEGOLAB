import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import lejos.geom.Line;

public class MainFrame extends JFrame {

	public static void main(String[] args) {
		MainFrame f = new MainFrame();
		f.run();
	}

	private void run() {
		while (true) {
			synchronized (lines) {
				Random r = new Random();
				lines.add(new Line(r.nextInt(500), r.nextInt(500), r
						.nextInt(500), r.nextInt(500)));
				lines.notifyAll();
				System.out.println("BUO2");
			}
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private ArrayList<Line> lines = new ArrayList<Line>();
	private DrawingPanel panel = new LinesPanel(640, 480, lines);

	private MainFrame() {
		this.add(panel);
		this.pack();
		this.setResizable(false);
		this.setVisible(true);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		panel.ready();
		panel.update();
	}

}
