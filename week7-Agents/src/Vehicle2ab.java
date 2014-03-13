import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.LightSensor;
import lejos.nxt.SensorPort;
import lejos.nxt.*;


public class Vehicle2ab {
	static LightSensor l1 = new LightSensor(SensorPort.S4);
	static LightSensor l2 = new LightSensor(SensorPort.S1);
	static UltrasonicSensor us1 = new UltrasonicSensor(SensorPort.S3);
	static UltrasonicSensor us2 = new UltrasonicSensor(SensorPort.S2);
	
	
	static RunningRangeNormalizer rrnorm1 = new RunningRangeNormalizer();
	static RunningRangeNormalizer rrnorm2 = new RunningRangeNormalizer();
	static RunningRangeNormalizer rrnorm3 = new RunningRangeNormalizer();
	static RunningRangeNormalizer rrnorm4 = new RunningRangeNormalizer();
	static MinpowerNormalizer mpnorm = new MinpowerNormalizer(){{low = 40.0f; high= 100.0f;}};
	static AlphaNormalizer anorm1 = new AlphaNormalizer(){{alpha = 0.1f;}};
	static AlphaNormalizer anorm2 = new AlphaNormalizer(){{alpha = 0.1f;}};
	static AlphaNormalizer anorm3 = new AlphaNormalizer(){{alpha = 0.1f;}};
	static AlphaNormalizer anorm4 = new AlphaNormalizer(){{alpha = 0.1f;}};
	
	public static void main(String[] args) throws Throwable
	{
		Sound.beepSequence();
		while(!Button.ESCAPE.isDown())
		{
			LCD.clear();
			float f1 = l1.readValue();
			float f2 = l2.readValue();
			f1 = rrnorm1.handle(f1);
			f2 = rrnorm2.handle(f2);
			f1 = anorm1.handle(f1);
			f2 = anorm2.handle(f2);
			
			float f3 = us1.getRange();
			float f4 = us2.getRange();
			
			f3 = rrnorm3.handle(f3);
			f4 = rrnorm4.handle(f4);
			f3 = anorm3.handle(f3);
			f4 = anorm4.handle(f4);
			
			int lp = (int)mpnorm.handle((f1+f3)/2.0f);
			int rp = (int)mpnorm.handle((f2+f4)/2.0f);
			Car.forward(lp, rp);
			
			Thread.sleep(10);
			
		}
	}
}
