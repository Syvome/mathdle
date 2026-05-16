package com.example.mathdle;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class MyCustomAdapter extends ArrayAdapter<Choose> {

    private ArrayList<Choose> chooseArrayList;
    Context context;

    public MyCustomAdapter(ArrayList<Choose> chooseArrayList, Context context) {
        super(context, R.layout.grid_item_layout, chooseArrayList);
        this.chooseArrayList = chooseArrayList;
        this.context = context;
    }

    // View Holder: Used to cache references to the views within an item layout
    private static class MyViewHolder{
        TextView Name;
        ImageView Img;
    }

    // GetView(): Used to create and return a view for a specific item in Grid.
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {


        Choose choose = getItem(position);

        // 2- Inflating Layout:
        MyViewHolder myViewHolder;

        if (convertView == null){
            // no view went off-screen --> Create a new view
            myViewHolder= new MyViewHolder();
            LayoutInflater inflater = LayoutInflater.from(getContext());

            convertView = inflater.inflate(
                    R.layout.grid_item_layout,
                    parent,
                    false
            );

            // Finding the Views

            myViewHolder.Img  = (ImageView) convertView.findViewById(R.id.imageView);

            convertView.setTag(myViewHolder);



        }else{
            // a view went off-screen  --> re-use it
            myViewHolder = (MyViewHolder) convertView.getTag();

        }


        // Getting the data from the model class (Choose)

        myViewHolder.Img.setImageResource(choose.getImg());

        return convertView;




    }
}