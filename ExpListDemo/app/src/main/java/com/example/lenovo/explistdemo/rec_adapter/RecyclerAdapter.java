package com.example.lenovo.explistdemo.rec_adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.lenovo.explistdemo.Group;
import com.example.lenovo.explistdemo.R;

import java.util.List;

/**
 * Created by Lenovo on 23-08-2018.
 */

public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.MyViewHolder>{


    private List<Group> groupList;
    public RecyclerAdapter(List<Group> groupList){
        this.groupList = groupList;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.group_item,parent,false);
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Group group = groupList.get(position);
        holder.group_name.setText("("+position+") "+group.getName());
    }

    @Override
    public int getItemCount() {
        return groupList.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        TextView group_name;
        public MyViewHolder(View itemView) {
            super(itemView);
            group_name = (TextView)itemView.findViewById(R.id.group_name);
        }
    }
}
