package com.example.mbalza.googlemapsfuckingwork;

import android.location.Location;
import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by mbalza on 9/22/16.
 */

public class getPokemonLocations extends AsyncTask <Location,Void,String >{

    String returnstring;
    JSONArray jsonArray;

    getPokemonLocations(JSONArray jarray)
    {
        jsonArray = jarray;
    }


    @Override
    protected String doInBackground(Location... params) {

        Location location = params[0];
        String r = "";
        try
        {
            String ur = "http://190.144.171.172/function3.php?lat="+location.getLatitude()+"&lng="+location.getLongitude();

            URL url = new URL(ur);

            URLConnection urlConnection = url.openConnection();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));

            String iline = bufferedReader.readLine();

            while (iline!=null)
            {
                r += iline;
                iline = bufferedReader.readLine();
            }

        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return r;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        returnstring = s;

        s = " { \"MyArray\":"+ returnstring+"}";

        JSONObject jsonObject = null;
        try {
            jsonObject = new JSONObject(s);
            jsonArray =  jsonObject.getJSONArray("MyArray");
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
