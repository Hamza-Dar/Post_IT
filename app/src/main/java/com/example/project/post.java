package com.example.project;


public class post {
    private String UID, Desc, image_uri, UserName, dp;


    public post() {
    }

    public post(String UID, String desc, String image_uri, String userName, String dp) {
        this.UID = UID;
        Desc = desc;
        this.image_uri = image_uri;
        UserName = userName;
        this.dp = dp;
    }

    public String getDp() {
        return dp;
    }

    public void setDp(String dp) {
        this.dp = dp;
    }

    public String getUID() {
        return UID;
    }

    public void setUID(String UID) {
        this.UID = UID;
    }

    public String getDesc() {
        return Desc;
    }

    public void setDesc(String desc) {
        Desc = desc;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public void setImage_uri(String image_uri) {
        this.image_uri = image_uri;
    }

    public String getUserName() {
        return UserName;
    }

    public void setUserName(String userName) {
        UserName = userName;
    }
}
