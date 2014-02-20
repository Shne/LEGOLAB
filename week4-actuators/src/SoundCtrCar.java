import lejos.nxt.*;
/**
 * The locomotions of a  LEGO 9797 car is controlled by
 * sound detected through a microphone on port 1. 
 * 
 * @author  Ole Caprani
 * @version 23.08.07
 */
public class SoundCtrCar 
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
        
        while (! Button.ESCAPE.isDown())
        {
        	waitForClap();		    			   
            LCD.drawString("Forward ",0,1);
            Car.forward(100, 100);
		    
            waitForClap();		    			   
            LCD.drawString("Right   ",0,1);
            Car.forward(100, 0);
		    
            waitForClap();		    			   
            LCD.drawString("Left    ",0,1);
            Car.forward(0, 100);
		    
            waitForClap();		    			   
            LCD.drawString("Stop    ",0,1); 
            Car.stop();
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