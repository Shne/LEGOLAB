import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import static java.lang.Math.*;

public class DubCar {
	static NXTRegulatedMotor m1 = new NXTRegulatedMotor(MotorPort.A), m2 = new NXTRegulatedMotor(
			MotorPort.C);
	public static void main(String[] aArg) throws Exception {
		DifferentialPilot2 p = new DifferentialPilot2(8.2, 11.5,
				new NXTRegulatedMotor(MotorPort.C), new NXTRegulatedMotor(
						MotorPort.A));
		p.setTravelSpeed(20);
		Button.ENTER.waitForPress();
		new Thread() {
			public void run() {
				try {
					LineFollowerCal.Go();

				} catch (Throwable t) {

				}
				;
			}

			{
				setDaemon(true);
			}
		}.start();
		
		p.travel(5);
		p.travelArc(-18.5, 0.01 * PI);
		p.die();
		Linetrack(4000);
		p.travel(10);
		p.travelArc(-18.5, 19.5 * PI);
		p.die();
		Linetrack(m1.getTachoCount()+ m2.getTachoCount()+3800);

	}

	public static void Linetrack(int tacho) {
		LineFollowerCal.run = true;
		while (!Button.ESCAPE.isDown()) {
			int count = m1.getTachoCount() + m2.getTachoCount();
			LCD.drawInt(count % 100, 0, 0);
			if (count > tacho) {
				LineFollowerCal.run = false;
				m1.resetTachoCount();
				m2.resetTachoCount();
				return;
			}
		}
	}

}
