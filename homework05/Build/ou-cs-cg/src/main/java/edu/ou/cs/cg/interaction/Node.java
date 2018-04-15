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
   // int originalindex=0;

    public Node(double centerx, double centery,double width,double height, float[] rgb,int side,String name)
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
    	//this.originalindex=originalindex;

    }
}