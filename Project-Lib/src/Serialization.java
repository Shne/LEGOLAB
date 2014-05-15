import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

import lejos.geom.Line;

public class Serialization {
	public static byte[] SerializeLine(Line l) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream(16);
		DataOutputStream dos = new DataOutputStream(baos);
		try {
			dos.writeFloat(l.x1);
			dos.writeFloat(l.y1);
			dos.writeFloat(l.x2);
			dos.writeFloat(l.y2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return baos.toByteArray();
	}
	
	public static Line DeSerializeLine(byte[] bytes) {
		ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
		DataInputStream dos = new DataInputStream(bais);
		try {
			Line res = new Line(dos.readFloat(),dos.readFloat(),dos.readFloat(),dos.readFloat());
			return res;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
