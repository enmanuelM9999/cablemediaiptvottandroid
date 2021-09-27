package co.cablebox.tv.utils;

import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

// Conexion a Servidor por Volley GET y POST
public class VolleyS {

    public static final String TAG = "myTag";
    private RequestQueue mQueue;
    private String URL = "https://api.myjson.com/bins/kp9wz";

    public VolleyS(RequestQueue mQueue){
        this.mQueue = mQueue;
        httpGET(URL);
        //httpPOST(URL);
    }

    public void httpGET(String url){
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, "RESPONSE FROM SERVER: "+response);
                    }
                }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    try {
                        String responseBody = new String(error.networkResponse.data, "utf-8");
                        Log.d(TAG, "ERROR "+responseBody);
                    }catch (UnsupportedEncodingException e){
                        Log.d(TAG, e.toString());
                    }
                }
        });
        mQueue.add(stringRequest);
    }

    public void httpPOST(String url){
        StringRequest postrequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d(TAG, response);
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try {
                            String responseBody = new String(error.networkResponse.data, "utf-8");
                            Log.d(TAG, "ERROR "+responseBody);
                        }catch (UnsupportedEncodingException e){
                            Log.d(TAG, e.toString());
                        }
                    }
                }
        ){
            @Override
            protected Map<String, String> getParams(){
                Map<String, String> params = new HashMap<String, String>();
                params.put("username", "johndoe");
                params.put("email", "example@gmaiil.co0m");
                return params;
            }
        };
        mQueue.add(postrequest);
    }

    private void jsonParse(){
        String url = "https://api.myjson.com/bins/kp9wz";

        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Formateo
                        try{
                            JSONArray jsonArray = response.getJSONArray("employess");

                            System.out.println("JSON:");
                            for(int i = 0; i < jsonArray.length(); i++){
                                JSONObject employee = jsonArray.getJSONObject(i);

                                String firtName = employee.getString("firstname");
                                int age = employee.getInt("age");
                                String mail = employee.getString("mail");

                                System.out.println(firtName+", "+String.valueOf(age)+", "+mail+"\n\n");
                            }
                        }catch (JSONException e){
                            System.out.println("ERROR VOLLEY "+e.toString());
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        });
    }
}