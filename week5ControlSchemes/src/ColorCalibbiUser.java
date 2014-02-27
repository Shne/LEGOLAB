import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;

public class ColorCalibbiUser {
	public static void main(String[] args) throws Throwable {
		LCD.drawString("RDY", 0, 0);
		Button.ENTER.waitForPress();
		BlackWhiteGreenSensor s = new BlackWhiteGreenSensor(SensorPort.S1);
		s.calibrate();
		while (!Button.ESCAPE.isPressed()) {
			LCD.clear();
			if (s.black()) {
				LCD.drawString("BLACK", 0, 0);
			} else if (s.white()) {
				LCD.drawString("WHITE", 0, 0);
			} else {
				LCD.drawString("GREEN", 0, 0);
			}

			Thread.sleep(100);
		}

	}
}