package com.cxj170002.fingerpaint;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class DatabaseActivity extends AppCompatActivity {

    // variables used throughout the program
    RecyclerView paintList;
    RecycleAdapter adapter;
    protected int paintIndex;
    static ArrayList<String> paintNames = new ArrayList<String>();
    ArrayList<String> lineString;
    static ArrayList<String> drawings = new ArrayList<>();
    boolean isLoad;
    DatabaseHelper helper;
    EditText drawName;

    /* Written by Christy Jacob for CS4301.002, assignment 6 Fingerpainting Program, starting May 3, 2021.
    NetID: cxj170002
    This is the database activity class which allows you to save or load a drawing. If you are saving, you can overwrite an existing one
    or create a new one. If you are loading, you must choose an existing one.
 */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_database);

        // getting the information from the intent and determing if it is a load or save
        isLoad = getIntent().getBooleanExtra("isLoad", false);
        Button load = findViewById(R.id.btnLoadButton);
        Button save = findViewById(R.id.btnSaveButton);
        TextView drawNameText = findViewById(R.id.nameText);
        drawName = findViewById(R.id.nameEdit);

        // database helper to communicate with the database
        helper = new DatabaseHelper(this);

        // if it is a load, show the proper views
        if(isLoad)
        {
            save.setVisibility(View.INVISIBLE);
            drawNameText.setText("Choose Drawing");
            drawName.setVisibility(View.INVISIBLE);
        }
        else // if it is a save, show the proper views
        {
            load.setVisibility(View.INVISIBLE);
            drawNameText.setText("Name Drawing");
        }

        // setting initial recycler view index to -1
        paintIndex = -1;

        // getting the information about the drawing passed from the intent
        lineString = (ArrayList<String>) getIntent().getSerializableExtra("lineString");

        // getting the name field
        drawName = findViewById(R.id.nameEdit);

        // creating and initializing the Recycler view with row items that show the quiz names
        paintList = findViewById(R.id.paintRecyclerView);
        adapter = new RecycleAdapter(paintNames, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        paintList.setLayoutManager(layoutManager);
        paintList.setItemAnimator(new DefaultItemAnimator());
        paintList.setAdapter(adapter);
    }

    // recycle adapter class
    public class RecycleAdapter extends RecyclerView.Adapter<RecycleAdapter.RecycleRow>
    {
        // array list to hold all the paintings
        private ArrayList<String> allPaintings;
        // int to hold the currently selected item of the recycle view
        private int selectedItem = -1;
        // main activity variable
        DatabaseActivity parent;

        // constructor that takes an array of paint names and the main activity
        public RecycleAdapter(ArrayList<String> names, DatabaseActivity p)
        {
            // initializing the paintings to the passed string arraylist
            allPaintings = names;
            // initializing main activity
            parent = p;
        }

        // function to change the dataset of the adapter
        public void changeDataset(ArrayList<String> updatedData)
        {
            allPaintings = updatedData;
            notifyDataSetChanged();
        }


        @NonNull
        @Override
        // when view holder is created, return the holder with the view
        public RecycleRow onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycle_items, parent, false);
            return new RecycleRow(v);
        }

        @Override
        // when view is bound, it gets the data to display which happens once for every list item
        public void onBindViewHolder(@NonNull RecycleRow holder, int position) {
            holder.paintNameText.setText(allPaintings.get(position));
            if(selectedItem == position)
                holder.itemView.setBackgroundColor(Color.CYAN);
            else
                holder.itemView.setBackgroundColor(Color.WHITE);
        }

        @Override
        // function to determine how many will fit on screen
        public int getItemCount() {
            return allPaintings == null ? 0 : allPaintings.size();
        }

        // view holder for each recycle view row
        public class RecycleRow extends RecyclerView.ViewHolder implements View.OnClickListener
        {
            // textview to hold painting name
            TextView paintNameText;

            public RecycleRow(@NonNull View itemView) {
                super(itemView);
                // initializing paint name textview
                paintNameText = (TextView) itemView.findViewById(R.id.QuizName);
                // setting on click listener
                itemView.setOnClickListener(this);
            }

            @Override
            public void onClick(View v) {
                // changing the paint index which holds what paint was selected
                paintIndex = paintList.getChildLayoutPosition(v);
                // notifying that item has been changed
                notifyItemChanged(selectedItem);
                // updating the selected item
                selectedItem = getLayoutPosition();
                // notifying that item has been changed
                notifyItemChanged(selectedItem);

            }
        }
    }

    // button to handle the save button being clicked
    public void btnSave(View view) {

        // getting the name input
        String name = drawName.getText().toString();
        if(paintIndex>=0) // if an existing painting is selected, overwrite it
        {
            // getting painting information
            String temp = "";
            for(int paintIn = 0; paintIn < lineString.size(); paintIn++)
            {
                if(paintIn > 0)
                {
                    temp+=" | ";
                }
                temp += lineString.get(paintIn);
            }

            // overwriting painting in the database
            temp += " e ";
            drawings.set(paintIndex, temp);
            helper.writeDB(name, temp, true); // saving to database
            btnCancel(view); // going back to main activity


        }
        else if(!name.equals(""))
        {
            // getting painting information
            String temp = "";
            for(int paintIn = 0; paintIn < lineString.size(); paintIn++)
            {
                if(paintIn > 0)
                {
                    temp+=" | ";
                }
                temp += lineString.get(paintIn);
            }

            // adding painting in the database
            temp += " e ";
            paintNames.add(name);
            drawings.add(temp);
            helper.writeDB(name, temp, false); // saving to database
            adapter.changeDataset(paintNames);
            btnCancel(view); // going back to main activity
        }
        else // send error message if an existing painting isn't selected and no name has been entered
        {
            Toast.makeText(this, "Must select drawing or enter name", Toast.LENGTH_LONG).show();

        }

    }

    // method to handle load button being clicked
    public void btnLoad(View view) {

        // only works if a painting is selected
        if(paintIndex>=0)
        {
            // getting the drawing from the database
            String temp = drawings.get(paintIndex);
            String info = helper.getFromDB(paintNames.get(paintIndex)); // getting drawing information from database

            // sending the information about the drawing to the main activity to load
            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("isLoad", true);
            intent.putExtra("draws", temp);
            startActivity(intent); // starting the main activity
        }

        else // if no drawing is selected, set an error message
        {
            Toast.makeText(this, "Must select drawing to load", Toast.LENGTH_LONG).show();
        }

    }

    // method to handle the cancel button being clicked
    public void btnCancel(View view) {
        // go back to main activity
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("isLoad", false);
        startActivity(intent);
    }
}