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
		Point2D.Float	v = calcCoordinatesInView(e.getX(), e.getY());
        view.centerlist.add(v);
        view.counterlist.add(0);
        view.directionlist.add(view.generateRandomDirection());
	   // view.drawrandom(v);
	}

	public void		mouseEntered(MouseEvent e)
	{
		// Point2D.Double	v = calcCoordinatesInView(e.getX(), e.getY());

		// view.setCursor(v);
	}

	public void		mouseExited(MouseEvent e)
	{
		//view.setCursor(null);
	}

	public void		mousePressed(MouseEvent e)
	{
	}

	public void		mouseReleased(MouseEvent e)
	{
	}

	//**********************************************************************
	// Override Methods (MouseMotionListener)
	//**********************************************************************

	public void		mouseDragged(MouseEvent e)
	{
		 Point2D.Float v = calcCoordinatesInView(e.getX(), e.getY());


		// view.add(v);
		// view.setCursor(v);
	}

	public void		mouseMoved(MouseEvent e)
	{
		//Point2D.Double	v = calcCoordinatesInView(e.getX(), e.getY());

		//view.setCursor(v);
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

	private Point2D.Float	calcCoordinatesInView(int sx, int sy)
	{
		int				w = view.getWidth();
		int				h = view.getHeight();
		Point2D.Float	p = view.getOrigin();
		float			vx = p.x + (sx * 2.0f) / w - 1.0f;
		float		vy = p.y - (sy * 2.0f) / h + 1.0f;

		return new Point2D.Float(vx, vy);
	}
}

//******************************************************************************
