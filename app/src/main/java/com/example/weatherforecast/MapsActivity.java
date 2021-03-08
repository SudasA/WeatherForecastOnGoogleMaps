package com.example.weatherforecast;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.weatherforecast.data.WeatherData;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.example.weatherforecast.databinding.ActivityMapsBinding;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, View.OnClickListener {

    private GoogleMap mMap;
    private ActivityMapsBinding binding;

    private Api mApi = Api.Instance.getApi();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMapsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button moscowButton = findViewById(R.id.moscow_button);
        moscowButton.setOnClickListener(this);

        findViewById(R.id.minsk_button).setOnClickListener(this);
    }

    private void receiveWeather(final String city) {
        mApi.getWeatherDataByCity(city, "d9d714e6d9c6451ad942f01d3225876c", "metric" )
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<WeatherData>() {
                    @Override
                    public void accept(WeatherData weatherData) throws Exception {

                      //  Toast.makeText(MapsActivity.this, weatherData.getName() + " " + weatherData.getMain().getTemp(), Toast.LENGTH_LONG).show();

                        showWeatherMarker(weatherData);
                    }
                });
    }

    private void showWeatherMarker(WeatherData weatherData) {
        LatLng latlng = new LatLng(weatherData.getCoord().getLat(), weatherData.getCoord().getLng());
        mMap.addMarker(new MarkerOptions().position(latlng).title(weatherData.getName()+":" + weatherData.getMain().getTemp())).showInfoWindow();
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latlng, 6));
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

        // Add a marker in Sydney and move the camera
        LatLng minsk = new LatLng(53.62, 28.75);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(minsk, 4));




    }

    @Override
    public void onClick(View v) {
        receiveWeather(((Button)v).getText().toString());
    }
}