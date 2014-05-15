import java.io.*;
import java.util.Random;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.nxt.BasicMotorPort;
import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.Sound;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.*;

public class Navigator {

	public static void main(String[] args) throws InterruptedException {
		Button.ESCAPE.addButtonListener(new ButtonListener() {
			public void buttonReleased(Button b) {
			}

			public void buttonPressed(Button b) {
				System.exit(0);
			}
		});
		Upload();
		new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(Sound.playSample(new File("indy.wav")));
					} catch (InterruptedException e) {
					}
				}
			}
		}.start();
		DifferentialPilot p = new DifferentialPilot(8.16, 15.85,
				new NXTRegulatedMotor(MotorPort.A), new NXTRegulatedMotor(
						MotorPort.C));
		p.setTravelSpeed(20.0);
		p.setRotateSpeed(100.0);
		p.setAcceleration(50);
		lejos.robotics.navigation.Navigator n = new lejos.robotics.navigation.Navigator(
				p, new OdometryPoseProvider(p));
		// Button.ENTER.waitForPressAndRelease();
		while (true) {
			n.addWaypoint(new Waypoint(new Point(50, 0)));
			n.addWaypoint(new Waypoint(new Point(50, 50)));
			n.addWaypoint(new Waypoint(new Point(0, 50)));
			n.addWaypoint(new Waypoint(0, 0, 0));
			n.followPath();
			Thread.sleep(10000);
		}
		// Thread.sleep(1000000000);
	}

	static Random rand = new Random();

	public static void Upload() {
		new Thread() {
			public void run() {
				try {
					int counter = 0;
					BTConnection con = Bluetooth.waitForConnection();
					DataOutputStream dos = con.openDataOutputStream();

					while (true) {
						Line l = new Line(1, 2, 3, 4);
						dos.write(Serialization.SerializeLine(l));
						dos.flush();
						Thread.sleep(1000);
					}

				} catch (Throwable t) {
				}
			}

			{
				setDaemon(true);
			}
		}.start();
	}

}
