package com.example.project;
import android.arch.persistence.room.Database;
import android.arch.persistence.room.Room;
import android.arch.persistence.room.RoomDatabase;
import android.content.Context;

@Database(entities = {post_save.class},version = 1)
abstract class MyDatabase extends RoomDatabase {
    private static MyDatabase INSTANCE;

    public abstract postDAO userDao();

    public static MyDatabase getAppDatabase(Context context) {
        if (INSTANCE == null) {
            INSTANCE =
                    Room.databaseBuilder(context.getApplicationContext(), MyDatabase.class, "user-database").build();
        }
        return INSTANCE;
    }
    public static void destroyInstance() {
        INSTANCE = null;
    }


}
