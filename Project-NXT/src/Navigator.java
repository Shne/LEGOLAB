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

	public static void main(String[] args) throws InterruptedException {
		Button.ESCAPE.addButtonListener(new ButtonListener() {
			public void buttonReleased(Button b) {
			}

			public void buttonPressed(Button b) {
				System.exit(0);
			}
		});
		Upload();
		/*
		 * new Thread() { public void run() { while (true) { try {
		 * Thread.sleep(Sound.playSample(new File("indy.wav"))); } catch
		 * (InterruptedException e) { } } } }.start();
		 */

		p.setTravelSpeed(20.0);
		p.setRotateSpeed(10.0);
		p.setAcceleration(50);
		lejos.robotics.navigation.Navigator n = new lejos.robotics.navigation.Navigator(
				p, new OdometryPoseProvider(p));
		// Button.ENTER.waitForPressAndRelease();
		while (true) {
			p.rotate(10000);
			Thread.sleep(10000);
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

						BTConnection con = Bluetooth.waitForConnection();
						DataOutputStream dos = con.openDataOutputStream();
						Sound.beepSequence();
						Point lastPoint = null;
						while (true) {
							Thread.sleep(200);
							int reading = ultron.getDistance();
							if (reading == 255) {
								lastPoint = null;
								continue;
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
								dos.write(Serialization.SerializeLine(l));
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
