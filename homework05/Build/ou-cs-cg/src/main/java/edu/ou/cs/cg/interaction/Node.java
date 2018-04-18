package edu.ou.cs.cg.interaction;

//import java.lang.*;
import java.awt.Color;
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

public final class Node
{
    public double width=0.1;
    public double height=0.1;
    public double centerx=0.0;
    public double centery=0.0;
    float[] rgb=new float[3];
    public int side=0;
    public String name;
    public double angle;
    //thsi is to check to see if the node is in the hull or not, if it is true, 
    //it will be in the hull
    public boolean hull=false;
    //this is two points that attach to the polygon in order to calculate the balloon
    // public Point2D.Double b1=0.0;
    // public Point2D.Double b2=0.0;

    public Node(double centerx, double centery,double width,double height, float[] rgb,int side,String name,double angle)
    {
    	this.centerx=centerx;
    	this.centery=centery;
    	this.width=width;
    	this.height=height;
    	this.rgb[0]=rgb[0];
    	this.rgb[1]=rgb[1];
    	this.rgb[2]=rgb[2];
    	this.side=side;
    	this.name=name;
    	this.angle=angle;
    	//this.originalindex=originalindex;

    }
   // public Node(double centerx,double centery)
   // {
   // 	this.centerx=centerx;
   // 	this.centery=centery;
   // }

   // public Node scale( double f ) {
   //     Node v2 = new Node( this.centerx*f, this.centery*f);
   //     return v2;
   // }
}