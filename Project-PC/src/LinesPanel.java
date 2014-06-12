import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.VolatileImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.imageio.ImageIO;

import org.jfree.data.xy.Vector;

import lejos.geom.Line;
import lejos.geom.Point;
import lejos.geom.Rectangle;
import lejos.robotics.mapping.LineMap;
import lejos.robotics.navigation.DestinationUnreachableException;
import lejos.robotics.navigation.Pose;
import lejos.robotics.navigation.Waypoint;
import lejos.robotics.pathfinding.Path;
import lejos.robotics.pathfinding.PathFinder;
import lejos.robotics.pathfinding.ShortestPathFinder;
import static java.lang.Math.*;

import com.tomgibara.cluster.gvm.*;
import com.tomgibara.cluster.gvm.dbl.DblClusters;
import com.tomgibara.cluster.gvm.flt.FltClusters;
import com.tomgibara.cluster.gvm.flt.FltResult;

import net.sf.javaml.clustering.*;
import net.sf.javaml.clustering.mcl.MCL;
import net.sf.javaml.core.*;
import net.sf.javaml.distance.EuclideanDistance;
import net.sf.javaml.distance.NormalizedEuclideanDistance;
import net.sf.javaml.distance.NormalizedEuclideanSimilarity;
import net.sf.javaml.distance.RBFKernel;

public class LinesPanel extends DrawingPanel {

	private VolatileImage Indy;
	private VolatileImage Grail;

	private static final long serialVersionUID = 2439767843L;
	private ArrayList<Line> lines;
	private ArrayList<Line> lines2 = new ArrayList<Line>();
	private ArrayList<Line> pathses = new ArrayList<Line>();
	private ArrayList<Waypoint> waypoints;
	private float lastx, lasty;
	public volatile Pose pose;

	Dataset dataz = new DefaultDataset();

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
				pathGen(e.getPoint().x, e.getPoint().y);
			}
		});
	}

	protected void pathGen(int x, int y) {
		float fx = minx + (lx) * ((float) x) / ((float) PWIDTH);
		float fy = miny + (ly) * ((float) y) / ((float) PHEIGHT);
		lastx = fx;
		lasty = fy;
		PathTo(fx, fy);
	}

	protected void rePath() {
		new Thread() {
			public void run() {
				try {
					Thread.sleep(5000);
					PathTo(lastx, lasty);
				} catch (InterruptedException e) {
				}
			}
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
			float d = 18;
			Point a = new Point(l.x1, l.y1);
			Point b = new Point(l.x2, l.y2);
			Point v = b.subtract(a);
			Point o1 = v.multiply(d).multiply(1 / v.length());
			Point o2 = new Point(o1.y, -o1.x).multiply(0.4f);

			Point p1 = b.add(o1).add(o2);
			Point p2 = b.add(o1).subtract(o2);
			Point p3 = a.subtract(o1).subtract(o2);
			Point p4 = a.subtract(o1).add(o2);
			synchronized (lines2) {
				lines2.add(new Line(p1.x, p1.y, p2.x, p2.y));
				lines2.add(new Line(p2.x, p2.y, p3.x, p3.y));
				lines2.add(new Line(p3.x, p3.y, p4.x, p4.y));
				lines2.add(new Line(p4.x, p4.y, p1.x, p1.y));
			}
		}
		LineMap lm = new LineMap(((Line[]) lines2.toArray(new Line[lines2
				.size()])), new Rectangle(minx - 1f, miny - 1f, maxx + 1f,
				maxy + 1f));

		ShortestPathFinder pather = new ShortestPathFinder(lm);
		// pather.lengthenLines(20f);
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
			waypoints.add(path.get(1));
			waypoints.notifyAll();
		}
	}

	float maxx, minx, maxy, miny, lx, ly;

	Random r = new Random();

	public Color lastColor;

	private void loop() {
		while (true) {
			try {
				synchronized (lines) {
					if (lines.size() == 0)
						continue;
					Line newLine = lines.get(lines.size() - 1);
					dataz.add(new DenseInstance(new double[] { newLine.x1,
							newLine.y1, }));
					dataz.add(new DenseInstance(new double[] { newLine.x2,
							newLine.y2, }));
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

					if (lines.size() > 10) {
						//for (Dataset data : new MCL(new RBFKernel()).cluster(dataz)) {
						for(Dataset data : new DensityBasedSpatialClustering().cluster(dataz)){
							Color c = new Color(r.nextInt());
							ArrayList<NumberedPoint> plist = new ArrayList<NumberedPoint>();
							int i=0;
							for (Instance point : data) {
								plist.add(new NumberedPoint(point.value(0), point.value(1), ++i));
							}
							if(plist.size()<2)
							{
								continue;
							}
							List<NumberedPoint> reslist = INC_CH.convexHull_INC_CH(plist);
							NumberedPoint last = reslist.get(reslist.size()-1);
							for(NumberedPoint p : reslist)
							{
								int x1 = (int) (((last.x - minx) / lx) * PWIDTH);
								int y1 = (int) (((last.y - miny) / ly) * PHEIGHT);
								int x2 = (int) (((p.x - minx) / lx) * PWIDTH);
								int y2 = (int) (((p.y - miny) / ly) * PHEIGHT);
								paintLine(x1, y1, x2, y2, Color.RED);
								last = p;
							}
						}
					}
					synchronized (lines2) {
						for (Line l : lines2) {
							int x1 = (int) (((l.x1 - minx) / lx) * PWIDTH);
							int y1 = (int) (((l.y1 - miny) / ly) * PHEIGHT);
							int x2 = (int) (((l.x2 - minx) / lx) * PWIDTH);
							int y2 = (int) (((l.y2 - miny) / ly) * PHEIGHT);
							paintLine(x1, y1, x2, y2, Color.CYAN);
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
