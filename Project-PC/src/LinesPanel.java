import static java.lang.Math.*;

import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Path2D;
import java.awt.image.VolatileImage;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.awt.*;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.geom.Rectangle;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.DestinationUnreachableException;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.Path;
import lejos.robotics.pathfinding.ShortestPathFinder;

public class LinesPanel extends DrawingPanel {

	private VolatileImage Indy;
	private VolatileImage Grail;

	// Nice parameters, plz do magic
	private final static float PATHFINDING_D = 18f;
	private final static double PRUNING_FAC = 4d;
	private final static double PRUNING_WAIT = 25;

	private static final long serialVersionUID = 2439767843L;
	private ArrayList<Line> lines;
	private ArrayList<Line> lines2 = new ArrayList<Line>();
	private ArrayList<Line> pathses = new ArrayList<Line>();
	private ArrayList<Waypoint> waypoints;
	private float lastx, lasty;
	public volatile Pose pose;

	public LinesPanel(int width, int height) {
		super(width, height);
		// TODO Auto-generated constructor stub
		throw new IllegalAccessError();
	}

	public LinesPanel(int width, int height, ArrayList<Line> lines,
			ArrayList<Waypoint> waypoints) {
		super(width, height);
		Indy = getImg("indy.png");
		Grail = getImg("grail.png");
		this.lines = lines;
		this.waypoints = waypoints;
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
				if (e.isShiftDown()) {
					forcePath(e.getPoint().x, e.getPoint().y);
				} else {
					pathGen(e.getPoint().x, e.getPoint().y);
				}

			}
		});
	}

	protected void pathGen(int x, int y) {
		float fx = minx + (lx) * ((float) x) / ((float) PWIDTH);
		float fy = miny + (ly) * ((float) y) / ((float) PHEIGHT);
		lastx = fx;
		lasty = fy;
		rePath(0);
	}

	protected void forcePath(int x, int y) {
		float fx = minx + (lx) * ((float) x) / ((float) PWIDTH);
		float fy = miny + (ly) * ((float) y) / ((float) PHEIGHT);
		lastx = fx;
		lasty = fy;
		synchronized (pathses) {
			pathses.clear();
			pathses.add(new Line(pose.getX(), pose.getY(), fx, fy));
		}
		synchronized (waypoints) {
			waypoints.clear();
			waypoints.add(new Waypoint(fx, fy));
			waypoints.notifyAll();
		}
	}

	protected void rePath(final int wait) {
		new Thread() {
			public void run() {
				try {
					Thread.sleep(wait);
					if (pose.distanceTo(new Point(lastx, lasty)) > 1f)
						PathTo(lastx, lasty);
				} catch (InterruptedException e) {
				}
			}
			{setDaemon(true);}
		}.start();
	}

	private void PathTo(float fx, float fy) {
		System.out.println(fx);
		System.out.println(fy);
		lines2.clear();
		for (Line l : lines) {
			// l.x1, l.y1, l.x2, l.y2 is the original line
			// lines2.add(new Line(l.x1, l.y1, l.x2, l.y2)); //adding only the
			// original line

			Point[] pts = expandLine(l);

			Path2D.Double p = new Path2D.Double();

			p.moveTo(pts[0].x, pts[0].y);
			p.lineTo(pts[1].x, pts[1].y);
			p.lineTo(pts[2].x, pts[2].y);
			p.lineTo(pts[3].x, pts[3].y);
			p.lineTo(pts[0].x, pts[0].y);

			if (p.contains(pose.getX(), pose.getY()) || p.contains(fx, fy))
				continue;

			synchronized (lines2) {
				lines2.add(new Line(pts[0].x, pts[0].y, pts[1].x, pts[1].y));
				lines2.add(new Line(pts[1].x, pts[1].y, pts[2].x, pts[2].y));
				lines2.add(new Line(pts[2].x, pts[2].y, pts[3].x, pts[3].y));
				lines2.add(new Line(pts[3].x, pts[3].y, pts[0].x, pts[0].y));
			}
		}
		LineMap lm = new LineMap(((Line[]) lines2.toArray(new Line[lines2
				.size()])), new Rectangle(minx - 1f, miny - 1f, maxx + 1f,
				maxy + 1f));

		ShortestPathFinder pather = new ShortestPathFinder(lm);
		pather.lengthenLines(0.1f);
		Path path = null;
		try {
			path = pather.findRoute(pose, new Waypoint(fx, fy));
		} catch (DestinationUnreachableException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Waypoint l = new Waypoint(pose);
		synchronized (pathses) {
			pathses.clear();
			for (Waypoint p : path) {
				System.out.println(l.x + ", " + l.y + ", " + p.x + ", " + p.y);
				pathses.add(new Line(l.x, l.y, p.x, p.y));

				l = p;
			}
		}
		synchronized (waypoints) {
			waypoints.clear();
			waypoints.addAll(path);
			waypoints.notifyAll();
		}
	}

	private Point[] expandLine(Line l) {
		float d = PATHFINDING_D;
		Point a = new Point(l.x1, l.y1);
		Point b = new Point(l.x2, l.y2);
		Point v = b.subtract(a);
		Point o1 = v.multiply(d).multiply(1 / v.length());
		Point o2 = new Point(o1.y, -o1.x).multiply(0.4f);

		Point p1 = b.add(o1).add(o2);
		Point p2 = b.add(o1).subtract(o2);
		Point p3 = a.subtract(o1).subtract(o2);
		Point p4 = a.subtract(o1).add(o2);
		return new Point[] { p1, p2, p3, p4 };
	}

	private void pruneLines(ArrayList<Line> lines) {
		HashSet<Line> prunes = new HashSet<Line>();
		HashSet<Line> news = new HashSet<Line>();
		// TODO: Real line segment intersection
		for (int i = 0; i < lines.size(); ++i) {
			Line l1o = lines.get(i);
			if (prunes.contains(l1o))
				continue;
			for (int j = i + 1; j < lines.size(); ++j) {
				Line l2o = lines.get(j);
				if (prunes.contains(l2o))
					continue;
				Line l1 = (Line) l1o.clone();
				Line l2 = (Line) l2o.clone();
				l1.lengthen(1f);
				l2.lengthen(1f);
				Point intersect = l1.intersectsAt(l2);
				if (intersect == null)
					continue;
				double m1 = (l1.y1 - l1.y2) / (l1.x1 - l1.x2);
				double m2 = (l2.y1 - l2.y2) / (l2.x1 - l2.x2);

				double angle = atan((m1 - m2) / (1d - m1 * m2));
				if (abs(angle) > PI / PRUNING_FAC)
					continue;
				prunes.add(l1o);
				prunes.add(l2o);
				double maxdist = Double.MIN_VALUE;
				Point p1 = null, p2 = null;
				if (l1o.getP1().distance(l2o.getP1()) > maxdist) {
					p1 = l1o.getP1();
					p2 = l2o.getP1();
					maxdist = l1o.getP1().distance(l2o.getP1());
				}
				if (l1o.getP2().distance(l2o.getP1()) > maxdist) {
					p1 = l1o.getP2();
					p2 = l2o.getP1();
					maxdist = l1o.getP2().distance(l2o.getP1());
				}
				if (l1o.getP1().distance(l2o.getP2()) > maxdist) {
					p1 = l1o.getP1();
					p2 = l2o.getP2();
					maxdist = l1o.getP1().distance(l2o.getP2());
				}
				if (l1o.getP2().distance(l2o.getP2()) > maxdist) {
					p1 = l1o.getP2();
					p2 = l2o.getP2();
					maxdist = l1o.getP2().distance(l2o.getP2());
				}
				if (l1o.getP2().distance(l1o.getP1()) > maxdist) {
					p1 = l1o.getP2();
					p2 = l1o.getP1();
					maxdist = l1o.getP2().distance(l1o.getP1());
				}
				if (l2o.getP2().distance(l2o.getP1()) > maxdist) {
					p1 = l2o.getP2();
					p2 = l2o.getP1();
					maxdist = l2o.getP2().distance(l2o.getP1());
				}
				news.add(new Line(p1.x, p1.y, p2.x, p2.y));
				break;
			}
		}
		lines.removeAll(prunes);
		lines.addAll(news);
	}

	float maxx, minx, maxy, miny, lx, ly;

	Random r = new Random();

	public Color lastColor;

	int counter = 0;

	Line lastLine = null;

	private void loop() {
		while (true) {
			try {
				synchronized (lines) {
					if (lines.size() == 0)
						continue;
					if (++counter % PRUNING_WAIT == 0)
						pruneLines(lines);

					Line newLine = lines.get(lines.size() - 1);

					if (newLine != lastLine) {

						Point[] pts = expandLine(newLine);

						Line[] newPathLines = new Line[4];

						if (!Float.isNaN(pts[0].x)) {

							newPathLines[0] = new Line(pts[0].x, pts[0].y,
									pts[1].x, pts[1].y);
							newPathLines[1] = new Line(pts[1].x, pts[1].y,
									pts[2].x, pts[2].y);
							newPathLines[2] = new Line(pts[2].x, pts[2].y,
									pts[3].x, pts[3].y);
							newPathLines[3] = new Line(pts[3].x, pts[3].y,
									pts[0].x, pts[0].y);

							outer: for (int i = 0; i < 4; ++i) {
								for (Line l : pathses) {
									if (newPathLines[i].intersectsLine(l)) {
										synchronized (waypoints) {
											waypoints.clear();
											waypoints.notify();
										}
										rePath(0);
										
										
										break outer;
									}
								}
							}
						}

						lastLine = newLine;
					}

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

					if (pose != null) {
						maxx = max(pose.getX(), maxx);
						maxy = max(pose.getY(), maxy);
						minx = min(pose.getX(), minx);
						miny = min(pose.getY(), miny);
					}

					maxx += 25f;
					minx -= 25f;
					maxy += 25f;
					miny -= 25f;

					lx = maxx - minx;
					ly = maxy - miny;
					if (lx > ly)
						ly = lx;
					if (ly > lx)
						lx = ly;

					paintRect(0, 0, PWIDTH, PHEIGHT, Color.WHITE);

					for (Line l : lines) {
						int x1 = (int) (((l.x1 - minx) / lx) * PWIDTH);
						int y1 = (int) (((l.y1 - miny) / ly) * PHEIGHT);
						int x2 = (int) (((l.x2 - minx) / lx) * PWIDTH);
						int y2 = (int) (((l.y2 - miny) / ly) * PHEIGHT);
						paintLine(x1, y1, x2, y2, Color.BLACK);
					}

					synchronized (lines2) {
						for (Line l : lines2) {
							int x1 = (int) (((l.x1 - minx) / lx) * PWIDTH);
							int y1 = (int) (((l.y1 - miny) / ly) * PHEIGHT);
							int x2 = (int) (((l.x2 - minx) / lx) * PWIDTH);
							int y2 = (int) (((l.y2 - miny) / ly) * PHEIGHT);
							//paintLine(x1, y1, x2, y2, Color.CYAN);
						}
					}

					synchronized (pathses) {
						for (Line l : pathses) {
							int x1 = (int) (((l.x1 - minx) / lx) * PWIDTH);
							int y1 = (int) (((l.y1 - miny) / ly) * PHEIGHT);
							int x2 = (int) (((l.x2 - minx) / lx) * PWIDTH);
							int y2 = (int) (((l.y2 - miny) / ly) * PHEIGHT);
							paintLine(x1, y1, x2, y2, Color.BLUE);
						}
						if (pathses.size() != 0) {
							Line grailPos = pathses.get(pathses.size() - 1);
							int x = (int) (((grailPos.x2 - minx) / lx) * PWIDTH) - 8;
							int y = (int) (((grailPos.y2 - miny) / ly) * PHEIGHT) - 8;
							paintImg(x, y, Grail, 0d);
						}
					}

					if (pose != null) {
						// System.out.println(pose.getX() + ", " + pose.getY());
						int x = (int) (((pose.getX() - minx) / lx) * PWIDTH);
						int y = (int) (((pose.getY() - miny) / ly) * PHEIGHT);

						paintRect(x - 5, y - 5, 10, 10, Color.RED);

						paintImg(x - 32, y - 32, Indy,
								toRadians(pose.getHeading()));
					}

					// paintImg(0, 0, getImg("map.png"));
					update();

					// System.out.println("BUO");
					lines.wait();
				}
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

}
