package com.example.mbalza.googlemapsfuckingwork;

import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

public class MapsActivity extends AppCompatActivity /*FragmentActivity*/ implements LocationListener, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, GoogleMap.OnMarkerClickListener{

    private GoogleMap mMap;
    private GoogleApiClient googleApiClient;
    private Location lastLocation;
    private LocationRequest locationRequest;
    Marker lastmarker;
    Marker pok1, pok2, pok3, pok4;
    Location lastset;
    JSONArray jsonLocations;
    JSONArray jsonPokemon;

    ArrayList<Bitmap> bitmaps;
    ArrayList<Marker> pokemarkers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        googleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();

        locationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1000);

        bitmaps = new ArrayList<>();
        pokemarkers = new ArrayList<>();


    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setBuildingsEnabled(true);
        mMap.setOnMarkerClickListener(this);


        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));

        //BackGroundFirstTimeLoad bf = new BackGroundFirstTimeLoad(jsonPokemon);



        //bf.execute();

        FirstTimeLoad firstTimeLoad = new FirstTimeLoad();
        firstTimeLoad.execute();

        /*
        try {
            jsonArray = new JSONObject(bf.returnline);
            jsonPokemon = jsonArray.getJSONArray("");

        } catch (Exception e) {
            e.printStackTrace();
        }*/

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);

        LatLng newMarker = new LatLng(location.getLatitude(),location.getLongitude());

        lastmarker = mMap.addMarker(new MarkerOptions().position(newMarker).title("My Position"));
        lastset = null;

        SetPokemon(location);
        //lastset = location;

        newLocation(location);



        LocationServices.FusedLocationApi.requestLocationUpdates(googleApiClient,locationRequest,this);

        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newMarker,17.0f));

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    protected void onResume() {
        super.onResume();

        googleApiClient.connect();

    }

    @Override
    protected void onPause() {
        super.onPause();

        if(googleApiClient.isConnected())
            googleApiClient.disconnect();
    }

    @Override
    public void onLocationChanged(Location location) {
        newLocation(location);

    }

    public void newLocation(Location location)
    {
        //mMap.clear();

        LatLng newMarker = new LatLng(location.getLatitude(),location.getLongitude());

        SetPokemon(location);



        lastmarker.setPosition(newMarker);




        //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newMarker,17.0f));



    }

    public void FinishSetPokemon(Location location)
    {

        try {
            if (lastset == null || location.distanceTo(lastset) >= 2f) {
                Toast.makeText(this, "Entre a Finish set pokemons", Toast.LENGTH_SHORT).show();
                //JSONObject object = new JSONObject(g.returnstring);
                //JSONArray jlocations = object.getJSONArray("");

                for(int i=0; i<jsonLocations.length(); i++)
                {
                    JSONObject pos = jsonLocations.getJSONObject(i);
                    JSONObject poke = jsonPokemon.getJSONObject(i);
                    String name = poke.getString("Name");
                    String lt = pos.getString("lt");
                    String lat = pos.getString("lng");
                    LatLng postition = new LatLng(Double.parseDouble(lt), Double.parseDouble(lat));
                    pokemarkers.add(mMap.addMarker(new MarkerOptions().position(postition).title(name).icon(BitmapDescriptorFactory.fromBitmap(bitmaps.get(i)))));
                }

                lastset = location;
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(postition, 17.0f));

            }

        }catch (Exception e){e.printStackTrace();}


    }

    public void SetPokemon(Location location)
    {
        try {
            //Toast.makeText(this,"Entre a set pokemons",Toast.LENGTH_SHORT).show();
            if (jsonLocations == null) {

                getPokemonLocation g = new getPokemonLocation();
                g.execute(location);

                getPokemonIcons getPokemonIcons = new getPokemonIcons();
                getPokemonIcons.execute();


                //Toast.makeText(this,"Entre a getpokemons",Toast.LENGTH_SHORT).show();

                //if (g.returnstring != null)
                //{

                //}

            }

            //lastset = location;
        }
        catch (Exception e){e.printStackTrace();}
    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        if(!marker.getTitle().matches("My Position"))
        {
            marker.setVisible(false);
        }

        return false;
    }

    private class FirstTimeLoad extends AsyncTask<Void, Void, String>
    {
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);

            String returnline = s;
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
                jsonPokemon =  jsonObject.getJSONArray("MyArray");

                getPokemonIcons getPokemonIcons = new getPokemonIcons();
                getPokemonIcons.execute();

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

    private class getPokemonIcons extends AsyncTask <Void, Void, Void>
    {

        //ArrayList<Bitmap> bitmaps;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            //bitmaps = new ArrayList<>();
        }

        @Override
        protected Void doInBackground(Void... params) {

            try
            {
                String pngurl = "";
                for(int i=0; i<jsonPokemon.length();i++)
                {
                    pngurl = jsonPokemon.getJSONObject(i).getString("ImgFront");

                    URL url = new URL(pngurl);

                    URLConnection urlConnection = url.openConnection();

                    urlConnection.connect();

                    InputStream inputStream = urlConnection.getInputStream();

                    BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream);

                    Bitmap bitmap = BitmapFactory.decodeStream(bufferedInputStream);

                    bitmaps.add(bitmap);

                    bufferedInputStream.close();
                    inputStream.close();

                }

            }
            catch(Exception e){e.printStackTrace();}
            return null;
        }
    }

    private class getPokemonLocation extends AsyncTask<Location, Void, String>
    {
        Location loc;

        @Override
        protected String doInBackground(Location... params) {
            Location location = params[0];
            loc = location;
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

            String returnstring = s;

            s = " { \"MyArray\":"+ returnstring+"}";

            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(s);
                jsonLocations =  jsonObject.getJSONArray("MyArray");

                FinishSetPokemon(loc);

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class DataBaseConnection extends AsyncTask <String, Void, Void>
    {

        @Override
        protected Void doInBackground(String... params) {
            return null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.firstmenu,menu);

        return true;//super.onCreateOptionsMenu(menu);
    }
}
