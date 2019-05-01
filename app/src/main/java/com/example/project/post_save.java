package com.example.project;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "post")
public class post_save {

    @NonNull
    @PrimaryKey()
    String pid;

    @ColumnInfo(name = "D_name")
    private String Name;

    @ColumnInfo(name = "desc")
    private String desc;

    @ColumnInfo(name = "UID")
    private String UID;

    @ColumnInfo(name = "img_url")
    private String img;

    @ColumnInfo(name = "dp")
    private String dp;

    public post_save(String pid, String name, String desc, String UID, String img, String dp) {
        this.pid = pid;
        Name = name;
        this.desc = desc;
        this.UID = UID;
        this.img = img;
        this.dp = dp;
    }

    public post_save() {
    }

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }
}
