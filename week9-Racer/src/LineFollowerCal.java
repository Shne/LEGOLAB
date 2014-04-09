import java.io.DataInputStream;

import lejos.nxt.*;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;
import static java.lang.Math.*;

/**
 * A simple line follower for the LEGO 9797 car with a light sensor. Before the
 * car is started on a line a BlackWhiteSensor is calibrated to adapt to
 * different light conditions and colors.
 * 
 * The light sensor should be connected to port 3. The left motor should be
 * connected to port C and the right motor to port B.
 * 
 * @author Ole Caprani
 * @version 20.02.13
 */
public class LineFollowerCal {

	static int delay = 5;
	static int Kp = 20000;
	static int Ki = 30 * delay;
	static int Kd = 700000 / delay;
	static int offset = 0; 
	static int tp = 75;
	static int integral = 0;
	static int lastError = 0;
	static int derivative = 0;
	public static boolean run = true;

	public static void Go() throws Exception {
		listen();
		LightSensor s1 = new LightSensor(SensorPort.S1), s2 = new LightSensor(
				SensorPort.S4);
		// BlackWhiteSensor sensor = new BlackWhiteSensor(SensorPort.S1);

		//sensor.calibrate();
		Thread.sleep(2000);
		
		LCD.clear();
		//offset = sensor.calibbi();
		while (!Button.ESCAPE.isDown()) {
			if(!run)
			{
				Thread.sleep(50);
				integral = 0;
				continue;
			}
			int error = s1.readValue() - s2.readValue();
			//int error = sensor.light() -  offset;
			LCD.drawInt(error, 4, 10, 2);
			LCD.refresh();
			integral += error;
			if (signum(error) != signum(lastError))
				integral = 0;
			derivative = error - lastError;
			int turn = Kp * error + Ki * integral + Kd * derivative;
			turn = turn / 10000;
			int powerB = tp + turn;
			int powerC = tp - turn;
			
			lastError = error;
			Car.forward(powerC, powerB);
			Thread.sleep(delay);
		}

	}

	public static void listen() {
		new Thread() {
			public void run() {
				try {
					int counter = 0;
					BTConnection con = Bluetooth.waitForConnection();
					DataInputStream dis = con.openDataInputStream();
					while (true) {
						Kp = dis.readInt();
						Ki = dis.readInt();
						Kd = dis.readInt();
						tp = dis.readInt();

						lastError = 0;
						integral = 0;
						MotorPort.A.controlMotor(0, BasicMotorPort.FLOAT);
						MotorPort.B.controlMotor(0, BasicMotorPort.FLOAT);
						LCD.drawInt(counter++, 0, 0);
						LCD.drawInt(Kp, 0, 1);
						LCD.drawInt(Ki, 0, 2);
						LCD.drawInt(Kd, 0, 3);
						LCD.drawInt(tp, 0, 4);

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
}