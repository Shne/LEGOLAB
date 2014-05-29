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
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.UltrasonicSensor;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.navigation.*;
import static java.lang.Math.*;

public class Navigator {

	static private DifferentialPilot p = new DifferentialPilot(8.16, 15.85,
			new NXTRegulatedMotor(MotorPort.A), new NXTRegulatedMotor(
					MotorPort.C));
	static private OdometryPoseProvider PP = new OdometryPoseProvider(p);
	static private BTConnection con = Bluetooth.waitForConnection();
	static private DataOutputStream dos;
	static private DataInputStream dis;

	public static void main(String[] args) throws InterruptedException {
		Button.ESCAPE.addButtonListener(new ButtonListener() {
			public void buttonReleased(Button b) {
			}

			public void buttonPressed(Button b) {
				System.exit(0);
			}
		});

		dos = con.openDataOutputStream();
		dis = con.openDataInputStream();
		Upload();

		new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(Sound.playSample(new File("indy.wav")));
					} catch

					(InterruptedException e) {
					}
				}
			}
		}.start();

		p.setTravelSpeed(20.0);
		p.setRotateSpeed(10.0);
		p.setAcceleration(50);
		lejos.robotics.navigation.Navigator n = new lejos.robotics.navigation.Navigator(
				p, new OdometryPoseProvider(p));
		p.rotate(1000000., true);
		// Button.ENTER.waitForPressAndRelease();
		byte[] b = new byte[24];
		while (true) {
			try {
				dis.read(b);
				Waypoint point = Serialization.DeSerializeWaypoint(b);
				if (Double.isNaN(point.getX())) {
					n.stop();
					n.clearPath();
					p.stop();
					Sound.beepSequenceUp();
				} else {
					n.addWaypoint(point);
					n.followPath();
				}

			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		// Thread.sleep(1000000000);
	}

	static Random rand = new Random();

	private static UltrasonicSensor ultron = new UltrasonicSensor(SensorPort.S1);

	public static void Upload() {
		new Thread() {
			public void run() {
				while (true) {
					try {

						Sound.beepSequence();
						Point lastPoint = null;
						while (true) {
							Thread.sleep(200);
							int reading = ultron.getDistance();
							if (reading == 255) {
								lastPoint = null;
								// continue;
							}

							Pose p = PP.getPose();

							double Lx = ((double) reading)
									* cos(toRadians(p.getHeading()));
							double Ly = ((double) reading)
									* sin(toRadians(p.getHeading()));
							Point newPoint = new Point(p.getX() + (float) Lx,
									p.getY() + (float) Ly);
							if (lastPoint != null
									&& lastPoint.distanceSq(newPoint) < 100f) {
								Line l = new Line(lastPoint.x, lastPoint.y,
										newPoint.x, newPoint.y);
								dos.write(Serialization.SerializeLinePose(l, p));
								dos.flush();
							} else {
								Line l = new Line(Float.NaN, Float.NaN, Float.NaN, Float.NaN);
								dos.write(Serialization.SerializeLinePose(l, p));
								dos.flush();
							}
							lastPoint = newPoint;
						}

					} catch (Throwable t) {
					}
				}
			}

			{
				setDaemon(true);
			}
		}.start();

	}

}
