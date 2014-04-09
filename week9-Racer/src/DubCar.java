import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import static java.lang.Math.*;

public class DubCar {
	static NXTRegulatedMotor m1 = new NXTRegulatedMotor(MotorPort.A),
			m2 = new NXTRegulatedMotor(MotorPort.C);

	public static void main(String[] aArg) throws Throwable {
		DifferentialPilot2 p = new DifferentialPilot2(8.2, 11.5, m2, m1);
		p.setAcceleration(60);
		p.setTravelSpeed(60);
		p.setRotateSpeed(200);
		Button.ENTER.waitForPress();
		new Thread() {
			public void run() {
				try {
					LineFollowerCal.tp = 85;
					LineFollowerCal.Go();

				} catch (Throwable t) {

				}
				;
			}

			{
				setDaemon(true);
			}
		}.start();
		
		//move onto the line
		p.travel(10);
		p.die();
		
		//first incline
		Linetrack(3850);
		
		//first platform
		p.reset();
		p.travelArc(-18.5, 19.2 * PI);
		p.die();
		
		//second incline
		Linetrack(3750);
		
		//second platform
		p.reset();
		p.travelArc(18.5, 19.2 * PI);
		p.die();
		
		//third incline
		Linetrack(3700);
		
		//top platform
		p.reset();
		p.travel(35);
		p.rotate(185);
		p.travel(30);
		p.die();
		
		//3rd incline down
		Linetrack(3350);
		
		//cheatsy-doodles
		//p.reset();
		//p.rotate(-92);
		//p.travel(80);
		//p.die();
		
		//2nd platform down
		p.reset();
		p.travelArc(-18.5, 19.2 * PI);
		p.die();
		
		//2nd incline down
		Linetrack(3700);
		
		//1st platform down
		p.reset();
		p.travelArc(18.5, 19.0 * PI);
		p.die();
		
		//1st incline down
		Linetrack(4000);
	}

	public static void Linetrack(int tacho) throws Throwable {
		tacho += m1.getTachoCount() + m2.getTachoCount();
		LineFollowerCal.run = true;

		while (!Button.ESCAPE.isDown()) {
			int count = m1.getTachoCount() + m2.getTachoCount();
			Thread.sleep(5);
			LCD.drawInt(count % 100, 0, 0);
			if (count > tacho) {
				LineFollowerCal.run = false;
				m1.resetTachoCount();
				m2.resetTachoCount();
				m1.flt();
				m2.flt();
				Thread.sleep(100);
				return;
			}
		}
	}

}
