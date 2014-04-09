import lejos.nxt.*;
import static java.lang.Math.*;
/**
 * A simple line follower for the LEGO 9797 car with
 * a light sensor. Before the car is started on a line
 * a BlackWhiteSensor is calibrated to adapt to different
 * light conditions and colors.
 * 
 * The light sensor should be connected to port 3. The
 * left motor should be connected to port C and the right 
 * motor to port B.
 * 
 * @author  Ole Caprani
 * @version 20.02.13
 */
public class LineFollowerCal
{
  public static void main (String[] aArg)
  throws Exception
  {  
     BlackWhiteSensor sensor = new BlackWhiteSensor(SensorPort.S1);
	 
     sensor.calibrate();
	 Thread.sleep(2000);
	 Button.ENTER.waitForPress();
     LCD.clear();
     LCD.drawString("Light: ", 0, 2); 
     int delay = 5;
     int Kp = 20000;
     int Ki = 30*delay;
     int Kd = 800000/delay;
     int offset = sensor.calibbi();
     int tp = 75;
     int integral = 0;
     int lastError = 0;
     int derivative = 0;
     while (! Button.ESCAPE.isDown())
     {
	     LCD.drawInt(sensor.light(),4,10,2);
	     LCD.refresh();
	     int lv = sensor.light();
	     int error = lv - offset;
	     integral += error;
	     if(signum(error)!=signum(lastError))
	    	 integral = 0;
	     derivative = error-lastError;
	     int turn = Kp*error + Ki*integral + Kd*derivative;
	     turn = turn/10000;
	     int powerB = tp + turn;
	     int powerC = tp - turn;
	     LCD.drawInt(powerB,4,10,1);
	     LCD.drawInt(powerC,4,10,0);
	     lastError = error;
	     Car.forward(powerC, powerB);
	     Thread.sleep(delay);
     }
     
     Car.stop();
     LCD.clear();
     LCD.drawString("Program stopped", 0, 0);
     LCD.refresh();
   }
}