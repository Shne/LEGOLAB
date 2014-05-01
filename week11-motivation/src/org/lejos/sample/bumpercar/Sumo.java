package org.lejos.sample.bumpercar;

import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.Motor;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.TouchSensor;
import lejos.nxt.UltrasonicSensor;
//import lejos.robotics.MirrorMotor;
import lejos.robotics.RegulatedMotor;

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
public class Sumo {
	static RegulatedMotor leftMotor = Motor.A;
	static RegulatedMotor rightMotor = Motor.C;

	// Use these definitions instead if your motors are inverted
	// static RegulatedMotor leftMotor = MirrorMotor.invertMotor(Motor.A);
	// static RegulatedMotor rightMotor = MirrorMotor.invertMotor(Motor.C);

	public static void main(String[] args) throws InterruptedException {
		leftMotor.setSpeed(600);
		rightMotor.setSpeed(600);
		Behavior b1 = new SumoDriveForward();
		Behavior b2 = new DetectLine();
		Behavior b3 = new SumoExit();
		Behavior b5 = new Attack();
		Behavior[] behaviorList = { b1, b2, b3, b5 };
		Arbitrator arbitrator = new Arbitrator(behaviorList);
		LCD.drawString("Sumo-å+´´ Car", 0, 1);
		Button.waitForAnyPress();
		Thread.sleep(3000);
		arbitrator.start();
	}
}

class SumoDriveForward implements Behavior {

	private boolean _suppressed = false;

	public int takeControl() {
		return 10; // this behavior always wants control.
	}

	public void suppress() {
		_suppressed = true;// standard practice for suppress methods
	}

	public void action() {
		_suppressed = false;
		Sumo.leftMotor.forward();
		Sumo.rightMotor.forward();
		while (!_suppressed) {
			Thread.yield(); // don't exit till suppressed
		}
		Sumo.leftMotor.stop();
		Sumo.rightMotor.stop();
	}
}

class DetectLine implements Behavior
{
	LightSensor light = new LightSensor(SensorPort.S4){{setFloodlight(true);}};;

	Thread actionThread = null;
	
	public int takeControl() {
		if(light.getLightValue() > 50)
			return 250;
		if(actionThread != null)
			return 50;
		return 0;
	}

	public void action() {
		try {
			actionThread = Thread.currentThread();
			Sumo.leftMotor.backward();
			Sumo.rightMotor.backward();
			Thread.sleep(1000);
			
			Sumo.leftMotor.stop();
			Sumo.rightMotor.stop();
			
			Sumo.leftMotor.rotate(-600);
			

		} catch (InterruptedException e) {
			Sound.beep();
		}
		BumperCar.leftMotor.stop();
		BumperCar.rightMotor.stop();
		actionThread = null;
		return;
	}
		
	

	public void suppress() {
		if(actionThread != null)
		{
			actionThread.interrupt();
		}
	}
	
}

class Attack implements Behavior
{
	TouchSensor touch1 = new TouchSensor(SensorPort.S1);
	TouchSensor touch2 = new TouchSensor(SensorPort.S2);

	Thread actionThread = null;
	
	UltrasonicSensor sonar = new UltrasonicSensor(SensorPort.S3);	
	private volatile int distance = 255;

	
	public Attack()
	{new Thread() {
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
	
	public int takeControl() {
		if(touch1.isPressed() || touch2.isPressed() || distance < 20)
			return 200;
		return 0;
	}

	public void action() {
		try {
			actionThread = Thread.currentThread();
			Sound.beepSequence();
			Sumo.leftMotor.setSpeed(2000);
			Sumo.rightMotor.setSpeed(2000);
			Sumo.leftMotor.forward();
			Sumo.rightMotor.forward();
			Thread.sleep(10000000);
		} catch (InterruptedException e) {
			Sound.beep();
		}
		BumperCar.leftMotor.stop();
		BumperCar.rightMotor.stop();
		Sumo.leftMotor.setSpeed(600);
		Sumo.rightMotor.setSpeed(600);
		actionThread = null;
		return;
	}
		
	

	public void suppress() {
		if(actionThread != null)
		{
			actionThread.interrupt();
		}
	}
	
}


class SumoExit implements Behavior {

	public SumoExit() {

	}

	public int takeControl() {
		if(Button.ESCAPE.isPressed())
			return 300;
		return 0;
	}

	public void suppress() {
		// Since this is highest priority behavior, suppress will never be
		// called.
	}

	public void action() {
		System.exit(0);
	}
}