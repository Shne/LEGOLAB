package org.lejos.sample.bumpercar;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
//import lejos.robotics.MirrorMotor;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.subsumption.Arbitrator;
import lejos.robotics.subsumption.Behavior;

/**
 * Demonstration of the Behavior subsumption classes.
 * 
 * Requires a wheeled vehicle with two independently controlled motors connected
 * to motor ports A and C, and a touch sensor connected to sensor port 1 and an
 * ultrasonic sensor connected to port 3;
 * 
 * @author Brian Bagnall and Lawrie Griffiths, modified by Roger Glassey
 * 
 */
public class BumperCar {
	static RegulatedMotor leftMotor = Motor.A;
	static RegulatedMotor rightMotor = Motor.C;

	// Use these definitions instead if your motors are inverted
	// static RegulatedMotor leftMotor = MirrorMotor.invertMotor(Motor.A);
	// static RegulatedMotor rightMotor = MirrorMotor.invertMotor(Motor.C);

	public static void main(String[] args) {
		leftMotor.setSpeed(400);
		rightMotor.setSpeed(400);
		Behavior b1 = new DriveForward();
		Behavior b2 = new DetectWall();
		Behavior b3 = new Exit();
		Behavior[] behaviorList = { b1, b2, b3 };
		Arbitrator arbitrator = new Arbitrator(behaviorList);
		LCD.drawString("Bumper Car", 0, 1);
		Button.waitForAnyPress();
		arbitrator.start();
	}
}

class DriveForward implements Behavior {

	private boolean _suppressed = false;

	public boolean takeControl() {
		return true; // this behavior always wants control.
	}

	public void suppress() {
		_suppressed = true;// standard practice for suppress methods
	}

	public void action() {
		_suppressed = false;
		BumperCar.leftMotor.forward();
		BumperCar.rightMotor.forward();
		while (!_suppressed) {
			Thread.yield(); // don't exit till suppressed
		}
		BumperCar.leftMotor.stop();
		BumperCar.leftMotor.stop();
	}
}

class DetectWall implements Behavior {

	private TouchSensor touch;
	private TouchSensor touch2;
	private UltrasonicSensor sonar;
	private volatile int distance = 255;

	public DetectWall() {
		touch = new TouchSensor(SensorPort.S1);
		touch2 = new TouchSensor(SensorPort.S2);
		sonar = new UltrasonicSensor(SensorPort.S3);
		new Thread() {
			public void run() {
				while (true) {
					sonar.ping();
					distance = sonar.getDistance();
					LCD.drawInt(distance, 0, 0);
					try {
						Thread.sleep(20);
					} catch (Throwable t) {
					}
				}
			}
		}.start();
	}

	public boolean takeControl() {

		return touch.isPressed() || touch2.isPressed() || distance < 25;
	}

	public void suppress() {
		// Since this is highest priority behavior, suppress will never be
		// called.
	}

	public void action() {
		BumperCar.leftMotor.backward();
		BumperCar.rightMotor.backward();

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
		}
		BumperCar.leftMotor.stop();
		BumperCar.rightMotor.stop();

		BumperCar.leftMotor.rotate(-180, true);// start Motor.A rotating
												// backward
		BumperCar.rightMotor.rotate(-360); // rotate C farther to make the turn
	}

}

class Exit implements Behavior {

	public Exit() {

	}

	public boolean takeControl() {
		return Button.ESCAPE.isPressed();
	}

	public void suppress() {
		// Since this is highest priority behavior, suppress will never be
		// called.
	}

	public void action() {
		System.exit(0);
	}
}