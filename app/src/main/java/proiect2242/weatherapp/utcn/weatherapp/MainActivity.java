package proiect2242.weatherapp.utcn.weatherapp;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Paint;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.ActivityCompat;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.squareup.picasso.Picasso;

import java.lang.reflect.Type;

import proiect2242.weatherapp.utcn.weatherapp.Common.Common;
import proiect2242.weatherapp.utcn.weatherapp.Helper.Helper;
import proiect2242.weatherapp.utcn.weatherapp.Model.OpenWeatherMap;

public class MainActivity extends AppCompatActivity implements LocationListener {

    TextView txtCity, txtLastUpdate, txtDescription, txtHumidity, txtTime, txtCelsius;
    ImageView imageView;
    Button coords;
    LocationManager locationManager;
    String provider;
    static double lat, lng;
    double lat2 = 0;
    double lng2 = 0;
    OpenWeatherMap openWeatherMap = new OpenWeatherMap();
    private GetWeather getWeather;

    int MY_PERMISSION = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final SwipeRefreshLayout swipeRefreshLayout;
        //Control

        txtCity = (TextView) findViewById(R.id.txtCity);
        txtLastUpdate = (TextView) findViewById(R.id.txtLastUpdate);
        txtDescription = (TextView) findViewById(R.id.txtDescription);
        txtHumidity = (TextView) findViewById(R.id.txtHumidity);
        txtCelsius = (TextView) findViewById(R.id.txtCelsius);
        txtTime = (TextView) findViewById(R.id.txtTime);
        coords = (Button) findViewById(R.id.coords);
        txtCity.setPaintFlags(txtCity.getPaintFlags()| Paint.UNDERLINE_TEXT_FLAG);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);

        imageView = (ImageView) findViewById(R.id.imageView);


        //get coord
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        provider = locationManager.getBestProvider(new Criteria(), false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSION);
        }
        Location location = locationManager.getLastKnownLocation(provider);

        if (location == null)
            Log.e("TAG", "No Location");
        else {
            lat = location.getLatitude();
            lng = location.getLongitude();
            getWeather = (GetWeather) new GetWeather(MainActivity.this).execute(Common.apiRequest(String.valueOf(lat), String.valueOf(lng)));
        }
        getWeather = (GetWeather) new GetWeather(MainActivity.this).execute(Common.apiRequest(String.valueOf(lat) ,String.valueOf(lng)));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                (new Handler()).postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                        getWeather = (GetWeather) new GetWeather(MainActivity.this).execute(Common.apiRequest(String.valueOf(lat) ,String.valueOf(lng)));


                    }
                },300);
            }
        });

    txtCity.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            Intent i = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(i);
            return false;
        }
    });


    coords.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            lat2= Double.parseDouble(getIntent().getExtras().getString("lat"));
            lng2= Double.parseDouble(getIntent().getExtras().getString("lon"));


                getWeather = (GetWeather) new GetWeather(MainActivity.this).execute(Common.apiRequest(String.valueOf(lat2), String.valueOf(lng2)));

        }
    });

    }



    @Override
    protected void onPause() {
        super.onPause();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSION);
        }
        locationManager.removeUpdates(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(MainActivity.this, new String[]{
                    Manifest.permission.INTERNET,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_NETWORK_STATE,
                    Manifest.permission.SYSTEM_ALERT_WINDOW,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE
            }, MY_PERMISSION);
        }
        locationManager.requestLocationUpdates(provider, 400, 1, this );
    }


    @Override
    public void onLocationChanged(Location location) {
        lat = location.getLatitude();
        lng = location.getLongitude();

        //if(getWeather != null && getWeather.getStatus() == AsyncTask.Status.FINISHED)
        //getWeather = (GetWeather) new GetWeather(MainActivity.this).execute(Common.apiRequest(String.valueOf(lat) ,String.valueOf(lng)));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    private class GetWeather extends AsyncTask<String,Void,String>{

        private Context context;

        public GetWeather(Context context){
            this.context = context;
            pd = new ProgressDialog(context);
        }
        ProgressDialog pd;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setTitle("Please wait...");
            pd.show();

        }



        @Override
        protected String doInBackground(String... params) {
            String stream = null;
            String urlString = params[0];

            Helper http = new Helper();
            stream = http.getHTTPData(urlString);
            return stream;
        }



        @Override




        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            if(s.contains("Error:Not found city")){
                pd.dismiss();
                return;
            }


           // getWeather = (GetWeather) new GetWeather(MainActivity.this).execute(Common.apiRequest(String.valueOf(lat) ,String.valueOf(lng)));



            Gson gson = new Gson();
            Type mType = new TypeToken<OpenWeatherMap>(){}.getType();
            openWeatherMap = gson.fromJson(s,mType);
            pd.dismiss();
            txtCity.setText(String.format("Country: %s,%s",openWeatherMap.getName(), openWeatherMap.getSys().getCountry()));
            txtLastUpdate.setText(String.format("Last Update: %s", Common.getDateNow()));
            txtDescription.setText(String.format("Description: %s",openWeatherMap.getWeather().get(0).getDescription()));
            txtHumidity.setText(String.format("Humidity: %d%%",openWeatherMap.getMain().getHumidity()));
            txtTime.setText(String.format("Sunrise %s  Sunset %s",Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunrise()),Common.unixTimeStampToDateTime(openWeatherMap.getSys().getSunset())));
            txtCelsius.setText(String.format("Temperature: %.2f °C",openWeatherMap.getMain().getTemp()));
            Picasso.with(context)
                    .load(Common.getImage(openWeatherMap.getWeather().get(0).getIcon()))
                    .into(imageView);


        }


    }
}
