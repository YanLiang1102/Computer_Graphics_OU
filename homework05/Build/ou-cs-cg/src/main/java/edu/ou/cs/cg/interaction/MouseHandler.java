//******************************************************************************
// Copyright (C) 2016 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Mon Feb 29 23:46:15 2016 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20160225 [weaver]:	Original file.
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

//******************************************************************************

/**
 * The <CODE>MouseHandler</CODE> class.<P>
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class MouseHandler extends MouseAdapter
{
	//**********************************************************************
	// Private Members
	//**********************************************************************

	// State (internal) variables
	private final View	view;
	//store the position when the mouse is clicked
	private Double cx=0.0;
	private Double cy=0.0;
	private Double origx=0.0;
	private Double origy=0.0;
	private boolean fornode=false;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public MouseHandler(View view)
	{
		this.view = view;

		Component	component = view.getComponent();

		component.addMouseListener(this);
		component.addMouseMotionListener(this);
		component.addMouseWheelListener(this);
	}

	//**********************************************************************
	// Override Methods (MouseListener)
	//**********************************************************************

	public void		mouseClicked(MouseEvent e)
	{
		 

		// if (Utilities.isShiftDown(e))
		// 	view.setOrigin(v);
		// else
		// 	view.add(v);
		//System.out.println("hey I pressed the mouse.");
		//this is the cursor
		Point2D.Double	v = calcCoordinatesInView(e.getX(), e.getY());
		//System.out.println("cursor x: "+e.getX());
		//System.out.println("cursor y: "+e.getY());
 		int index=view.findFrontMostSelected(v);
 		//which means u clicked some node
 		if(index!=-1)
 		{	
           view.highlightNode(index);
           fornode=true;
 		}
 		else{
 			fornode=false;
 		}
        //System.out.println("frontmost index for this is: "+index);
		
		//inoder for the view to be repaint
		//why do we need this repaint is the fps going to fix this???
		view.setCursor(v);
		//cx is the current position of the cursor before somebody drag it.
		cx=v.x;
		cy=v.y;
		Point2D.Double origin=view.getOrigin();
		origx=origin.x;
		origy=origin.y;

	}

	public void		mouseEntered(MouseEvent e)
	{
		// Point2D.Double	v = calcCoordinatesInView(e.getX(), e.getY());

		// view.setCursor(v);
	}

	public void		mouseExited(MouseEvent e)
	{
		view.setCursor(null);
	}

	public void		mousePressed(MouseEvent e)
	{
	}

	public void		mouseReleased(MouseEvent e)
	{
		//System.out.println("hey mouse released!");
	}

	//**********************************************************************
	// Override Methods (MouseMotionListener)
	//**********************************************************************

	public void		mouseDragged(MouseEvent e)
	{
		Point2D.Double	v = calcCoordinatesInView(e.getX(), e.getY());
		Point2D.Double diff=new Point2D.Double(v.x-cx,v.y-cy);
		if(fornode)
		{
			//when shift is up
			if(!Utilities.isShiftDown(e))
			{
				
				 
				 //this is set center for the currently selected node.
				 view.setCenter(new Point2D.Double(origx+diff.x,origy+diff.y));
				 
			}
			//when shift is down
			else
			{
	          //need to rotate;
				Point2D.Double v1=new Point2D.Double(cx-origx,cy-origy);
				Point2D.Double v2=new Point2D.Double(v.x-origx,v.y-origy);
				Double angle=view.angleBetweenVectors(v1,v2);
				//and this angle need to be reset when the highlighted node is changed.
				//view.rotateAngle=angle;
				Node node=view.getNode();
				node.angle=angle;

			}

		}

		else{
			view.setOrigin(new Point2D.Double(origx-0.5*diff.x,0.5*origy-diff.y));
		}
		
		view.setCursor(v);
		 
	}

	

	public void		mouseMoved(MouseEvent e)
	{
		// Point2D.Double	v = calcCoordinatesInView(e.getX(), e.getY());

		// view.setCursor(v);
	}

	//**********************************************************************
	// Override Methods (MouseWheelListener)
	//**********************************************************************

	public void		mouseWheelMoved(MouseWheelEvent e)
	{
		//System.out.println("he")
	}

	//**********************************************************************
	// Private Methods
	//**********************************************************************

	private Point2D.Double	calcCoordinatesInView(int sx, int sy)
	{
		int				w = view.getWidth();
		int				h = view.getHeight();
		Point2D.Double	p = view.getOrigin();
		double			vx = p.x + (sx * 2.0) / w - 1.0;
		double			vy = p.y - (sy * 2.0) / h + 1.0;

		return new Point2D.Double(vx, vy);
	}
}


//******************************************************************************
