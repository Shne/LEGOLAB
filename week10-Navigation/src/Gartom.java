import java.io.IOException;
import java.util.ArrayList;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.geom.Rectangle;
import lejos.nxt.*;
import lejos.robotics.localization.OdometryPoseProvider;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.*;
import lejos.robotics.objectdetection.*;
import lejos.robotics.pathfinding.*;

public class Gartom implements FeatureListener {
	static NXTRegulatedMotor m1 = new NXTRegulatedMotor(MotorPort.A),
			m2 = new NXTRegulatedMotor(MotorPort.C);
	static final DifferentialPilot pilot = new DifferentialPilot(5.77, 12.2,
			m2, m1);
	static final OdometryPoseProvider PP = new OdometryPoseProvider(pilot);
	static final Navigator n = new Navigator(pilot, PP);
	static final TouchFeatureDetector tfd = new TouchFeatureDetector(
			new TouchSensor(SensorPort.S4), 0, 12.0);
	static final ArrayList<Line> lines = new ArrayList<Line>();

	static final ShortestPathFinder path = new ShortestPathFinder(new LineMap(new Line[0], new Rectangle(-1000, -1000, 2000, 2000)));

	public static void main(String[] args) {
	 new Gartom().go();
	}
		private void go()
		{
		try {
			Button.ENTER.waitForPress();
			pilot.setTravelSpeed(20.0);
			pilot.setRotateSpeed(100.0);
			pilot.setAcceleration(50);

			n.addWaypoint(100, 0);
			n.followPath();
			tfd.enableDetection(true);
			tfd.addListener(this);

			Button.ESCAPE.waitForPress();
			try {
				new LineMap(((Line[]) path.getMap().toArray(new Line[path.getMap().size()])), new Rectangle(
						-500, -500, 1000, 1000)).createSVGFile("map.svg");
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
				Button.ESCAPE.waitForPress();
			}
			
			;
		} catch (Throwable t) {
			Sound.beepSequenceUp();
			t.printStackTrace();
		}
		
	}

	public void featureDetected(Feature feature, FeatureDetector detector) {
		pilot.stop();
		Sound.beep();
		n.stop();
		n.clearPath();

		Point p = PP.getPose().getLocation();
		pilot.travel(-25);

		Line l1 = new Line(p.x - 25, p.y, p.x + 25, p.y);
		Line l2 = new Line(p.x, p.y - 25, p.x, p.y + 25);

		lines.add(l1);
		lines.add(l2);

		path.setMap(lines);
		
		try {
			Path np = path.findRoute(PP.getPose(), new Waypoint(100, 0));
			n.setPath(np);
			n.followPath();
		} catch (DestinationUnreachableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

}
