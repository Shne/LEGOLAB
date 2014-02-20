import lejos.nxt.Button;
import lejos.nxt.ButtonListener;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.SoundSensor;
import lejos.nxt.TachoMotorPort;
import lejos.robotics.navigation.DifferentialPilot;
/**
 * The locomotions of a  LEGO 9797 car is controlled by
 * sound detected through a microphone on port 1. 
 * 
 * @author  Ole Caprani
 * @version 23.08.07
 */
public class Rotation
{
    private static int soundThreshold = 85;
    private static int soundThresholdLow = 50;
    private static SoundSensor sound = new SoundSensor(SensorPort.S1);
	
    private static  void waitForLoudSound() throws Exception
    {
        int soundLevel;

        Thread.sleep(50);

        do
        {
            soundLevel = sound.readValue();
            LCD.drawInt(soundLevel,4,10,0); 
        }
        while ( soundLevel < soundThreshold );
    }
 
    private static  void waitForNoLoudSound() throws Exception
    {
        int soundLevel;

        Thread.sleep(50);
     
        do
        {
            soundLevel = sound.readValue();
            LCD.drawInt(soundLevel,4,10,0); 
        }
        while ( soundLevel > soundThresholdLow );
    }
    
    private static  void waitForClap() throws Exception
    {
 

        Thread.sleep(500);
        long time;
        do
        {
        	
        	waitForNoLoudSound();
        	waitForLoudSound();
        	time = System.currentTimeMillis();
        	waitForNoLoudSound();
        	long dt = System.currentTimeMillis()-time;
        	LCD.drawInt((int)dt,5,10,2); 
            if(dt <= 200)
            {
            	break;
            }
        }
        while (true);
    }
    
 
    
    public static void main(String [] args) throws Throwable
    {
        LCD.drawString("dB level: ",0,0);
        LCD.refresh();
	   	
        Button.ESCAPE.addButtonListener(new ButtonListener() {
			
			public void buttonReleased(Button b) {
				try{
				exit();
				} catch(Throwable t) {
					
				}
				
			}
			
			public void buttonPressed(Button b) {
				// TODO Auto-generated method stub
				
			}
		});
        DifferentialPilot pilot = new DifferentialPilot(5.5, 12.0,new  NXTRegulatedMotor(MotorPort.B), new  NXTRegulatedMotor(MotorPort.C));
        pilot.setRotateSpeed(50);
        pilot.setTravelSpeed(30);
        float tach = 0.0f;
        int best = 0; 
        while (! Button.ESCAPE.isDown())
        {
        	pilot.rotate(360, true);
        	while(pilot.isMoving())
        	{
        		int read = sound.readValue();
        	 if(read > best)
        	 {
        		 tach = pilot.getMovement().getAngleTurned();
        		 best = read;
        	 }
        	 LCD.drawInt(read,3,7,0);
        	 LCD.drawInt(best,3,7,1);
        	 LCD.drawInt((int)pilot.getMovement().getAngleTurned(),3,7,2);
        	 Thread.sleep(30);
        	}
        	pilot.stop();
        	best = 0;
        	pilot.rotate(tach);
        	pilot.stop();
        	pilot.forward();
        	Thread.sleep(300);
        	pilot.stop();
        }
        exit();
   }
    
   public static void exit() throws Throwable
   {
       Car.stop();
       LCD.clear();
       LCD.drawString("Program stopped", 0, 0);
       Thread.sleep(2000);
       System.exit(0);
   }
   
}