package com.example.lenovo.explistdemo;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

import java.util.ArrayList;

import android.widget.ImageView;
import android.widget.TextView;
/**
 * Created by Lenovo on 19-08-2018.
 */

public class ExpandableListAdapter{

    private Context context;
    private ArrayList<Group> groups;


    public ExpandableListAdapter(Context context, ArrayList<Group> groups) {
        this.context = context;
        this.groups = groups;
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{

        public MyViewHolder(View itemView) {
            super(itemView);
        }
    }


}
