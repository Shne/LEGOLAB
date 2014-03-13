import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.SensorPort;
import lejos.nxt.Sound;
import lejos.nxt.SoundSensor;


public class Vehicle1 {
	static SoundSensor sound = new SoundSensor(SensorPort.S1);
	static RunningRangeNormalizer rrnorm = new ForgettingRunningRangeNormalizer(){{ffactor = 0.001f;}};
	static MinpowerNormalizer mpnorm = new MinpowerNormalizer(){{low = -80.0f; high= 100.0f;}};
	static DoubleAlphaNormalizer danorm = new DoubleAlphaNormalizer(0.1f, 0.01f);
	
	public static void main(String[] args) throws Throwable
	{
		Sound.beepSequence();
		while(!Button.ESCAPE.isDown())
		{
			LCD.clear();
			float f1 = sound.readValue();
			danorm.set(f1);
			LCD.drawString(String.valueOf(danorm.get()), 0, 0);
			rrnorm.set(danorm.get());
			LCD.drawString(String.valueOf(rrnorm.get()), 0, 1);
			mpnorm.set(rrnorm.get());
			LCD.drawString(String.valueOf(mpnorm.get()), 0, 2);
			int pow = (int)mpnorm.get();
			LCD.drawInt(pow, 0, 3);
			if(pow>0)
				Car.forward(pow,pow);
			else{
				Car.backward(pow*-1, pow*-1);
			}
			Thread.sleep(10);
			
		}
	}
}
