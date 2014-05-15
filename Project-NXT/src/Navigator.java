import java.io.*;
import java.util.Random;

import lejos.geom.Line;
import lejos.nxt.BasicMotorPort;
import lejos.nxt.LCD;
import lejos.nxt.MotorPort;
import lejos.nxt.comm.BTConnection;
import lejos.nxt.comm.Bluetooth;



public class Navigator {
	
	public static void main(String[] args) throws InterruptedException{
		Upload();
		Thread.sleep(1000000000);
	}
	
	static Random rand = new Random();
	
	public static void Upload() {
		new Thread() {
			public void run() {
				try {
					int counter = 0;
					BTConnection con = Bluetooth.waitForConnection();
					DataOutputStream dos = con.openDataOutputStream();
					
					while (true) {
						Line l = new Line(1, 2, 3, 4);
						dos.write(Serialization.SerializeLine(l));
						dos.flush();
						Thread.sleep(1000);
					}

				} catch (Throwable t) {
				}
			}

			{
				setDaemon(true);
			}
		}.start();
	}

}
