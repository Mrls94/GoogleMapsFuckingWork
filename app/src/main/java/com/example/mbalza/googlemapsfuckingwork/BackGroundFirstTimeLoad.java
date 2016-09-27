package com.example.mbalza.googlemapsfuckingwork;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by mbalza on 9/22/16.
 */

public class BackGroundFirstTimeLoad extends AsyncTask <Void, Void, String>{

    String returnline;
    JSONArray jsonArray;

    BackGroundFirstTimeLoad (JSONArray jsonArray)
    {
        this.jsonArray = jsonArray;
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);
        returnline = s;
        s = " { \"MyArray\":"+ returnline+"}";

        try {
            //JSONObject jsonObject = new JSONObject(s);
            String json = "[{\"Id\": 1,\n" +
                    "   \"Name\": \"Bulbasaur\",\n" +
                    "   \"Type\": \"GRASS-POISON\",\n" +
                    "   \"Total\": 318,\n" +
                    "   \"HP\": 45,\n" +
                    "   \"Attack\": 49,\n" +
                    "   \"Defense\": 49,\n" +
                    "   \"Sp. Atk\": 65,\n" +
                    "   \"Sp. Def\": 65,\n" +
                    "   \"Speed\": 45,\n" +
                    "   \"ImgFront\": \"https://img.pokemondb.net/sprites/black-white/normal/bulbasaur.png\",\n" +
                    "   \"ImgBack\": \"https://img.pokemondb.net/sprites/black-white/back-normal/bulbasaur.png\",\n" +
                    "   \"GifFront\": \"https://img.pokemondb.net/sprites/black-white/anim/normal/bulbasaur.gif\",\n" +
                    "   \"GifBack\": \"https://img.pokemondb.net/sprites/black-white/anim/back-normal/bulbasaur.gif\",\n" +
                    "   \"ImgUrl\": \"http://190.144.171.172//proyectoMovil//Pokemon-DB//img//001Bulbasaur.png\",\n" +
                    "   \"ev_id\": 2}]";

            JSONObject jsonObject = new JSONObject(s);
            jsonArray =  jsonObject.getJSONArray("MyArray");

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    protected String doInBackground(Void... params) {

        URL url = null;
        String inputline = "";

        try {
            url = new URL ("https://raw.githubusercontent.com/FTorrenegraG/Pokemon_json_example/master/example.json");
            URLConnection urlConnection = url.openConnection();

            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String iline = bufferedReader.readLine();


            while(iline!=null)
            {

                inputline += iline;
                iline = bufferedReader.readLine();
            }
            bufferedReader.close();


        } catch (Exception e) {
            e.printStackTrace();
        }


        return inputline;
    }
}
