import java.io.DataInputStream;
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

public class MainFrame extends JFrame {

	public static void main(String[] args) {
		MainFrame f = new MainFrame();
		f.run();
	}

	private void run() {

		try {
			NXTComm nxt = NXTCommFactory
					.createNXTComm(NXTCommFactory.BLUETOOTH);
			NXTInfo info = new NXTInfo();
			info.name = "KRISBOT";
			info.deviceAddress = "00165317366A";
			nxt.open(info);
			DataInputStream input = new DataInputStream(nxt.getInputStream());
			while (true) {
				byte[] b = new byte[16];
				input.read(b);
				Line l = Serialization.DeSerializeLine(b);
				
				synchronized (lines) {
					lines.add(l);
					lines.notifyAll();
					System.out.println("BUO2");
					System.out.println(l);
				}
				

			}
		} catch (NXTCommException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
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
