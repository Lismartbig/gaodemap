package com.smart.gaodemap.weather;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.services.core.AMapException;
import com.amap.api.services.weather.LocalDayWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearch.OnWeatherSearchListener;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citylist.Toast.ToastUtils;
import com.lljjcoder.style.citylist.utils.CityListLoader;
import com.lljjcoder.style.citypickerview.CityPickerView;
import com.lljjcoder.style.citythreelist.ProvinceActivity;
import com.smart.gaodemap.MainActivity;
import com.smart.gaodemap.R;
import com.smart.gaodemap.route.RouteActivity;
import com.smart.gaodemap.util.ToastUtil;

import java.util.List;

/**
 * 天气查询 示例查询
 */
public class WeatherActivity extends Activity implements OnWeatherSearchListener {
    private TextView forecasttv;
    private TextView reporttime1;
    private TextView reporttime2;
    private TextView weather;
    private TextView Temperature;
    private TextView wind;
    private TextView humidity;
    private WeatherSearchQuery mquery;
    private WeatherSearch mweathersearch;
    private LocalWeatherLive weatherlive;
    private LocalWeatherForecast weatherforecast;
    private List<LocalDayWeatherForecast> forecastlist = null;
    //声明AMapLocationClient类对象
    private AMapLocationClient mLocationClient;
    //声明定位回调监听器
    private AMapLocationClientOption mLocationOption;
    CityPickerView mPicker=new CityPickerView();
    private String cityname;//天气搜索的城市，可以写名称或adcode；
    private TextView citytx;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.weather_activity);

        //预先加载仿iOS滚轮实现的全部数据
        mPicker.init(this);
        try {
            startLocaion();
        } catch (Exception e) {
            e.printStackTrace();
        }

        init();

