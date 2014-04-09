import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;
import static java.lang.Math.*;

public class DubCar {
	static NXTRegulatedMotor m1 = new NXTRegulatedMotor(MotorPort.A),
			m2 = new NXTRegulatedMotor(MotorPort.C);

	public static void main(String[] aArg) throws Throwable {
		DifferentialPilot2 p = new DifferentialPilot2(8.2, 11.5, m2, m1);
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

		// p.travel(5);
		// p.travelArc(-18.5, 0.01 * PI);
		// p.die();
		Linetrack(4000);
		
		p.reset();
		p.travel(10);
		p.travelArc(-18.5, 19.2 * PI);
		p.die();
		//m1.resetTachoCount();
		//m2.resetTachoCount();
		//p.reset();
		Linetrack(3800);

	}

	public static void Linetrack(int tacho) throws Throwable {
		
		LineFollowerCal.run = true;

		while (!Button.ESCAPE.isDown()) {
			int count = m1.getTachoCount() + m2.getTachoCount();
			Thread.sleep(5);
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
