import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import lejos.geom.Line;
import static java.lang.Math.*;

public class LinesPanel extends DrawingPanel {

	private static final long serialVersionUID = 2439767843L;
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

			{
				setDaemon(true);
			}
		}.start();
	}

	private void loop() {
		while (true) {
			try {
				synchronized (lines) {
					if (lines.size() == 0)
						continue;
					/*
					 * new LineMap(((Line[]) lines.toArray(new
					 * Line[lines.size()])), new Rectangle(-500, -500, 1000,
					 * 1000)) .createSVGFile("map.svg");
					 * Runtime.getRuntime().exec(
					 * "cmd /c convert -background white -flatten -density 200 -resize 400x400 MSVG:map.svg -depth 8 map.png"
					 * ).waitFor();
					 */

					float maxx = Float.MIN_VALUE, maxy = Float.MIN_VALUE, minx = Float.MAX_VALUE, miny = Float.MAX_VALUE;

					for (Line l : lines) {
						maxx = max(l.x1, max(l.x2, maxx));
						maxy = max(l.y1, max(l.y2, maxy));
						minx = min(l.x1, min(l.x2, minx));
						miny = min(l.y1, min(l.y2, miny));
					}

					float lx = maxx - minx, ly = maxy - miny;

					paintRect(0, 0, PWIDTH, PHEIGHT, Color.WHITE);

					for (Line l : lines) {
						int x1 = (int) (((l.x1 - minx) / lx) * PWIDTH);
						int y1 = (int) (((l.y1 - miny) / ly) * PHEIGHT);
						int x2 = (int) (((l.x2 - minx) / lx) * PWIDTH);
						int y2 = (int) (((l.y2 - miny) / ly) * PHEIGHT);
						paintLine(x1, y1, x2, y2, Color.BLACK);
					}

					// paintImg(0, 0, getImg("map.png"));
					update();
					
					BufferedImage outimg = new BufferedImage(WIDTH, HEIGHT, BufferedImage.TYPE_4BYTE_ABGR);
					
					this.paintComponent(outimg.getGraphics());
					
					try {
						ImageIO.write(outimg, "png", new File("map.png"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					System.out.println("BUO");
					lines.wait();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
