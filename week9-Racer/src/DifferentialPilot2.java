import lejos.robotics.RegulatedMotor;
import lejos.robotics.navigation.DifferentialPilot;


public class DifferentialPilot2 extends DifferentialPilot {

	public DifferentialPilot2(double wheelDiameter, double trackWidth,
			RegulatedMotor leftMotor, RegulatedMotor rightMotor) {
		super(wheelDiameter, trackWidth, leftMotor, rightMotor);
		// TODO Auto-generated constructor stub
	}

	public void die()
	{
		_right.flt();
		_left.flt();
		
		//finalize();
	}
}
