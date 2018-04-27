//******************************************************************************
// Copyright (C) 2016 University of Oklahoma Board of Trustees.
//******************************************************************************
// Last modified: Tue Feb  9 20:33:16 2016 by Chris Weaver
//******************************************************************************
// Major Modification History:
//
// 20160225 [weaver]:	Original file.
//
//******************************************************************************
// Notes:
//
//******************************************************************************

package edu.ou.cs.cg.project;

//import java.lang.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.media.opengl.*;
import javax.media.opengl.awt.*;
import javax.media.opengl.glu.*;

import com.jogamp.newt.event.WindowAdapter;
import com.jogamp.newt.event.WindowEvent;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.*;
import com.jogamp.opengl.util.gl2.GLUT;

import java.awt.DisplayMode;

import java.io.File;
import java.io.IOException;



//******************************************************************************

/**
 * The <CODE>Interaction</CODE> class.<P>
 *
 * @author  Chris Weaver
 * @version %I%, %G%
 */
//******************************************************************************

public final class projectCubic
//implements GLEventListener
{
	public static final GLU		GLU = new GLU();
	public static final GLUT	GLUT = new GLUT();
	public static final Random	RANDOM = new Random();	
	private static final String TITLE = "Graphic Project Rubik's Cube";	
	private static final int CANVAS_WIDTH  = 1280;
	private static final int CANVAS_HEIGHT = 720;


	//**********************************************************************
	// Main
	//**********************************************************************
	
	public static void main(String[] args)
	{
		File im = new File("/Users/yanliang/comGraphic/gitCG/Computer_Graphics_OU/finalproject/Build/ou-cs-cg/build/install/template/bin/ou3.jpeg");
	          //System.out.println(im.getCanonicalPath());
		System.out.println("hey I am not here can u do that?");
		GLProfile glp = GLProfile.getDefault();
		GLCapabilities caps = new GLCapabilities(glp);
		GLWindow window = GLWindow.create(caps);	 
		window.setSize(CANVAS_WIDTH, CANVAS_HEIGHT);
		window.setTitle(TITLE);
		window.setVisible(true);	
		View view = new View(window);					
	}
	
	
	
	
}

























