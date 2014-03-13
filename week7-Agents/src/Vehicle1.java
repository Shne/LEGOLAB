import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.SoundSensor;


public class Vehicle1 {
	static SoundSensor sound = new SoundSensor(SensorPort.S1);
	static RunningRangeNormalizer rrnorm = new RunningRangeNormalizer();
	static MinpowerNormalizer mpnorm = new MinpowerNormalizer(){{low = 0.0f; high= 100.0f;}};
	static AlphaNormalizer anorm = new AlphaNormalizer() {{alpha = 0.2f;}};
	
	public static void main(String[] args) throws Throwable
	{
		Sound.beepSequence();
		while(!Button.ESCAPE.isDown())
		{
			LCD.clear();
			float f1 = sound.readValue();
			rrnorm.set(f1);
			LCD.drawString(String.valueOf(rrnorm.get()), 0, 0);
			anorm.set(rrnorm.get());
			LCD.drawString(String.valueOf(anorm.get()), 0, 1);
			mpnorm.set(anorm.get());
			LCD.drawString(String.valueOf(mpnorm.get()), 0, 2);
			int pow = (int)mpnorm.get();
			LCD.drawInt(pow, 0, 3);
			Car.forward(pow,pow);
			Thread.sleep(10);
			
		}
	}
}
