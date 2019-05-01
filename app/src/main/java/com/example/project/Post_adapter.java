package com.example.project;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

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

    boolean like =false;
    @Override
    public void onBindViewHolder(@NonNull post_viewholder viewHolder, int position) {
        if(items != null && viewHolder != null) {
            post_save ps = items.get(position);
            post p = new post(ps.getUID(), ps.getDesc(), ps.getImg(), ps.getName(), ps.getDp());
            viewHolder.set_post(p, ps.getPid(), c);
            viewHolder.save.setVisibility(View.GONE);
            DatabaseReference likeref = FirebaseDatabase.getInstance().getReferenceFromUrl("https://projectsmd-4aa60.firebaseio.com/likes");
            DatabaseReference UserRef = FirebaseDatabase.getInstance().getReference().child("Users");
            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            likeref.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    if (dataSnapshot.child( ps.getPid()).hasChild(mAuth.getCurrentUser().getUid())) {
                        viewHolder.like.setChecked(true);
                    } else {
                        viewHolder.like.setChecked(false);
                    }
                }
                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            viewHolder.like.setOnClickListener(v -> {
                like = true;
                likeref.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (like) {
                            if (dataSnapshot.child(ps.getPid()).hasChild(mAuth.getCurrentUser().getUid())) {
                                UserRef.child(ps.getUID()).child("likers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"|"+ps.getPid()).removeValue();
                                likeref.child(ps.getPid()).child(mAuth.getCurrentUser().getUid()).removeValue();
                                like=false;
                            } else {
                                UserRef.child(ps.getUID()).child("likers").child(FirebaseAuth.getInstance().getCurrentUser().getUid()+"|"+ps.getPid()).setValue("liked");
                                likeref.child(ps.getPid()).child(mAuth.getCurrentUser().getUid()).setValue("Random");
                                like=false;
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            });
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
