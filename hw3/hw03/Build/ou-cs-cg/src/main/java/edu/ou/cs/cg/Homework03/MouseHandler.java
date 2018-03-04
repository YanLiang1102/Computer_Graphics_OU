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

package edu.ou.cs.cg.Homework03;

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
		Boolean movekite=view.getmovekite();
		if(!movekite)
		{
				Point2D.Double	v = calcCoordinatesInView(e.getX(), e.getY());

			int currentStar=view.getCurrentStar();
			if(currentStar==-1)
			{
				//view.setCurrentStar(1);
				currentStar=0;
			}
			//this need to use 720 to flip it since we use our own set 1280 and 540 so we need the stuff to working we need this.
			int skyline=view.getSkyline();
			//only draw if the click is above the skyline!
			if(v.y>skyline)
			{
				view.setStarPosition(currentStar,(int)(v.x),(int)(v.y));	
			}

		}
		
        

	}

	public void		mouseEntered(MouseEvent e)
	{
		Point2D.Double	v = calcCoordinatesInView(e.getX(), e.getY());

		view.setCursor(v);
	}

	public void		mouseExited(MouseEvent e)
	{
		view.setCursor(null);
	}

	public void		mousePressed(MouseEvent e)
	{
		//when pressed set the kite center here
		// Boolean movekite=view.getmovekite();
		// if(movekite)
		// {
		//   Point2D.Double	v = calcCoordinatesInView(e.getX(), e.getY());
		//   //view.setkite((int)v.x,(int)v.y);
		//   //System.out.println("pointed added to the arraylist!!!");
		//   view.addPointToKiteLineDrag((int)(v.x),(int)(v.y));
		//   view.setkite((int)(v.x),(int)(v.y));

		// }

	}

	public void		mouseReleased(MouseEvent e)
	{
		//draw the polyline
		// Boolean movekite=view.getmovekite();
		// if(movekite)
		// {
		// 	view.setDrawKiteLine(true);

		// }
		//System.out.println("release going to be called everytime?");
		Boolean movekite=view.getmovekite();
		if(movekite)
		{
		  Point2D.Double	v = calcCoordinatesInView(e.getX(), e.getY());
		  //view.setkite((int)v.x,(int)v.y);
		  //System.out.println("pointed added to the arraylist!!!");
		  view.addPointToKiteLineDrag((int)(v.x),(int)(v.y));
		  view.setkite((int)(v.x),(int)(v.y));
          //set the during drag to be false in taht way kite draw is not transparent any more
          view.setDrag(false);
		}
	}

	//**********************************************************************
	// Override Methods (MouseMotionListener)
	//**********************************************************************

	public void		mouseDragged(MouseEvent e)
	{
		//set during drag to be true
		Boolean movekite=view.getmovekite();
		if(movekite)
		{
		   view.setDrag(true);
		}

		else
		{
			//need to change centroid if the movekite is not fired
			 Point2D.Double	v = calcCoordinatesInView(e.getX(), e.getY());
			 view.changeCentroid((int)(v.x),(int)(v.y));
		}
		
	}

	public void		mouseMoved(MouseEvent e)
	{
		Point2D.Double	v = calcCoordinatesInView(e.getX(), e.getY());

		view.setCursor(v);
	}

	//**********************************************************************
	// Override Methods (MouseWheelListener)
	//**********************************************************************

	public void		mouseWheelMoved(MouseWheelEvent e)
	{

	}

	//**********************************************************************
	// Private Methods
	//**********************************************************************

	private Point2D.Double	calcCoordinatesInView(int sx, int sy)
	{
		// int				w = view.getWidth();
		// int				h = view.getHeight();
		// Point2D.Double	p = view.getOrigin();
		// double			vx = p.x + (sx * 2.0) / w - 1.0;
		// double			vy = p.y - (sy * 2.0) / h + 1.0;
		// return new Point2D.Double(vx, vy);
		int w=view.getWidth();
		int h=view.getHeight();
		//Point2D.Double p=view.getOrigin();
		double vx=(sx*1.0/w)*1280.0;
		double vy=(sy*1.0/h)*720.0;

		return new Point2D.Double(vx, 720-vy);

	}
}

//******************************************************************************
