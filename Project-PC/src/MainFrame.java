import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;

import javax.swing.JFrame;
import javax.swing.JPanel;

import lejos.geom.Line;
import lejos.pc.comm.NXTComm;
import lejos.pc.comm.NXTCommException;
import lejos.pc.comm.NXTCommFactory;
import lejos.pc.comm.NXTInfo;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;

public class MainFrame extends JFrame {

	public static void main(String[] args) {
		final MainFrame f = new MainFrame();
		f.NXTConnect();
		new Thread(){
			public void run()
			{
				f.runWaypoints();
			}
		}.start();
		f.run();
	}

	DataInputStream input;
	DataOutputStream output;

	private void NXTConnect() {
		NXTComm nxt;
		try {
			nxt = NXTCommFactory.createNXTComm(NXTCommFactory.BLUETOOTH);

			NXTInfo info = new NXTInfo();
			info.name = "KRISBOT";
			info.deviceAddress = "00165317366A";
			nxt.open(info);
			input = new DataInputStream(nxt.getInputStream());
			output = new DataOutputStream(nxt.getOutputStream());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void run() {
		try {
			while (true) {
				byte[] b = new byte[28];
				input.read(b);
				Pair<Line, Pose> pair = Serialization.DeSerializeLinePose(b);

				jerk:synchronized (lines) {
					panel.pose = pair.getSecond();
					if(!Double.isNaN(pair.getFirst().getX1()))
					{
						lines.add(pair.getFirst());
					}
					lines.notifyAll();				
					// System.out.println("BUO2");
					// System.out.println(l);
				}
			}
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
	}

	private void runWaypoints() {
		while (true) {
			synchronized (waypoints) {
				try {
					waypoints.wait();
					byte[] b0 = Serialization.SerializeWaypoint(new Waypoint(
							Double.NaN, Double.NaN, Double.NaN));
					output.write(b0);
					output.flush();
					for (Waypoint p : waypoints) {
						byte[] b = Serialization.SerializeWaypoint(p);
						output.write(b);
						output.flush();
					}
					b0 = Serialization.SerializeWaypoint(new Waypoint(
							Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY, Double.POSITIVE_INFINITY));
					output.write(b0);
					output.flush();
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}

		}
	}

	private ArrayList<Line> lines = new ArrayList<Line>();
	private ArrayList<Waypoint> waypoints = new ArrayList<Waypoint>();
	private LinesPanel panel = new LinesPanel(800, 800, lines, waypoints);

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
