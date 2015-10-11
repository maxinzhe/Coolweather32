package com.example.xinzhe.coolweather3.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.xinzhe.coolweather3.R;
import com.example.xinzhe.coolweather3.db.CoolWeatherDB;
import com.example.xinzhe.coolweather3.model.City;
import com.example.xinzhe.coolweather3.model.County;
import com.example.xinzhe.coolweather3.model.Province;
import com.example.xinzhe.coolweather3.util.HttpCallbackListener;
import com.example.xinzhe.coolweather3.util.HttpUtil;
import com.example.xinzhe.coolweather3.util.*;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

/**
 * Created by Xinzhe on 2015/10/9.
 */
public class ChooseAreaActivity extends Activity {
    public static final int LEVEL_PROVINCE=0;
    public static final int LEVEL_CITY=1;
    public static final int LEVEL_COUNTY=2;

    private int currentLevel;
    private City selectedCity;
    private Province selectedProvince;

    private List<Province> provinceList;
    private List<City> cityList;
    private List<County> countyList;

    private ProgressDialog progressDialog;

    private TextView titleText;



    private List<String> dataList=new ArrayList<String>();
    private ArrayAdapter<String> adapter ;
    private ListView listView;

    private CoolWeatherDB coolWeatherDB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /**
         * 1、完成activity建立以后先去修改manifest文件，然后完成activity的框架，重载函数和标题设置等，加载布局文件
         */
        super.onCreate(savedInstanceState);
       // requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.choose_area);


      //  2、按照功能此时应该载入arraylist一类的控件，可以先去上面定义，然后在下面获取
        titleText=(TextView)findViewById(R.id.title_text);

        adapter=new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1,dataList);
        listView=(ListView)findViewById(R.id.list_view);
        listView.setAdapter(adapter);



      /*  3、数据源部分
         */
        coolWeatherDB=CoolWeatherDB.getInstance(this);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(currentLevel==LEVEL_PROVINCE){
                    selectedProvince=provinceList.get(position);
                    queryCities();
                }else if(currentLevel==LEVEL_CITY){
                    selectedCity=cityList.get(position);
                    queryCounties();
                }
            }
        });
        //如果没有点击，就查询省的列表
        queryProvinces();

    }
    private void queryProvinces(){
        provinceList=coolWeatherDB.loadProvinces();
        if(!provinceList.isEmpty()){
            dataList.clear();
            for(Province province:provinceList){
                dataList.add(province.getProvinceName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText("中国");
            currentLevel=LEVEL_PROVINCE;
        }else{
            queryFromServer(null,"province");
        }
    }
    private void queryCities(){
        cityList=coolWeatherDB.loadCities(selectedProvince.getId());
        if(!cityList.isEmpty()){
            dataList.clear();
            for(City city:cityList){
                dataList.add(city.getCityName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            titleText.setText(selectedProvince.getProvinceName());
            currentLevel=LEVEL_CITY;
        }else{
            queryFromServer(selectedProvince.getProvinceCode(),"city");

        }

    }
    private void queryCounties(){
        countyList=coolWeatherDB.loadCounties(selectedCity.getId());
        if(!countyList.isEmpty()){
            dataList.clear();
            for(County county:countyList){
                dataList.add(county.getCountyName());
            }
            adapter.notifyDataSetChanged();
            listView.setSelection(0);
            currentLevel=LEVEL_COUNTY;
        }else{
            queryFromServer(selectedCity.getCityCode(),"county");
        }
    }
    private void queryFromServer(final String code,final String type){
        String address;
        if(!TextUtils.isEmpty(code)){
            address="http://www.weather.com.cn/data/list3/city" +
                    code+
                    ".xml";
        }else{
            address="http://www.weather.com.cn/data/list3/city.xml";
        }
        showProgressDialog();

        HttpUtil.sendHttpRequest(address, new HttpCallbackListener() {
            @Override
            public void onFinish(String response) {
                boolean result =false;
                if ("province".equals(type)){
                    result=Utility.handleProvinceResponse(coolWeatherDB,response);

                }else if("city".equals(type)){
                    result=Utility.handleCitiesResponse(coolWeatherDB,response,selectedProvince.getId());

                }else if("county".equals(type)){
                    result=Utility.handleCountyResponse(coolWeatherDB,response,selectedCity.getId());
                }
                if(result){
                    //通过runOnUiThread回到主线程处理逻辑
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            closeProgressDialog();
                            if ("province".equals(type)){
                                queryProvinces();
                            }else if("city".equals(type)){
                                queryCities();
                            }else if("county".equals(type)){
                                queryCounties();
                            }
                        }
                    });
                }
            }

            @Override
            public void onError(Exception e) {
                    //获得主线程处理逻辑
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        closeProgressDialog();
                        Toast.makeText(ChooseAreaActivity.this,"加载失败",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private void showProgressDialog(){
        if(progressDialog==null){
            progressDialog=new ProgressDialog(this);
            progressDialog.setMessage("正在加载");
            progressDialog.setCanceledOnTouchOutside(false);
        }
        progressDialog.show();
    }
    private  void closeProgressDialog(){
        if(progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    @Override
    public void onBackPressed() {
       // super.onBackPressed();这里不要原来的功能了
        if (currentLevel==LEVEL_COUNTY){
            queryCities();
        }
        else if(currentLevel==LEVEL_CITY){
            queryProvinces();
        }else{
            finish();
        }
    }
}
