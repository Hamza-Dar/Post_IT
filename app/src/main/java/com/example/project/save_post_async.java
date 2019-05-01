package com.example.project;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

public class save_post_async extends AsyncTask {
    private post_save p;
    private Context c;

    save_post_async(post_save p, Context c){
        this.p=p;
        this.c=c;
    }

    @Override
    protected Object doInBackground(Object[] objects) {
        MyDatabase.getAppDatabase(c).userDao().insertAll(p);
        return null;
    }

    @Override
    protected void onPostExecute(Object o) {
        super.onPostExecute(o);

        Toast.makeText(c, "Post Saved", Toast.LENGTH_LONG).show();
    }
}
