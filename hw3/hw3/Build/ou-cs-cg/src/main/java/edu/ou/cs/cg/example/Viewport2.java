//******************************************************************************
// Copyright (C) 2016 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Tue Feb  9 19:41:58 2016 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20160209 [weaver]:	Original file.
//
//******************************************************************************
// Notes:
//
//******************************************************************************

package edu.ou.cs.cg.example;

//import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Point2D;
import java.util.Random;
import javax.swing.JFrame;
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;

//******************************************************************************

/**
 * The <CODE>Viewport2</CODE> class.<P>
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class Viewport2
	implements GLEventListener
{
	//**********************************************************************
	// Public Class Members
	//**********************************************************************

	//public static final GLU		GLU = new GLU();
	//public static final GLUT	GLUT = new GLUT();
	public static final Random	RANDOM = new Random();

	//**********************************************************************
	// Private Members
	//**********************************************************************

	// State (internal) variables
	private double	theta = 0.0;
	private double	s = 0.0;
	private double	c = 0.0;
	private int		m = 1;
	private int		k = 0;
	private int		cmode = 0;
	private int		vmode = 0;

	private int		w = 0;
	private int		h = 0;

	//**********************************************************************
	// Main
	//**********************************************************************

	public static void main(String[] args)
	{
		GLProfile		profile = GLProfile.getDefault();
		GLCapabilities	capabilities = new GLCapabilities(profile);
		GLCanvas		canvas = new GLCanvas(capabilities);	// Single-buffer
		//GLJPanel		canvas = new GLJPanel(capabilities);	// Double-buffer
		JFrame			frame = new JFrame("Viewport");

		canvas.setPreferredSize(new Dimension(1240, 240));

		frame.setBounds(50, 50, 600, 600);
		frame.getContentPane().add(canvas);
		frame.pack();
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});

		canvas.addGLEventListener(new Viewport2());

		FPSAnimator		animator = new FPSAnimator(canvas, 24);

		animator.start();
	}

	//**********************************************************************
	// Override Methods (GLEventListener)
	//**********************************************************************

	public void		init(GLAutoDrawable drawable)
	{
		w = 1240;
		h = 240;
	}

	public void		dispose(GLAutoDrawable drawable)
	{
	}

	public void		display(GLAutoDrawable drawable)
	{
		update();
		render(drawable);
	}

	public void		reshape(GLAutoDrawable drawable, int x, int y, int w, int h)
	{
		this.w = w;
		this.h = h;
	}

	//**********************************************************************
	// Private Methods (Rendering)
	//**********************************************************************

	private void	update()
	{
		theta += 0.02;
		s = Math.sin(theta);
		c = Math.cos(theta);

		if (m > 100000)
			m = 1;
		else
			m = (int)Math.floor(m * 1.07) + 1;

		if ((k++ % 300) == 0)
		{
			cmode = RANDOM.nextInt(3);
			vmode = RANDOM.nextInt(3);

			System.out.println("Mode: " + cmode + ":" + vmode);
		}
	}

	private void	render(GLAutoDrawable drawable)
	{
		GL2		gl = drawable.getGL().getGL2();
		GLU		glu = new GLU();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT);		// Clear the buffer

		float	xmin = -2.0f;
		float	xmax =  2.0f;
		float	ymin = -2.0f;
		float	ymax =  2.0f;

		for (int i=0; i<5; i++)
		{
			gl.glMatrixMode(GL2.GL_PROJECTION);		// Prepare for matrix xform
			gl.glLoadIdentity();					// Set to identity matrix
			glu.gluOrtho2D(xmin, xmax, ymin, ymax);	// 2D translate and scale

			gl.glMatrixMode(GL2.GL_MODELVIEW);
			gl.glLoadIdentity();
			gl.glViewport(i*w/5, 0, w/5 - 8, h);	// Five ports left to right

			drawTriangle(gl);						// Draw a colored triangle
			drawGasket(gl);							// Draw a Serpinski gasket

			// Zoom by x2 and offset a little to make levels more interesting
			xmin *= 0.5f + 0.02f;
			xmax *= 0.5f + 0.02f;
			ymin *= 0.5f + 0.02f;
			ymax *= 0.5f + 0.02f;

			drawBounds(gl, xmin, xmax, ymin, ymax);	// Draw inset box
		}
	}

	//**********************************************************************
	// Private Methods (Scene)
	//**********************************************************************

	private void	drawBounds(GL2 gl, float xmin, float xmax, float ymin,
							   float ymax)
	{
		gl.glColor3f(0.5f, 0.5f, 0.5f);
		gl.glBegin(GL.GL_LINE_LOOP);

		gl.glVertex2d(xmax, ymax);
		gl.glVertex2d(xmin, ymax);
		gl.glVertex2d(xmin, ymin);
		gl.glVertex2d(xmax, ymin);

		gl.glEnd();

		gl.glBegin(GL.GL_LINES);

		gl.glVertex2d(xmin, ymax);
		gl.glVertex2d(xmax * 2.0f - 0.02f, ymax * 2.0f - 0.02f);

		gl.glVertex2d(xmin, ymin);
		gl.glVertex2d(xmax * 2.0f - 0.02f, ymin * 2.0f - 0.02f);

		gl.glEnd();
	}

	private void	drawTriangle(GL2 gl)
	{
		gl.glBegin(GL.GL_TRIANGLES);

		Point2D.Double[]	t = new Point2D.Double[3];
		Color[]				rgb = new Color[3];

		calcColors(rgb, cmode);
		calcVertices(t, vmode);

		float[]				rgb1 = rgb[0].getRGBColorComponents(null);
		float[]				rgb2 = rgb[1].getRGBColorComponents(null);
		float[]				rgb3 = rgb[2].getRGBColorComponents(null);

		gl.glColor3f(rgb1[0], rgb1[1], rgb1[2]);
		gl.glVertex2d(t[0].x, t[0].y);

		gl.glColor3f(rgb2[0], rgb2[1], rgb2[2]);
		gl.glVertex2d(t[1].x, t[1].y);

		gl.glColor3f(rgb3[0], rgb3[1], rgb3[2]);
		gl.glVertex2d(t[2].x, t[2].y);

		gl.glEnd();
	}

	private void	drawGasket(GL2 gl)
	{
		gl.glBegin(GL.GL_POINTS);
		gl.glColor3f(1.0f, 1.0f, 1.0f);

		Point2D.Double[]	t = new Point2D.Double[3];
		Point2D.Double		p = new Point2D.Double(c, c);

		calcVertices(t, vmode);

		p.x = t[0].x;
		p.y = t[0].y;

		for (int i=0; i<m; i++)
		{
			int	index = RANDOM.nextInt(3);

			p.x = (p.x + t[index].x) / 2;
			p.y = (p.y + t[index].y) / 2;
			gl.glVertex2d(p.x, p.y);
		}

		gl.glEnd();
	}

	//**********************************************************************
	// Private Methods (Utility)
	//**********************************************************************

	private void	calcColors(Color[] rgb, int version)
	{
		if (version == 1)			// Fixed RGB
		{
			rgb[0] = Color.RED;
			rgb[1] = Color.GREEN;
			rgb[2] = Color.BLUE;
		}
		else if (version == 2)		// Full R, G, or B with other two oscillating
		{
			float	ca = (float)Math.abs(c);
			float	cb = (float)Math.abs(s);
			float	cc = (float)Math.abs((c+s) / 2.0);

			rgb[0] = new Color(1.0f, ca, cb);
			rgb[1] = new Color(cc, 1.0f, ca);
			rgb[2] = new Color(cb, cc, 1.0f);
		}
		else						// Rotating hues
		{
			double	a = theta / 10.0;

			rgb[0] = Color.getHSBColor((float)(a + 0.0 / 3.0), 1.0f, 1.0f);
			rgb[1] = Color.getHSBColor((float)(a + 1.0 / 3.0), 1.0f, 1.0f);
			rgb[2] = Color.getHSBColor((float)(a + 2.0 / 3.0), 1.0f, 1.0f);
		}
	}

	private void	calcVertices(Point2D.Double[] t, int version)
	{
		if (version == 1)			// Three vertices flipping
		{
			t[0] = new Point2D.Double(-c, -c);
			t[1] = new Point2D.Double(0, c);
			t[2] = new Point2D.Double(s, -s);
		}
		else if (version == 2)		// One vertex rotating
		{
			t[0] = new Point2D.Double(c, s);
			t[1] = new Point2D.Double(-1.0, 0.0);
			t[2] = new Point2D.Double(1.0, 0.0);
		}
		else						// Three vertices fixed
		{
			t[0] = new Point2D.Double(0.0, 1.0);
			t[1] = new Point2D.Double(-1.0, -1.0);
			t[2] = new Point2D.Double(1.0, -1.0);
		}
	}

	//**********************************************************************
	// Fake Methods (Line Clipping)
	//**********************************************************************

// 	int		clipLineSegment(Point a, Point b, Rectangle r)
// 	{
// 		do
// 		{
// 			if (r.contains(a) and r.contains(b))
// 				return 1;
// 			else if ([both left, both right, both above, both below])
// 				return 0;
// 			else if (!r.contains(a))
// 			{
// 				if (a.isLeft(r))        a = r.chopLeft(a);
// 				else if (a.isRight(r))  a = r.chopRight(a);
// 				else if (a.isBelow(r))  a = r.chopBottom(a);
// 				else if (a.above(r))    a = r.chopTop(a);
// 			}
// 			else if (!r.contains(a))
// 			{
// 				if (b.isLeft(r))        b = r.chopLeft(b);
// 				else if (b.isRight(r))  b = r.chopRight(b);
// 				else if (b.isBelow(r))  b = r.chopBottom(b);
// 				else if (b.above(r))    b = r.chopTop(b);
// 			}
// 		}
// 		while (true);
// 	}
}

//******************************************************************************
