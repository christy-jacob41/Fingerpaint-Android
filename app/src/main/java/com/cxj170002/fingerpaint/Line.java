package com.cxj170002.fingerpaint;

import android.graphics.Path;

/* Written by Christy Jacob for CS4301.002, assignment 5 Fingerpainting Program, starting April 15, 2021.
    NetID: cxj170002
This class is my own class that can store the strokewidth, color, and path of the line that is currently
being drawn. This class is used in an array in paint view which stores all the lines that are drawn.
 */
public class Line {

    // variables used throughout the file
    public int color;
    public int strokeWidth;
    public Path path;

    // constructor for line class
    public Line(int color, int strokeWidth, Path path) {
        // set the color, stroke width, and path to the passed parameters
        this.color = color;
        this.strokeWidth = strokeWidth;
        this.path = path;
    }
}