
package edu.ou.cs.cg.project;



import static javax.media.opengl.GL.GL_COLOR_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_BUFFER_BIT;
import static javax.media.opengl.GL.GL_DEPTH_TEST;
import static javax.media.opengl.GL.GL_LEQUAL;
import static javax.media.opengl.GL.GL_NICEST;
import static javax.media.opengl.GL2.GL_QUADS;
import static javax.media.opengl.GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT;
import static javax.media.opengl.fixedfunc.GLLightingFunc.GL_SMOOTH;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_MODELVIEW;
import static javax.media.opengl.fixedfunc.GLMatrixFunc.GL_PROJECTION;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.awt.GLCanvas;
import javax.media.opengl.glu.GLU;
import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.newt.event.MouseEvent;
import com.jogamp.newt.event.MouseListener;
import com.jogamp.newt.opengl.GLWindow;
import com.jogamp.opengl.util.FPSAnimator;



public class View extends GLCanvas implements GLEventListener, KeyListener, MouseListener

{
	//**********************************************************************
	// Public Class Members
	//**********************************************************************



	
	private static final int CANVAS_WIDTH  = 1280;
	private static final int CANVAS_HEIGHT = 720;
	private static final int FPS = 60;
	
	private static final int size = 3;
	private static final float ZERO_F = 0.0f;
	private static final float ONE_F  = 1.0f;
	private static final float TWO_F  = 2.0f;
	private static final float CUBIE_GAP_F = 0.2f; // gap between cubies
	private static final float CUBIE_TRANSLATION_FACTOR = TWO_F + CUBIE_GAP_F;
			
	private static final float DEFAULT_CAMERA_ANGLE_X = 45.0f;
	private static final float DEFAULT_CAMERA_ANGLE_Y = 45.0f;
	private static final float DEFAULT_ZOOM = -20.0f;
	private static final int CAMERA_ROTATE_STEP_DEGREES  = 5;
	private GLU glu;
	private float cameraAngleX = DEFAULT_CAMERA_ANGLE_X;
	private float cameraAngleY = DEFAULT_CAMERA_ANGLE_Y;
	private float cameraAngleZ = ZERO_F;
	private float zoom         = DEFAULT_ZOOM;
	private float[] columnAnglesX;
	private float[] rowAnglesY;
	private float[] faceAnglesZ;
	private int mouseX = CANVAS_WIDTH/2;
	private int mouseY = CANVAS_HEIGHT/2;
	private RubiksCube rubiksCube;
    //private int count=0;
	private GLWindow canvas;
	private final FPSAnimator		animator;

	private int count=0;
	//0 will be the flower and 1 will be the zigzag
	private int cuttingoptions=-1;
	private boolean reset=false;
	private boolean stop=true;
	public enum Color { BLACK, YELLOW, GREEN, ORANGE, BLUE, RED; };
	//**********************************************************************
	// Constructors and Finalizer
	//**********************************************************************
  

	public View(GLWindow window)
	{		

	
			
		this.canvas = window;
		// Initialize rendering
		window.addGLEventListener(this);	
		window.addMouseListener(this);
		window.addKeyListener(this);
		rubiksCube = new RubiksCube(size);
		this.columnAnglesX = new float[size];
		this.rowAnglesY = new float[size];
		this.faceAnglesZ = new float[size];
		animator = new FPSAnimator(canvas, FPS);
		animator.start();
		
	}


	//**********************************************************************
	// Override Methods (GLEventListener)
	//**********************************************************************

	@Override
	public void init(GLAutoDrawable drawable) {
		GL2 gl = drawable.getGL().getGL2();
		glu = new GLU();
		gl.glClearColor(ZERO_F, ZERO_F, ZERO_F, ZERO_F);
		gl.glClearDepth(ONE_F); 
		gl.glEnable(GL_DEPTH_TEST);
		gl.glDepthFunc(GL_LEQUAL);
		gl.glHint(GL_PERSPECTIVE_CORRECTION_HINT, GL_NICEST);
		gl.glShadeModel(GL_SMOOTH);
	
	}
	
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL2 gl = drawable.getGL().getGL2();
	      
		if (height == 0) height = 1;
		float aspect = (float) width/height;
		
		gl.glViewport(0, 0, width, height);
		gl.glMatrixMode(GL_PROJECTION);
		gl.glLoadIdentity();
		glu.gluPerspective(45.0, aspect, 0.1, 100.0);
			 
