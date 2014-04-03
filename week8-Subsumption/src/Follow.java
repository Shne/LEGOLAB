
import lejos.nxt.*;
import lejos.util.Delay;
/*
 * Follow behavior , inspired by p. 304 in
 * Jones, Flynn, and Seiger: 
 * "Mobile Robots, Inspiration to Implementation", 
 * Second Edition, 1999.
 */

class Follow extends Thread
{
    private SharedCar car = new SharedCar();

	private int power = 60, ms = 100;
	LightSensor light = new LightSensor(SensorPort.S4);
	NXTRegulatedMotor motor = new NXTRegulatedMotor(MotorPort.A);
	
	int frontLight, leftLight, rightLight, delta;
	int lightThreshold;
	
    public Follow(SharedCar car)
    {
       this.car = car;	
       lightThreshold = light.getLightValue();
    }
    
	public void run() 
    {				       
        while (true)
        {
	    	// Monitor the light in front of the car and start to follow
	    	// the light if light level is above the threshold
        	frontLight = light.getLightValue();
	    	while ( frontLight <= lightThreshold )
	    	{
	    		car.noCommand();
	    		frontLight = light.getLightValue();
	    	}
	    	
	    	// Follow light as long as the light level is above the threshold
	    	while ( frontLight > lightThreshold )
	    	{
	    		// Get the light to the left
	    		motor.rotateTo(20);
	    		leftLight = light.getLightValue();
	    		
	    		// Get the light to the right
	    		motor.rotateTo(-20);
	    		rightLight = light.getLightValue();
	    		
	    		// Turn back to start position
	    		motor.rotateTo(0);
	    		Delay.msDelay(ms);
	    		
	    		motor.stop();
	    	
	    		// Follow light for a while
	    		delta = leftLight-rightLight;
	    		car.forward(power+delta, power-delta);
	    		Delay.msDelay(ms);
    		
	    		frontLight = light.getLightValue();
	    	}
	    	
	    	car.stop();
	    	Delay.msDelay(ms);
 			
        }
    }
}

