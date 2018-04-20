package com.akashrajput.volleyhelper.network;


import org.json.JSONException;

public interface VolleyManagerListener<T>{
    public void getResult(T object) throws JSONException;

    public void getError(String errorMessage);
}