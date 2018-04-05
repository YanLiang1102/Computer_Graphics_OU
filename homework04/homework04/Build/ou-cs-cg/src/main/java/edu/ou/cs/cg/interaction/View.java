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
    private ArrayList<Vector> perpenlist;
    //1 is the box, 2 is the regular hexagon ,2 is the 32 hexagon for the circle, 4 is the non-regular one
    private int container=1;
    //the default shape will just be the point itself 
    private int shape=6;

	private TextRenderer			renderer;
	//the radisu of the point
	private float r=0.02f;
	//private float recr=0.02f;
	private float commonr=0.02f;
	//this is to check how far we allow it to go to the boundary of the container.
	private float threshold=0.02f;

	private Point2D.Double				origin;		// Current origin coordinates
	private Point2D.Double				cursor;		// Current cursor coordinates
	private ArrayList<Point2D.Double>	points;		// User's polyline points
	private ArrayList<Point2D.Float> allpointscontainer;//for all the points on the irregular container.
	private ArrayList<Point2D.Float> allpointsshape;
	private float impulseUp=1.1f;
	private float impulseDown=0.6f;
	private int bouncecount=0;
	private float colormagnitude=1.0f;
	private float diff=0.05f; //since it will disappear in 20 times.
	private Point2D.Float closestpoint;
	//private Vector diffvector;
	private ArrayList<Point2D.Float> diffvector;
	private Point2D.Float current1;
	private Point2D.Float p11;
	private Point2D.Float p22;
    private float distance1;

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
	public void setvx(float vx)
	{
      this.vx=vx;
	}
	public void setstartx(float startx)
	{
		this.startx=startx;
	}
	public void setstarty(float starty)
	{
		this.starty=starty;
	}
	public void setvy(float vy)
	{
	  this.vy=vy;
	}
	public void setCommonr(float commonr)
	{
		this.commonr=commonr;
	}
	public float getCommonr()
	{
        return this.commonr;
	}
    public void setcolormagnitude(float colormagnitude)
    {
    	this.colormagnitude=colormagnitude;
    }
    public void setcounter(int counter)
    {
    	this.counter=counter;
    }
    public void setshape(int shape)
    {
    	this.shape=shape;
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
	    //GL2		gl = drawable.getGL().getGL2();

		float vx1=generateRandom();
		float vy1=generateRandom();
		float len=(float)Math.sqrt(vx1*vx1+vy1*vy1);
		//normalize this direction function.

		vx=vx1/len;
		vy=vy1/len;
	}

    //generate random number from -1 to 1.
    public Vector generateRandomDirection()
    {
    	//within 20 degrees, not make it on the otherside
    	int randomNum = ThreadLocalRandom.current().nextInt(12, 100);
    	float tan=(float)(Math.tan(2*3.14f/(randomNum*1.0f)));
        float tx=1.0f;
        float ty=tan*tx;
        float dx=tx/(float)(Math.sqrt(tx*tx+ty*ty));
        int anotherRandom=ThreadLocalRandom.current().nextInt(0,10);
        float dy=ty/(float)(Math.sqrt(tx*tx+ty*ty));
        if(anotherRandom>5)
        {
           dy=-1.0f*dy;
        }
        
        return new Vector(dx,dy);
    }
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
	 private Vector getPerpenVector(Vector v)
	 {
	 	float y1=1.0f;
	 	float x1=-1.0f*v.dY/v.dX;
	 	float x=x1/(float)(Math.sqrt(x1*x1+y1*y1));
	 	float y=y1/(float)(Math.sqrt(x1*x1+y1*y1));
	 	//also need to make sure teh cross product is product since it is always point inside of the polygon
	 	return new Vector(x,y);

	 }

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
		gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
		//update the color of the canvas.
        gl.glColor3f(1.0f, 0.0f, 0.0f); 

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
		
		gl.glClear(GL.GL_COLOR_BUFFER_BIT|GL.GL_DEPTH_BUFFER_BIT);		// Clear the buffer

		switch(container){
			case 1:
			drawRec(gl);
			break;

			case 2:
			drawSixHex(gl);
			break;

			case 3:
			drawThirtyTwoCirle(gl);
			break;

			case 4:
			drawIrregularContainer(gl);
			break;
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
    	int size=pointlist.size();
    	for (int i=0;i<size;i++)
    	{
           Point2D.Float p1=pointlist.get(i);

           Point2D.Float p2=pointlist.get((i+1)%size);
           Vector a=new Vector(p1.x-startpoint.x,p1.y-startpoint.y);
           Vector c=new Vector(p2.x-startpoint.x,p2.y-startpoint.y);
           if(checkVectorWithinTwoVectors(startdirection,a,c))
           {
           	return (i);
           }
           
    	}
    	//if none, need to return 0.
    	return 1000; 


    }
    //use the cross product property to check this, check if b is with in vector a and c
    private boolean checkVectorWithinTwoVectors(Vector b, Vector a, Vector c)
    {
      return (a.dY*b.dX-a.dX*b.dY)*(a.dY*c.dX-a.dX*c.dY)>0&&(c.dY*b.dX-c.dX*b.dY)*(c.dY*a.dX-c.dX*a.dY)>0;

    }
    //draw shapes
    private void drawPoint(GL2 gl)
    {
        gl.glBegin(GL.GL_LINE_LOOP);
		gl.glColor3f(0.5f, 0.5f, 0.5f);
	    //the side of this point
        
		for (int i=0; i<32; i++)
		{
			double	theta = (2.0 * Math.PI) * (i / 32.0);

			gl.glVertex2d(currentx+ r * Math.cos(theta),
						  currenty + r * Math.sin(theta));
		}
		gl.glEnd();
    }

    //draw rec as a shape
    private void drawrecshape(GL2 gl,Point2D.Float center)
    {//colormagnitude
    	//thsi need to be enabled!
       	gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    	gl.glColor4f(0.25f, 0.25f, 0.25f,colormagnitude);
		gl.glBegin(GL.GL_LINE_LOOP);
        //pointlist=new ArrayList<Point2D.Float>();
        // diffvector=new ArrayList<Point2D.Float>();
        // allpointsshape=new ArrayList<Point2D.Float>();
        // diffvector.add(new Point2D.Float(-commonr,-commonr));
        // diffvector.add(new Point2D.Float(commonr,-commonr));
        // diffvector.add(new Point2D.Float(commonr,commonr));
        // diffvector.add(new Point2D.Float(-commonr,commonr));
        Point2D.Float p1=new Point2D.Float(center.x-commonr,center.y-commonr);
        Point2D.Float p2=new Point2D.Float(center.x+commonr,center.y-commonr);
        Point2D.Float p3=new Point2D.Float(center.x+commonr,center.y+commonr);
        Point2D.Float p4=new Point2D.Float(center.x-commonr,center.y+commonr);
        // allpointsshape.add(p1);
        // allpointsshape.add(p2);
        // allpointsshape.add(p3);
        // allpointsshape.add(p4);

		gl.glVertex2d(p1.x, p1.y);
		gl.glVertex2d(p2.x, p2.y);
		gl.glVertex2d(p3.x, p3.y);
		gl.glVertex2d(p4.x, p4.y);
		gl.glEnd();
    }
    //draw a regular polygon as a shape
    private void drawpolyshape(GL2 gl,Point2D.Float center)
    {
    	//thsi need to be enabled!
       	gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    	gl.glColor4f(0.25f, 0.25f, 0.25f,colormagnitude);
		gl.glBegin(GL.GL_LINE_LOOP);
		for (int i=0; i<6; i++)
		{
			double	theta = (2.0 * Math.PI) * (i / 6.0);

			gl.glVertex2d(currentx+ commonr * Math.cos(theta),
						  currenty + commonr * Math.sin(theta));
		}
		gl.glEnd();
    }

    private void drawirregularshape(GL2 gl,Point2D.Float center)
    {
    	gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    	gl.glColor4f(0.25f, 0.25f, 0.25f,colormagnitude);

    	allpointsshape=new ArrayList<Point2D.Float>();
		//perpenlist=new ArrayList<Vector>();
		Point2D.Float p1=new Point2D.Float(center.x-commonr*.5f,center.y+commonr*0.5f);
		Point2D.Float p2=new Point2D.Float(center.x,center.y+commonr);
		Point2D.Float p3=new Point2D.Float(center.x+commonr*.5f,center.y+commonr*.5f);
		Point2D.Float p4=new Point2D.Float(center.x+commonr,center.y-commonr*0.5f);
		Point2D.Float p5=new Point2D.Float(center.x-commonr*.5f,center.y-commonr);
		allpointsshape.add(p1);
		allpointsshape.add(p2);
		allpointsshape.add(p3);
		allpointsshape.add(p4);
		allpointsshape.add(p5);
		
		gl.glBegin(GL.GL_LINE_LOOP);
		for(int i=0;i<allpointsshape.size();i++)
		{
			gl.glVertex2d(allpointsshape.get(i).x,allpointsshape.get(i).y);
		}
		gl.glEnd();

    }

    private void movePoint(GL2 gl, float speed,float xleft, float xright, float ybottom, float ytop)
    {
    	currentx=startx+counter*speed*vx;
    	currenty=starty+counter*speed*vy;
        Vector direction=new Vector(vx,vy);
        Point2D.Float startpoint=new Point2D.Float(startx,starty);
    	int index=checkIfReturnAnyIntersection(pointlist,startpoint,direction);
        //System.out.println("index is: "+index);
        switch(shape){
        	//will be the point
        	case 6:
        	drawPoint(gl);
        	break;

        	case 7:
        	drawrecshape(gl,new Point2D.Float(currentx,currenty));
        	break;

        	case 8:
        	drawpolyshape(gl,new Point2D.Float(currentx,currenty));
        	break;

        	case 9:
        	drawirregularshape(gl,new Point2D.Float(currentx,currenty));
        	break;


        }
	    
		switch(container)
		{
			case 1:
                //bouncecount=0;

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
		        	speed=speed*impulseUp;
		        	bouncecount++;
		        	colormagnitude=colormagnitude-diff;
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
		        	speed=speed*impulseDown;
		        	bouncecount++;
		        	colormagnitude=colormagnitude-diff;
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
		        	speed=speed*impulseUp;
		        	bouncecount++;
		        	colormagnitude=colormagnitude-diff;
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
		        	speed=speed*impulseDown;
		        	bouncecount++;
		        	colormagnitude=colormagnitude-diff;
		        }
		   break;

		   case 2:
			int index1=checkIfReturnAnyIntersection(pointlist,startpoint,direction);
	        Vector in1;
		
		        		if(index1%2==0)
		        		{
                          speed=speed*impulseUp;
		        		}
		        		else
		        		{
		        		  speed=speed*impulseDown;
		        		}
		        		 p11=pointlist.get(index1);
					     p22=pointlist.get((index1+1)%6);
					    current1=new Point2D.Float(currentx, currenty);	    
				        distance1=getDistanceOfPointToLineSegment(p11,p22,current1);
				        //give it a little bit room to react that why multiply 2.
				        if(distance1<=2*r)
				        {
				          in1 =new Vector(vx,vy);
				          bouncecount++;
				          colormagnitude=colormagnitude-diff;
		             	  resetAll(in1,perpenlist.get(index1));

				        }

		    break;

		    case 3:
		    	index1=checkIfReturnAnyIntersection(pointlist,startpoint,direction);
	            //Vector in1;
		        // for(int i=0;i<32;i++)
		        // {
		        // 	if(index1==i)
		        // 	{
		        		//impulse up and down
		        		//when it is even speed up , when it is odd speed down by the factor
		        		if(index1%2==0)
		        		{
                          speed=speed*impulseUp;
		        		}
		        		else
		        		{
		        		  speed=speed*impulseDown;
		        		}

		        		 p11=pointlist.get(index1);
					     p22=pointlist.get((index1+1)%32);
					     current1=new Point2D.Float(currentx, currenty);	    
				         distance1=getDistanceOfPointToLineSegment(p11,p22,current1);
				        //give it a little bit room to react that why multiply 2.
				        if(distance1<=2*r)
				        {
				          in1 =new Vector(vx,vy);
				          bouncecount++;
				          colormagnitude=colormagnitude-diff;
		             	  resetAll(in1,perpenlist.get(index1));

				        }

		        //      	break;

		        // 	}
		        // }
		    break;

		    case 4:
		    	 index1=checkIfReturnAnyIntersection(pointlist,startpoint,direction);
		    	 //System.out.println("the bounce side index is: "+index1);
		    	 //int closestindex=getClosestPointOfSegmentOnTheShape(allpointsshape,pointlist.get(index1),pointlist.get(index1+1));
		    	 //System.out.println("the closest point is: "+closestindex);
	             //closestpoint=new Point2D.Float(currentx+diffvector.get(closestindex).x,currenty+diffvector.get(closestindex).y);
                 //closestpoint=allpointsshape.get(closestindex);
	                    //10 points on the irregular
	                     if(index1%2==0)
		        		{
                          speed=speed*impulseUp;
		        		}
		        		else
		        		{
		        		  speed=speed*impulseDown;
		        		}

		        		p11=pointlist.get(index1);
					    p22=pointlist.get((index1+1)%10);
					    current1=new Point2D.Float(currentx, currenty);
                       // closestpoint=new Point.Float(currentx+diffvector.dX,currenty+diffvector.dY);
				       // float distance=getDistanceOfPointToLineSegment(p1,p2,closestpoint);
					    distance1=getDistanceOfPointToLineSegment(p11,p22,current1);
				        //give it a little bit room to react that why multiply 2.
				        if(distance1<=2*r)
				        {
				          in1 =new Vector(vx,vy);
				          bouncecount++;
				          colormagnitude=colormagnitude-diff;
		             	  resetAll(in1,perpenlist.get(index1));		       
				        }
		    break;

		}
        
    }

   
    private void resetAll(Vector in, Vector per)
    {
    	Vector out=getReflectionDirection(in,per);
    	vx=out.dX;
    	vy=out.dY;
    	counter=0;
    	startx=currentx;
    	starty=currenty;
    }
    public float crossprodvalue(Vector a, Vector b)
    {
    	return (float)Math.abs(a.dX*b.dY-a.dY*b.dX);
    }
    
    public float getDistanceOfPointToLineSegment(Point2D.Float a, Point2D.Float b, Point2D.Float c)
    {
    	//a,b is the point of the line segment and c is the moving poirnt
    	Vector v1=new Vector(b.x-a.x,b.y-a.y);
    	Vector v2=new Vector(a.x-c.x,a.y-c.y);
    	return crossprodvalue(v1,v2)/v1.length();
         
      
    }
    //return the index of the point on the allpointshape and then use this to fetch the diffvector to the center, so we
    //can generate the current closte point facing the bounch edge.
    public int getClosestPointOfSegmentOnTheShape(ArrayList<Point2D.Float> points,Point2D.Float a, Point2D.Float b)
    {
    	//Point2D.Float closest=points.get(0);
    	//float mindist=getDistanceOfPointToLineSegment(a,b,closest);
    	float mindistance=100.0f;
    	int bestindex=0;
    	for(int i=0;i<points.size();i++)
    	{
    		float next=getDistanceOfPointToLineSegment(a,b,points.get(i));
    		if(next<mindistance)
    		{
    			bestindex=i;
    		}


    	}
    	//this will be the closest point interact with the side of the container.
        return bestindex;

    }
	private void drawRec(GL2 gl)
	{
		//reset the boundcount when a new container is draw
		bouncecount=0;
		//colormagnitude=1.0f;
		gl.glBegin(GL.GL_LINES);
		gl.glColor3f(0.3f, 0.3f, 0.3f);
		gl.glVertex2d(xleft, ybottom);
		gl.glVertex2d(xright, ybottom);
		gl.glEnd();
		
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
        gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
        gl.glLineWidth(2);
        gl.glColor3f(0.1f, 0.1f, 0.1f);
		gl.glVertex2d(xleft, ybottom);
		gl.glColor3f(0.3f, 0.3f, 0.3f);
		gl.glVertex2d(xright, ybottom);
		gl.glColor3f(0.5f, 0.5f, 0.5f);
		gl.glVertex2d(xright, ytop);
		gl.glColor3f(0.7f, 0.7f, 0.7f);
		gl.glVertex2d(xleft, ytop);
		//System.out.println("does this get called!");

		gl.glEnd();




	}
	private void drawSixHex(GL2 gl)
	{
		bouncecount=0;
		//colormagnitude=1.0f;
		gl.glColor3f(0.25f, 0.25f, 0.25f);
		gl.glBegin(GL.GL_LINE_LOOP);
		pointlist=new ArrayList<Point2D.Float>();
		perpenlist=new ArrayList<Vector>();
		for (int i=0; i<6; i++)
		{
			double	theta = (2.0 * Math.PI) * (i / 6.0);
            //teh radius is 0.8 here.
			gl.glVertex2d(0 + 0.8f * Math.cos(theta),
						  0 + 0.8f * Math.sin(theta));
			Point2D.Float p= new Point2D.Float(0.0f+0.8f * (float)Math.cos(theta),0.0f + 0.8f *(float) Math.sin(theta));
			pointlist.add(p);
		

		}
		gl.glEnd();
		for(int i=0;i<6;i++)
		{
			Point2D.Float p1=pointlist.get(i);
			Point2D.Float p2=pointlist.get((i+1)%6);
			Vector in=new Vector(p2.x-p1.x,p2.y-p1.y);
			Vector perpen=getPerpenVector(in);
			//System.out.println("perpenx is: "+i+" "+perpen.dX);
			//System.out.println("perpeny is: "+i+" "+perpen.dY);
			if(in.dX*perpen.dY-in.dY*perpen.dX>0)
			{
				//need to make sure the cross product is positive here.
				perpenlist.add(perpen);

			}
			 else
			 {
			 	perpenlist.add(perpen.scale(-1.0f));
			 }
			
		}
	
	}

	private void drawThirtyTwoCirle(GL2 gl)
	{
		bouncecount=0;
		//colormagnitude=1.0f;
		gl.glColor3f(0.25f, 0.25f, 0.25f);
		gl.glBegin(GL.GL_LINE_LOOP);
		pointlist=new ArrayList<Point2D.Float>();
		perpenlist=new ArrayList<Vector>();
		for (int i=0; i<32; i++)
		{
			double	theta = (2.0 * Math.PI) * (i / 32.0);
            //teh radius is 0.8 here.
			gl.glVertex2d(0 + 0.8f * Math.cos(theta),
						  0 + 0.8f * Math.sin(theta));
			Point2D.Float p= new Point2D.Float(0.0f+0.8f * (float)Math.cos(theta),0.0f + 0.8f *(float) Math.sin(theta));
			pointlist.add(p);
		}
		gl.glEnd();
		for(int i=0;i<32;i++)
		{
			Point2D.Float p1=pointlist.get(i);
			Point2D.Float p2=pointlist.get((i+1)%32);
			Vector in=new Vector(p2.x-p1.x,p2.y-p1.y);
			Vector perpen=getPerpenVector(in);
			//System.out.println("perpenx is: "+i+" "+perpen.dX);
			//System.out.println("perpeny is: "+i+" "+perpen.dY);
			if(in.dX*perpen.dY-in.dY*perpen.dX>0)
			{
				//need to make sure the cross product is positive here.
				perpenlist.add(perpen);

			}
			 else
			 {
			 	perpenlist.add(perpen.scale(-1.0f));
			 }
			
		}

	}

	private void drawIrregularContainer(GL2 gl)
	{
		bouncecount=0;
		//colormagnitude=1.0f;
		//set points
		pointlist=new ArrayList<Point2D.Float>();
		perpenlist=new ArrayList<Vector>();
		Point2D.Float p1=new Point2D.Float(-0.8f,0.0f);
		Point2D.Float p2=new Point2D.Float(-0.6f,0.2f);
		Point2D.Float p3=new Point2D.Float(-0.3f,0.5f);
		Point2D.Float p4=new Point2D.Float(0.0f,0.8f);
		Point2D.Float p5=new Point2D.Float(0.3f,0.7f);
		Point2D.Float p6=new Point2D.Float(0.6f,-0.2f);
		Point2D.Float p7=new Point2D.Float(0.5f,-0.5f);
		Point2D.Float p8=new Point2D.Float(0.2f,-0.7f);
		Point2D.Float p9=new Point2D.Float(-0.4f,-0.6f);
		Point2D.Float p10=new Point2D.Float(-0.7f,-0.2f);
		pointlist.add(p1);
		pointlist.add(p2);
		pointlist.add(p3);
		pointlist.add(p4);
		pointlist.add(p5);
		pointlist.add(p6);
		pointlist.add(p7);
		pointlist.add(p8);
		pointlist.add(p9);
		pointlist.add(p10);
		gl.glColor3f(0.25f, 0.25f, 0.25f);
		gl.glBegin(GL.GL_LINE_LOOP);
		for(int i=0;i<pointlist.size();i++)
		{
			gl.glVertex2d(pointlist.get(i).x,pointlist.get(i).y);
		}
		//gl.glVertex2d(xleft, ybottom);
		
		gl.glEnd();

		for(int i=0;i<10;i++)
		{
			Point2D.Float pa=pointlist.get(i);
			Point2D.Float pb=pointlist.get((i+1)%10);
			Vector in=new Vector(pb.x-pa.x,pb.y-pa.y);
			Vector perpen=getPerpenVector(in);
			//System.out.println("perpenx is: "+i+" "+perpen.dX);
			//System.out.println("perpeny is: "+i+" "+perpen.dY);
			if(in.dX*perpen.dY-in.dY*perpen.dX>0)
			{
				//need to make sure the cross product is positive here.
				perpenlist.add(perpen);

			}
			 else
			 {
			 	perpenlist.add(perpen.scale(-1.0f));
			 }
			
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
