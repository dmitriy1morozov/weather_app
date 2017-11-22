package com.example.borsh.weather_test_task;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.example.borsh.timezone_pojo.TimeZonePojo;
import com.example.borsh.weather_pojo_current.Response;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import retrofit2.Call;
import retrofit2.Callback;

public class MainActivity extends AppCompatActivity
		implements OnMapReadyCallback, GoogleMap.OnMapClickListener {

		private static final String TAG = "MainActivity MyLogs";

		private static final int MY_PERMISSIONS_REQUEST_COARSE_LOCATION = 0;
		private static final int MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1;

		@BindView(R.id.btn_main_forecast) Button mBtnHistory;
		@BindView(R.id.text_main_city) EditText mTextCity;
		@BindView(R.id.text_main_coordinates) TextView mTextCoordinates;
		@BindView(R.id.text_main_temperature) TextView mTextTemperature;
		@BindView(R.id.text_main_weather_conditions) TextView mTextWeatherConditions;
		@BindView(R.id.text_main_local_time) TextView mLocalTime;

		private Marker mMarker;

		//-------------------------------------Static methods-------------------------------------------
		public static int convertFahrenheitToCelsius(double fahrenheit) {
				int celsius = (int) ((fahrenheit - 32) / 1.8);
				return celsius;
		}

		public static int convertKelvinToCelsius(double kelvins) {
				int celsius = (int) (kelvins - 273);
				return celsius;
		}

		//-------------------------------------Activity LifeCycle---------------------------------------
		@Override protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_main);
				ButterKnife.bind(this);

				SupportMapFragment mapFragment =
						(SupportMapFragment) getSupportFragmentManager().findFragmentById(
								R.id.map_main_googleMap);
				mapFragment.getMapAsync(this);
		}

		//--------------------------------------Private methods-----------------------------------------
		private void showNoLocationPermissionsSnackBar() {
				Snackbar.make(mBtnHistory, "Unable to access GPS data", Snackbar.LENGTH_LONG)
						.setAction("Add permissions", new View.OnClickListener() {
								@Override public void onClick(View v) {
										requestLocationPermissions();
								}
						})
						.show();
		}

		private void requestLocationPermissions() {
				if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
						!= PackageManager.PERMISSION_GRANTED) {
						ActivityCompat.requestPermissions(this,
								new String[] { Manifest.permission.ACCESS_FINE_LOCATION },
								MY_PERMISSIONS_REQUEST_FINE_LOCATION);
				}
				if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
						== PackageManager.PERMISSION_GRANTED) {
						ActivityCompat.requestPermissions(this,
								new String[] { Manifest.permission.ACCESS_COARSE_LOCATION },
								MY_PERMISSIONS_REQUEST_COARSE_LOCATION);
				}
		}

		//-------------------------------------UI actions-----------------------------------------------
		@OnClick(R.id.btn_main_forecast) public void showHistory(View view) {
				if (mMarker == null || mMarker.getPosition() == null) {
						Toast.makeText(this, "Can't display history. No position set.", Toast.LENGTH_SHORT)
								.show();
						return;
				}

				Intent history = new Intent(this, ForecastActivity.class);
				history.putExtra("lat", mMarker.getPosition().latitude);
				history.putExtra("lon", mMarker.getPosition().longitude);
				startActivity(history);
		}

		//----------------------------------------------------------------------------------------------
		@Override public void onRequestPermissionsResult(final int requestCode,
				@NonNull String[] permissions, @NonNull int[] grantResults) {
				switch (requestCode) {
						case MY_PERMISSIONS_REQUEST_FINE_LOCATION:
						case MY_PERMISSIONS_REQUEST_COARSE_LOCATION:
								if (grantResults.length > 0
										&& grantResults[0] != PackageManager.PERMISSION_GRANTED) {
										showNoLocationPermissionsSnackBar();
								}
								break;
				}
		}

		@Override public void onMapReady(final GoogleMap googleMap) {
				// Add a marker in Dnipro and move the map's camera to the same location.
				double lat = 48.450001;
				double lng = 34.98333;
				final LatLng dniproCity = new LatLng(lat, lng);
				googleMap.moveCamera(CameraUpdateFactory.newLatLng(dniproCity));
				MarkerOptions markerOptions = new MarkerOptions().position(dniproCity).title("Your position");
				mMarker = googleMap.addMarker(markerOptions);
				googleMap.setOnMapClickListener(this);

				requestCurrentWeatherFromApi(lat, lng);
				String latLng = "" + lat + "," + lng;
				long timestamp = System.currentTimeMillis()/1000;
				requestLocalTimeFromApi(latLng, timestamp);
		}

		@Override public void onMapClick(LatLng latLng) {
				mMarker.setPosition(latLng);
				requestCurrentWeatherFromApi(latLng.latitude, latLng.longitude);
				String latitudeLongitude = "" + latLng.latitude + "," + latLng.longitude;
				long timestamp = System.currentTimeMillis()/1000;
				requestLocalTimeFromApi(latitudeLongitude, timestamp);
		}

		private void requestCurrentWeatherFromApi(double lat, double lng) {
				ApiWeather currentWeather = ApiWeather.retrofit.create(ApiWeather.class);
				final Call<Response> call = currentWeather.getCurrentWeatherByCoordinates(lat, lng);
				call.enqueue(new Callback<Response>() {
						@Override
						public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
								if (response.isSuccessful()) {
										String city = response.body().getName();
										String country = response.body().getSys().getCountry();
										Location location = new Location("");
										location.setLatitude(response.body().getCoord().getLat());
										location.setLongitude(response.body().getCoord().getLon());
										String coordinates = String.format("%1$.2f;%2$.2f",
												location.getLatitude(), location.getLongitude());
										int currentTemperature = convertKelvinToCelsius(response.body().getMain().getTemp());
										String weatherConditions = response.body().getWeather().get(0).getMain();
										weatherConditions +=
												" (" + response.body().getWeather().get(0).getDescription() + ")";

										mTextCity.setText(city + " [" + country + "]");
										mTextCoordinates.setText(coordinates);
										mTextTemperature.setText(String.valueOf(currentTemperature) + "\u2103");
										mTextWeatherConditions.setText(weatherConditions);
								}
						}

						@Override public void onFailure(Call<Response> call, Throwable t) {
								Log.d(TAG, "Retrofit Weather onResponse: Failure" + t.getMessage());
								Log.d(TAG, "onFailure: " + call.toString());
								mTextCity.setText("Failed to retrieve data");
								mTextCoordinates.setText("");
								mTextTemperature.setText("");
								mTextWeatherConditions.setText("");
						}
				});
		}



		private void requestLocalTimeFromApi(String latLng, final long timestamp) {
				ApiTimeZone apiTimeZone = ApiTimeZone.retrofit.create(ApiTimeZone.class);
				Call<TimeZonePojo> call = apiTimeZone.getTimeZone(latLng, timestamp);
				call.enqueue(new Callback<TimeZonePojo>() {
						@Override public void onResponse(Call<TimeZonePojo> call, retrofit2.Response<TimeZonePojo> response) {
								long rawOffset = response.body().getRawOffset();
								String localTime = String.format("Local time = %1$tT", System.currentTimeMillis() + rawOffset * 1000);
								mLocalTime.setText(localTime);
								Log.d(TAG, "onResponse: RawOffset=" + rawOffset);
						}

						@Override public void onFailure(Call<TimeZonePojo> call, Throwable t) {
								Log.d(TAG, "Retrofit TimeZone onResponse: Failure" + t.getMessage());
								Log.d(TAG, "onFailure: " + call.toString());
								mLocalTime.setText("");
						}
				});
		}
}