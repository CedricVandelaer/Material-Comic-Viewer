package com.comicviewer.cedric.comicviewer.Model;

import com.google.gson.Gson;

import java.io.Serializable;

/**
 * Created by Cédric on 6/05/2015.
 */
public class CloudService implements Serializable{

    private String mName;
    private String mToken;
    private String mUserName;
    private String mEmail;

    public CloudService(String name, String token, String username, String email)
    {
        mName = name;
        mToken = token;
        mUserName = username;
        mEmail = email;
    }

    public String serialize()
    {
        Gson gson = new Gson();
        return gson.toJson(this);
    }

    static public CloudService create(String serializedData) {
        // Use GSON to instantiate this class using the JSON representation of the state
        Gson gson = new Gson();
        return gson.fromJson(serializedData, CloudService.class);
    }

    public void setToken(String token)
    {
        mToken = token;
    }

    public String getName()
    {
        return mName;
    }

    public String getEmail()
    {
        return mEmail;
    }

    public String getToken()
    {
        return mToken;
    }

    public String getUsername(){return mUserName;}


}
