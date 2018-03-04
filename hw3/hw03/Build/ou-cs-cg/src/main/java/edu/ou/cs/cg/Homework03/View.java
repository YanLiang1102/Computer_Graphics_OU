//******************************************************************************
// Copyright (C) 2016 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Tue Mar  1 18:52:22 2016 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20160209 [weaver]:	Original file.
//
//******************************************************************************
// Notes:
//
//******************************************************************************

package edu.ou.cs.cg.Homework03;

//import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.*;
import java.text.DecimalFormat;
import java.util.*;
import javax.swing.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.awt.TextRenderer;

//******************************************************************************

/**
 * The <CODE>Interaction</CODE> class.<P>
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class View
	implements GLEventListener
{
	//**********************************************************************
	// Public Class Members
	//**********************************************************************

	public static final int				DEFAULT_FRAMES_PER_SECOND = 60;
	private static final DecimalFormat	FORMAT = new DecimalFormat("0.000");

		//**********************************************************************
	// Public Class Members
	//**********************************************************************

	//public static final GLU		GLU = new GLU();
	//public static final GLUT	GLUT = new GLUT();
	public static final Random	RANDOM = new Random();

	//**********************************************************************
	// Private Members
	//**********************************************************************

	// State (internal) variables
	private int				k = 0;		// Just an animation counter

	//private int				w;			// Canvas width
	//private int				h;			// Canvas height

	//**********************************************************************
	// Private Members
	//**********************************************************************

	// State (internal) variables
	private final GLJPanel			canvas;
	private int						w;				// Canvas width
	private int						h;				// Canvas height

	private final KeyHandler		keyHandler;
	private final MouseHandler		mouseHandler;

	private final FPSAnimator		animator;
	private int						counter = 0;	// Frame display counter

	private TextRenderer			renderer;
	private Point2D.Double				origin;		// Current origin coordinates
	private Point2D.Double				cursor;		// Current cursor coordinates
	private ArrayList<Point2D.Double>	points;		// User's polyline points
	public Boolean shade;    //to control the shade of the window.
	//that is the code define in Dr.weaver's code as a base.
	private int fenceHeight=102;
	private int hopx=673;
	private int hopy=634;
	private int sidewalkLength=79;
	private int fanNo=5;
	private int colorG=255;
	private ArrayList<Integer> starColorG = new ArrayList<Integer>(){{
		add(255);
		add(255);
		add(255);
		add(255);
		add(255);
	}};
	private int currentStar=-1;
	//private int[][] starPosition = new int[][] {{921, 720 - 29},{1052, 720 -  61},{1177, 720 -  49},{1205, 720 - 153},{1146, 720 - 254}};
	private ArrayList<Point> starPosition=new ArrayList<Point>(){
		{
			add(new Point(921,720-29));
			add(new Point(1052,720-61));
			add(new Point(1177,720-49));
			add(new Point(1205,720-153));
			add(new Point(1146,720-254));
		}
	};
	private int skyline=312;
	private int kitex=956;
	private int kitey=490;
	private int fencex=1024;
	private int fencey=244;
	private Boolean drawKiteLineBool=false;
	private Boolean movekite=false;
	private Boolean duringDrag=false;
	//15 is the radiuis of the star

    //initialize the arraylist with the fence point on it
	private ArrayList<Point> kitelinedrag= new ArrayList<Point>(){{
    add(new Point(fencex,fencey));

}};
	//add the fence location on the list.
			// kiteline.add(new Point(1024, 244));
			// kiteline.add(new Point( 964, 272));
			// kiteline.add(new Point( 924, 364));
			// kiteline.add(new Point( 928, 396));
			// kiteline.add(new Point( 900, 428));
			// kiteline.add(new Point( 912, 464));
			// kiteline.add(new Point( 936, 472));
			// kiteline.add(new Point( 956, 490));


	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public View(GLJPanel canvas)
	{
		this.canvas = canvas;

		// Initialize model
		origin = new Point2D.Double(0.0, 0.0);
		cursor = null;
		points = new ArrayList<Point2D.Double>();
		this.shade=false;

		// Initialize rendering
		canvas.addGLEventListener(this);
		animator = new FPSAnimator(canvas, DEFAULT_FRAMES_PER_SECOND);
		animator.start();

		// Initialize interaction
		keyHandler = new KeyHandler(this);
		mouseHandler = new MouseHandler(this);
	}

	//**********************************************************************
	// Getters and Setters
	//**********************************************************************

	public int	getWidth()
	{
		return w;
	}
	public int getStarNo()
	{
		return starColorG.size();
	}
	public int getSidewalkLength(){
		return this.sidewalkLength;
	}

	public void addPointToKiteLineDrag(int x, int y)
	{
		this.kitelinedrag.add(new Point(x,y));

	}
	//cx and cy define the new centroid.
	public void changeCentroid(int cx, int cy)
	{
		//need to know 15 is the center of all start ,need to find the minx, maxx, miny and maxy among all the stars
		int minx=1280;
		int maxx=0;
		int miny=720;
		int maxy=0;
		//the loop through all the points and find all the min and max for x and y
	    int x=getCentroid().x;
	    int y=getCentroid().y;
	    //the move is only allowed if everything is in the view
	    int xmove=cx-x;
	    int ymove=cy-y;

	    for(Point p: starPosition)
	    {
	    	minx=Math.min(minx,p.x);
	    	maxx=Math.max(maxx,p.x);
	    	miny=Math.min(miny,p.y);
	    	maxy=Math.max(maxy,p.y);
	    }
        
	    if(xmove<0)
	    {
	    	//jsut need to check if minx ,since we will move to left with a abs(xmove)
	    	if(minx-15+xmove<0)
	    	{
	    		return;
	    	}
	    }
	    else
	    {
	    	//will move to right , need to check maxx
	    	if(maxx+15+xmove>1280)
	    	{
	    		return;
	    	}
	    }
	    if(ymove<0)
	    {
	    	//which mean we will move down
	    	//need to check miny
	    	if(miny-15+ymove<0)
	    	{
	    		return;
	    	}

	    }
	    else
	    {
	    	if(maxy+15+ymove>720)
	    	{
	    		return;
	    	}
	    }
	    //if not return we will make the move that is make all the points in the positionStar make a shift
	    for(Point p: starPosition)
	    {
	    	p.x=p.x+xmove;
	    	p.y=p.y+ymove;
	    }


	}
    
	public int	getHeight()
	{
		return h;
	}
	public void setDrag(Boolean drag)
	{
		this.duringDrag=drag;
	}

   //need to press k to make the move kite mode begin in that way the click event be muted.since press will also fire the click
	public void setmovekite(Boolean move)
	{
		this.movekite=move;
		//also set the kit eto the original place

	}

	public Boolean getmovekite()
	{
		return this.movekite;
	}

	public int getSkyline()
	{
		return this.skyline;
	}
	public void setStarPosition(int index, int cx, int cy)
	{
		// System.out.println("index is: "+index);
		// System.out.println("cx is "+cx);
		// System.out.println("cy is "+cy);
		starPosition.get(index).x=cx;
		starPosition.get(index).y=cy;

	}
	public void setIthStarColorG(int index)
	{
		//first need to set the all the values back to 255, the set the index one to be 165
		for(int i=0;i<starColorG.size(); i++)
		{
			if(i==index)
			starColorG.set(i,165);
		    else
		    starColorG.set(i,255);
		}

	}
	//customized function for how to find the centroid
	public Point getCentroid()  {
    int centroidX = 0; 
    int centroidY = 0;

        for(Point p : starPosition) {
            centroidX += p.getX();
            centroidY += p.getY();
        }
    return new Point((int)(centroidX *1.0/ starPosition.size()), (int)(centroidY*1.0 /starPosition.size()));
	}
	//get centroid of all the stars
	// public Point2D getCentroidOfStars()
	// {
	// 	return Point2D.centroid(starPosition);
	// }
	public void setCurrentStar(int diff)
	{
		//5 is hardcoded here can be change later.
		this.currentStar=((this.currentStar)+diff)%(this.starColorG.size());
	}
	public int getCurrentStar()
	{
		return this.currentStar;
	}

	public void setcolorG(int gvalue)
	{
		this.colorG=gvalue;
	}

	public void setfans(int fans)
	{
		this.fanNo=fans;
	}

	public Point2D.Double	getOrigin()
	{
		return new Point2D.Double(origin.x, origin.y);
	}

	public void		setOrigin(Point2D.Double origin)
	{
		this.origin.x = origin.x;
		this.origin.y = origin.y;
		canvas.repaint();
	}

	public void setShade(Boolean shade)
	{
		this.shade=shade;
		//canvas.repaint();
	}
	//set the hop scotch left and right based on if the shift key is pressed or not.
	public void sethopxleft(Boolean shift)
	{
		if(shift)
		{
		 this.hopx=this.hopx-(int)(sidewalkLength*0.1);	
		}
		else
		{
		this.hopx=this.hopx-sidewalkLength;	
		}
		
	}
	public void setDrawKiteLine(Boolean draw)
	{
		this.drawKiteLineBool=draw;
	}

		public void sethopxright(Boolean shift)
	{
		if(shift)
		{
		 this.hopx=this.hopx+(int)(sidewalkLength*0.1);	
		}
		else
		{
		this.hopx=this.hopx+sidewalkLength;	
		}
	}
	public void setkite(int x, int y)
	{
		this.kitex=x;
		this.kitey=y;
	}
	public void addStar()
	{
     //need to add the start within in the view
	 //since 15 is the center iof the star, so in order for all the generated star in the view I need the center generated follow this.
	//[15,1280-15],[15,720-25]
     int x=15 + (int)(Math.random() * ((1280-15- 15) + 1));
     int y=15 + (int)(Math.random() * ((720-15 - 15) + 1));
     this.starColorG.add(255);
     this.starPosition.add(new Point(x,y));

	}
	public void deleteStar()
	{
		//need to remove two elements one is the color one is the 
		//u can directly move elements by index and set currentstar to be -1 again
	    if(currentStar==-1)
	    {
	    	//in this way will just remove the first star
	    	currentStar=0;
	    }
	    this.starColorG.remove(currentStar);
	    this.starPosition.remove(currentStar);


	}
	//the folling are the criteria
		//720-hopy-25+0>sidewalk*0.1
		//720-hopy-25+52<127-sidewalk*0.1
	//set the hopscotch up and down
	public void sethopydown()
	{
		//the top of the scopth has to be less than this inoder to go top
		int movestep=(int)(sidewalkLength*0.1);
		if(720-this.hopy-25+0>movestep)
		 {
			this.hopy=this.hopy+movestep;
		}
		
	}
	public void sethopyup()
	{
		int movestep=(int)(sidewalkLength*0.1);
		//teh bottom of the scotch needs to be bigger than this
		if(720-this.hopy-25+52<127-movestep)
		{
			this.hopy=this.hopy-movestep;
		}
	}

	public void increaseFenceHeight()
	{
		this.fenceHeight=this.fenceHeight+20;
	}


	public void decreaseFenceHeight()
	{
		this.fenceHeight=this.fenceHeight-20;
	}


	public Point2D.Double	getCursor()
	{
		return cursor;
	}

	public void		setCursor(Point2D.Double cursor)
	{
		this.cursor = cursor;
		canvas.repaint();
	}

	public void		clear()
	{
		points.clear();
		canvas.repaint();
	}

	public void		add(Point2D.Double p)
	{
		points.add(p);
		canvas.repaint();
	}

	//**********************************************************************
	// Public Methods
	//**********************************************************************

	public Component	getComponent()
	{
		return (Component)canvas;
	}

	public GLAutoDrawable getCanvas()
	{
		return (GLAutoDrawable)(canvas);
	}


	//**********************************************************************
	// Override Methods (GLEventListener)
	//**********************************************************************

	public void		init(GLAutoDrawable drawable)
	{
		w = drawable.getWidth();
		h = drawable.getHeight();

		renderer = new TextRenderer(new Font("Monospaced", Font.PLAIN, 12),
									true, true);
	}

	public void		dispose(GLAutoDrawable drawable)
	{
		renderer = null;
	}

	public void		display(GLAutoDrawable drawable)
	{
		updateProjection(drawable);

		update(drawable);
		render(drawable);
	}

	public void		reshape(GLAutoDrawable drawable, int x, int y, int w, int h)
	{
		this.w = w;
		this.h = h;
	}

	//**********************************************************************
	// Private Methods (Viewport)
	//**********************************************************************

	private void	updateProjection(GLAutoDrawable drawable)
	{
		GL2		gl = drawable.getGL().getGL2();
		GLU		glu = new GLU();

		float	xmin = (float)(origin.x - 1.0);
		float	xmax = (float)(origin.x + 1.0);
		float	ymin = (float)(origin.y - 1.0);
		float	ymax = (float)(origin.y + 1.0);

		gl.glMatrixMode(GL2.GL_PROJECTION);			// Prepare for matrix xform
		gl.glLoadIdentity();						// Set to identity matrix
		glu.gluOrtho2D(xmin, xmax, ymin, ymax);		// 2D translate and scale
	}

	//**********************************************************************
	// Private Methods (Rendering)
	//**********************************************************************

	private void	update(GLAutoDrawable drawable)
	{
		counter++;								// Counters are useful, right?
	}

	public void	render(GLAutoDrawable drawable)
	{
		// GL2		gl = drawable.getGL().getGL2();

		// gl.glClear(GL.GL_COLOR_BUFFER_BIT);		// Clear the buffer
		// drawBounds(gl);							// Unit bounding box
		// drawAxes(gl);							// X and Y axes
		// drawCursor(gl);							// Crosshairs at mouse location
		// drawCursorCoordinates(drawable);		// Draw some text
		// drawPolyline(gl);						// Draw the user's sketch
		GL2		gl = drawable.getGL().getGL2();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT);		// Clear the buffer

		// Make the sky gradient easier by enabling alpha blending.
		// Note: OpenGL supports translucency very poorly!
		gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);

		setLorenzProjection(gl);
		drawLorenz(gl);

		setScreenProjection(gl);
		drawSky(gl);
		drawGround(gl);
		drawSidewalkBase(gl);
		if(this.drawKiteLineBool)
		{
			   drawKiteLineDrag(gl);
		}
     
		drawStars(gl);
		drawMoon(gl);
		drawSidewalk(gl);
		drawHopscotch(gl,this.hopx,this.hopy);
		drawHouses(gl);
		drawFence(gl,fenceHeight);
		drawKite(gl);
		if(this.movekite)
		{
			drawCustomKiteLine(gl);
		}
		//even though u don't have the animator, the rendering is keep being called!
		//System.out.println("I am keep rendering stuff!!!!");
	}

	//**********************************************************************
	// Private Methods (Scene)
	//**********************************************************************

	private void	drawBounds(GL2 gl)
	{
		gl.glColor3f(0.1f, 0.1f, 0.1f);
		gl.glBegin(GL.GL_LINE_LOOP);

		gl.glVertex2d(1.0, 1.0);
		gl.glVertex2d(-1.0, 1.0);
		gl.glVertex2d(-1.0, -1.0);
		gl.glVertex2d(1.0, -1.0);

		gl.glEnd();
	}


    private void drawKiteLineDrag(GL2 gl)
    {
    		gl.glColor3f(1.0f, 1.0f, 0.0f);
			gl.glBegin(GL.GL_LINE_STRIP);
			gl.glVertex2d(this.fencex, this.fencey);
			gl.glVertex2d(this.kitex,this.kitey);
			gl.glEnd();
    	
    }
    private void drawKiteZigZag(GL2 gl)
    {

    }

	private void	drawAxes(GL2 gl)
	{
		gl.glBegin(GL.GL_LINES);

		gl.glColor3f(0.25f, 0.25f, 0.25f);
		gl.glVertex2d(-10.0, 0.0);
		gl.glVertex2d(10.0, 0.0);

		gl.glVertex2d(0.0, -10.0);
		gl.glVertex2d(0.0, 10.0);

		gl.glEnd();
	}

	private void	drawCursor(GL2 gl)
	{
		if (cursor == null)
			return;

		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glColor3f(0.5f, 0.5f, 0.5f);

		for (int i=0; i<32; i++)
		{
			double	theta = (2.0 * Math.PI) * (i / 32.0);

			gl.glVertex2d(cursor.x + 0.05 * Math.cos(theta),
						  cursor.y + 0.05 * Math.sin(theta));
		}

		gl.glEnd();
	}

	private void	drawCursorCoordinates(GLAutoDrawable drawable)
	{
		if (cursor == null)
			return;

		String	sx = FORMAT.format(new Double(cursor.x));
		String	sy = FORMAT.format(new Double(cursor.y));
		String	s = "(" + sx + "," + sy + ")";

		renderer.beginRendering(drawable.getWidth(), drawable.getHeight());
		renderer.setColor(1.0f, 1.0f, 0, 1.0f);
		renderer.draw(s, 2, 2);
		renderer.endRendering();
	}

	private void	drawPolyline(GL2 gl)
	{
		gl.glColor3f(1.0f, 0.0f, 0.0f);

		for (Point2D.Double p : points)
		{
			gl.glBegin(GL2.GL_POLYGON);

			gl.glVertex2d(p.x - 0.01, p.y - 0.01);
			gl.glVertex2d(p.x - 0.01, p.y + 0.01);
			gl.glVertex2d(p.x + 0.01, p.y + 0.01);
			gl.glVertex2d(p.x + 0.01, p.y - 0.01);

			gl.glEnd();
		}

		gl.glColor3f(1.0f, 1.0f, 0.0f);
		gl.glBegin(GL.GL_LINE_STRIP);

		for (Point2D.Double p : points)
			gl.glVertex2d(p.x, p.y);

		gl.glEnd();
	}

	//**********************************************************************
	// Private Methods (Coordinate System)
	//**********************************************************************

	private void	setLorenzProjection(GL2 gl)
	{
		GLU		glu = new GLU();

		gl.glMatrixMode(GL2.GL_PROJECTION);			// Prepare for matrix xform
		gl.glLoadIdentity();						// Set to identity matrix
		glu.gluOrtho2D(-1.0f, 1.0f, -1.45f, 1.0f);	// 2D translate and scale
	}

	private void	setScreenProjection(GL2 gl)
	{
		GLU		glu = new GLU();

		gl.glMatrixMode(GL2.GL_PROJECTION);			// Prepare for matrix xform
		gl.glLoadIdentity();						// Set to identity matrix
		glu.gluOrtho2D(0.0f, 1280.0f, 0.0f, 720.0f);// 2D translate and scale
	}

	//**********************************************************************
	// Private Methods (Scene)
	//**********************************************************************

	// I faded the galaxy a bit to experiment with animation.
	private void	drawLorenz(GL2 gl)
	{
		gl.glBegin(GL.GL_POINTS);
		gl.glColor3f(1.0f, 1.0f, 1.0f);

		double		dt = 0.01;
		double		sigma = 10.0;
		double		beta = 8.0 / 3.0;
		double		rho = 28.0;
		double		lx = 0.1;
		double		ly = 0.0;
		double		lz = 0.0;

		for (int i=0; i<10000; i++)
		{
			double	llx = lx + dt * sigma * (ly - lx);
			double	lly = ly + dt * (lx * (rho - lz) - ly);
			double	llz = lz + dt * (lx * ly - beta * lz);

			lx = llx;
			ly = lly;
			lz = llz;
			//System.out.println(" " + lx + " " + ly + " " + lz);
			float	cc = (float)((lz + 30.0) / 60.0);

			if (Math.abs(k % 10000 - i) < 20)	// Window moves with animation
				setColor(gl, 255, 32, 32);		// Some dots red, cycling around
			else
				gl.glColor4f(cc, cc, cc, 0.25f);

			gl.glVertex2d(-lx / 30.0, ly / 30.0);
		}

		gl.glEnd();
	}

	private void	drawSky(GL2 gl)
	{
		gl.glBegin(GL2.GL_QUADS);

		setColor(gl, 128, 112, 80);				// Opaque gold on horizon
		gl.glVertex2i(0, this.skyline);
		gl.glVertex2i(1280, this.skyline);
		setColor(gl, 32, 48, 96, 64);			// Translucent dark blue at top
		gl.glVertex2i(1280, 720);
		gl.glVertex2i(0, 720);

		gl.glEnd();
	}

	private void	drawGround(GL2 gl)
	{
		gl.glBegin(GL2.GL_QUADS);

		setColor(gl, 64, 48, 48);				// Red-purple on horizon
		gl.glVertex2i(0, this.skyline);
		gl.glVertex2i(1280, this.skyline);
		setColor(gl, 80, 128, 64);				// Moss green by sidewalk
		gl.glVertex2i(1280, 129);
		gl.glVertex2i(0, 129);

		gl.glEnd();
	}

	private void	drawSidewalkBase(GL2 gl)
	{
		gl.glBegin(GL2.GL_QUADS);

		setColor(gl, 255, 255, 255);			// White
		gl.glVertex2i(0, 0);
		gl.glVertex2i(1280, 0);
		gl.glVertex2i(1280, 129);
		gl.glVertex2i(0, 129);

		gl.glEnd();
	}

	private void	drawSidewalk(GL2 gl)
	{
		for (int i=-1; i<16; i++)
			drawSidewalkSlab(gl, i * (this.sidewalkLength));
	}

	private void	drawSidewalkSlab(GL2 gl, int dx)
	{
		gl.glBegin(GL2.GL_QUADS);

		setColor(gl, 128, 128, 128);			// Medium gray
		gl.glVertex2i(dx +  34,   2);
		gl.glVertex2i(dx +  57, 127);
		gl.glVertex2i(dx + 134, 127);
		gl.glVertex2i(dx + 111,   2);

		gl.glEnd();
	}

	private void	drawHopscotch(GL2 gl, int hopx,int hopy)
	{
		//hopy=634
		//hopx=673	
		drawHopscotchSquare(gl, hopx, 720-(hopy-12)-25);
		drawHopscotchSquare(gl, hopx+31, 720-(hopy-12)-25);
		drawHopscotchSquare(gl, hopx+ 63, 720-(hopy-12)-25);
		//the bottom and top of the square decid ehow far the hopscotch can go within the sideawalk slab.
		//720-hopy-25+0>sidewalk*0.1
		//720-hopy-25+52<127-sidewalk*0.1
		drawHopscotchSquare(gl, hopx+91, 720-hopy-25);
		//720-(hopy-26)-25+26<127
		drawHopscotchSquare(gl, hopx+97, 720-(hopy-26)-25);

		drawHopscotchSquare(gl, hopx+125, 720-(hopy-14)-25);

		drawHopscotchSquare(gl, hopx+153, 720-(hopy-3)-25);
		drawHopscotchSquare(gl, hopx+159, 720-(hopy-28)-25);

		drawHopscotchSquare(gl, hopx+188, 720-(hopy-14)-25);
	}

	private void	drawHopscotchSquare(GL2 gl, int dx, int dy)
	{
		setColor(gl, 255, 255, 192, 128);			// Taupe + alpha
		gl.glBegin(GL2.GL_POLYGON);
		doHopscotchLoop(gl, dx, dy);
		gl.glEnd();

		// This approach cuts off the corners
		// Could do this better by drawing four trapezoid using GL_QUADS
		setColor(gl, 229, 229, 229);				// Light gray
		gl.glLineWidth(3);
		gl.glBegin(GL2.GL_LINE_LOOP);
		doHopscotchLoop(gl, dx, dy);
		gl.glEnd();
		gl.glLineWidth(1);
	}

	private void	doHopscotchLoop(GL2 gl, int dx, int dy)
	{
		gl.glVertex2i(dx +  0, dy +  0);
		gl.glVertex2i(dx +  5, dy + 25);
		gl.glVertex2i(dx + 35, dy + 25);
		gl.glVertex2i(dx + 30, dy +  0);
	}

	private void	drawFence(GL2 gl,int h)
	{
		drawFenceSlat(gl, false,    6, 132,h);
		drawFenceSlat(gl, false,   30, 132,h);
		drawFenceSlat(gl, false,   54, 132,h);
		drawFenceSlat(gl, false,   78, 132,h);

		drawFenceSlat(gl, false,  290, 132,h);
		drawFenceSlat(gl, false,  314, 132,h);
		drawFenceSlat(gl, false,  338, 132,h);
		drawFenceSlat(gl, false,  362, 132,h);

		drawFenceSlat(gl, false,  391, 132,h);
		drawFenceSlat(gl, false,  415, 132,h);
		drawFenceSlat(gl, false,  439, 132,h);
		drawFenceSlat(gl, false,  463, 132,h);

		drawFenceSlat(gl, false,  856, 132,h);
		drawFenceSlat(gl, true,   880, 132,h);
		drawFenceSlat(gl, false,  904, 132,h);
		drawFenceSlat(gl, true,   928, 132,h);
		drawFenceSlat(gl, false,  952, 132,h);
		drawFenceSlat(gl, true,   976, 132,h);
		drawFenceSlat(gl, false, 1000, 132,h);
		drawFenceSlat(gl, true,  1024, 132,h);

		drawFenceSlat(gl, false, 1224, 132,h);
		drawFenceSlat(gl, true,  1248, 132,h);
	}

	// Draws a single fence slat with bottom left corner at dx, dy.
	// If flip is true, the slat is higher on the left, else on the right.
	private void	drawFenceSlat(GL2 gl, boolean flip, int dx, int dy,int h)
	{
		gl.glBegin(GL2.GL_POLYGON);					// Fill the slat, in...

		setColor(gl, 192, 192, 128);				// ...tan
		gl.glVertex2i(dx +  0, dy +   0);
		gl.glVertex2i(dx +  0, dy + (flip ? h+10 : h));
		gl.glVertex2i(dx + 24, dy + (flip ? h : h+10));
		gl.glVertex2i(dx + 24, dy +   0);

		gl.glEnd();

		gl.glBegin(GL2.GL_LINE_LOOP);				// Edge the slat, in...

		setColor(gl, 0, 0, 0);						// ...black
		gl.glVertex2i(dx +  0, dy +   0);
		gl.glVertex2i(dx +  0, dy + (flip ? h+10 : h));
		gl.glVertex2i(dx + 24, dy + (flip ? h : h+10));
		gl.glVertex2i(dx + 24, dy +   0);

		gl.glEnd();
	}
	private void	drawStars(GL2 gl)
	{
		setColor(gl, 255, this.starColorG.get(0), 0, (int)(1.00f * 255));
		drawStar(gl,  this.starPosition.get(0).x, this.starPosition.get(0).y);
		setColor(gl, 255, this.starColorG.get(1), 0, (int)(0.90f * 255));
		drawStar(gl, this.starPosition.get(1).x, this.starPosition.get(1).y);
		setColor(gl, 255, this.starColorG.get(2), 0, (int)(0.95f* 255));
		drawStar(gl, this.starPosition.get(2).x, this.starPosition.get(2).y);
		setColor(gl, 255, this.starColorG.get(3), 0, (int)(0.50f * 255));
		drawStar(gl, this.starPosition.get(3).x, this.starPosition.get(3).y);
		setColor(gl, 255, this.starColorG.get(4), 0, (int)(0.30f * 255));
		drawStar(gl, this.starPosition.get(4).x, this.starPosition.get(4).y);

		//need to draw extra star if it has more than 5 stars there.
		if(this.starColorG.size()>5)
		{
		   for(int index=5;index<this.starColorG.size();index++)
		   {
		   	setColor(gl, 255, this.starColorG.get(index), 0, (int)(1.00f * 255));
		    drawStar(gl,  this.starPosition.get(index).x, this.starPosition.get(index).y);

		   }
		}
	}

	private void	drawStar(GL2 gl, int cx, int cy)
	{
		double	theta = 0.5 * Math.PI;
		// need to set the secodn 255 into 165 if needs to change it into orange.
		//setColor(gl, 255, this.colorG, 0, (int)(alpha * 255));	// Yellow + alpha
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glVertex2d(cx, cy);
		doStarVertices(gl, cx, cy, 8, 20.0, 8.0);
		gl.glVertex2d(cx + 15 * Math.cos(theta), cy + 15 * Math.sin(theta));
		gl.glEnd();
	}

	private static final int		SIDES_MOON = 18;
	private static final double		ANGLE_MOON = 2.0 * Math.PI / SIDES_MOON;

	private void	drawMoon(GL2 gl)
	{
		double	theta = 0.20 * ANGLE_MOON;
		int		cx = 94;
		int		cy = 720 - 92;
		int		r = 59;

		// Fill the whole moon in white
		gl.glBegin(GL.GL_TRIANGLE_FAN);

		setColor(gl, 255, 255, 255);				// White
		gl.glVertex2d(cx, cy);

		for (int i=0; i<SIDES_MOON+1; i++)			// 18 sides
		{
			gl.glVertex2d(cx + r * Math.cos(theta), cy + r * Math.sin(theta));
			theta += ANGLE_MOON;
		}

		gl.glEnd();

		// Fill the outside shadow in dark bluish gray
		theta = -1.80 * ANGLE_MOON;

		gl.glBegin(GL.GL_TRIANGLE_FAN);

		setColor(gl, 64, 64, 80);
		gl.glVertex2d(cx, cy);

		for (int i=0; i<8; i++)						// 7 sides
		{
			gl.glVertex2d(cx + r * Math.cos(theta), cy + r * Math.sin(theta));
			theta += ANGLE_MOON;
		}

		gl.glEnd();

		// Fill the inside shadow in dark bluish gray
		theta = 1.50 * ANGLE_MOON;
		cx = 128;
		cy = 650;
		theta = 7.2 * ANGLE_MOON;

		gl.glBegin(GL.GL_TRIANGLE_FAN);

		setColor(gl, 64, 64, 80);
		gl.glVertex2d(cx, cy);

		for (int i=0; i<8; i++)						// 7 sides
		{
			gl.glVertex2d(cx + r * Math.cos(theta), cy + r * Math.sin(theta));
			theta += ANGLE_MOON;
		}

		gl.glEnd();
	}

	private void	drawKite(GL2 gl)
	{
		drawKiteLine(gl);
		drawKiteFans(gl);
	}

	private ArrayList<Point>	kiteline = null;

	// Keep this simpler than the drawing, since HW#3 will define the
	private void	drawKiteLine(GL2 gl)
	{
		if (kiteline == null)
		{
			kiteline = new ArrayList<Point>();
			kiteline.add(new Point(1024, 244));
			kiteline.add(new Point( 964, 272));
			kiteline.add(new Point( 924, 364));
			kiteline.add(new Point( 928, 396));
			kiteline.add(new Point( 900, 428));
			kiteline.add(new Point( 912, 464));
			kiteline.add(new Point( 936, 472));
			kiteline.add(new Point( 956, 490));
		}

		setColor(gl, 128, 128, 96);
		gl.glLineWidth(2);
		gl.glBegin(GL.GL_LINE_STRIP);

		for (Point p : kiteline)
			gl.glVertex2i(p.x, p.y);

		gl.glEnd();
		gl.glLineWidth(1);
	}

	private void drawCustomKiteLine(GL2 gl)
	{
	    setColor(gl, 128, 128, 96);
		gl.glLineWidth(2);
		gl.glBegin(GL.GL_LINE_STRIP);

		for (Point p : this.kitelinedrag)
			gl.glVertex2i(p.x, p.y);
		gl.glEnd();
		gl.glLineWidth(1);	

	}

	private void	drawKiteFans(GL2 gl)
	{
		int		cx = this.kitex;
		int		cy = this.kitey;
		int		r = 80;

		// Flap those wings!
		int		ticks = 120;
		double	phase = ((k % (2 * ticks)) - ticks) / (double)ticks;
		double	variance = ANGLE_MOON * Math.cos(2 * Math.PI * phase);

		// The min and max angles of each wing, with variance over time
		double	amin =  4.0 * ANGLE_MOON - variance;
		double	amax =  9.0 * ANGLE_MOON + variance;
		double	bmin = 13.0 * ANGLE_MOON - variance;
		double	bmax = 18.0 * ANGLE_MOON + variance;

		int		fans = this.fanNo;
		double	astep = (amax - amin) / fans;
		double	bstep = (bmax - bmin) / fans;

		for (int i=0; i<fans; i++)
		{
			double	a = amin + astep * i;
			double	b = bmin + bstep * i;

			drawKiteBlade(gl, cx, cy, r, a, a + astep);		// Upper blade
			drawKiteBlade(gl, cx, cy, r, b, b + bstep);		// Lower blade
		}
	}

	private void	drawKiteBlade(GL2 gl, int cx, int cy, int r,
								  double a1, double a2)
	{
		// Fill in the blade
		if(this.duringDrag)
		{
			setColor(gl, 48, 80, 224,20);	
		}
		else
		{
			setColor(gl,48,80,224);
		}
		
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex2d(cx, cy);
		gl.glVertex2d(cx + r * Math.cos(a1), cy + r * Math.sin(a1));
		gl.glVertex2d(cx + r * Math.cos(a2), cy + r * Math.sin(a2));
		gl.glEnd();

		// Draw the thin struts
		setColor(gl, 96, 96, 96);
		gl.glBegin(GL.GL_LINE_STRIP);
		gl.glVertex2d(cx, cy);
		gl.glVertex2d(cx + r * Math.cos(a1), cy + r * Math.sin(a1));
		gl.glVertex2d(cx + r * Math.cos(a2), cy + r * Math.sin(a2));
		gl.glVertex2d(cx, cy);
		gl.glEnd();

		// Draw the thick translucent edges
		setColor(gl, 128, 128, 128, 64);
		gl.glLineWidth(6);
		gl.glBegin(GL.GL_LINE_STRIP);
		gl.glVertex2d(cx, cy);
		gl.glVertex2d(cx + r * Math.cos(a1), cy + r * Math.sin(a1));
		gl.glVertex2d(cx + r * Math.cos(a2), cy + r * Math.sin(a2));
		gl.glVertex2d(cx, cy);
		gl.glEnd();
		gl.glLineWidth(1);
	}

	private static final Point[]		HOUSE_OUTLINE = new Point[]
	{
		new Point(0, 0),		// lower left corner
		new Point(0, 162),		// bottom left corner
		new Point(88, 250),		// apex
		new Point(176, 162),	// top right corner
		new Point(176, 0),		// bottom left corner
	};

	private static final Point[]		HOUSE_OUTLINE1 = new Point[]
	{
		new Point(-1, -1),		// lower left corner
		new Point(-1, 162),		// bottom left corner
		new Point(88, 251),		// apex
		new Point(177, 162),	// top right corner
		new Point(177, -1),		// bottom left corner
	};

	// Too much variation to encapsulate house drawing in a drawHouse() method
	private void	drawHouses(GL2 gl)
	{

		int		tx = 108;
		int		ty = 132;

		drawChimney(gl, tx + 114, ty + 162, true);
		drawOutline(gl, tx, ty, 0, 1);
		drawRoof(gl, tx + 88, ty + 250);
		drawWindow(gl, tx + 127, ty + 127, this.shade);
		drawDoor(gl, tx + 39, ty);

		tx = 634;
		ty = 158;

		drawChimney(gl, tx + 30, ty + 162, false);
		drawOutline(gl, tx, ty, 1, 2);
		drawWindow(gl, tx + 98, ty + 64, this.shade);
		drawWindow(gl, tx + 144, ty + 64, this.shade);
		drawDoor(gl, tx + 7, ty);
		drawHouseStar(gl, tx + 88, ty + 200);

		tx = 1048;
		ty = 132;

		drawChimney(gl, tx + 30, ty + 162, false);
		drawOutline(gl, tx, ty, 2, 2);
		drawWindow(gl, tx + 98, ty + 64, this.shade);
		drawWindow(gl, tx + 144, ty + 64, this.shade);
		drawDoor(gl, tx + 7, ty);
		drawDoorWindow(gl, tx + 27, ty + 71);
	}

	private void	drawChimney(GL2 gl, int sx, int sy, boolean smoke)
	{
		setColor(gl, 128, 0, 0);					// Firebrick red
		fillRect(gl, sx, sy, 30, 88);

		setColor(gl, 0, 0, 0);						// Black
		drawRect(gl, sx, sy, 30, 88);

		if (smoke)
			drawSmoke(gl, sx + 3, sy + 88);
	}

	private LinkedList<Point>	smoke = new LinkedList<Point>();

	// The picture's quads are boring...let's have some fun with animation!
	private void	drawSmoke(GL2 gl, int sx, int sy)
	{
		// Random walk up to two pixels on each end of the previous smoke line
		// Each point in the list defines (xmin, xmax) for a smoke line
		Point	p = ((smoke.size() == 0) ?
					 new Point(3, 27) : smoke.getFirst());
		int		ql = Math.min(30, Math.max( 0, p.x + RANDOM.nextInt(5) - 2));
		int		qr = Math.max( 0, Math.min(30, p.y + RANDOM.nextInt(5) - 2));
		Point	q = ((ql < qr) ? new Point(ql, qr) : new Point(qr, ql));

		smoke.addFirst(q);			// Add the lowest line to beginning

		if (smoke.size() > 255)		// If it's long enough,
			smoke.removeLast();		// remove the highest (=transparent) line

		int		alpha = 0;			// For opaque line closest to the chimney

		for (Point a : smoke)		// Draw all the lines lowest to highest,
		{
			if (RANDOM.nextInt(1024) < alpha)	// simulate diffusion leftward
				a.x--;

			if (RANDOM.nextInt(1024) < alpha)	// and rightward
				a.y++;

			setColor(gl, 255, 255, 255, 255 - alpha++);	// fading along the way

			gl.glBegin(GL2.GL_LINES);
			gl.glVertex2i(sx + a.x, sy + alpha + 1);	// as height goes up
			gl.glVertex2i(sx + a.y, sy + alpha + 1);
			gl.glEnd();
		}
	}

	private void	drawOutline(GL2 gl, int sx, int sy, int shade, int thickness)
	{
		if (shade == 0)
			setColor(gl, 64, 64, 0);				// Dark green
		else if (shade == 1)
			setColor(gl, 143, 82, 10);				// Medium brown
		else
			setColor(gl, 128, 64, 0);				// Medium brown

		fillPoly(gl, sx, sy, HOUSE_OUTLINE);

		setColor(gl, 0, 0, 0);						// Black
		gl.glLineWidth(thickness);
		drawPoly(gl, sx, sy, HOUSE_OUTLINE);
		gl.glLineWidth(1);
	}

	private void	drawRoof(GL2 gl, int cx, int cy)
	{
		setColor(gl, 80, 64, 32);					// Dark brown

		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glVertex2i(cx, cy);
		gl.glVertex2i(cx - 88, cy - 88);
		gl.glVertex2i(cx - 56, cy - 88);
		gl.glVertex2i(cx - 24, cy - 88);
		gl.glVertex2i(cx + 24, cy - 88);
		gl.glVertex2i(cx + 56, cy - 88);
		gl.glVertex2i(cx + 88, cy - 88);
		gl.glEnd();

		setColor(gl, 0, 0, 0);						// Black

		gl.glBegin(GL.GL_LINE_STRIP);				// Leftmost board
		gl.glVertex2i(cx, cy);
		gl.glVertex2i(cx - 88, cy - 88);
		gl.glVertex2i(cx - 56, cy - 88);
		gl.glVertex2i(cx, cy);
		gl.glEnd();

		gl.glBegin(GL.GL_LINE_STRIP);				// Left-center board
		gl.glVertex2i(cx, cy);
		gl.glVertex2i(cx - 56, cy - 88);
		gl.glVertex2i(cx - 24, cy - 88);
		gl.glVertex2i(cx, cy);
		gl.glEnd();

		gl.glBegin(GL.GL_LINE_STRIP);				// Center board
		gl.glVertex2i(cx, cy);
		gl.glVertex2i(cx - 24, cy - 88);
		gl.glVertex2i(cx + 24, cy - 88);
		gl.glVertex2i(cx, cy);
		gl.glEnd();

		gl.glBegin(GL.GL_LINE_STRIP);				// Right-center board
		gl.glVertex2i(cx, cy);
		gl.glVertex2i(cx + 24, cy - 88);
		gl.glVertex2i(cx + 56, cy - 88);
		gl.glVertex2i(cx, cy);
		gl.glEnd();

		gl.glBegin(GL.GL_LINE_STRIP);				// Rightmost board
		gl.glVertex2i(cx, cy);
		gl.glVertex2i(cx + 56, cy - 88);
		gl.glVertex2i(cx + 88, cy - 88);
		gl.glVertex2i(cx, cy);
		gl.glEnd();
	}

	private void	drawDoor(GL2 gl, int cx, int cy)
	{
		setColor(gl, 192, 128, 0);					// Light brown
		fillRect(gl, cx, cy, 40, 92);

		setColor(gl, 0, 0, 0);						// Black
		drawRect(gl, cx, cy, 40, 92);

		setColor(gl, 176, 192, 192);				// Light steel
		fillOval(gl, cx + 8, cy + 46, 4, 4);

		setColor(gl, 0, 0, 0);						// Black
		drawOval(gl, cx + 8, cy + 46, 4, 4);
	}

	private void	drawWindow(GL2 gl, int cx, int cy, boolean shade)
	{
		int		dx = 20;
		int		dy = 20;

		setColor(gl, 255, 255, 128);				// Light yellow
		fillRect(gl, cx - dx, cy - dy, 2 * dx, 2 * dy);

		if (shade)
			setColor(gl, 224, 224, 224);			// Light gray
		else
			setColor(gl, 224, 192, 224);			// Light pink

		gl.glBegin(GL2.GL_POLYGON);					// Left shade fill
		gl.glVertex2i(cx - dx, cy - dy);
		gl.glVertex2i(cx - dx, cy + dy);
		gl.glVertex2i(cx     , cy + dy);
		gl.glEnd();

		gl.glBegin(GL2.GL_POLYGON);					// Right shade fill
		gl.glVertex2i(cx     , cy + dy);
		gl.glVertex2i(cx + dx, cy + dy);
		gl.glVertex2i(cx + dx, cy - dy);
		gl.glEnd();

		setColor(gl, 0, 0, 0);						// Black

		gl.glBegin(GL2.GL_LINE_LOOP);				// Left shade edge
		gl.glVertex2i(cx - dx, cy - dy);
		gl.glVertex2i(cx - dx, cy + dy);
		gl.glVertex2i(cx     , cy + dy);
		gl.glEnd();

		gl.glBegin(GL2.GL_LINE_LOOP);				// Right shade edge
		gl.glVertex2i(cx     , cy + dy);
		gl.glVertex2i(cx + dx, cy + dy);
		gl.glVertex2i(cx + dx, cy - dy);
		gl.glEnd();

		setColor(gl, 0, 0, 0);						// Black

		// Window frame: bottom, middle, top
		fillRect(gl, cx - dx - 1, cy - dy - 1, 2 * dx + 3, 3);
		fillRect(gl, cx - dx - 1, cy +  0 - 1, 2 * dx + 3, 3);
		fillRect(gl, cx - dx - 1, cy + dy - 1, 2 * dx + 3, 3);

		// Window frame: left, middle, right
		fillRect(gl, cx - dx - 1, cy - dy - 1, 3, 2 * dy + 3);
		fillRect(gl, cx +  0 - 1, cy - dy - 1, 3, 2 * dy + 3);
		fillRect(gl, cx + dx - 1, cy - dy - 1, 3, 2 * dy + 3);

		// Could use LINE_STRIP for the thick window frames instead
	}

	private void	drawHouseStar(GL2 gl, int cx, int cy)
	{
		double	theta = 0.5 * Math.PI;

		setColor(gl, 255, 255, 0);
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glVertex2d(cx, cy);
		doStarVertices(gl, cx, cy, 5, 20.0, 8.0);
		gl.glVertex2d(cx + 20 * Math.cos(theta), cy + 20 * Math.sin(theta));
		gl.glEnd();

		setColor(gl, 0, 0, 0);
		gl.glBegin(GL.GL_LINE_STRIP);
		doStarVertices(gl, cx, cy, 5, 20.0, 8.0);
		gl.glVertex2d(cx + 20 * Math.cos(theta), cy + 20 * Math.sin(theta));
		gl.glEnd();
	}

	private void	drawDoorWindow(GL2 gl, int cx, int cy)
	{
		double	theta = 0.5 * Math.PI;

		setColor(gl, 255, 255, 128);
		gl.glBegin(GL.GL_TRIANGLE_FAN);
		gl.glVertex2d(cx, cy);
		doStarVertices(gl, cx, cy, 4, 15.0, 13.5);
		gl.glVertex2d(cx + 15 * Math.cos(theta), cy + 15 * Math.sin(theta));
		gl.glEnd();

		setColor(gl, 0, 0, 0);
		gl.glBegin(GL.GL_LINE_STRIP);
		doStarVertices(gl, cx, cy, 4, 15.0, 13.5);
		gl.glVertex2d(cx + 15 * Math.cos(theta), cy + 15 * Math.sin(theta));
		gl.glEnd();
	}

	//**********************************************************************
	// Private Methods (Utility Functions)
	//**********************************************************************

	private void	setColor(GL2 gl, int r, int g, int b, int a)
	{
		gl.glColor4f(r / 255.0f, g / 255.0f, b / 255.0f, a / 255.0f);
	}

	private void	setColor(GL2 gl, int r, int g, int b)
	{
		setColor(gl, r, g, b, 255);
	}

	private void	fillRect(GL2 gl, int x, int y, int w, int h)
	{
		gl.glBegin(GL2.GL_POLYGON);
		gl.glVertex2i(x+0, y+0);
		gl.glVertex2i(x+0, y+h);
		gl.glVertex2i(x+w, y+h);
		gl.glVertex2i(x+w, y+0);
		gl.glEnd();
	}

	private void	drawRect(GL2 gl, int x, int y, int w, int h)
	{
		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glVertex2i(x+0, y+0);
		gl.glVertex2i(x+0, y+h);
		gl.glVertex2i(x+w, y+h);
		gl.glVertex2i(x+w, y+0);
		gl.glEnd();
	}

	private void	fillOval(GL2 gl, int cx, int cy, int w, int h)
	{
		gl.glBegin(GL2.GL_POLYGON);

		for (int i=0; i<32; i++)
		{
			double	a = (2.0 * Math.PI) * (i / 32.0);

			gl.glVertex2d(cx + w * Math.cos(a), cy + h * Math.sin(a));
		}

		gl.glEnd();
	}

	private void	drawOval(GL2 gl, int cx, int cy, int w, int h)
	{
		gl.glBegin(GL.GL_LINE_LOOP);

		for (int i=0; i<32; i++)
		{
			double	a = (2.0 * Math.PI) * (i / 32.0);

			gl.glVertex2d(cx + w * Math.cos(a), cy + h * Math.sin(a));
		}

		gl.glEnd();
	}

	private void	fillPoly(GL2 gl, int startx, int starty, Point[] offsets)
	{
		gl.glBegin(GL2.GL_POLYGON);

		for (int i=0; i<offsets.length; i++)
			gl.glVertex2i(startx + offsets[i].x, starty + offsets[i].y);

		gl.glEnd();
	}

	private void	drawPoly(GL2 gl, int startx, int starty, Point[] offsets)
	{
		gl.glBegin(GL2.GL_LINE_LOOP);

		for (int i=0; i<offsets.length; i++)
			gl.glVertex2i(startx + offsets[i].x, starty + offsets[i].y);

		gl.glEnd();
	}

	private void	doStarVertices(GL2 gl, int cx, int cy, int sides,
								   double r1, double r2)
	{
		double	delta = Math.PI / sides;
		double	theta = 0.5 * Math.PI;

		for (int i=0; i<sides; i++)
		{
			gl.glVertex2d(cx + r1 * Math.cos(theta), cy + r1 * Math.sin(theta));
			theta += delta;

			gl.glVertex2d(cx + r2 * Math.cos(theta), cy + r2 * Math.sin(theta));
			theta += delta;
		}
	}
}

//******************************************************************************
