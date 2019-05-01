package com.example.project;

import android.content.Context;
import android.os.AsyncTask;

import java.util.List;

public class Post_async extends AsyncTask {
    Post_adapter p;
    List<post_save> arr;
    Context c;
    Post_async(Post_adapter p, Context c){
        this.p=p;
        this.c = c;
    }


    @Override
    protected Object doInBackground(Object[] objects) {
        arr = MyDatabase.getAppDatabase(c).userDao().getAll();
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);
        p.set_arr(arr);
        p.notifyDataSetChanged();
    }
}
