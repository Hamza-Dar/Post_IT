package com.example.project;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;

import java.util.List;

public class Saved_posts extends AppCompatActivity {

    RecyclerView rv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_saved_posts);

        rv=findViewById(R.id.rv_saved);
        List<post_save> arr = MyDatabase.getAppDatabase(this).userDao().getAll();
        Post_adapter adapter = new Post_adapter(arr, R.layout.post_view, this);


    }
}
