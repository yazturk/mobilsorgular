package com.ayazturk.mobilsorgular;

import androidx.fragment.app.FragmentActivity;

import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.maps.DirectionsApi;
import com.google.maps.GeoApiContext;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.GeocodingResult;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.Socket;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Sorgu3Sonuc extends FragmentActivity implements OnMapReadyCallback {
    private Geocoder gc;
    private Polyline mPolyline;
    ArrayList<LatLng> mMarkerPoints;
    GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sorgu3_sonuc2);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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

        Socket myClient;
        PrintWriter out;
        BufferedReader in;

        String  url, adresString, gecici;

        Address[] adres = new Address[4];
        double[] lat = new double[4], lng = new double[4];
        LatLng[] coor = new LatLng[4];

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        mMap = googleMap;
        gc = new Geocoder(this);
        try {
            myClient = new Socket("34.207.127.179", 1163);
            out = new PrintWriter(myClient.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(myClient.getInputStream()));
            out.println("sorgu3");

            for(int i=0; i<4; i++) {
                gecici = in.readLine();
                adresString = in.readLine();
                adresString = adresString + ", " + gecici;

                adres[i] = gc.getFromLocationName(adresString + ", New York", 1).get(0);
                lat[i] = adres[i].getLatitude();
                lng[i] = adres[i].getLongitude();

                coor[i] = new LatLng(lat[i], lng[i]);
                mMap.addMarker(new MarkerOptions().position(coor[i]).title(adresString));

            }

            in.close();
            out.close();
            myClient.close();
        }
        catch (Exception e) {
            //Hata varsa haritada İstanbul gösterilir
            coor[0] = new LatLng(41,28);
            mMap.addMarker(new MarkerOptions().position(coor[0]).title("İstanbul"));
        }
        finally{
            mMap.getUiSettings().setZoomControlsEnabled(true);
            mMap.moveCamera(CameraUpdateFactory.zoomTo(12));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(coor[0]));
        }
        url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                + lat[0] + "," + lng[0] + "&destination=" + lat[1] + "," + lng[1]
                + "&key=" + getString(R.string.google_maps_key);
        drawRoute(url);
        url = "https://maps.googleapis.com/maps/api/directions/json?origin="
                + lat[2] + "," + lng[2] + "&destination=" + lat[3] + "," + lng[3]
                + "&key=" + getString(R.string.google_maps_key);
        drawRoute(url);
    }

    //Buradan aşağısı ve DirectionsJSONParser.java dosyası, yani rotayı çizdirmeye yarayan kısımlar
    //şu siteden alınmıştır:
    // https://www.wingsquare.com/blog/drawing-driving-route-directions-between-two-locations-using-google-directions-in-google-map-android/
    private void drawRoute(String url){
        DownloadTask downloadTask = new DownloadTask();
        downloadTask.execute(url);
    }
    private String downloadURL(String strUrl){
        String data = "";
        InputStream in = null;
        HttpURLConnection urlConnection = null;

        try {
            URL url = new URL(strUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.connect();
            in = urlConnection.getInputStream();
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            StringBuffer sb = new StringBuffer();
            String line = "";
            while((line = br.readLine()) != null){
                sb.append(line);
            }
            data = sb.toString();
            br.close();
            in.close();

        } catch (Exception e) {
            Log.d("Rota indirilirken hata", e.toString());
        } finally {
            urlConnection.disconnect();
        }
        return data;
    }
    private class DownloadTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... url){
            String data = "";
            try {
                data = downloadURL(url[0]);
                Log.d("DownloadTask", "DownloadTask: " + data);
            } catch (Exception e) {
                Log.d("Background Task", e.toString());
            }
            return data;
        }
        @Override
        protected void onPostExecute(String result) {
            super .onPostExecute(result);
            ParserTask parserTask = new ParserTask();
            parserTask.execute(result);
        }
    }
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> >{

        // Parsing the data in non-ui thread
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                // Starts parsing data
                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;

            // Traversing through all the routes
            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                // Fetching i-th route
                List<HashMap<String, String>> path = result.get(i);

                // Fetching all the points in i-th route
                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                // Adding all the points in the route to LineOptions
                lineOptions.addAll(points);
                lineOptions.width(8);
                lineOptions.color(Color.RED);
            }

            // Drawing polyline in the Google Map for the i-th route
            if(lineOptions != null) {
        /*        if(mPolyline != null){
                    mPolyline.remove();
                }*/
                mPolyline = mMap.addPolyline(lineOptions);

            }else
                Toast.makeText(getApplicationContext(),"No route is found", Toast.LENGTH_LONG).show();
        }
    }
}