//        searchliveweather();
//        searchforcastsweather();
    }


    //城市选择器的回调方法
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ProvinceActivity.RESULT_DATA) {
            if (resultCode == RESULT_OK) {
                if (data == null) {
                    return;
                }
                //省份结果
                CityBean province = data.getParcelableExtra("province");
                //城市结果
                CityBean city = data.getParcelableExtra("city");
                //区域结果
                CityBean area = data.getParcelableExtra("area");

                //显示/
                String cityString = province.getName()+city.getName()+area.getName();
                Toast.makeText(this, cityString, Toast.LENGTH_SHORT).show();
            }
        }
    }
    public void startLocaion() throws Exception {
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(mLocationListener);
        citytx = (TextView) findViewById(R.id.weather_city);
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //获取一次定位结果：
        //该方法默认为false。
        mLocationOption.setOnceLocation(true);
        //设置是否允许模拟位置,默认为false，不允许模拟位置
        mLocationOption.setMockEnable(false);

        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        //启动定位
        mLocationClient.startLocation();
        citytx.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //添加默认的配置，不需要自己定义，当然也可以自定义相关熟悉，详细属性请看demo
                CityConfig cityConfig = new CityConfig.Builder().build();
                mPicker.setConfig(cityConfig);
                //监听选择点击事件及返回结果
                mPicker.setOnCityItemClickListener(new OnCityItemClickListener() {
                    @Override
                    public void onSelected(ProvinceBean province, CityBean city, DistrictBean district) {
                        if(district == null){
                            cityname = city.getName();
                        }else {
                            cityname = district.getName();
                        }
                        citytx.setText(cityname);
                        searchliveweather();
                        searchforcastsweather();
                    }
                });
                //显示
                mPicker.showCityPicker( );
            }
        });
    }


    //声明定位回调监听器
    public AMapLocationListener mLocationListener = new AMapLocationListener() {
        @Override
        public void onLocationChanged(AMapLocation amapLocation) {
            if (amapLocation != null) {
                if (amapLocation.getErrorCode() == 0) {
                    // 定位成功回调信息，设置相关消息
                    cityname = amapLocation.getDistrict(); // 或者 amapLocation.getCity()
                    // 更新UI
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            citytx.setText(cityname);
                            // 一旦完成定位，立即进行天气查询
                            searchliveweather();
                            searchforcastsweather();
                        }
                    });
                } else {
                    // 显示错误信息ErrCode是错误码，errInfo是错误信息，详见错误码表。
                    Log.e("AmapError", "location Error, ErrCode:"
                            + amapLocation.getErrorCode() + ", errInfo:"
                            + amapLocation.getErrorInfo());
                }
            }
        }
    };


    private void init() {
//        TextView city = (TextView) findViewById(R.id.city);
        citytx.setText(cityname);
        forecasttv = (TextView) findViewById(R.id.forecast);
        reporttime1 = (TextView) findViewById(R.id.reporttime1);
        reporttime2 = (TextView) findViewById(R.id.reporttime2);
        weather = (TextView) findViewById(R.id.weather);
        Temperature = (TextView) findViewById(R.id.temp);
        wind = (TextView) findViewById(R.id.wind);
        humidity = (TextView) findViewById(R.id.humidity);
    }

    /**
     * 预报天气查询
     */
    private void searchforcastsweather() {
        mquery = new WeatherSearchQuery(cityname, WeatherSearchQuery.WEATHER_TYPE_FORECAST);//检索参数为城市和天气类型，实时天气为1、天气预报为2
        try {
            mweathersearch = new WeatherSearch(this);
            mweathersearch.setOnWeatherSearchListener(this);
            mweathersearch.setQuery(mquery);
            mweathersearch.searchWeatherAsyn(); //异步搜索
        } catch (AMapException e) {
            e.printStackTrace();
        }

    }

    /**
     * 实时天气查询
     */
    private void searchliveweather() {
        mquery = new WeatherSearchQuery(cityname, WeatherSearchQuery.WEATHER_TYPE_LIVE);//检索参数为城市和天气类型，实时天气为1、天气预报为2
        try {
            mweathersearch = new WeatherSearch(this);
            mweathersearch.setOnWeatherSearchListener(this);
            mweathersearch.setQuery(mquery);
            mweathersearch.searchWeatherAsyn(); //异步搜索
        } catch (AMapException e) {
            e.printStackTrace();
        }

    }

    /**
     * 实时天气查询回调
     */
    @Override
    public void onWeatherLiveSearched(LocalWeatherLiveResult weatherLiveResult, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (weatherLiveResult != null && weatherLiveResult.getLiveResult() != null) {
                weatherlive = weatherLiveResult.getLiveResult();
                reporttime1.setText(weatherlive.getReportTime() + "发布");
                weather.setText(weatherlive.getWeather());
                Temperature.setText(weatherlive.getTemperature() + "°");
                wind.setText(weatherlive.getWindDirection() + "风     " + weatherlive.getWindPower() + "级");
                humidity.setText("湿度         " + weatherlive.getHumidity() + "%");
            } else {
                ToastUtil.show(WeatherActivity.this, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(WeatherActivity.this, rCode);
        }
    }

    /**
     * 天气预报查询结果回调
     */
    @Override
    public void onWeatherForecastSearched(
            LocalWeatherForecastResult weatherForecastResult, int rCode) {
        if (rCode == AMapException.CODE_AMAP_SUCCESS) {
            if (weatherForecastResult != null && weatherForecastResult.getForecastResult() != null
                    && weatherForecastResult.getForecastResult().getWeatherForecast() != null
                    && weatherForecastResult.getForecastResult().getWeatherForecast().size() > 0) {
                weatherforecast = weatherForecastResult.getForecastResult();
                forecastlist = weatherforecast.getWeatherForecast();
                fillforecast();

            } else {
                ToastUtil.show(WeatherActivity.this, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(WeatherActivity.this, rCode);
        }
    }

    private void fillforecast() {
        reporttime2.setText(weatherforecast.getReportTime() + "发布");
        String forecast = "";
        for (int i = 0; i < forecastlist.size(); i++) {
            LocalDayWeatherForecast localdayweatherforecast = forecastlist.get(i);
            String week = null;
            switch (Integer.valueOf(localdayweatherforecast.getWeek())) {
                case 1:
                    week = "周一";
                    break;
                case 2:
                    week = "周二";
                    break;
                case 3:
                    week = "周三";
                    break;
                case 4:
                    week = "周四";
                    break;
                case 5:
                    week = "周五";
                    break;
                case 6:
                    week = "周六";
                    break;
                case 7:
                    week = "周日";
                    break;
                default:
                    break;
            }
            String temp = String.format("%-3s/%3s",
                    localdayweatherforecast.getDayTemp() + "°",
                    localdayweatherforecast.getNightTemp() + "°");
            String date = localdayweatherforecast.getDate();
            forecast += date + "  " + week + "                       " + temp + "\n\n";
        }
        forecasttv.setText(forecast);
    }
}

