package com.example.borsh.weather_test_task;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ApiWeather {
		String OPEN_WEATHER_MAP_API_KEY = "f6b8ddf6514488a5ab1f20a1f204fbd2";
		String BASE_URL = "http://api.openweathermap.org/data/2.5/";

		@GET("weather?APPID="+ OPEN_WEATHER_MAP_API_KEY)
		Call<com.example.borsh.weather_pojo_current.Response> getCurrentWeatherByCoordinates(
				@Query("lat") double lat,
				@Query("lon") double lon);

		@GET("forecast?APPID="+ OPEN_WEATHER_MAP_API_KEY)
		Call<com.example.borsh.weather_pojo_forecast.Response> getForecast5DaysByCoordinates(
				@Query("lat") double lat,
				@Query("lon") double lon);

		Retrofit retrofit = new Retrofit.Builder()
				.baseUrl(BASE_URL)
				.addConverterFactory(GsonConverterFactory.create())
				.build();
}
