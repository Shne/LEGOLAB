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
	/*	new Thread() {
			public void run() {
				while (true) {
					try {
						Thread.sleep(Sound.playSample(new File("indy.wav")));
					} catch (InterruptedException e) {
					}
				}
			}
		}.start();*/
		
		p.setTravelSpeed(20.0);
		p.setRotateSpeed(10.0);
		p.setAcceleration(50);
		lejos.robotics.navigation.Navigator n = new lejos.robotics.navigation.Navigator(
				p, new OdometryPoseProvider(p));
		// Button.ENTER.waitForPressAndRelease();
		while (true) {
			n.addWaypoint(new Waypoint(0f,0f, 90.f));
			n.addWaypoint(new Waypoint(0f,0f, 180.f));
			n.addWaypoint(new Waypoint(0f,0f, 270.f));
			n.addWaypoint(new Waypoint(0f,0f, 0.f));
			n.followPath();
			Thread.sleep(10000);
		}
		// Thread.sleep(1000000000);
	}

	static Random rand = new Random();
	
	private static UltrasonicSensor ultron = new UltrasonicSensor(SensorPort.S1);
	
	public static void Upload() {
		new Thread() {
			public void run() {
				try {
					
					BTConnection con = Bluetooth.waitForConnection();
					DataOutputStream dos = con.openDataOutputStream();

					while (true) {
						Thread.sleep(1000);
						int reading = ultron.getDistance();
						if(reading == 255) continue;
						
						Pose p = PP.getPose();
						
						double Lx = ((double)reading)*cos(toRadians(p.getHeading()));
						double Ly = ((double)reading)*sin(toRadians(p.getHeading()));
						
						double Ll = sqrt(Lx*Lx+Ly*Ly);
						
						double LxT = -Ly/Ll*10d;
						double LyT = Lx/Ll*10d;
						
						double Px1 = p.getX()+Lx+LxT;
						double Px2 = p.getX()+Lx-LxT;
						double Py1 = p.getY()+Ly+LyT;
						double Py2 = p.getY()+Ly-LyT;
						
						
						
						Line l = new Line((float)Px1, (float)Py1, (float)Px2, (float)Py2);
						dos.write(Serialization.SerializeLine(l));
						dos.flush();
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
