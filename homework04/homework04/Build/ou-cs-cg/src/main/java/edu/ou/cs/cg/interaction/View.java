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

package edu.ou.cs.cg.interaction;

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
//this is to generate random number for the direction
import java.util.concurrent.ThreadLocalRandom;
import java.lang.Math;

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
	private float vx=0.0f;
    private float vy=0.0f;
    //60fps, 2 secs,is 120 slides, so each slides should move 1.8f/120=0.015f.
    private  float speed=0.015f;
    private float xleft=-0.9f;
    private float xright=0.9f;
    private float ybottom=-0.9f;
    private float ytop=0.9f;
    private float startx=-0.8f;
    private float starty=0.0f;
    private float currentx=0.0f;
    private float currenty=0.0f;
    private ArrayList<Point2D.Float> pointlist;
    //1 is the box, 2 is the regular hexagon ,2 is the 32 hexagon for the circle, 4 is the non-regular one
    private int container=1; 

	private TextRenderer			renderer;

	private Point2D.Double				origin;		// Current origin coordinates
	private Point2D.Double				cursor;		// Current cursor coordinates
	private ArrayList<Point2D.Double>	points;		// User's polyline points

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************
	// public class Vector()
	// {
	//    public float x;
	//    public float y;

 //       public Vector(float x, float y)
 //       {
 //       	this.x=x;
 //       	this.y=y;
 //       }
	// }

public class Vector {

   protected float dX;
   protected float dY;

   // Constructor methods ....

   public Vector() {
      dX = dY = 0.0f;
   }

   public Vector( float dX, float dY ) {
      this.dX = dX;
      this.dY = dY;
   }

   // Convert vector to a string ...
    
   public String toString() {
      return "Vector(" + dX + ", " + dY + ")";
   }

   // Compute magnitude of vector ....
 
   public float length() {
      return (float)Math.sqrt ( dX*dX + dY*dY );
   }

   // Sum of two vectors ....

   public Vector add( Vector v1 ) {
       Vector v2 = new Vector( this.dX + v1.dX, this.dY + v1.dY );
       return v2;
   }

   // Subtract vector v1 from v .....

   public Vector subv( Vector v1 ) {
       Vector v2 = new Vector( this.dX - v1.dX, this.dY - v1.dY );
       return v2;
   }

   // Scale vector by a constant ...

   public Vector scale( float scaleFactor ) {
       Vector v2 = new Vector( this.dX*scaleFactor, this.dY*scaleFactor );
       return v2;
   }

   // Normalize a vectors length....

   public Vector normalize() {
      Vector v2 = new Vector();

      float length =(float)Math.sqrt( this.dX*this.dX + this.dY*this.dY );
      if (length != 0) {
        v2.dX = this.dX/length;
        v2.dY = this.dY/length;
      }

      return v2;
   }   

   // Dot product of two vectors .....

