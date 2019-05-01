package com.example.project;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class Post_adapter extends RecyclerView.Adapter<post_viewholder> {
    private List<post_save> items;
    private int itemLayout;
    Context c;
    public Post_adapter(List<post_save> items, int Layout, Context c){
        this.items = items;
        this.itemLayout = Layout;
        this.c = c;
    }

    void set_arr(List<post_save> p ){
        items = p;
    }

    @NonNull
    @Override
    public post_viewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(itemLayout, parent, false);
        return new post_viewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull post_viewholder holder, int position) {
        if(items != null && holder != null) {
            post_save ps = items.get(position);
            post p = new post(ps.getUID(), ps.getDesc(), ps.getImg(), ps.getName(), ps.getDp());
            holder.set_post(p, ps.getPid(), c);
        }
    }

    @Override
    public int getItemCount() {
        if(items != null)
            return items.size();
        else
            return 0;
    }
    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }


}
