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
     final int power = 80;
	  
     BlackWhiteSensor sensor = new BlackWhiteSensor(SensorPort.S1);
	 
     sensor.calibrate();
	 Thread.sleep(2000);
	 Button.ENTER.waitForPress();
     LCD.clear();
     LCD.drawString("Light: ", 0, 2); 
	 /*
	  * Kp = 1000                             ! REMEMBER we are using Kp*100 so this is really 10 !
	Ki = 100                              ! REMEMBER we are using Ki*100 so this is really 1 !
Kd = 10000                            ! REMEMBER we are using Kd*100 so this is really 100!
offset = 45                           ! Initialize the variables
Tp = 50 
integral = 0                          ! the place where we will store our integral
lastError = 0                         ! the place where we will store the last error value
derivative = 0                        ! the place where we will store the derivative
Loop forever
   LightValue = read light sensor     ! what is the current light reading?
   error = LightValue - offset        ! calculate the error by subtracting the offset
   integral = integral + error        ! calculate the integral 
   derivative = error - lastError     ! calculate the derivative
   Turn = Kp*error + Ki*integral + Kd*derivative  ! the "P term" the "I term" and the "D term"
 Turn = Turn/100                      ! REMEMBER to undo the affect of the factor of 100 in Kp, Ki and Kd!
   powerA = Tp + Turn                 ! the power level for the A motor
   powerC = Tp - Turn                 ! the power level for the C motor
   MOTOR A direction=forward power=PowerA   ! actually issue the command in a MOTOR block
   MOTOR C direction=forward power=PowerC   ! same for the other motor but using the other power level
   lastError = error                  ! save the current error so it can be the lastError next time around
end loop forever                      ! done with loop, go back and do it again.
	  */
     int delay = 5;
     int Kp = 5000;
     int Ki = 50*delay;
     int Kd = 200000/delay;
     int offset = sensor.calibbi();
     int tp = 85;
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
	     turn = turn/1000;
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