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
	private final Component com;
	private final GLAutoDrawable canvas;
	//private final Boolean isShiftDown;
	//private final GL2 gl;

	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************

	public KeyHandler(View view)
	{

		this.view = view;

		Component	component = view.getComponent();
		this.com=component;
		this.canvas=view.getCanvas();
      //  this.gl=component.getGL().getGL2();
		component.addKeyListener(this);
	}

	//**********************************************************************
	// Override Methods (KeyListener)
	//**********************************************************************
    
	public void		keyPressed(KeyEvent e)
	{
		Point2D.Double	p = view.getOrigin();
		double			a = (Utilities.isShiftDown(e) ? 0.01 : 0.1);
   		//System.out.println("get key code "+e.getKeyCode());
   		int width=view.getWidth();
   		int height=view.getHeight();
        if(Utilities.isShiftDown(e))
        {
        	if(e.getKeyCode()==KeyEvent.VK_LEFT)
        	{
                System.out.println("you pressed the shift and left key!!");
                view.sethopxleft(true);

        	}
        	else if(e.getKeyCode()==KeyEvent.VK_RIGHT)
        	{
        		System.out.println("you pressed the shift and right key!!");
        		view.sethopxright(true);
        	}

        }
        else
        {
		        	switch (e.getKeyCode())
				{

					case KeyEvent.VK_NUMPAD5:
						p.x = 0.0;	p.y = 0.0;	break;

					//case KeyEvent.VK_NUMPAD4:S
					case KeyEvent.VK_PAGE_UP:
						view.increaseFenceHeight();
						break;
					case KeyEvent.VK_PAGE_DOWN:
						view.decreaseFenceHeight();
						break;
					//w for the shade of the window
					case KeyEvent.VK_W:
						 view.setShade((!view.shade));
						 break;
					case KeyEvent.VK_LEFT:
						view.sethopxleft(false);
						break;
					case KeyEvent.VK_RIGHT:
		                view.sethopxright(false);
						break;
				    case KeyEvent.VK_DOWN:
				    	view.sethopydown();
						break;
					case KeyEvent.VK_UP:
						view.sethopyup();
						break;
					//dealing with the number of fans on the polygon
					case KeyEvent.VK_1:
						view.setfans(1);
						break;
					case KeyEvent.VK_2:
						view.setfans(2);
					    break;
					case KeyEvent.VK_3:
						view.setfans(3);
						break;
					case KeyEvent.VK_4:
						view.setfans(4);
						break;
					case KeyEvent.VK_5:
						view.setfans(5);
						break;
					case KeyEvent.VK_6:
						view.setfans(6);
						break;
					case KeyEvent.VK_7:
						view.setfans(7);
						break;
					case KeyEvent.VK_8:
						view.setfans(8);
						break;
					case KeyEvent.VK_9:
						view.setfans(9);
						break;
					//press A will add a randomly postioned star

					//I am using the key T for tab.
					case KeyEvent.VK_T:

						int currentstar=view.getCurrentStar();
						int starNo=view.getStarNo();
						int index=(currentstar+1)%starNo;
						view.setIthStarColorG(index);

						//take the diff of star index and mod 5
						view.setCurrentStar(1);

						break;
					case KeyEvent.VK_K:

				       //PRESS K in order for move kite to work
					    view.setmovekite(true);
						break;
					case KeyEvent.VK_L:
					    //PRESS L in order to turn off the move kite and for the star move around to start to work
					    view.setmovekite(false);	
					    break;
					//using A to be a key to add a randomly positioned star
					case KeyEvent.VK_A:
					    view.addStar();
					    break;		
					//using D to delete the current select start
					case KeyEvent.VK_D:
					//if there is no current star set it will delete the first star.
					    view.deleteStar();
					    break;
					//turn on the change centroid mode by pressing c
					//case KeyEvent.VK_C:
					    //view.setChangeCentroid(true);
					//this will print the centroid of the star on the console
					//case KeyEvent.VK_F:
					//find the centroid of all the stars
					    //Point c=view.getCentroid();
					    //System.out.println("x is: "+c.x);
					    //System.out.println("y is: "+c.y);

					case KeyEvent.VK_DELETE:
						view.clear();
						return;
				}

         }
		

		view.setOrigin(p);
	}
}

//******************************************************************************
