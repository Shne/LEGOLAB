import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;

import lejos.geom.Line;
import lejos.geom.Rectangle;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.DestinationUnreachableException;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.Path;
import lejos.robotics.pathfinding.PathFinder;
import lejos.robotics.pathfinding.ShortestPathFinder;
import static java.lang.Math.*;

public class LinesPanel extends DrawingPanel {

	private static final long serialVersionUID = 2439767843L;
	private ArrayList<Line> lines;
	private ArrayList<Line> pathses = new ArrayList<Line>();

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
		this.addMouseListener(new MouseAdapter() {
			public void mouseReleased(MouseEvent e) {
				pathGen(e.getPoint().x, e.getPoint().y);
			}
		});
	}

	protected void pathGen(int x, int y) {
		float fx = minx + (maxx - minx) * ((float) x) / ((float) PWIDTH);
		float fy = miny + (maxy - miny) * ((float) y) / ((float) PHEIGHT);

		System.out.println(fx);
		System.out.println(fy);
		ArrayList<Line> lines2 = new ArrayList<Line>();
		for(Line l : lines)
		{
			lines2.add(new Line(l.x1, l.y1, l.x2, l.y2));
		}
		LineMap lm = new LineMap(
				((Line[]) lines2.toArray(new Line[lines2.size()])),
				new Rectangle(minx - 1f, miny - 1f, maxx + 1f, maxy + 1f));

		ShortestPathFinder pather = new ShortestPathFinder(lm);
		pather.lengthenLines(1f);
		Path path = null;
		try {
			path = pather.findRoute(new Pose(minx + (maxx - minx) / 2f, miny
					+ (maxy - miny) / 2f, 0f), new Waypoint(fx, fy));
		} catch (DestinationUnreachableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Waypoint l = new Waypoint(new Pose(minx + (maxx - minx) / 2f, miny
				+ (maxy - miny) / 2f, 0f));
		synchronized (pathses) {
			pathses.clear();
			for (Waypoint p : path) {
				System.out.println(l.x + ", " + l.y + ", " + p.x + ", " + p.y);
				pathses.add(new Line(l.x, l.y, p.x, p.y));

				l = p;
			}
		}
	}

	float maxx, minx, maxy, miny;

	private void loop() {
		while (true) {
			try {
				synchronized (lines) {
					if (lines.size() == 0)
						continue;
					maxx = Float.MIN_VALUE;
					maxy = Float.MIN_VALUE;
					minx = Float.MAX_VALUE;
					miny = Float.MAX_VALUE;

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
					synchronized (pathses) {
						for (Line l : pathses) {
							int x1 = (int) (((l.x1 - minx) / lx) * PWIDTH);
							int y1 = (int) (((l.y1 - miny) / ly) * PHEIGHT);
							int x2 = (int) (((l.x2 - minx) / lx) * PWIDTH);
							int y2 = (int) (((l.y2 - miny) / ly) * PHEIGHT);
							paintLine(x1, y1, x2, y2, Color.BLUE);
						}
					}

					// paintImg(0, 0, getImg("map.png"));
					update();

					BufferedImage outimg = new BufferedImage(WIDTH, HEIGHT,
							BufferedImage.TYPE_INT_RGB);

					this.paintComponent(outimg.getGraphics());

					try {
						ImageIO.write(outimg, "png", new File("map.png"));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}

					//System.out.println("BUO");
					lines.wait();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
