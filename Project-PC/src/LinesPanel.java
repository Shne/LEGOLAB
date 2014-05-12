import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import lejos.geom.Line;
import lejos.geom.Rectangle;
import lejos.robotics.mapping.LineMap;

public class LinesPanel extends DrawingPanel {

	private ArrayList<Line> lines;

	public LinesPanel(int width, int height) {
		super(width, height);
		// TODO Auto-generated constructor stub
		throw new IllegalAccessError();
	}

	public LinesPanel(int width, int height, ArrayList<Line> lines) {
		super(width, height);
		this.lines = lines;
		new Thread() {
			public void run() {
				loop();
			}
		}.start();
	}

	private void loop() {
		while (true) {
			try {
				new LineMap(((Line[]) lines.toArray(new Line[lines.size()])),
						new Rectangle(-500, -500, 1000, 1000))
						.createSVGFile("map.svg");
				Runtime.getRuntime().exec("cmd /c convert -background white -flatten -density 200 -resize 400x400 MSVG:map.svg -depth 8 map.png").waitFor();
				
				paintImg(0, 0, getImg("map.png"));
				update();
				synchronized (lines) {
					System.out.println("BUO");
					lines.wait();
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
