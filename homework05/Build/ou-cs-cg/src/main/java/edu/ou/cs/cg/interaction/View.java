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

	private TextRenderer			renderer;

	private Point2D.Double				origin;		// Current origin coordinates
	private Point2D.Double				cursor;		// Current cursor coordinates
	private ArrayList<Point2D.Double>	points;		// User's polyline points

	private int nameindex=0;
	private int activename;

	public Network network;
	public String[] namelist;
	public Color[] colorlist;
	public int[] sidelist;
	public ArrayList<Node> nodelist=new ArrayList<Node>();
	//the default hightlighted will be the last addeed node in this way
	public int highlightedIndex=0;


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

		// Initialize rendering
		canvas.addGLEventListener(this);
		animator = new FPSAnimator(canvas, DEFAULT_FRAMES_PER_SECOND);
		//animator.start();

		// Initialize interaction
		keyHandler = new KeyHandler(this);
		mouseHandler = new MouseHandler(this);
	}

	//**********************************************************************
	// Getters and Setters
	//**********************************************************************
	public void drawEdge(Node node,GL2 gl,boolean highlight)
	{
		int side=node.side;
        double centerx=node.centerx;
        double centery=node.centery;
        double width=node.width;
        float[] rgb=node.rgb;
        gl.glColor3f(rgb[0],rgb[1],rgb[2]);
        gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
    	
        if(highlight)
        {
        	gl.glColor3f(1.0f, 1.0f, 1.0f);
	    	
        }
        else
        {
        	//this is the dark grey.
        	float c=105.0f/255.0f;
        	gl.glColor3f(c,c,c);
        }
        gl.glLineWidth(4.0f);
		gl.glBegin(GL.GL_LINE_LOOP);
        for(int i=0;i<side;i++)
    	{
    		double theta=((2.0 * Math.PI)/side)*i;
    		gl.glVertex2d(centerx+width*Math.cos(theta),centery+width*Math.sin(theta));
    	}
		gl.glEnd();
        

	}
    public void drawNode(Node node,GL2 gl,boolean highlight)
    {
    	    if(highlight)
    	    {
    	    drawEdge(node,gl,true);

    	    }
    		else
    		{
    			//edge the other stuff in dark gray.
    			//dark grey is (105,105,105)
    			drawEdge(node,gl,false);

    		}
    			int side=node.side;
		        double centerx=node.centerx;
		        double centery=node.centery;
		        double width=node.width;
		        float[] rgb=node.rgb;
		        gl.glColor3f(rgb[0],rgb[1],rgb[2]);
		    	gl.glBegin(GL2.GL_POLYGON); 
		    	for(int i=0;i<side;i++)
		    	{
		    		double theta=((2.0 * Math.PI)/side)*i;
		    		gl.glVertex2d(centerx+width*Math.cos(theta),centery+width*Math.sin(theta));
		    	}
				gl.glEnd();
    	
        
    }

    public void HighlightUp()
    {
    	int size=nodelist.size();
    	highlightedIndex=(highlightedIndex+1)%size;
    }
    public void HighlightDown()
    {
    	int size=nodelist.size();
    	if(highlightedIndex<=0)
    		highlightedIndex=size-1;
    	else
    		highlightedIndex=highlightedIndex-1;
    }
    //pass in the index of the node in the nodelist that you want to highlight
    public void highlightNode(int index)
    {
    	highlightedIndex=index;
    }
    //this will return the index of the node that reside in the original list
    public int getIndexForTheNode(String name)
    {
    	int wholesize=namelist.length;
    	//the index after the activename will be the name that in the nodelist
        for(int i=activename;i<wholesize;i++)
        {
           if(namelist[i]==name)
           {
           	return i;
           }
        }
        //this will be the case that fail.
        return -1;
    }
   

    public void removeNode()
    {
    	//do nothing if there is no node on the view.
    	if(nodelist.size()==0)
    	{
    		return;
    	}
    	System.out.println("before activename is: "+activename);
    	String name=nodelist.get(highlightedIndex).name;
    	int index=getIndexForTheNode(name);
    	//System.out.println("index from the name is: "+index);
    	//we need to replace the index with the namelist[activename] so it become available in that way
    	if(index<activename)
    	{
    		System.out.println("index is: "+index);
    		System.out.println("activename is: "+activename);
    		System.out.println("error!!");
    	}
    	else if(index==activename)
    	{
    		//then do nothing
    	}
    	else
    	{
    		//replace the node
    		String temp=namelist[activename];
    		namelist[activename]=namelist[index];
    		namelist[index]=temp;
    	}
    	//then need up add 1 to the activename to let the system know one more node is added in
    	activename=activename+1;
    	//then remove the hightlighted node from the nodelist.
    	//move the hightlightedOne to be the next:
    	nodelist.remove(highlightedIndex);
    	
    	if(nodelist.size()!=0)
    	{
    		//this is the way to figure out which one to highlight for the next one.
    		highlightedIndex=(highlightedIndex)%(nodelist.size());

    	}
    	System.out.println("active after remove node: "+activename);
    }

    public void addNode()
    {
    	if(activename<=0)
    	{
    		return;
    	}
    	 String name=namelist[nameindex];
    	 int side=network.getSides(name);
    	 Color color=network.getColor(name);
    	 float[] rgb=getRgbColor(color);
    	 double width=0.1;
    	 double height=0.1;
    	 double centerx=getRandom(0.2);
         double centery=getRandom(0.2);

         Node newNode=new Node(centerx,centery,width,height,rgb,side,name);
         //when a node get added to the netwrok, we replace its with the last node in the nodelist and decreas
         //the number of active node
         //this is to rememeber how many active name are in the list
         //and the index agter that we still store the name, but we mark it as not avaailble in this way
         //this.nameindex=(this.nameindex+1)%activename;
         //the order of the node appear is going to chnage but it is not loop it again it is O(1), instead of 
         //O(n)
         
         nodelist.add(newNode);
         updateAll();
         //update the highlightedindex, the highlighted one should be the last one being added.
         highlightedIndex=nodelist.size()-1;
    }
    public double getRandom(double max)
    {
    	double min=0.0d;
    	double random =(min + Math.random() * (max - min));
    	return random;

    }
    //give the position of a cursor return which is the front most node this cursor is on
    public int findFrontMostSelected(Point2D.Double cursor)
    {
    	//looping through the end of the nodelist and find which is the first one that contain this point
    	int size=nodelist.size();
    	for(int i=size-1;i>=0;i--)
    	{
    		if(checkPointInsidePolygon(cursor,i))
    		{
    			//return the index of the node inside of the nodelist
    			return i;
    		}

    	}
    	//this means this point is not in any of the polygon
    	return -1;
   
    }

    //use the algorithm that draw a ray to see if it has even or odd intersection with the polygons,
    //if it is odd then it is inside, if it is even it is outside.
    public boolean checkPointInsidePolygon(Point2D.Double p,int nodeindex)
    {
    	//since all our polygon is regular, this is such a clever algorithm to solve it:
    	//https://everything2.com/title/Determining+if+a+point+is+inside+a+regular+polygon

    	//first of all use the nodeindex to find the information of this node.
    	Node node=nodelist.get(nodeindex);
    	double cx=node.centerx;
    	double cy=node.centery;
    	Point2D.Double c=new Point2D.Double(cx,cy);
    	int side=node.side;
    	double width=node.width;
    	//calcute the distance between the center and the current point, just keep the square to make it
    	//efficient
    	double cursorToCenter=squareDistance(p,c);
      
    	//then for each edge in the polygon reflect the center point against it
    	//and then check the distance of the center to this reflection point
    	for(int i=0;i<side;i++)
    	{
    		double theta=((2.0 * Math.PI)/side)*i;
    		int nexti=(i+1)%side;
    		double theta1=((2.0 * Math.PI)/side)*nexti;
    		//gl.glVertex2d(centerx+width*Math.cos(theta),centery+width*Math.sin(theta));
            Point2D.Double p1=new Point2D.Double(cx+width*Math.cos(theta),cy+width*Math.sin(theta));
            Point2D.Double p2=new Point2D.Double(cx+width*Math.cos(theta1),cy+width*Math.sin(theta1));

            Point2D.Double reflect=getReflectionPoint(p1,p2,c);

            double cursorToReflect=squareDistance(reflect,p);
            //System.out.print("cursor to center: "+cursorToCenter);
            //System.out.print("cursor to center: "+cursorToCenter);
            if(cursorToCenter>cursorToReflect)
            {
            	//under this condition the cursor has to be outside fo the polygon
            	return false;
            }
    	}
    	//if not find any one like that , return true which means the cursor is inside polygon.
        return true;
    }

    private double squareDistance(Point2D.Double p1, Point2D.Double p2)
    {
    	return (p1.x-p2.x)*(p1.x-p2.x)+(p1.y-p2.y)*(p1.y-p2.y);
    }

    private Point2D.Double getReflectionPoint(Point2D.Double p1, Point2D.Double p2, Point2D.Double c)
    {
    	double midx=p1.x+p2.x;
    	double midy=p1.y+p2.y;

    	double reflectx=midx-c.x;
    	double reflecty=midy-c.y;

    	return new Point2D.Double(reflectx,reflecty);
    }
    
	public int	getWidth()
	{
		return w;
	}

	public int	getHeight()
	{
		return h;
	}
    public void indexUp()
    {
       this.nameindex=(this.nameindex+1)%activename;
    }
    public void indexDown()
    {
       //this.nameindex=(((this.nameindex)+10)-1)%10;
    	if(this.nameindex>=1)
    	{
    		this.nameindex=this.nameindex-1;
    	}
    	else
    	{
    		this.nameindex=activename-1;
    	}
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

	public void		init(GLAutoDrawable drawable)
	{
		w = drawable.getWidth();
		h = drawable.getHeight();

		renderer = new TextRenderer(new Font("Monospaced", Font.PLAIN, 12),
									true, true);
		network=new Network();

        namelist=network.getAllNames();
        colorlist=network.getAllColors();
        sidelist=network.getAllSides();
        activename=namelist.length;
        System.out.println("active length is: "+activename);

	}
    
    //this will update the order of 3 things correspondingly in the list
    public void updateAll()
    {
    	 String nametemp=namelist[nameindex];
         namelist[nameindex]=namelist[activename-1];
         namelist[activename-1]=nametemp;

         int sidetemp=sidelist[nameindex];
         sidelist[nameindex]=sidelist[activename-1];
         sidelist[activename-1]=sidetemp;

         Color colortemp=colorlist[nameindex];
         colorlist[nameindex]=colorlist[activename-1];
         colorlist[activename-1]=colortemp;
         
         //this is to rememeber how many active name are in the list
         //and the index agter that we still store the name, but we mark it as not avaailble in this way
         //this.nameindex=(this.nameindex+1)%activename;
         //the order of the node appear is going to chnage but it is not loop it again it is O(1), instead of 
         //O(n)
         activename=activename-1;

    }

	public float[] getRgbColor(Color color)
	{
	    float[] rgbcolor=new float[3];
	    rgbcolor[0]=(float)(color.getRed()/255.0f);
	    rgbcolor[1]=(float)(color.getGreen()/255.0f);
	    rgbcolor[2]=(float)(color.getBlue()/255.0f);
	    return rgbcolor;
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

	private void	render(GLAutoDrawable drawable)
	{
		GL2		gl = drawable.getGL().getGL2();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT);		// Clear the buffer
		drawBounds(gl);							// Unit bounding box
		drawAxes(gl);							// X and Y axes
		drawCursor(gl);							// Crosshairs at mouse location
		drawCursorCoordinates(drawable);		// Draw some text
		drawPolyline(gl);						// Draw the user's sketch
		String textToRender="no-name-to-display";
		//activaename is range from 1 to the length of the original total names.
        if(activename>0)
        {
        	textToRender=namelist[nameindex];
        }
  
		TextRenderer textRenderer = new TextRenderer(new Font("Verdana", Font.BOLD, 12));
		textRenderer.beginRendering(drawable.getWidth(), drawable.getHeight());
		textRenderer.setColor(Color.YELLOW);
		textRenderer.setSmoothing(true);
		textRenderer.draw(textToRender, 50,50);
		textRenderer.endRendering();

		//draw node
		int count=0;
		for(Node node: nodelist)
		{
			if(count==highlightedIndex)
			{
				drawNode(node,gl,true);
			}
			else
			{
				drawNode(node,gl,false);
			}
			count=count+1;
			
		}
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