		gl.glMatrixMode(GL_MODELVIEW);
		gl.glLoadIdentity();
	}

	@Override
	public void display(GLAutoDrawable drawable) {
		count=(count+1)%450;//%30;
		//System.out.println("count is: "+count);
		//when count goes to 180, the cube should get split!!
		drawRubiksCube(drawable.getGL().getGL2());

		//drawPlane(drawable.getGL().getGL2());
		if(cuttingoptions==0 && !stop)
		{
			//reset the count
			if(!reset)
			{
			  reset=true;
			  count=0;	
			}
			
			drawPlane(drawable.getGL().getGL2(),count);
			drawZigZagPlane(drawable.getGL().getGL2(),count,1,0);
			drawZigZagPlane(drawable.getGL().getGL2(),count,-1,2);


		}
			}
	
	public void drawPlane(GL2 gl,int speed)
	{
		/*gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();*/
		gl.glColor3f(0.0f,1.0f,0.0f);
		//gl.glBegin(GL.GL_TRIANGLES);
		gl.glBegin(GL_QUADS);
		//gl.glTranslatef(-3.0f, -3.0f, -3.0f);
		//gl.glPushMatrix();
		gl.glVertex3f(-12.0f+speed*0.1f,3.0f,0.0f);
		gl.glVertex3f(-6.0f+speed*0.1f,3.0f,0.0f);
		gl.glVertex3f(-6.0f+speed*0.1f,-3.0f,0.0f);
		gl.glVertex3f(-12.0f+speed*0.1f,-3.0f,0.0f);
		gl.glEnd();
      
	}
	//tilt is to change the sign and make the plane go left or right
	public void drawZigZagPlane(GL2 gl, int speed, int tilt,int color)
	{
		if(color==0)
		{
			gl.glColor3f(1.0f,0.5f,0.5f);	
		}
		else if(color==1)
		{
			gl.glColor3f(0.5f,1.0f,0.5f);
		}
		else
		{
			gl.glColor3f(0.5f,0.5f,1.0f);	
		}
		
		//gl.glBegin(GL.GL_TRIANGLES);
		gl.glBegin(GL_QUADS);
		//gl.glTranslatef(-3.0f, -3.0f, -3.0f);
		//gl.glPushMatrix();
		gl.glVertex3f(-12.0f+speed*0.1f,3.0f,tilt*3.0f);
		gl.glVertex3f(-6.0f+speed*0.1f,3.0f,tilt*3.0f);
		gl.glVertex3f(-6.0f+speed*0.1f,-3.0f,-tilt*3.0f);
		gl.glVertex3f(-12.0f+speed*0.1f,-3.0f,-tilt*3.0f);
		gl.glEnd();

	}

	
	//**********************************************************************
	// Private Methods (Viewport)
	//**********************************************************************

	private void drawRubiksCube(GL2 gl) {
		//count=count+1;
		//System.out.println("I am getting drawed!"+count);
		gl.glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);
		gl.glLoadIdentity();
		
		// camera transformations
		gl.glTranslatef(ZERO_F, ZERO_F, zoom);
		gl.glRotatef(cameraAngleX, ONE_F, ZERO_F, ZERO_F);
		gl.glRotatef(cameraAngleY, ZERO_F, ONE_F, ZERO_F);
		gl.glRotatef(cameraAngleZ, ZERO_F, ZERO_F, ONE_F);
		
		int lastIdx = rubiksCube.getSize()-1;
		for (int x=0; x<rubiksCube.getSize(); x++) {
			for (int y=0; y<rubiksCube.getSize(); y++) {
				for (int z=0; z<rubiksCube.getSize(); z++) {
					gl.glPushMatrix();
					//THe code following are useless for now.
					//gl.glRotatef(columnAnglesX[x], ONE_F, ZERO_F, ZERO_F);
					//gl.glRotatef(rowAnglesY[y], ZERO_F, ONE_F, ZERO_F);
					//gl.glRotatef(faceAnglesZ[z], ZERO_F, ZERO_F, ONE_F);
					
					// bottom-left-front corner of cube is (0,0,0) so we need to center it at the origin
					float t = (float) lastIdx/2;
					gl.glTranslatef((x-t)*CUBIE_TRANSLATION_FACTOR, (y-t)*CUBIE_TRANSLATION_FACTOR, -(z-t)*CUBIE_TRANSLATION_FACTOR);
					
					drawCubie(gl, rubiksCube.getVisibleFaces(x, y, z), rubiksCube.getCubie(x, y, z));
				/*	if(x==1 && y==1&&z==1)
					{
		            			drawPlane(gl);
		            			System.out.println("I am getting drawed!");
	
						}*/

				    gl.glPopMatrix();
				}
			}
		}

	}
	
	private void drawCubie(GL2 gl, int visibleFaces, Cubie cubie) {
		gl.glBegin(GL_QUADS);
//		
		// top face
		gl.glColor3f(ZERO_F, ZERO_F, ZERO_F);
		if ((visibleFaces & Cubie.FACELET_TOP) > 0) glApplyColor(gl, cubie.topColor);
		gl.glVertex3f(ONE_F, ONE_F, -ONE_F);
		gl.glVertex3f(-ONE_F, ONE_F, -ONE_F);
		gl.glVertex3f(-ONE_F, ONE_F, ONE_F);
		gl.glVertex3f(ONE_F, ONE_F, ONE_F);
	 
		// bottom face
		gl.glColor3f(ZERO_F, ZERO_F, ZERO_F);
		if ((visibleFaces & Cubie.FACELET_BOTTOM) > 0) glApplyColor(gl, cubie.bottomColor);
		gl.glVertex3f(ONE_F, -ONE_F, ONE_F);
		gl.glVertex3f(-ONE_F, -ONE_F, ONE_F);
		gl.glVertex3f(-ONE_F, -ONE_F, -ONE_F);
		gl.glVertex3f(ONE_F, -ONE_F, -ONE_F);
			 
		// front face
		gl.glColor3f(ZERO_F, ZERO_F, ZERO_F);
		if ((visibleFaces & Cubie.FACELET_FRONT) > 0) glApplyColor(gl, cubie.frontColor);
		gl.glVertex3f(ONE_F, ONE_F, ONE_F);
		gl.glVertex3f(-ONE_F, ONE_F, ONE_F);
		gl.glVertex3f(-ONE_F, -ONE_F, ONE_F);
		gl.glVertex3f(ONE_F, -ONE_F, ONE_F);
			 
		// rear face
		gl.glColor3f(ZERO_F, ZERO_F, ZERO_F);
		if ((visibleFaces & Cubie.FACELET_REAR) > 0) glApplyColor(gl, cubie.rearColor);
		gl.glVertex3f(ONE_F, -ONE_F, -ONE_F);
		gl.glVertex3f(-ONE_F, -ONE_F, -ONE_F);
		gl.glVertex3f(-ONE_F, ONE_F, -ONE_F);
		gl.glVertex3f(ONE_F, ONE_F, -ONE_F);
			 
		// left face
		gl.glColor3f(ZERO_F, ZERO_F, ZERO_F);
		if ((visibleFaces & Cubie.FACELET_LEFT) > 0) glApplyColor(gl, cubie.leftColor);
		gl.glVertex3f(-ONE_F, ONE_F, ONE_F);
		gl.glVertex3f(-ONE_F, ONE_F, -ONE_F);
		gl.glVertex3f(-ONE_F, -ONE_F, -ONE_F);
		gl.glVertex3f(-ONE_F, -ONE_F, ONE_F);
	 
		// right face
		gl.glColor3f(ZERO_F, ZERO_F, ZERO_F);
		if ((visibleFaces & Cubie.FACELET_RIGHT) > 0) glApplyColor(gl, cubie.rightColor);
		gl.glVertex3f(ONE_F, ONE_F, -ONE_F);
		gl.glVertex3f(ONE_F, ONE_F, ONE_F);
		gl.glVertex3f(ONE_F, -ONE_F, ONE_F);
		gl.glVertex3f(ONE_F, -ONE_F, -ONE_F);
			
		gl.glEnd();
	}
	
	private void glApplyColor(GL2 gl, Color color) {
		switch (color) {
			case BLACK:
				gl.glColor3f(0.31f, 1f, 1f); break;
			case YELLOW:
				gl.glColor3f(ZERO_F,ONE_F,ZERO_F); break;
			case GREEN:
				gl.glColor3f(ONE_F, ZERO_F, ZERO_F); break;
			case ORANGE:
				gl.glColor3f(ZERO_F, ZERO_F, ONE_F); break;
			case BLUE:
				gl.glColor3f(ONE_F, ONE_F, ONE_F); break;
			case RED:
				gl.glColor3f(ONE_F, ONE_F, ZERO_F); break;
		}
	}
	

	
	
	@Override
	public void keyPressed(KeyEvent e) {
		switch (e.getKeyCode()) {
			case KeyEvent.VK_UP:
				cameraAngleX -= CAMERA_ROTATE_STEP_DEGREES;
				break;
			case KeyEvent.VK_DOWN:
				cameraAngleX += CAMERA_ROTATE_STEP_DEGREES;
				break;
			case KeyEvent.VK_LEFT:
				if (e.isShiftDown()) cameraAngleZ += CAMERA_ROTATE_STEP_DEGREES;
				else cameraAngleY -= CAMERA_ROTATE_STEP_DEGREES;
				break;
			case KeyEvent.VK_RIGHT:
				if (e.isShiftDown()) cameraAngleZ -= CAMERA_ROTATE_STEP_DEGREES;
				else cameraAngleY += CAMERA_ROTATE_STEP_DEGREES;
				break;
			//means start to draw the flower:
			case KeyEvent.VK_F:
			    cuttingoptions=0;
			    stop=false;
			    break;
			//stop the animation
			case KeyEvent.VK_S:
				stop=true;
			case KeyEvent.VK_R:
				cameraAngleX = DEFAULT_CAMERA_ANGLE_X;
				cameraAngleY = DEFAULT_CAMERA_ANGLE_Y;
				cameraAngleZ = ZERO_F;
				zoom = DEFAULT_ZOOM;
				if (e.isShiftDown()) {
					columnAnglesX = new float[rubiksCube.getSize()];
					rowAnglesY = new float[rubiksCube.getSize()];
					faceAnglesZ = new float[rubiksCube.getSize()];
					rubiksCube.resetState();
				}
				break;
		}
	}
		
	@Override
	public void mouseDragged(MouseEvent e) {
		final int buffer = 2;
		
		
		if (e.getX() < mouseX-buffer) cameraAngleY -= CAMERA_ROTATE_STEP_DEGREES;
		else if (e.getX() > mouseX+buffer) cameraAngleY += CAMERA_ROTATE_STEP_DEGREES;
		
		if (e.getY() < mouseY-buffer) cameraAngleX -= CAMERA_ROTATE_STEP_DEGREES;
		else if (e.getY() > mouseY+buffer) cameraAngleX += CAMERA_ROTATE_STEP_DEGREES;
		
		
		mouseX = e.getX();
		mouseY = e.getY();
	}


	@Override
	public void mouseClicked(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mousePressed(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseReleased(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void mouseWheelMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}


	@Override
	public void dispose(GLAutoDrawable arg0) {
		// TODO Auto-generated method stub
		
	}
	

		
	
	//**********************************************************************
	//  class Cubie
	//**********************************************************************
	public static class Cubie {
		
		// bits denoting faces of the cubie
		public static final int FACELET_FRONT  = (1 << 0);
		public static final int FACELET_REAR   = (1 << 1);
		public static final int FACELET_LEFT   = (1 << 2);
		public static final int FACELET_RIGHT  = (1 << 3);
		public static final int FACELET_TOP    = (1 << 4);
		public static final int FACELET_BOTTOM = (1 << 5);
		
		public  final static Color SOLVED_STATE_FRONT_COLOR  = Color.BLACK;
		public  final static Color SOLVED_STATE_REAR_COLOR   = Color.YELLOW;
		public  final static Color SOLVED_STATE_TOP_COLOR    = Color.GREEN;
		public  final static Color SOLVED_STATE_BOTTOM_COLOR = Color.BLUE;
		public  final static Color SOLVED_STATE_LEFT_COLOR   = Color.RED;
		public  final static Color SOLVED_STATE_RIGHT_COLOR  = Color.ORANGE;
		

		
		Color frontColor  = SOLVED_STATE_FRONT_COLOR;
		Color rearColor   = SOLVED_STATE_REAR_COLOR;
		Color topColor    = SOLVED_STATE_TOP_COLOR;
		Color bottomColor = SOLVED_STATE_BOTTOM_COLOR;
		Color leftColor   = SOLVED_STATE_LEFT_COLOR;
		Color rightColor  = SOLVED_STATE_RIGHT_COLOR;
		
		public Cubie() { }
		
		public Cubie(Color front, Color rear, Color top, Color bottom, Color left, Color right) {
			this.frontColor = front;
			this.rearColor = rear;
			this.topColor = top;
			this.bottomColor = bottom;
			this.leftColor = left;
			this.rightColor = right;
		}
		
		public Cubie getCopy() {
			return new Cubie(frontColor, rearColor, topColor, bottomColor, leftColor, rightColor);

		}

	}
	
	
	
	//**********************************************************************
	//  class RubiksCube
	//**********************************************************************
	public static class RubiksCube {
		
		// constants specifying individual sections of a 3x3x3 cube
		public static final int COLUMN_LEFT   = 0;
		public static final int COLUMN_MIDDLE = 1;
		public static final int COLUMN_RIGHT  = 2;
		public static final int ROW_BOTTOM    = 0;
		public static final int ROW_MIDDLE    = 1;
		public static final int ROW_TOP       = 2;
		public static final int FACE_FRONT    = 0;
		public static final int FACE_MIDDLE   = 1;
		public static final int FACE_REAR     = 2;

		private final int size;
		private Cubie[][][] state;

		public RubiksCube(int size) {
			this.size = size;
			this.state = new Cubie[size][size][size];
			resetState();
		}
		
		public RubiksCube(Cubie[][][] state) {
			this.size = state.length;
			this.state = state;
		}
		
		public int getSize() {
			return size;
		}
		
		public Cubie[][][] getState() {
			return state;
		}
		
		public Cubie getCubie(CubiePosition position) {
			return getCubie(position.x, position.y, position.z);
		}
		
		public Cubie getCubie(int x, int y, int z) {
			return state[x][y][z];
		}
		
		
		// returns integer denoting which of the faces on the cubie at the specified position are visible
		public int getVisibleFaces(int x, int y, int z) {
			int lastIdx = size-1;
			int visibleFaces = (x == 0) ? Cubie.FACELET_LEFT   : ((x == lastIdx) ? Cubie.FACELET_RIGHT : 0);
			visibleFaces    |= (y == 0) ? Cubie.FACELET_BOTTOM : ((y == lastIdx) ? Cubie.FACELET_TOP   : 0);
			visibleFaces    |= (z == 0) ? Cubie.FACELET_FRONT  : ((z == lastIdx) ? Cubie.FACELET_REAR  : 0);
			return visibleFaces;
		}
		
		public List<Color> getVisibleColors(CubiePosition position) {
			return getVisibleColors(position.x, position.y, position.z);
		}
		
		// returns a list of all the currently visible colors for the cubie at the specified position
		public List<Color> getVisibleColors(int x, int y, int z) {
			List<Color> colors = new ArrayList<Color>(3);
			int visibleFaces = getVisibleFaces(x, y, z);
			
			if ((visibleFaces & Cubie.FACELET_LEFT) > 0)   colors.add(getCubie(x, y, z).leftColor);
			if ((visibleFaces & Cubie.FACELET_RIGHT) > 0)  colors.add(getCubie(x, y, z).rightColor);
			if ((visibleFaces & Cubie.FACELET_BOTTOM) > 0) colors.add(getCubie(x, y, z).bottomColor);
			if ((visibleFaces & Cubie.FACELET_TOP) > 0)    colors.add(getCubie(x, y, z).topColor);
			if ((visibleFaces & Cubie.FACELET_FRONT) > 0)  colors.add(getCubie(x, y, z).frontColor);
			if ((visibleFaces & Cubie.FACELET_REAR) > 0)   colors.add(getCubie(x, y, z).rearColor);
			
			return colors;
		}
		

		public void resetState() {
			for (int x=0; x<size; x++) {
				for (int y=0; y<size; y++) {
					for (int z=0; z<size; z++) {
						state[x][y][z] = new Cubie();
					}
				}
			}
		}
		

	}
	
	
	
	
	//**********************************************************************
	//  class CubiePosition
	//**********************************************************************
	public static class CubiePosition {

		int x, y, z;
		
		public CubiePosition(int x, int y, int z) {
			this.x = x;
			this.y = y;
			this.z = z;
		}

		/* convenience methods for a 3x3x3 cube */
		public boolean isInColumnLeft()   { return this.x == RubiksCube.COLUMN_LEFT;   }
		public boolean isInColumnMiddle() { return this.x == RubiksCube.COLUMN_MIDDLE; }
		public boolean isInColumnRight()  { return this.x == RubiksCube.COLUMN_RIGHT;  }
		public boolean isInRowBottom()    { return this.y == RubiksCube.ROW_BOTTOM;    }
		public boolean isInRowMiddle()    { return this.y == RubiksCube.ROW_MIDDLE;    }
		public boolean isInRowTop()       { return this.y == RubiksCube.ROW_TOP;       }
		public boolean isInFaceFront()    { return this.z == RubiksCube.FACE_FRONT;    }
		public boolean isInFaceMiddle()   { return this.z == RubiksCube.FACE_MIDDLE;   }
		public boolean isInFaceRear()     { return this.z == RubiksCube.FACE_REAR;     }
		
		@Override
		public boolean equals(Object obj) {
			if (this == obj) return true;
			if (obj == null) return false;
			if (getClass() != obj.getClass()) return false;
			CubiePosition other = (CubiePosition) obj;
			if (x != other.x) return false;
			if (y != other.y) return false;
			if (z != other.z) return false;
			return true;
		}
		
	}
	
		

}