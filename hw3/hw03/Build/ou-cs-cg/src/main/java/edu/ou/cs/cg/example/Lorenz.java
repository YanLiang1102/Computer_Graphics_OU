//******************************************************************************
// Copyright (C) 2016 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Tue Mar  1 00:57:45 2016 by Chris Weaver
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
import javax.swing.JFrame;
import javax.media.opengl.*;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.*;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;

//******************************************************************************

/**
 * The <CODE>Lorenz</CODE> class.<P>
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
public final class Lorenz
	implements GLEventListener
{
	//**********************************************************************
	// Public Class Members
	//**********************************************************************

	public static final GLU		GLU = new GLU();
	public static final GLUT	GLUT = new GLUT();

	//**********************************************************************
	// Private Members
	//**********************************************************************

	// State (internal) variables
	private int		m = 1;

	//**********************************************************************
	// Main
	//**********************************************************************

	public static void main(String[] args)
	{
		GLProfile		profile = GLProfile.getDefault();
		GLCapabilities	capabilities = new GLCapabilities(profile);
		GLCanvas		canvas = new GLCanvas(capabilities);
		JFrame			frame = new JFrame("Lorenz");

		frame.setBounds(50, 50, 1302, 724);
		frame.getContentPane().add(canvas);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

		frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});

		canvas.addGLEventListener(new Lorenz());

		FPSAnimator		animator = new FPSAnimator(canvas, 60);

		animator.start();
	}

	//**********************************************************************
	// Override Methods (GLEventListener)
	//**********************************************************************

	public void		init(GLAutoDrawable drawable)
	{
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
	}

	//**********************************************************************
	// Private Methods (Rendering)
	//**********************************************************************

	private void	update()
	{
		if (m > 100000)
			m = 1;
		else
			m++;
		m = (int)Math.floor(m * 1.01) + 1;
	}

	private void	render(GLAutoDrawable drawable)
	{
		GL2		gl = drawable.getGL().getGL2();

		gl.glClear(GL.GL_COLOR_BUFFER_BIT);		// Clear the buffer
		drawLorenz(gl);							// Draw a Serpinski gasket
		gl.glFlush();							// Finish and display
	}

	//**********************************************************************
	// Private Methods (Scene)
	//**********************************************************************

	private void	drawLorenz(GL2 gl)
	{
		gl.glBegin(GL.GL_POINTS);
		gl.glColor3f(1.0f, 1.0f, 1.0f);

		double		dt = 0.01;
		double		sigma = 10.0;
		double		beta = 8.0 / 3.0;
		double		rho = 28.0;
		double		lx = 0.1;
		double		ly = 0.0;
		double		lz = 0.0;

		for (int i=0; i<m; i++)
		{
			double	llx = lx + dt * sigma * (ly - lx);
			double	lly = ly + dt * (lx * (rho - lz) - ly);
			double	llz = lz + dt * (lx * ly - beta * lz);

			lx = llx;
			ly = lly;
			lz = llz;
			//System.out.println(" " + lx + " " + ly + " " + lz);

			float	cc = (float)((lz + 30.0) / 60.0);

			gl.glColor3f(cc, cc, cc);
			gl.glVertex2d(lx / 30.0, ly / 30.0);
		}

		gl.glEnd();
	}
}

//******************************************************************************
