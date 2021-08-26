package com.cxj170002.fingerpaint;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.SeekBar;
import android.widget.Toast;

import java.util.ArrayList;

/* Written by Christy Jacob for CS4301.002, assignment 6 Fingerpainting Program, starting May 3, 2021.
    NetID: cxj170002
This program allows the user to draw using their finger. They can choose between 4 colors: red, black,
blue, and green using buttons. They can also change the stroke width using the slider. They also have the
option to undo which deletes the latest line they drew or clear which deletes all the lines. You can also
save or load drawings.
 */

public class MainActivity extends AppCompatActivity {

    // variables used throughout the file
    private PaintView paintView;
    private SeekBar seekbar;
    static int runs = 0;
    boolean Load = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // if it's not the initial creation of activity get boolean extra
        if(runs>0)
        {
            Load = getIntent().getBooleanExtra("isLoad", false);
        }

        // increment runs
        runs++;

        // finding the seekbar id and adding an on change listener to it
        seekbar = findViewById(R.id.seekBar);
        seekbar.setOnSeekBarChangeListener(seekChange);

        // getting the pain view by id and initializing it
        paintView = (PaintView) findViewById(R.id.paintView);
        DisplayMetrics metrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metrics);
        paintView.init(metrics);

        // if its a load, load the drawing
        if(Load)
        {
            // getting information about the drawing that was passed
            String draws = getIntent().getStringExtra("draws");
            paintView.loadDrawing(draws); // calling paint view function to load the drawing
        }

    }

    // on change listener for the seekbar
    SeekBar.OnSeekBarChangeListener seekChange = new SeekBar.OnSeekBarChangeListener() {
        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            // changing the stroke width of the current line based on the progress of the seek bar
            paintView.setStrokeWidth(progress);
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {

        }

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {

        }
    };

    // on click handler for when a color button is clicked
    public void changeColor(View view) {

        // getting the id of the button clicked
        int clickColor = view.getId();

        // changing the current color of the line based on the button clicked
        if(clickColor==R.id.redButton)
            paintView.setCurrentColor(Color.RED);
        else if(clickColor==R.id.greenButton)
            paintView.setCurrentColor(Color.GREEN);
        else if(clickColor==R.id.blueButton)
            paintView.setCurrentColor(Color.BLUE);
        else
            paintView.setCurrentColor(Color.BLACK);
    }

    // on click handler for when the clear button is clicked
    public void clear(View view) {
        // calling clear function in paint view
        paintView.clear();
    }

    // on click handler for when undo button is clicked
    public void undo(View view) {
        // calling undo button in paint view
        paintView.undo();
    }

    // on click handler for the save button
    public void save(View view) {
        ArrayList<String> lineString = paintView.getLineString();
        if(lineString.size()>0) // if they drew something go to the save activity
        {
            // starting an intent for the save activity and adding information about the drawing into it as well as letting it know it's not a load but a save
            Intent intent = new Intent(this, DatabaseActivity.class);
            intent.putExtra("isLoad", false);
            intent.putExtra("lineString", lineString);
            startActivity(intent); // starting the activity
        }
        else // if they didn't draw anything, send an error message
        {
            Toast.makeText(MainActivity.this, "Must draw something" , Toast.LENGTH_LONG).show();
        }

    }

    // on click handler for the open button
    public void open(View view) {
        // starting an intent for the open activity and letting it know you should load a drawing
        Intent intent = new Intent(this, DatabaseActivity.class);
        intent.putExtra("isLoad", true);
        startActivity(intent); // starting the activity
    }
}