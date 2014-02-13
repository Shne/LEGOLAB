import lejos.nxt.Button;
import lejos.nxt.LCD;
import lejos.nxt.Motor;
import lejos.nxt.MotorPort;
import lejos.nxt.NXTRegulatedMotor;
import lejos.nxt.SensorPort;
import lejos.nxt.UltrasonicSensor;


public class WallFollower {
	private static NXTRegulatedMotor leftMotor =  Motor.C;
    private static NXTRegulatedMotor rightMotor= Motor.B;
    private final static int forward  = 1,
            backward = 2,
            stop     = 3;
	public static void main(String[] args) throws InterruptedException
	{
		
		 int distance, desire = 35, error, dprev = 0, dx;
		 lejos.nxt.UltrasonicSensor us = new UltrasonicSensor(SensorPort.S1);
		 LCD.drawString("BIPBUP: ", 0, 1);
		 dprev = us.getDistance();
		 
		 while(true)
		 {
			 //rightMotor.forward();
			 //leftMotor.forward();
			 distance = us.getDistance();
			 error = (desire - distance)/2;
			 dx = distance-dprev;
			 dprev = distance;
			 /*if (distance<=desire) // Close to the wall
			    {
			      if (distance<=desire-15)
			      {
			          rightMotor.backward(); // Very close, turn in place
			      }
			      else if (distance<=desire-7) 
			      {
			    	  rightMotor.stop(); // close enough, turn 
			      }
			      else if (distance<=desire)
			      {
			    	  rightMotor.flt(); // a bit too close, shallow turn
			      }
			      
			    }
			    else // far from the wall
			    {
			      if (distance>=desire+15) // far from wall,
			      {
			        if (dx<5) // if small variation turn (avoids turning too short on salient angles)
			            leftMotor.stop();
			        }
			      else if (distance>=desire)
			      {
			         leftMotor.flt(); // a bit too far, shallow turn

			      }
			      
			    }*/
			 
			 Car.forward(80+error, 80-error);
			 Thread.sleep(30);
		 }
	}
}
