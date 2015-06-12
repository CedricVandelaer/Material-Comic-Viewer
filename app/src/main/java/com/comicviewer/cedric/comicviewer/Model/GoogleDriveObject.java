package com.comicviewer.cedric.comicviewer.Model;

import java.io.Serializable;

/**
 * Created by CV on 12/06/2015.
 */
public class GoogleDriveObject implements Serializable{

    private ObjectType mObjectType = ObjectType.UNKNOWN;
    private String mName;
    private String mID;
    private String mDownloadUrl;

    public GoogleDriveObject(String name, String id, String mimeType, String downloadUrl)
    {
        mName = name;
        mID = id;
        if (mimeType.contains("folder"))
            mObjectType = ObjectType.FOLDER;
        else
            mObjectType = ObjectType.FILE;
        mDownloadUrl = downloadUrl;
    }

    public String getName()
    {
        return mName;
    }

    public String getId()
    {
        return mID;
    }

    public ObjectType getType()
    {
        return mObjectType;
    }

    public String getDownloadUrl(){return mDownloadUrl;}
}
