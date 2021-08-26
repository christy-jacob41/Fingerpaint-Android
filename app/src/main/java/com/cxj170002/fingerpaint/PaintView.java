package com.cxj170002.fingerpaint;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;

import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;

/* Written by Christy Jacob for CS4301.002, assignment 6 Fingerpainting Program, starting May 3, 2021.
    NetID: cxj170002
This is the view derived class where I initialize the canvas and draw the lines using paint. This also
keeps track of the current color and stroke width as well. It can also load previous drawings.
 */

public class PaintView extends View {

    // variables used throughout the file
    private float mX, mY;
    private Path mPath;
    private Paint mPaint;
    private ArrayList<Line> lines = new ArrayList<>();
    private int currentColor;
    private int strokeWidth;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private Paint mBitmapPaint = new Paint();
    ArrayList<String> lineString = new ArrayList<>();

    // constructor for paint view
    public PaintView(Context context) {
        super(context);
        // creating the new paint
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    // constructor for paint view
    public PaintView(Context context, AttributeSet attrs) {
        // creating the new paint
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(Color.BLACK);
        mPaint.setStyle(Paint.Style.STROKE);
    }

    // method called to initialize the paint view
    public void init(DisplayMetrics metrics) {
        // getting the height and width of the display to help create the bitmap
        int height = metrics.heightPixels;
        int width = metrics.widthPixels;

        // creating the bitmap and the canvas
        mBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);

        // setting the default color and strokewidth to black and 15 to start
        currentColor = Color.BLACK;
        strokeWidth = 15;
    }

    // method to handle when the clear button is clicked
    public void clear() {
        // deleting all the lines and invalidating
        lines.clear();
        lineString.clear();
        invalidate();
    }

    // method to handle when the undo button is clicked
    public void undo(){
        // if there are lines in the array of lines, remove the latest one
        if(lines.size()>0)
        {
            // removing the latest line and invalidating
            lines.remove(lines.size()-1);
            lineString.remove(lineString.size()-1);
            invalidate();
        }
    }

    // method to handle loading a previous drawing
    public void loadDrawing(String drawing){

        // clearing the paint view
        clear();
        // looping through the information about a drawing to draw each path
        while (drawing.length()>0)
        {
            // getting the color and seting it
            int next = drawing.indexOf(" ");
            String color = drawing.substring(0, next);
            drawing = drawing.substring(next+1);
            int strokeColor = Integer.parseInt(color);
            setCurrentColor(strokeColor);

            // getting the stroke width and setting it
            next = drawing.indexOf(" ");
            int width = Integer.parseInt(drawing.substring(0, next));
            drawing = drawing.substring(next+1);
            setStrokeWidth(width);
//            Toast.makeText(getContext(), drawing , Toast.LENGTH_LONG).show();

            // variables to be used to get the x and y coordinates for the path
            String temp = "";
            float x = 0;
            float y = 0;

            int point = 0; // keep track of is it the starting point or not

            // loop through and get the x and y coordinates to draw the path
            while(drawing.contains(" "))
            {
                next = drawing.indexOf(" ");
                temp = drawing.substring(0, next);

                // if it equals e, the drawing is complete so break
                if(temp.equals("e"))
                {
                    drawing="";
                    break;
                }
                else if(temp.equals("|")) // if it equals "|", then you are about to draw a new path
                {
                    drawing = drawing.substring(next+1);
                    break;
                }
                else // if there is a space, then there are more x and y coordinates to get and to add to the path so do it
                {
                    // getting the x coordinate
                    x = Float.parseFloat(temp);
                    drawing = drawing.substring(next+1);
                    next = drawing.indexOf(" ");

                    // getting the y coordinate
                    temp = drawing.substring(0, next);
                    y = Float.parseFloat(temp);
                    drawing = drawing.substring(next+1);

                    // if it's the first point handle it
                    if(point==0)
                    {
                        touchStart(x,y);
                        point++;
                    }
                    else // if it's not the first point handle it
                    {
                        touchMove(x,y);
                    }

                }
                touchUp(); // the path is complete, so finish it off

            }
        }

    }

    // method that is getting the information about a path
    public ArrayList<String> getLineString()
    {
        return lineString;
    }


    // method to change the color of hte line being drawn
    public void setCurrentColor(int color) {
        // changes the current color
        currentColor = color;
        mPaint.setColor(color);
    }

    // method to change the stroke width of the line being drawn
    public void setStrokeWidth(int strokeWidth) {
        // changes the stroke width
        this.strokeWidth = strokeWidth; // stroke width can be anywhere from 3 to 20
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // setting background color to white
        canvas.save();
        mCanvas.drawColor(Color.WHITE);

        // drawing lines in the lines array
        for (Line fp : lines) {
            mPaint.setColor(fp.color);
            mPaint.setStrokeWidth(fp.strokeWidth);
            mCanvas.drawPath(fp.path, mPaint);
        }

        // drawing the canvas
        canvas.drawBitmap(mBitmap, 0, 0, mBitmapPaint);
        canvas.restore();
    }

    // handles when user first touches the screen
    private void touchStart(float x, float y) {
        // start a new poth
        mPath = new Path();
        // adding the path to the lines array
        Line fp = new Line(currentColor, strokeWidth, mPath);
        lines.add(fp);

        // moving to the first point of the path
        mPath.reset();
        mPath.moveTo(x, y);
        mX = x;
        mY = y;
        String temp = currentColor + " " + strokeWidth + " " + x + " " + y;
        lineString.add(temp);

    }

    // handles when user moves their finger while touching the screen
    private void touchMove(float x, float y) {
        // getting the distance from last point
        float dx = Math.abs(x - mX);
        float dy = Math.abs(y - mY);

        // connecting last point to new point if it's a distance of 4 away
        if (dx >= 4 || dy >= 4) {
            mPath.quadTo(mX, mY, (x + mX) / 2, (y + mY) / 2); // adding new points to path
            mX = x;
            mY = y;
            String temp = lineString.get(lineString.size()-1);
            temp += " " + (x + mX) / 2 + " " + (y + mY) / 2;
            lineString.set(lineString.size()-1, temp);
        }
    }

    // handles when user lifts up finger
    private void touchUp() {
        // end off line
        mPath.lineTo(mX, mY);
    }

    @Override // handles a touch event
    public boolean onTouchEvent(MotionEvent event) {
        // gets x and y points of touch event
        float x = event.getX();
        float y = event.getY();

        // finds out what kind of motion event it was and handles it accordingly using defined methods
        if(event.getAction()==MotionEvent.ACTION_DOWN)
        {
            touchStart(x, y);
        }
        else if(event.getAction()==MotionEvent.ACTION_MOVE)
        {
            touchMove(x, y);
        }
        else if(event.getAction()==MotionEvent.ACTION_UP)
        {
            touchUp();
        }
        // invalidate so that the view can be updated
        invalidate();

        return true;
    }
}