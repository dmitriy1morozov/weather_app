package com.example.borsh.weather_test_task;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.constraint.ConstraintLayout;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import com.example.borsh.weather_pojo_forecast.Main;
import com.example.borsh.weather_pojo_forecast.Response;
import retrofit2.Call;
import retrofit2.Callback;

public class ForecastActivity extends AppCompatActivity {

		private static final String TAG = "ForecastActivity MyLogs";

		@BindView(R.id.btn_forecast_back) ImageButton mBtnBack;
		@BindView(R.id.text_forecast_city) TextView mCity;
		@BindView(R.id.text_forecast_max_temp) TextView mMaxTemp;
		@BindView(R.id.text_forecast_min_temp) TextView mMinTemp;
		ProgressDialog mProgressDialog;

		@Override protected void onCreate(Bundle savedInstanceState) {
				super.onCreate(savedInstanceState);
				setContentView(R.layout.activity_forecast);
				ButterKnife.bind(this);
		}

		@Override protected void onStart() {
				super.onStart();
				mProgressDialog = new ProgressDialog(this);
				mProgressDialog.setIndeterminate(true);
				mProgressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				mProgressDialog.setMessage("Loading...");
				mProgressDialog.show();

				double lat = this.getIntent().getExtras().getDouble("lat");
				double lon = this.getIntent().getExtras().getDouble("lon");
				requestForecastFromApi(lat, lon);
		}

		//----------------------------------------------------------------------------------------------
		private void requestForecastFromApi(double lat, double lng) {
				ApiWeather forecast = ApiWeather.retrofit.create(ApiWeather.class);
				final Call<Response> call = forecast.getForecast5DaysByCoordinates(lat, lng);
				call.enqueue(new Callback<Response>() {
						@Override
						public void onResponse(Call<Response> call, retrofit2.Response<Response> response) {
								if (mProgressDialog != null && mProgressDialog.isShowing())
										mProgressDialog.dismiss();

								if (response.isSuccessful()) {

										String city = response.body().getCity().getName();
										String country = response.body().getCity().getCountry();
										double temperatureMin = response.body().getList().get(0).getMain().getTempMin();
										double temperatureMax = response.body().getList().get(0).getMain().getTempMax();
										for (int i = 1; i < response.body().getList().size(); i++) {
												Main listItem = response.body().getList().get(i).getMain();
												if (temperatureMax < listItem.getTempMax()) {
														temperatureMax = listItem.getTempMax();
												}
												if (temperatureMin > listItem.getTempMin()) {
														temperatureMin = listItem.getTempMin();
												}
										}

										mCity.setText(city + " [" + country + "]");
										mMaxTemp.setText(
												String.valueOf(MainActivity.convertKelvinToCelsius(temperatureMax)));
										mMinTemp.setText(
												String.valueOf(MainActivity.convertKelvinToCelsius(temperatureMin)));
								}
						}

						@Override public void onFailure(Call<Response> call, Throwable t) {
								if (mProgressDialog != null && mProgressDialog.isShowing())
										mProgressDialog.dismiss();

								Log.d(TAG, "Retrofit onResponse: Failure" + t.getMessage());
								Log.d(TAG, "onFailure: " + call.toString());
								mCity.setText("Failed to retrieve data");
								mMaxTemp.setText("");
								mMinTemp.setText("");
						}
				});
		}

		@OnClick(R.id.btn_forecast_back) public void onBackClicked() {
				onBackPressed();
		}
}
