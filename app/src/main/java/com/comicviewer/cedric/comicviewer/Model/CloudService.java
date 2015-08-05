package com.comicviewer.cedric.comicviewer.Model;

import com.google.gson.Gson;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by CV on 6/05/2015.
 * Model class for a cloud service
 */
public class CloudService implements Serializable{

    private String mName;
    private String mToken;
    private String mUserName;
    private String mEmail;

    private static final String NAME = "name";
    private static final String USERNAME = "username";
    private static final String TOKEN = "token";
    private static final String EMAIL = "email";

    public CloudService()
    {
        mName = null;
        mToken = null;
        mUserName = null;
        mEmail = null;
    }

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

    public void setName(String name){mName = name;}

    public void setUserName(String username){mUserName = username;}

    public void setEmail(String email){mEmail = email;}

    public JSONObject toJSON()
    {
        JSONObject jsonObject = new JSONObject();

        try
        {
            if (mName!=null)
                jsonObject.put(NAME, mName);
            if (mUserName!=null)
                jsonObject.put(USERNAME, mUserName);
            if (mEmail!=null)
                jsonObject.put(EMAIL, mEmail);
            if (mToken!=null)
                jsonObject.put(TOKEN, mToken);
        }
        catch (JSONException e)
        {
            e.printStackTrace();
        }
        return jsonObject;
    }

    public static CloudService fromJSON(JSONObject jsonObject)
    {
        CloudService cloudService = new CloudService();

        try {
            if (jsonObject.has(NAME))
                cloudService.setName(jsonObject.getString(NAME));
            if (jsonObject.has(USERNAME))
                cloudService.setUserName(jsonObject.getString(USERNAME));
            if (jsonObject.has(EMAIL))
                cloudService.setEmail(jsonObject.getString(EMAIL));
            if (jsonObject.has(TOKEN))
                cloudService.setToken(jsonObject.getString(TOKEN));

        }catch (JSONException e)
        {
            e.printStackTrace();
        }
        return cloudService;
    }

    public static CloudService fromJSON(String json)
    {
        try
        {
            return fromJSON(new JSONObject(json));
        }
        catch (JSONException e)
        {
            e.printStackTrace();
            return new CloudService();
        }
    }

}
