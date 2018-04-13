//******************************************************************************
// Copyright (C) 2016 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Mon Feb 29 23:36:04 2016 by Chris Weaver
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
import java.util.*;

//******************************************************************************

/**
 * The <CODE>KeyHandler</CODE> class.<P>
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class KeyHandler extends KeyAdapter
{
	//**********************************************************************
	// Private Members
	//**********************************************************************

	// State (internal) variables
	private final View	view;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public KeyHandler(View view)
	{
		this.view = view;

		Component	component = view.getComponent();

		component.addKeyListener(this);
	}

	//**********************************************************************
	// Override Methods (KeyListener)
	//**********************************************************************

	public void		keyPressed(KeyEvent e)
	{
		Point2D.Float	p = view.getOrigin();
		double			a = (Utilities.isShiftDown(e) ? 0.01 : 0.1);

		switch (e.getKeyCode())
		{
			case KeyEvent.VK_5:
			//nothing for thsi button
					break;
			case KeyEvent.VK_2:
			    view.setContainer(2);
			    //reset the stuff 
				Vector newdirection=view.generateRandomDirection();
				view.setvx(newdirection.dX);
				view.setvy(newdirection.dY);
                view.setstartx(-0.8f);
                view.setstarty(0.0f);
				view.setcounter(0);
				view.setcolormagnitude(1.0f);
				view.colorvector=new ArrayList<Point2D.Float>();
				for(int i=0;i<6;i++)
				{

				  view.colorvector.add(new Point2D.Float(1.0f-0.15f*i,0.5f+0.05f*i));

				}
			     break;

			case KeyEvent.VK_4:
			     view.setContainer(4);
			     newdirection=view.generateRandomDirection();
				 view.setvx(newdirection.dX);
				 view.setvy(newdirection.dY);
                 view.setstartx(-0.8f);
                 view.setstarty(0.0f);
				 view.setcounter(0);
				 view.setcolormagnitude(1.0f);	
				 view.colorvector=new ArrayList<Point2D.Float>();
					for(int i=0;i<10;i++)
					{

					  view.colorvector.add(new Point2D.Float(1.0f-0.1f*i,0.5f+0.05f*i));

					}
			     break;
			

			case KeyEvent.VK_7:
			    view.setshape(7);
			    break;

			case KeyEvent.VK_6:
			    view.setshape(6);
			    break;
			


			case KeyEvent.VK_NUMPAD8:
		

			case KeyEvent.VK_1:
				view.setContainer(1);
				view.colorvector=new ArrayList<Point2D.Float>();
				for(int i=0;i<4;i++)
				{
                  //set the vector color for the side of thsi regtangualr
				  view.colorvector.add(new Point2D.Float(1.0f-0.1f*i,0.5f+0.1f*i));

				}
				
				//when container chaneg reset the color magnitude
				view.setcolormagnitude(1.0f);	
				break;

			case KeyEvent.VK_3:
				view.setContainer(3);
				newdirection=view.generateRandomDirection();
				view.setvx(newdirection.dX);
				view.setvy(newdirection.dY);
                view.setstartx(-0.8f);
                view.setstarty(0.0f);
				view.setcounter(0);
				view.setcolormagnitude(1.0f);
				//colorvector=view.getColorvector();
				view.colorvector=new ArrayList<Point2D.Float>();
				for(int i=0;i<32;i++)
				{

				  view.colorvector.add(new Point2D.Float(1.0f-0.03f*i,0.5f+0.01f*i));

				}	
				break;

			case KeyEvent.VK_9:
			    view.setshape(9);
			    break;

			case KeyEvent.VK_8:
			    view.setshape(8);
			    break;
				
			//slow down
			case KeyEvent.VK_LEFT:
				view.setSpeed(view.getSpeed()*0.9f);	break;
		    //speed up
			case KeyEvent.VK_RIGHT:
			    view.setSpeed(view.getSpeed()*1.1f);;	break;

			case KeyEvent.VK_UP:
			    view.setCommonr(view.getCommonr()*1.41f);
			    break;
			case KeyEvent.VK_DOWN:
			    view.setCommonr(view.getCommonr()/1.41f);
			    break;
			case KeyEvent.VK_DELETE:
				view.clear();
				return;
		}

		view.setOrigin(p);
	}
}

//******************************************************************************
