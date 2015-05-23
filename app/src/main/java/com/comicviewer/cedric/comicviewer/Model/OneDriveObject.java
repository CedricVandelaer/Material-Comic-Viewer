package com.comicviewer.cedric.comicviewer.Model;

import java.io.Serializable;

/**
 * Created by CV on 23/05/2015.
 * class to represent a OneDrive file or folder
 */
public class OneDriveObject implements Serializable{

    private ObjectType mObjectType = ObjectType.UNKNOWN;
    private String mName;
    private String mID;

    public OneDriveObject(String name, String id)
    {
        mName = name;
        mID = id;
        if (id.startsWith("folder"))
            mObjectType = ObjectType.FOLDER;
        else if (id.startsWith("file"))
            mObjectType = ObjectType.FILE;
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

}

