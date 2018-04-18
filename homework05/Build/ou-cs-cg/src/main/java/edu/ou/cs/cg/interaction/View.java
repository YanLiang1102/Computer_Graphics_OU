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
	public ArrayList<Node> hulllist=new ArrayList<Node>();
	public ArrayList<Point2D.Double> bpoints=new ArrayList<Point2D.Double>();

	//this will store the index of the  hullist node as the key and the value will be 
	//a list of auxillary node
	Map<Integer, Point2D.Double[]> bmap = new HashMap<Integer, Point2D.Double[]>();
	//the default hightlighted will be the last addeed node in this way
	public int highlightedIndex=0;
	//the radius of the booloon
	public Double br=0.1;

	//public double rotateAngle=0.0;


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
    		gl.glVertex2d(centerx+width*Math.cos(theta-node.angle),centery+width*Math.sin(theta-node.angle));
    	}
		gl.glEnd();
        

	}

	//gamma will be the rotation
    public void drawNode(Node node,GL2 gl,boolean highlight)
    {
    	    Double gamma=node.angle;
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
		    		gl.glVertex2d(centerx+width*Math.cos(theta-gamma),centery+width*Math.sin(theta-gamma));
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

         Node newNode=new Node(centerx,centery,width,height,rgb,side,name,0.0);
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

    public void dragNode(Point2D.Double diff)
    {
    	try
    	{
    		System.out.println("highlighted index: "+highlightedIndex);
    		Node node=nodelist.get(highlightedIndex);
	    	node.centerx=node.centerx+diff.x;
	    	node.centery=node.centery+diff.y;

    	}
    	catch(Exception e)
    	{
          System.out.println("hey I failed: "+e);
    	}
    	

    }
    public Node getNode()
    {
    	return nodelist.get(highlightedIndex);
    }
    public Point2D.Double getHighlight()
	{
		Node node=nodelist.get(highlightedIndex);
    	return new Point2D.Double(node.centerx,node.centery);
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

    public void setCenter(Point2D.Double p)
    {
    	Node node=nodelist.get(highlightedIndex);
    	node.centerx=p.x;
    	node.centery=p.y;
    }
    public void scaleNode(String direction)
    {
    	//do nothing if there noting in the nodelist.
    	if(nodelist.size()==0)
    	{
    		return;
    	}
    	//be cause height is not being called, it is regular why it has height and width what do u want from that
    	if(direction=="height")
    	{
    		//System.out.println("is this scale height getting called")
    		Node node=nodelist.get(highlightedIndex);
    		Double width=node.width;
    		node.width=node.width*1.1;
    	}
    	//will be "width"
    	else
    	{
    		Node node=nodelist.get(highlightedIndex);
    		Double width=node.width;
    		node.width=node.width*1.1;
    	}
    }
    //index will be index of the node in the nodelist and direction will 
    //on whichever direction they want to translate
    public void translateNode(String direction)
    {
    	//do nothing if there noting in the nodelist.
    	if(nodelist.size()==0)
    	{
    		return;
    	}
    	if(direction=="left")
    	{

    	   //update the center of the selected node
    		Node node=nodelist.get(highlightedIndex);
    		Double width=node.width;
    		node.centerx=node.centerx-width*0.1;
    	}
    	else if(direction=="right")
    	{
    		Node node=nodelist.get(highlightedIndex);
    		Double width=node.width;
    		node.centerx=node.centerx+width*0.1;

    	}
    	else if(direction=="up")
    	{
    		Node node=nodelist.get(highlightedIndex);
    		Double height=node.height;
    		node.centery=node.centery+height*0.1;


    	}
    	else if(direction=="down")
    	{
    		Node node=nodelist.get(highlightedIndex);
    		Double height=node.height;
    		node.centery=node.centery-height*0.1;

    	}
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

        // Point2D.Double v1=new Point2D.Double(1.0,0.0);
        // Point2D.Double v2=new Point2D.Double(0.0,1.0);
        // Double result=angleBetweenVectors(v1,v2);
        // System.out.println("angle shoudl be 0.76: "+result);

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
		//drawCursor(gl);							// Crosshairs at mouse location
		drawCursorCoordinates(drawable);		// Draw some text
		//drawPolyline(gl);						// Draw the user's sketch
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
		drawConvexHull(gl);
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
    //I am using the point to represent the two components of the vector
	public Double angleBetweenVectors(Point2D.Double v1, Point2D.Double v2)
	{
		double length1=Math.sqrt(v1.x*v1.x+v1.y*v1.y);
		double length2=Math.sqrt(v2.x*v2.x+v2.y*v2.y);
		double innerpro=innerProduct(v1,v2);
		return Math.acos(innerpro/(length1*length2));

	}
    //the sign of the crossproduct will decide if it is clockwise or counter-colockwise.
	public Double crossProduct(Point2D.Double p1, Point2D.Double p2, Point2D.Double p3)
	{
		return -(p2.x-p1.x)*(p3.y-p2.y)+(p2.y-p1.y)*(p3.x-p2.x);
	}
    
    //construct the hull list based on the current nodelist
	// public void getHullPoint()
	// {
	// 	//clear the hullpoint
	// 	hulllist.clear();
	// 	int first=findLeftMostPoint();
	// 	Node node=nodelist.get(first);
	// 	Point2D.Double p=new Point2D.Double(node.centerx,node.centery);
	// 	hulllist.add(node);
 //        int size=nodelist.size();
 //        int first1=first;
 //        int next=0;
 //        //int next=(first+1)%size;
 //        //keep adding to the hull if it does not return back to the first one
 //       // while(next!=first)
 //        do
 //        {
 //        	next=(first1+1)%size;
 //        	Double nx=nodelist.get(next).centerx;
 //        	Double ny=nodelist.get(next).centery;
 //        	Point2D.Double q=new Point2D.Double(nx,ny);  

 //        	for(int i=0;i<size;i++)
 //        	{
 //        		Double x=nodelist.get(i).centerx;
 //        		Double y=nodelist.get(i).centery;
 //        		Point2D.Double r=new Point2D.Double(x,y);
 //                if(crossProduct(p,r,q)<0)
 //                {
 //                	next=i;
 //                	//add the node to the hull
 //                	hulllist.add(nodelist.get(i));
 //                }
               
 //        	}

 //        }

        

	// }

	//get the list of point for the booloon start with the first point on the convex hull
	public void getAuxilaryPoint()
	{
		// for(Node n: hulllist)
		// {
            //the point that stored in the hullist is ordered by the x value based on the algorithm
            //we are using here

		//}
		int size=hulllist.size();
		for(int i=0;i<size;i++)
		{
			int next=(i+1)%size;
			Node n1=hulllist.get(i);
			Node n2=hulllist.get(next);
			Point2D.Double p1=new Point2D.Double(n1.centerx,n1.centery);
			Point2D.Double p2=new Point2D.Double(n2.centerx,n2.centery);
			//the we get the vector between them represent by a point will be
			Vector v=new Vector(p2.x-p1.x,p2.y-p1.y);
			Vector vp=getPerpenVectorOutward(v);

		    Point2D.Double a1=addPointWithVector(p1,vp.scale(br));
		    Point2D.Double a2=addPointWithVector(p2,vp.scale(br));
            //add in those two point into bpoints
            bpoints.add(a1);
            bpoints.add(a2);
           // bmap.put(i,new Point2D.Double[]{a1,a2});
		}
	}
	//draw the booloon
	public void drawBooloon(GL2 gl)
	{
        gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		//make the line thicker
		gl.glLineWidth(6.0f);
		gl.glColor3f(1.0f,0.5f,0.5f);
		int size=bpoints.size();
		for(int i=0;i<size-1;i=i+2)
		{
			gl.glBegin(GL.GL_LINES);
			gl.glVertex2d(bpoints.get(i).x,bpoints.get(i).y);
			gl.glVertex2d(bpoints.get(i+1).x,bpoints.get(i+1).y);
			gl.glEnd();
		}
		int hullsize=hulllist.size();
		for(int i=0;i<hullsize;i++)
		{
			
			if(i==0)
			 {
			 	int j=bpoints.size()-1;
			 	Node n=hulllist.get(0);
			 	Point2D.Double c=new Point2D.Double(n.centerx,n.centery);
			 	drawArc(gl,c,bpoints.get(i),bpoints.get(j));
			 }
			 else
			 {
			 	int k=2*i-1;
			 	int j=2*i;
			 	Node n=hulllist.get(i);
			 	Point2D.Double c=new Point2D.Double(n.centerx,n.centery);
			 	System.out.println("index i is :"+i);
			 	drawArc(gl,c,bpoints.get(k),bpoints.get(j));
			 }
			 //Node n=hulllist.get(i);
			//Point2D.Double c=new Point2D.Double(n.centerx,n.centery);
			//drawArc(gl,c,bmap.get(i)[0],bmap.get(i)[1]);

		}

	}
    //1,2,3,4,5,6,7,8
    //0,(0,7) 1,(1,2),2(3,4),3(5,6)
    //actually I am calculate a poiunt add to a vector here
	public Point2D.Double addPointWithVector(Point2D.Double p, Vector v)
	{
       return new Point2D.Double(p.x+v.dX,p.y+v.dY);
	}

	//here point represent a vector
	private Vector getPerpenVectorOutward(Vector p)
	{

		Double x1=1.0;
		Double y1=-1.0*(p.dX/p.dY);
		//the above two will make sure the inner product is 0,
		//now need to make sure that the cross product is positive, so it will point out
		Double x=x1/(Math.sqrt(x1*x1+y1*y1));
	    Double y=y1/(Math.sqrt(x1*x1+y1*y1));

	    if(p.dY>=0)
	    {
	    	return new Vector(x,y);
	    }
        else
        {
        	return new Vector(-1.0*x,-1.0*y);
        }
	}



	 public  void convexHull()
    {
        // There must be at least 3 points
        //if (n < 3) return;
      
        // Initialize Result
       // Vector<Point> hull = new Vector<Point>();
      
        // Find the leftmost point
        hulllist.clear();
        int l = findLeftMostPoint();
       
      
        // Start from leftmost point, keep moving 
        // counterclockwise until reach the start point
        // again. This loop runs O(h) times where h is
        // number of points in result or output.
        int p = l, q;
        int n=nodelist.size();
        do
        {
            // Add current point to result
            hulllist.add(nodelist.get(p));
      
            // Search for a point 'q' such that 
            // orientation(p, x, q) is counterclockwise 
            // for all points 'x'. The idea is to keep 
            // track of last visited most counterclock-
            // wise point in q. If any point 'i' is more 
            // counterclock-wise than q, then update q.
            q = (p + 1) % n;
             
            for (int i = 0; i < n; i++)
            {
               // If i is more counterclockwise than 
               // current q, then update q
            	Point2D.Double p1=new Point2D.Double(nodelist.get(p).centerx,nodelist.get(p).centery);
            	Point2D.Double i1=new Point2D.Double(nodelist.get(i).centerx,nodelist.get(i).centery);
            	Point2D.Double q1=new Point2D.Double(nodelist.get(q).centerx,nodelist.get(q).centery);
               if (crossProduct(p1, i1, q1)<0)
               {
               	 q = i;

               }
                                                                      
            }
      
            // Now q is the most counterclockwise with
            // respect to p. Set p as q for next iteration, 
            // so that q is added to result 'hull'
            p = q;
      
        } while (p != l);  
    }
	//return the index of the node that have the x;
	public int findLeftMostPoint()
	{
		int size=nodelist.size();
		Double leftmost=10.0;
		int besti=0;
		for(int i=0;i<size;i++)
		{
          Double x=nodelist.get(i).centerx;
          if(x<leftmost)
          {
    		leftmost=x;
    		besti=i;
          }
		}
		return besti;
	}

	public void drawConvexHull(GL2 gl)
	{
		


	    //if there is only one point in the nodelist do noting
	    if(nodelist.size()==0)
	    {
	    	//do nothing.
	    }
	    else if(nodelist.size()==1)
	    {
	    	//do nothing
	    	Node node=nodelist.get(0);
	        node.hull=true;
	        hulllist.add(node);

	    }
	    else if(nodelist.size()==2)
	    {
	    	//then just draw a line
	    	gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			//make the line thicker
			gl.glLineWidth(6.0f);
			gl.glColor3f(1.0f,0.5f,0.5f);
			Node node1=nodelist.get(0);
			Node node2=nodelist.get(1);
			//set the property of the node to be on the hull
			node1.hull=true;
			node2.hull=true;
			Double x1=node1.centerx;
			Double x2=node2.centerx;
			Double y1=node1.centery;
			Double y2=node2.centery;
			gl.glBegin(GL.GL_LINES);
			gl.glVertex2d(x1,y1);
			gl.glVertex2d(x2,y2);
			gl.glEnd();
			//add the node to the hulllist
			if(node1.centerx<=node2.centerx)
			{
				hulllist.add(node1);
			    hulllist.add(node2);
			}
			else
			{
				hulllist.add(node2);
			    hulllist.add(node1);
			}
			
			//need to get rid of this later.
			bpoints.clear();
			getAuxilaryPoint();
			drawBooloon(gl);

			//choose this color for the edge	
	    }
	    //need to check if the three points are colinear
	  //   else if(nodelist.size()==3)
	  //   {
	  //   	Node node1=nodelist.get(0);
			// Node node2=nodelist.get(1);
			// Node node3=nodelist.get(2);
			// //check if they are colinear:
			// Point2D.Double p1=new Point2D.Double(node1.centerx,node1.centery);
			// Point2D.Double p2=new Point2D.Double(node2.centerx,node2.centery);
			// Point2D.Double p3=new Point2D.Double(node3.centerx,node3.centery);
			// //need to find which one is in the middle
			// gl.glEnable(GL.GL_BLEND);
			// gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			// //make the line thicker
			// gl.glLineWidth(6.0f);
			// gl.glColor3f(1.0f,0.5f,0.5f);
			// if(crossProduct(p1,p2,p3)==0)
			// {
	  //           gl.glBegin(GL.GL_LINES);
			// 	gl.glVertex2d(p1.x,p1.y);
			// 	gl.glVertex2d(p2.x,p2.y);
			// 	gl.glVertex2d(p3.x,p3.y);
			// 	gl.glEnd();
			// 	return;
			// }
			// else if(crossProduct(p1,p3,p2)==0)
			// {
			// 	gl.glBegin(GL.GL_LINES);
			// 	gl.glVertex2d(p1.x,p1.y);
			// 	gl.glVertex2d(p3.x,p3.y);
			// 	gl.glVertex2d(p2.x,p2.y);
			// // 	gl.glEnd();
			// // 	return;

			// // }
			// // else if(crossProduct(p2,p1,p3)==0)
			// // {
			// // 	gl.glBegin(GL.GL_LINES);
			// // 	gl.glVertex2d(p2.x,p2.y);
			// // 	gl.glVertex2d(p1.x,p1.y);
			// // 	gl.glVertex2d(p3.x,p3.y);
			// // 	gl.glEnd();
			// // 	return;
			// // }


			// //this is the no-colinaer case.
	  //   	gl.glEnable(GL.GL_BLEND);
			// gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			// //make the line thicker
			// gl.glLineWidth(6.0f);
			// gl.glColor3f(1.0f,0.5f,0.5f);
   // //          Node node1=nodelist.get(0);
			// // Node node2=nodelist.get(1);
			// // Node node3=nodelist.get(2);
			// Double x1=node1.centerx;
			// Double x2=node2.centerx;
			// Double x3=node3.centerx;
			// Double y1=node1.centery;
			// Double y2=node2.centery;
			// Double y3=node3.centery;
			// node1.hull=true;
			// node2.hull=true;
			// node3.hull=true;
			// gl.glBegin(GL.GL_LINE_LOOP);
			// gl.glVertex2d(x1,y1);
			// gl.glVertex2d(x2,y2);
			// gl.glVertex2d(x3,y3);
			// gl.glEnd();
			// gl.glColor4f(1.0f,1.0f,1.0f,0.5f);
			// gl.glBegin(GL2.GL_POLYGON);
			// gl.glVertex2d(x1,y1);
			// gl.glVertex2d(x2,y2);
			// gl.glVertex2d(x3,y3);
			// gl.glEnd();
			// //if(node1.centerx<=node2.centerx && node1.centerx<node3.centerx)
			// hulllist.add(node1);
			// hulllist.add(node2);
			// hulllist.add(node3);

			// //need to delete later
			// bpoints.clear();
			// getAuxilaryPoint();
		 //    drawBooloon(gl);
	  //   }
	    //more general case is here.
	    //add the first point that on the left most to the hull
		else
		{
			convexHull();
			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			//make the line thicker
			gl.glLineWidth(6.0f);
			gl.glColor3f(1.0f,0.5f,0.5f);
			gl.glBegin(GL.GL_LINE_LOOP);
			for(Node n :hulllist)
			{
				gl.glVertex2d(n.centerx,n.centery);
			}
            
			gl.glEnd();
			gl.glColor4f(1.0f,1.0f,1.0f,0.5f);
			gl.glBegin(GL2.GL_POLYGON);
			for(Node n :hulllist)
			{
				gl.glVertex2d(n.centerx,n.centery);
			}
            
			gl.glEnd();
			bpoints.clear();
			getAuxilaryPoint();
			// System.out.println("total points for auxillary: "+bpoints.size());
			// for(Node a:hulllist)
			// {
			// 	System.out.println("points are: "+a.centerx);
			// }
		    drawBooloon(gl);
		}
	}
    //if it is positive means it is counterclockwise, if it is negative means it is clockwise
    //it will means that ac to ab is clockwise or counterclockwise
    //true means countercolockwise
	public boolean checkClockwise(Point2D.Double c, Point2D.Double a, Point2D.Double b)
	{
         Vector v1=new Vector(a.x-c.x,a.y-c.y);
         Vector v2=new Vector(b.x-c.x,b.y-c.y);
         double cross=v1.crossProduct(v2);
         if(cross>=0)
         {
         	//this means it is counetrclockwise
         	return true;
         }
         else
         {
         	return false;
         }
	}
    //give 3 points need to draw the arch between them
    public void drawArc(GL2 gl,Point2D.Double c, Point2D.Double p1, Point2D.Double p2)
    {
    	//first of all need to calcluate the arch
    	Vector v11=new Vector(p1.x-c.x,p1.y-c.y);
    	Vector v22=new Vector(p2.x-c.x,p2.y-c.y);
        //then normalize it
        Vector v1=v11.normalize();
        Vector v2=v22.normalize();

        double dot=v1.dotProduct(v2);
        double theta=Math.acos(dot);

        //also need to find the start theta
        Vector base=new Vector(1,0);
        //System.out.println("angle between two radius: "+theta);
        double delta=theta/32.0;

        //check which direction is the correct one
        Vector f1;
        Vector f2;
        Point2D.Double pp1;
        Point2D.Double pp2;
        boolean direction=checkClockwise(c,p1,p2);
        if(direction)
        {
        	f1=v1;
        	f2=v2;
        	pp1=p1;
        	pp2=p2;
        }
        else
        {
        	f1=v2;
        	f2=v1;
        	pp1=p2;
        	pp2=p1;
        }
        double dot1=f1.dotProduct(base);
        double dot2=f2.dotProduct(base);
        double theta1=Math.acos(dot1);
        double theta2=Math.acos(dot2);

        //then need to decide if the start point is below the x-axis or above the x-axis
        //System.out.println("start angle is: "+theta1);
        System.out.println("pp1 is: "+pp1.x);
        if(pp1.y<=c.y)
        {
        	theta1=2*3.1415-theta1;
        }

        gl.glEnable(GL.GL_BLEND);
		gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		//make the line thicker
		gl.glLineWidth(6.0f);
		gl.glColor3f(1.0f,0.5f,0.5f);
		gl.glBegin(GL.GL_LINES);
		//System.out.println("old point: "+p1.x);
		//System.out.println("old point: "+p2.x);
        for(int i=0;i<32;i++)
        {
        	double data=c.x+br*Math.cos(theta1+delta*i);
        	double data1=c.x+br*Math.cos(theta1+delta*i);
        	gl.glVertex2d(c.x+br*Math.cos(theta1+delta*i),c.y+br*Math.sin(theta1+delta*i));
         
        }
        gl.glEnd();
    }
	public Double innerProduct(Point2D.Double v1, Point2D.Double v2)
	{
       return v1.x*v2.x+v1.y*v2.y;
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
