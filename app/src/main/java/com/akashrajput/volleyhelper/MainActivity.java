package com.akashrajput.volleyhelper;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.akashrajput.volleyhelper.network.VolleyManager;
import com.akashrajput.volleyhelper.network.VolleyManagerListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    private VolleyManager volley;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        volley  = new VolleyManager(this);

        //getJsonObject
        getData();

        //similarly post delete volley.pos...
    }

    private  void getData(){
        HashMap<String,String> params = new HashMap<>();
        volley.getORequest("http://url.com", params, false, this, new VolleyManagerListener<String>() {
            @Override
            public void getResult(String object) throws JSONException {

                JSONObject jsonObject = new JSONObject(object);

                String val = (String) jsonObject.get("somevalue");




            }

            @Override
            public void getError(String errorMessage) {
                //handler errorMessage
            }
        });

    }
}
