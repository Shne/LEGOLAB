import lejos.nxt.*;
/**
 * A simple sonar sensor test program.
 * 
 * The sensor should be connected to port 1. In the
 * known bugs and limitations of leJOS NXJ version alfa_03
 * it is mentioned that a gap of at least 300 msec is 
 * needed between calls of getDistance. This is the reason 
 * for the delay of 300 msec between sonar readings in the 
 * loop.
 * 
 * @author  Ole Caprani
 * @version 30.08.07
 */
public class SoundSensorTest 
{

   public static void main(String [] args)  
   throws Exception 
   {
	   
       SoundSensor us = new SoundSensor(SensorPort.S1);
       SoundSensor us2 = new SoundSensor(SensorPort.S2);

       LCD.drawString("DB ", 0, 0);
	   int i  = 0;
       while (! Button.ESCAPE.isDown())
       {
           LCD.drawInt(us.readValue(),3,13,0);
           LCD.drawInt(us2.readValue(),3,13,1);
           

           Thread.sleep(125);
       }
       LCD.clear();
       LCD.drawString("Program stopped", 0, 0);
       Thread.sleep(2000);
   }
}