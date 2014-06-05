

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Polygon;
import java.awt.geom.AffineTransform;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

import javax.imageio.ImageIO;
import javax.swing.JPanel;


public class DrawingPanel extends JPanel {
	private static final long serialVersionUID = 5464332804854626334L;
	protected static int PWIDTH;
	protected static int PHEIGHT;
	/**
	 * @uml.property  name="gfx"
	 */
	protected Graphics2D gfx;
	/**
	 * @uml.property  name="bufferImg"
	 */
	VolatileImage bufferImg;
	//BufferedImage bufferImg;
	
	
	public DrawingPanel (int width , int height)
	{
		PWIDTH = width;
		PHEIGHT = height;
		setBackground(Color.white);
		setPreferredSize(new Dimension(PWIDTH, PHEIGHT));
		setFocusable(true);
	}
	
	public void ready()
	{
		bufferImg = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleVolatileImage(PWIDTH, PHEIGHT, VolatileImage.OPAQUE);
		//bufferImg = new BufferedImage(PWIDTH, PHEIGHT, BufferedImage.TYPE_INT_ARGB);
		gfx = (Graphics2D) bufferImg.createGraphics();
//		AffineTransform tx = AffineTransform.getScaleInstance(1d, -1d);
//		tx.translate(0, -PHEIGHT);
//		gfx.setTransform(tx);
		clear();
	}
	
	public void clear()
	{
		gfx.setColor(Color.WHITE);
		gfx.setComposite(AlphaComposite.Src);
		gfx.clearRect(0, 0, PWIDTH, PHEIGHT);
	}
	
	public void update()
	{
		this.repaint();
	}
	
	public void paintComponent(Graphics g)
	{
		Graphics2D g2 = (Graphics2D) g;
		g2.drawImage(bufferImg,0,0,null);
	}
	
	protected void paintRect(int x, int y, int dx, int dy, Color color)
	{
		gfx.setColor(color);
		gfx.fillRect(x, y, dx, dy);
	}
	
	protected void paintLine(int x, int y, int dx, int dy, Color color)
	{
		gfx.setColor(color);
		gfx.drawLine(x, y, dx, dy);
	}
	
	protected void paintTriangle(int x1, int y1, int x2, int y2,int x3, int y3,Color color)
	{
		gfx.setColor(color);
		gfx.fillPolygon(new Polygon(new int[] {x1,x2,x3}, new int[] {y1,y2,y3}, 3));
	}
	
	protected void paintQuad(int x1, int y1, int x2, int y2,int x3, int y3, int x4, int y4,Color color)
	{
		gfx.setColor(color);
		gfx.fillPolygon(new Polygon(new int[] {x1,x2,x3,x4}, new int[] {y1,y2,y3,y4}, 4));
	}
	
	protected void paintImg(int x, int y, Image I, double rotation)
	{
		gfx.setComposite(AlphaComposite.SrcOver);
		int w = I.getWidth(this); int h = I.getHeight(this);
		BufferedImage bimg = new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR);
		bimg.getGraphics().drawImage(I,0,0,this);
		AffineTransform tx = new AffineTransform();
		tx.translate(w/2, h/2);
		tx.rotate(rotation);
		tx.translate(-w/2, -h/2);
		gfx.drawImage(bimg, new AffineTransformOp(tx, AffineTransformOp.TYPE_BICUBIC), x, y);
	}
	
	protected VolatileImage getImg(String s)
	{
		BufferedImage BI = null;
		try {
			BI = ImageIO.read(new File(s));
		} catch (IOException e) {
			e.printStackTrace();
		}
		VolatileImage VI = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().createCompatibleVolatileImage(BI.getWidth(), BI.getHeight(),VolatileImage.BITMASK);
		Graphics2D wr = VI.createGraphics();
		wr.setComposite(AlphaComposite.Src);
		wr.drawImage(BI,null,0,0);
		return VI;
	}
	
	
	
}

