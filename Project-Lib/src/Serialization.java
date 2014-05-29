import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.Map;

import lejos.geom.Line;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;

public class Serialization {
	public static byte[] SerializeLinePose(Line l, Pose p) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(28);
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeFloat(l.x1);
			dos.writeFloat(l.y1);
			dos.writeFloat(l.x2);
			dos.writeFloat(l.y2);
			dos.writeFloat(p.getX());
			dos.writeFloat(p.getY());
			dos.writeFloat(p.getHeading());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return baos.toByteArray();
	}
	
	public static Pair<Line, Pose> DeSerializeLinePose(byte[] bytes) {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		DataInputStream dos = new DataInputStream(bais);
		try {
			Line res = new Line(dos.readFloat(),dos.readFloat(),dos.readFloat(),dos.readFloat());
			Pose p = new Pose(dos.readFloat(), dos.readFloat(), dos.readFloat());
			return new Pair<Line, Pose>(res, p);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	public static byte[] SerializeWaypoint(Waypoint p) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(24);
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeDouble(p.getX());
			dos.writeDouble(p.getX());
			dos.writeDouble(p.getHeading());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return baos.toByteArray();
	}
	
	public static Waypoint DeSerializeWaypoint(byte[] bytes) {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		DataInputStream dos = new DataInputStream(bais);
		try {
			Waypoint res = new Waypoint(dos.readDouble(),dos.readDouble(),dos.readDouble());
			return res;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	
}
