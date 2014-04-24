import lejos.nxt.*;
import lejos.robotics.navigation.*;
import lejos.robotics.pathfinding.*;

public class Gartom {
	static NXTRegulatedMotor m1 = new NXTRegulatedMotor(MotorPort.A),
			m2 = new NXTRegulatedMotor(MotorPort.C);
	
	public static void main(String[] args)
	{	
		//Button.ENTER.waitForPress();
		DifferentialPilot pilot = new DifferentialPilot(5.77, 12.2, m2, m1);
		pilot.setTravelSpeed(20.0);
		pilot.setRotateSpeed(100.0);
		pilot.setAcceleration(50);
		final Navigator n = new Navigator(pilot);
		Path path = new Path();
		path.add(new Waypoint(0,0));
		path.add(new Waypoint(200/4,0));
		path.add(new Waypoint(100/4,100/4));
		path.add(new Waypoint(100/4,-50/4));
		path.add(new Waypoint(0,0,0.0));
		n.followPath(path);
		while(!n.waitForStop());
	}

}
