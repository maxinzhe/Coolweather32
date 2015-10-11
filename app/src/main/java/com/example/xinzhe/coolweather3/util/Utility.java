package com.example.xinzhe.coolweather3.util;

import android.text.TextUtils;

import com.example.xinzhe.coolweather3.db.CoolWeatherDB;
import com.example.xinzhe.coolweather3.model.City;
import com.example.xinzhe.coolweather3.model.County;
import com.example.xinzhe.coolweather3.model.Province;

/**
 * Created by Xinzhe on 2015/10/9.
 */
public class Utility {
    /**
     * 解析和处理服务器返回的数据
     */
    /*
        省级
     */
    public synchronized  static boolean handleProvinceResponse(CoolWeatherDB coolWeatherDB,String response){
        if (!TextUtils.isEmpty(response)){
            String[] allProvinces=response.split(",");
            if(allProvinces!=null&&allProvinces.length>0){//空和长度都要判断，尤其是空很容易出现空指针导致异常退出
                for(String p:allProvinces){
                    String array[]=p.split("\\|");
                    Province province=new Province();
                    province.setProvinceCode(array[0]);
                    province.setProvinceName(array[1]);
                    coolWeatherDB.saveProvince(province);
                }
                return true;
            }
        }
        return false;
    }
    public static boolean handleCitiesResponse(CoolWeatherDB coolWeatherDB,String response,int provinceId){
        if(!TextUtils.isEmpty(response)){
            String[] allCities=response.split(",");
            if(allCities!=null&&allCities.length>0){
                for (String p:allCities){
                    String []array=p.split("\\|");
                    City city=new City();
                    city.setCityCode(array[0]);
                    city.setCityName(array[1]);
                    city.setProvinceId(provinceId);
                    coolWeatherDB.saveCity(city);
                }
                return true;
            }
        }
        return false;
    }
    public static boolean handleCountyResponse(CoolWeatherDB coolWeatherDB,String response,int cityId){
        if(!TextUtils.isEmpty(response)){
            String[] allCounties=response.split(",");
            if (allCounties!=null&&allCounties.length>0){
                for(String p:allCounties){
                    String [] array=p.split("\\|");
                    County county=new County();
                    county.setCountyCode(array[0]);
                    county.setCountyName(array[1]);
                    county.setCityId(cityId);
                    coolWeatherDB.saveCounty(county);
                }
                return true;
            }
        }
        return false;
    }
}
