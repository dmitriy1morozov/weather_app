package com.example.borsh.weather_test_task;

import com.example.borsh.timezone_pojo.TimeZonePojo;
import com.example.borsh.weather_pojo_current.Response;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiTimeZone {
		String TIMEZONE_API_KEY = "AIzaSyCr7tCTqjWU9SARpa-NSgbKe38yk287uNk";
		String BASE_URL = "https://maps.googleapis.com/maps/api/timezone/";

		@GET("json?key="+ TIMEZONE_API_KEY)
		Call<TimeZonePojo> getTimeZone(
				@Query("location") String latLng,
				@Query("timestamp") long timeStamp);


		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build();
}
