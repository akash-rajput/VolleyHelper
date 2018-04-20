package com.akashrajput.volleyhelper.network;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NetworkResponse;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import com.akashrajput.volleyhelper.Main;

public class VolleyManager {
    private static final String TAG = "VolleyM";

    private HashMap<String, String> defaultParams = new HashMap<>();

    //for Volley API
    private RequestQueue requestQueue;
    private DefaultRetryPolicy defaultRetryPolicy;
    private Context context;

    // TODO:: probably should make it singleton

    public VolleyManager(Context context) {

        defaultParams.put("key", "val");
        this.context = context;
        requestQueue = Main.getInstance().getRequestQueue();
        defaultRetryPolicy = new DefaultRetryPolicy(0, 0, 0);

    }

    // other useful methods here

    public void postORequest( String url,final Map<String, String> prm, final Boolean needToken,final Context ctx,
                              final VolleyManagerListener<String> lnr) {
        if(prm != null)
            defaultParams.putAll(prm);

        Log.d(TAG, "postORequest: "+url);
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.POST, url, new JSONObject(defaultParams),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {

                        if(null != response){

                            try {
                                lnr.getResult(response.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }else {
                            try {
                                lnr.getResult("Operation Successful!");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        error.printStackTrace();

                        lnr.getError(getErrorMessage(error));


                    }
                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if(null != prm){
                    Log.e(TAG, prm.toString());
                    return prm;
                }else return super.getParams();

            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                if(needToken){
                    // headers.put("Authorization", "Bearer "+token);
                }
                Log.d(TAG, "getHeaders: "+headers);


                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));

                    JSONObject result = null;

                    if (jsonString != null && jsonString.length() > 0)
                        result = new JSONObject(jsonString);

                    return Response.success(result,
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }

        };

        request.setRetryPolicy(defaultRetryPolicy);

        requestQueue.add(request);
    }

    public void getORequest(String url, final Map<String, String> prm,   final Boolean needToken, final Context ctx,
                            final VolleyManagerListener<String> listener)
    {
        if(prm != null)
            defaultParams.putAll(prm);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, new JSONObject(defaultParams),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {

                        try {
                            listener.getResult(response.toString());
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {
                        listener.getError(getErrorMessage(error));

                    }
                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if(null != prm){
                    //Log.d(TAG, params.toString());
                    return prm;
                }else return super.getParams();

            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Accept", "application/json");
                if(needToken){

                    //  headers.put("Authorization", "Bearer "+token);
                }
                return headers;
            }
        };



        request.setRetryPolicy(defaultRetryPolicy);
        requestQueue.add(request);
    }


    public void getARequest(String url, final Map<String,String> prm, final Boolean needToken, final Context ctx,
                            final VolleyManagerListener<JSONArray> lnr){

        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                //Log.d(TAG + ": ", "JsonArrayRequest Response : " + response);
                try {
                    lnr.getResult(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                lnr.getError(getErrorMessage(error));
            }
        }){
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError){
                if(volleyError.networkResponse != null && volleyError.networkResponse.data != null){
                    VolleyError error = new VolleyError(new String(volleyError.networkResponse.data));
                    volleyError = error;
                }

                return volleyError;
            }

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if(null != prm){
                    Log.e(TAG, "Parameters"+prm.toString());
                    return prm;
                }else return super.getParams();

            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                if(needToken){
                    //
                }

                return headers;
            }
        };

        jsonArrayRequest.setRetryPolicy(defaultRetryPolicy);

        requestQueue.add(jsonArrayRequest);
    }


    public void deleteRequest(String url, final Map<String,String> prm, final Boolean needToken, final Context ctx,
                              final VolleyManagerListener<String> lnr){
        prm.put("demo", "key");
        prm.putAll(defaultParams);

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.DELETE, url, new JSONObject(prm),
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response)
                    {

                        if(null != response){

                            try {
                                lnr.getResult(response.toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }else {
                            try {
                                lnr.getResult("Deleted Successfully!");
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {


                        lnr.getError(getErrorMessage(error));


                    }
                }){

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                if(null != prm){
                    //Log.d(TAG, params.toString());
                    return prm;
                }else return super.getParams();

            }


            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                if(needToken){

                    // headers.put("Authorization", "Bearer "+token);
                }

                return headers;
            }

            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));

                    JSONObject result = null;

                    if (jsonString != null && jsonString.length() > 0)
                        result = new JSONObject(jsonString);

                    return Response.success(result,
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException e) {
                    return Response.error(new ParseError(e));
                } catch (JSONException je) {
                    return Response.error(new ParseError(je));
                }
            }

        };

        request.setRetryPolicy(defaultRetryPolicy);

        requestQueue.add(request);

    }

    private String getErrorMessage(VolleyError error){
        String errorMessage;
        if(error instanceof TimeoutError){
            errorMessage = "Request Timed Out";
        }else if (isServerProblem(error)){
            errorMessage = handleServerError(error);

        }else if(isNetworkProblem(error)) {
            errorMessage = "No internet connection";
        }else {
            errorMessage = "Oops.. server is not in mood";

            Log.d(TAG, "getErrorMessage: "+error.toString());
            error.printStackTrace();
        }
        return errorMessage;

    }

    private String handleServerError(Object error) {

        VolleyError er = (VolleyError)error;
        NetworkResponse response = er.networkResponse;
        String errorMessage;


        String data = new String(response.data);
        //errorMessage = trimMessage(data);
        errorMessage = "";
        try {

            JSONObject json = new JSONObject(data);
            if (json.has("message")){
                errorMessage += json.get("message")+"\n";
            }
            if(json.has("errors")){
                errorMessage += trimMessage(data);
                //errorMessage  = json.getJSONObject("errors").toString();
            }
            Log.e(TAG, "handleServerError: " + errorMessage );

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return errorMessage;


    }

    private String trimMessage(String json){

        JSONObject errors;
        String errorString= "";

        try{
            JSONObject obj = new JSONObject(json);


            if(obj.has("errors")){
                errors = obj.getJSONObject("errors");


                if (errors != null) {
                    //get all error keys
                    Set<String> keys = new HashSet<>();
                    Iterator iterator = errors.keys();
                    while (iterator.hasNext()) {
                        String key = (String)iterator.next();
                        errorString += errors.getJSONArray(key).get(0) + "\n";
                    }


                }
            }else errorString = obj.getString("message");

        } catch(JSONException e){
            e.printStackTrace();
            return null;
        }

        return errorString;
    }


    private static boolean isServerProblem(Object error) {
        return (error instanceof ServerError || error instanceof AuthFailureError);
    }

    private static boolean isNetworkProblem (Object error){
        return (error instanceof NetworkError || error instanceof NoConnectionError);
    }









}