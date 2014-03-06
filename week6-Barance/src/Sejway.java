import static java.lang.Math.signum;

import java.io.DataInputStream;

import lejos.nxt.*;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import lejos.robotics.Color;

/**
 * A controller for a self-balancing Lego robot with a light sensor on port 2.
 * The two motors should be connected to port B and C.
 * 
 * Building instructions in Brian Bagnall: Maximum Lego NXTBuilding Robots with
 * Java Brains</a>, Chapter 11, 243 - 284
 * 
 * @author Brian Bagnall
 * @version 26-2-13 by Ole Caprani for leJOS version 0.9.1
 */

public class Sejway {

	// PID constants
	volatile int KP = 600; // 600
	volatile int KI = 80; // 80
	volatile int KD = 1500; // 600
	volatile int SCALE = 100;
	volatile float AJUST = 2.5F;

	// Global vars:
	int offset;
	int prev_error;
	float int_error;

	ColorSensor ls;

	public Sejway() {
		ls = new ColorSensor(SensorPort.S2);
		ls.setFloodlight(Color.WHITE);
	}

	public void getBalancePos() {
		LCD.drawString("BARANCU!", 0, 0);
		// Wait for user to balance and press orange button
		while (!Button.ENTER.isDown()) {
			// NXTway must be balanced.
			offset = ls.getRawLightValue();
			LCD.clear();
			LCD.drawInt(offset, 2, 4);
			LCD.refresh();
		}
	}

	public void pidControl() throws InterruptedException {
		while (!Button.ESCAPE.isDown()) {
			int normVal = ls.getRawLightValue();

			// Proportional Error:
			int error = normVal - offset;
			// Adjust far and near light readings:
			if (error < 0)
				error = (int) (error * AJUST);

			// Integral Error:
			int_error = ((int_error + error) * 2) / 3;

			if (signum(error) != signum(prev_error))
				int_error = 0;

			// Derivative Error:
			int deriv_error = error - prev_error;
			prev_error = error;

			int pid_val = (int) (KP * error + KI * int_error + KD * deriv_error)
					/ SCALE;

			if (pid_val > 100)
				pid_val = 100;
			if (pid_val < -100)
				pid_val = -100;

			// Power derived from PID value:
			int power = Math.abs(pid_val);
			power = 55 + (power * 45) / 100; // NORMALIZE POWER
			LCD.clear();
			LCD.drawInt(power, 0, 0);
			LCD.drawInt(pid_val, 0, 1);
			LCD.drawInt(normVal, 0, 2);

			if (pid_val > 0) {
				MotorPort.B.controlMotor(power, BasicMotorPort.FORWARD);
				MotorPort.A.controlMotor(power, BasicMotorPort.FORWARD);
			} else {
				MotorPort.B.controlMotor(power, BasicMotorPort.BACKWARD);
				MotorPort.A.controlMotor(power, BasicMotorPort.BACKWARD);
			}
			Thread.sleep(10);
		}
	}

	public void shutDown() {
		// Shut down light sensor, motors
		Motor.B.flt();
		Motor.C.flt();
		ls.setFloodlight(false);
	}

	public void listen() {
		new Thread() {
			public void run() {
				try {
					int counter = 0;
					BTConnection con = Bluetooth.waitForConnection();
					DataInputStream dis = con.openDataInputStream();
					while (true) {
						KP = dis.readInt();
						KI = dis.readInt();
						KD = dis.readInt();
						SCALE = dis.readInt();
						AJUST = dis.readFloat();

						prev_error = 0;
						int_error = 0.0f;
						MotorPort.A.controlMotor(0, BasicMotorPort.FLOAT);
						MotorPort.B.controlMotor(0, BasicMotorPort.FLOAT);
						LCD.drawInt(counter++, 0, 0);
						LCD.drawInt(KP, 0, 1);
						LCD.drawInt(KI, 0, 2);
						LCD.drawInt(KD, 0, 3);
						LCD.drawInt(SCALE, 0, 4);

						Thread.sleep(100);
					}

				} catch (Throwable t) {
				}
			}

			{
				setDaemon(true);
			}
		}.start();
	}

	public static void main(String[] args) throws Throwable {
		Sejway sej = new Sejway();
		sej.listen();
		sej.getBalancePos();
		sej.pidControl();
		sej.shutDown();
	}
}