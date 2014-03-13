import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.*;


public class Vehicle2ab {
	static UltrasonicSensor l1 = new UltrasonicSensor(SensorPort.S1);
	static UltrasonicSensor l2 = new UltrasonicSensor(SensorPort.S4);
	
	static RunningRangeNormalizer rrnorm1 = new RunningRangeNormalizer();
	static RunningRangeNormalizer rrnorm2 = new RunningRangeNormalizer();
	static MinpowerNormalizer mpnorm = new MinpowerNormalizer(){{low = 100.0f; high= 0.0f;}};
	static AlphaNormalizer anorm1 = new AlphaNormalizer(){{alpha = 0.1f;}};
	static AlphaNormalizer anorm2 = new AlphaNormalizer(){{alpha = 0.1f;}};
	
	public static void main(String[] args) throws Throwable
	{
		Sound.beepSequence();
		while(!Button.ESCAPE.isDown())
		{
			LCD.clear();
			float f1 = l1.getRange();
			float f2 = l2.getRange();
			f1 = rrnorm1.handle(f1);
			f2 = rrnorm2.handle(f2);
			f1 = anorm1.handle(f1);
			f2 = anorm2.handle(f2);
			int lp = (int)mpnorm.handle(f1);
			int rp = (int)mpnorm.handle(f2);
			Car.forward(lp, rp);
			
			Thread.sleep(10);
			
		}
	}
}
