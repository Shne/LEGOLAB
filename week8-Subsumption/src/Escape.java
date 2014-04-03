import lejos.nxt.*;
import lejos.util.Delay;

/*
 * Avoid behavior
 */

class Escape extends Thread {
	private SharedCar car = new SharedCar();

	private int power = 70, ms = 500;
	TouchSensor tLeft = new TouchSensor(SensorPort.S2),
			tRight = new TouchSensor(SensorPort.S3);

	boolean left, right;

	public Escape(SharedCar car) {
		this.car = car;
	}

	public void run() {
		while (true) {
			left = tLeft.isPressed();
			right = tRight.isPressed();
			if (left & right) {
				car.backward(power, power);
				Delay.msDelay(ms);
				car.backward(power, 0);
				Delay.msDelay(ms);
				Delay.msDelay(ms);
			} else if (left) {
				car.backward(0, power);
				Delay.msDelay(ms);
				Delay.msDelay(ms);
			} else if (right) {
				car.backward(0, power);
				Delay.msDelay(ms);
				Delay.msDelay(ms);
			} else {
				car.noCommand();
				Delay.msDelay(ms);
				Delay.msDelay(ms);
			}
		}
	}
}
