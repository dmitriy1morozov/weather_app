package com.example.borsh.weather_pojo_forecast;

import javax.annotation.Generated;
import com.google.gson.annotations.SerializedName;

@Generated("com.robohorse.robopojogenerator")
public class Snow{

	@SerializedName("3h")
	private double jsonMember3h;

	public void setJsonMember3h(double jsonMember3h){
		this.jsonMember3h = jsonMember3h;
	}

	public double getJsonMember3h(){
		return jsonMember3h;
	}

	@Override
 	public String toString(){
		return 
			"Snow{" + 
			"3h = '" + jsonMember3h + '\'' + 
			"}";
		}
}