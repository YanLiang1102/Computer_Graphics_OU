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
		Point2D.Double	p = view.getOrigin();
		double			a = (Utilities.isShiftDown(e) ? 0.01 : 0.1);
		//shift key is down
		if(a==0.01)
		{
			if(e.getKeyCode()==KeyEvent.VK_LEFT)
        	{
                //scale the node with width
                view.scaleNode("width");

        	}
        	else if(e.getKeyCode()==KeyEvent.VK_RIGHT)
        	{
                view.scaleNode("width");
        	}
        	else if(e.getKeyCode()==KeyEvent.VK_UP)
        	{
               view.scaleNode("height");
        	}
        	else if(e.getKeyCode()==KeyEvent.VK_DOWN)
        	{
       		  view.scaleNode("height");
        	}


		}
		else
		{
						switch (e.getKeyCode())
				{
					case KeyEvent.VK_NUMPAD5:
						p.x = 0.0;	p.y = 0.0;	break;

					case KeyEvent.VK_NUMPAD4:
					//thsi is to loop through the name using K and L, need to high light this in the report
					case KeyEvent.VK_K:
							view.indexDown();
							break;

					case KeyEvent.VK_NUMPAD6:
					case KeyEvent.VK_L:
		   					view.indexUp();
							break;
				    //using u to change the size of the balloon up
				    case KeyEvent.VK_U:
				    if(view.br<0.01)
				    {
				    	view.br=5*view.br;
				    }
				    view.br=view.br*1.5;
				    break;
				    //using j to change the size of the balloon down.
				    case KeyEvent.VK_J:
				    view.br=view.br*0.9;
				    break;
				    //r will reset the origin
				    // case KeyEvent.VK_R:
				    // view.setOrigin(new Point2D.Double(0.0,0.0));
				    // System.out.println("I pressed on R");
				    // break;
					case KeyEvent.VK_ENTER:
					        view.addNode();
							break;
					case KeyEvent.VK_DELETE:
							view.removeNode();
							System.out.println("delete pressed!");
							break;
					case KeyEvent.VK_PERIOD:
						 view.HighlightDown();
						 break;
					case KeyEvent.VK_COMMA:
					     view.HighlightUp();
					     break;
					case KeyEvent.VK_NUMPAD2:
					

					case KeyEvent.VK_NUMPAD8:

					case KeyEvent.VK_NUMPAD1:
						p.x -= a;	p.y -= a;	break;

					case KeyEvent.VK_NUMPAD7:
						p.x -= a;	p.y += a;	break;

					case KeyEvent.VK_NUMPAD3:
						p.x += a;	p.y -= a;	break;

					case KeyEvent.VK_NUMPAD9:
						p.x += a;	p.y += a;	break;
					case KeyEvent.VK_F24:
						System.out.println("hey the shift key get pressed!");
						break;
					//with the shift key up we need to translate
					case KeyEvent.VK_RIGHT:
						view.translateNode("right");
					    break;
					case KeyEvent.VK_UP:
					    view.translateNode("up");
					    break;
					case KeyEvent.VK_DOWN:
					    view.translateNode("dowm");
					    break;
					case KeyEvent.VK_LEFT:
					    view.translateNode("left");
					    break;
					

					// case KeyEvent.VK_DELETE:
					// 	view.clear();
					// 	return;
				}

		}
		

		view.setOrigin(p);
	}
}

//******************************************************************************