   public float dotProduct ( Vector v1 ) {
        return this.dX*v1.dX + this.dY*v1.dY;
   }
}

	public View(GLJPanel canvas)
	{
		this.canvas = canvas;

		// Initialize model
		origin = new Point2D.Double(0.0, 0.0);
		cursor = null;
		points = new ArrayList<Point2D.Double>();

		// Initialize rendering
		canvas.addGLEventListener(this);
		//canvas.setPreferredSize(new Dimension(1280, 720));
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

	public int	getHeight()
	{
		return h;
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

	public Point2D.Double	getCursor()
	{
		return cursor;
	}

	public void		setCursor(Point2D.Double cursor)
	{
		this.cursor = cursor;
		canvas.repaint();
	}
	public void setSpeed(float newspeed)
	{
		this.speed=newspeed;
	}
	public float getSpeed()
	{
		return this.speed;
	}

	public void setContainer(int container)
	{ 
      this.container=container;
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

	//**********************************************************************
	// Override Methods (GLEventListener)
	//**********************************************************************

	public void init(GLAutoDrawable drawable)
	{
		w = drawable.getWidth();
		h = drawable.getHeight();

		renderer = new TextRenderer(new Font("Monospaced", Font.PLAIN, 12),
									true, true);
		float vx1=generateRandom();
		float vy1=generateRandom();
		float len=(float)Math.sqrt(vx1*vx1+vy1*vy1);
		//normalize this direction function.
		vx=vx1/len;
		vy=vy1/len;
	}

    //generate random number from -1 to 1.
	public float generateRandom()
	{
		int min=-50;
		int max=50;
		int randomNum = ThreadLocalRandom.current().nextInt(min, max + 1);
		return (2.0f*((randomNum-min)*1.0f)/((max-min)*1.0f)-1.0f);
	}

	public void		dispose(GLAutoDrawable drawable)
	{
		renderer = null;
	}

	public void		display(GLAutoDrawable drawable)
	{
		updateProjection(drawable);
        GL2		gl = drawable.getGL().getGL2();
       // System.out.println("display has been called a lot of times!");
        
		update(drawable);
		render(drawable,speed,xleft,xright,ybottom,ytop);
	}

	public void		reshape(GLAutoDrawable drawable, int x, int y, int w, int h)
	{
		this.w = w;
		this.h = h;
	}

	//**********************************************************************
	// Private Methods (Viewport)
	//**********************************************************************

	 private Vector getReflectionDirection(Vector incomevec,Vector perpenvec)
	 {	 	
	 	double inner=incomevec.dotProduct(perpenvec);
	 	//System.out.println("inner is: "+inner);
	    return incomevec.subv(perpenvec.scale((float)(2.0d*inner)));
	 	//return incomevec.subv(subvec);
	 }
    

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

	private void	render(GLAutoDrawable drawable, float speed,float xleft, float xright, float ybottom, float ytop)
	{
		GL2		gl = drawable.getGL().getGL2();
		gl.glClear(GL.GL_COLOR_BUFFER_BIT);		// Clear the buffer
		//System.out.println("container: "+container);
		if(container==1)
		{
		 drawRec(gl);	
		}
		else if(container==2)
		{
			drawSixHex(gl);
		}
		movePoint(gl,speed,xleft,xright,ybottom, ytop);
		//generateRandom();
		drawBounds(gl);							// Unit bounding box
		drawAxes(gl);							// X and Y axes
	    drawCursor(gl);							// Crosshairs at mouse location
	    drawCursorCoordinates(drawable);		// Draw some text
	    drawPolyline(gl);						// Draw the user's sketch
	    
	}

	//**********************************************************************
	// Private Methods (Scene)
	//**********************************************************************
	//retunr the index of that line

    private int checkIfReturnAnyIntersection(ArrayList<Point2D.Float> pointlist,Point2D.Float startpoint, Vector startdirection)
    {
    	//return the index of that intersection side in the list.
    	//(aybx-axby) * (aycx-axcy) > 0;  B and A and C and A
    	//also need B and C and A and C (cybx-cxby)*(cyax-cxay)>0
    	for (int i=0;i<pointlist.size()-1;i++)
    	{
           Point2D.Float p1=pointlist.get(i);
           Point2D.Float p2=pointlist.get(i+1);
           Vector a=new Vector(p1.x-startpoint.x,p1.y-startpoint.y);
           Vector c=new Vector(p2.x-startpoint.x,p2.y-startpoint.y);
           if(checkVectorWithinTwoVectors(startdirection,a,c))
           {
           	return (i+1);
           }
           
    	}
    	//if none, need to return 0.
    	return 0; 


    }
    //use the cross product property to check this, check if b is with in vector a and c
    private boolean checkVectorWithinTwoVectors(Vector b, Vector a, Vector c)
    {
      return (a.dY*b.dX-a.dX*b.dY)*(a.dY*c.dX-a.dX*c.dY)>0&&(c.dY*b.dX-c.dX*b.dY)*(c.dY*a.dX-c.dX*a.dY)>0;

    }

    private void movePoint(GL2 gl, float speed,float xleft, float xright, float ybottom, float ytop)
    {
    	currentx=startx+counter*speed*vx;
    	currenty=starty+counter*speed*vy;
        Vector direction=new Vector(vx,vy);
        Point2D.Float startpoint=new Point2D.Float(startx,starty);
    	int index=checkIfReturnAnyIntersection(pointlist,startpoint,direction);
        System.out.println("index is: "+index);

		gl.glBegin(GL.GL_LINE_LOOP);
		gl.glColor3f(0.5f, 0.5f, 0.5f);
	    //the side of this point
        float r=0.02f;
		for (int i=0; i<32; i++)
		{
			double	theta = (2.0 * Math.PI) * (i / 32.0);

			gl.glVertex2d(currentx+ r * Math.cos(theta),
						  currenty + r * Math.sin(theta));
		}
        
        if(currentx+r>=xright)
        {
        	System.out.println("right boundary");
        	//need to normalize this:
        	Vector in=new Vector(vx,vy);
        	Vector per=new Vector(-1.0f,0.0f);
        	Vector out=getReflectionDirection(in,per);
        	//change counter=0 and update the direction:
        	vx=out.dX;
        	vy=out.dY;
        	counter=0;
        	startx=currentx;
        	starty=currenty;
        }
        else if(currentx-r<=xleft)
        {
        	System.out.println("left boundary");
        	Vector in=new Vector(vx,vy);
        	Vector per=new Vector(1.0f,0.0f);
        	Vector out=getReflectionDirection(in,per);
        	vx=out.dX;
        	vy=out.dY;
        	counter=0;
        	vx=out.dX;
        	vy=out.dY;
        	counter=0;
        	startx=currentx;
        	starty=currenty;
        }
        else if(currenty+r>=ytop)
        {
        	System.out.println("top boundary");
        	Vector in=new Vector(vx,vy);
        	Vector per=new Vector(0.0f,-1.0f);
        	Vector out=getReflectionDirection(in,per);
        	vx=out.dX;
        	vy=out.dY;
        	counter=0;
        	vx=out.dX;
        	vy=out.dY;
        	counter=0;
        	startx=currentx;
        	starty=currenty;
        }
        else if(currenty-r<=ybottom)
        {
        	System.out.println("bottom boundayr");
        	Vector in=new Vector(vx,vy);
        	Vector per=new Vector(0.0f,1.0f);
        	Vector out=getReflectionDirection(in,per);
        	vx=out.dX;
        	vy=out.dY;
        	counter=0;
        	vx=out.dX;
        	vy=out.dY;
        	counter=0;
        	startx=currentx;
        	starty=currenty;
        }
		gl.glEnd();
    }

   
 
	private void drawRec(GL2 gl)
	{
		gl.glColor3f(0.25f, 0.25f, 0.25f);
		gl.glBegin(GL.GL_LINE_LOOP);
        pointlist=new ArrayList<Point2D.Float>();
        Point2D.Float p1=new Point2D.Float(xleft,ybottom);
        Point2D.Float p2=new Point2D.Float(xright,ybottom);
        Point2D.Float p3=new Point2D.Float(xright,ytop);
        Point2D.Float p4=new Point2D.Float(xleft,ytop);
        pointlist.add(p1);
        pointlist.add(p2);
        pointlist.add(p3);
        pointlist.add(p4);
		gl.glVertex2d(xleft, ybottom);
		gl.glVertex2d(xright, ybottom);
		gl.glVertex2d(xright, ytop);
		gl.glVertex2d(xleft, ytop);
		//System.out.println("does this get called!");

		gl.glEnd();

	}
	private void drawSixHex(GL2 gl)
	{
		gl.glColor3f(0.25f, 0.25f, 0.25f);
		gl.glBegin(GL.GL_LINE_LOOP);
		for (int i=0; i<6; i++)
		{
			double	theta = (2.0 * Math.PI) * (i / 6.0);
            //teh radius is 0.8 here.
			gl.glVertex2d(0 + 0.8f * Math.cos(theta),
						  0 + 0.8f * Math.sin(theta));
		}
	}

	private void drawBounds(GL2 gl)
	{
		gl.glColor3f(0.1f, 0.1f, 0.1f);
		gl.glBegin(GL.GL_LINE_LOOP);

		gl.glVertex2d(120, 70);
		gl.glVertex2d(1160, 70);
		gl.glVertex2d(1160, 650);
		gl.glVertex2d(120, 650);

		gl.glEnd();
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
}

//******************************************************************************
