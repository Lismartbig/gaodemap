package com.smart.gaodemap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;

import androidx.annotation.NonNull;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.AMapOptions;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.CameraPosition;
import com.amap.api.maps.model.MyLocationStyle;

import com.amap.api.services.core.AMapException;
import com.amap.api.services.core.ServiceSettings;
import com.amap.api.services.weather.LocalDayWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecast;
import com.amap.api.services.weather.LocalWeatherForecastResult;
import com.amap.api.services.weather.LocalWeatherLive;
import com.amap.api.services.weather.LocalWeatherLiveResult;
import com.amap.api.services.weather.WeatherSearch;
import com.amap.api.services.weather.WeatherSearch.OnWeatherSearchListener;
import com.amap.api.services.weather.WeatherSearchQuery;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.lljjcoder.Interface.OnCityItemClickListener;
import com.lljjcoder.bean.CityBean;
import com.lljjcoder.bean.DistrictBean;
import com.lljjcoder.bean.ProvinceBean;
import com.lljjcoder.citywheel.CityConfig;
import com.lljjcoder.style.citypickerview.CityPickerView;
import com.smart.gaodemap.mark.MarkerActivity;
import com.smart.gaodemap.poisearch.PoiKeywordSearchActivity;
import com.smart.gaodemap.route.RouteActivity;
import com.smart.gaodemap.util.ToastUtil;
import com.smart.gaodemap.weather.WeatherActivity;

import android.view.animation.RotateAnimation;
import android.view.animation.Animation;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends Activity implements OnClickListener, OnWeatherSearchListener {

    private BottomSheetBehavior bottomSheetBehavior;
    MapView mMapView = null;
    MyLocationStyle myLocationStyle;
    LinearLayout holder;
    //初始化地图控制器对象
    AMap aMap;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient;

    private Button basicmap;
    private Button rsmap;
    private Button nightmap;
    private Button navimap;
    private Button bt_rute;
    private Button bt_poi;
    private Button bt_weather;
    private Button bt_navigation;
    private LinearLayout expandableLayout;
    private FloatingActionButton fabExpand;
    private float lastBearing = 0;
    private RotateAnimation rotateAnimation;
    private ImageView ivCompass;

//    private TextView forecasttv;
    private TextView weather;
    private TextView Temperature;
    private TextView wind;
    private TextView humidity;
    private WeatherSearchQuery mquery;
    private WeatherSearch mweathersearch;
    private LocalWeatherLive weatherlive;
    private LocalWeatherForecast weatherforecast;
    private List<LocalDayWeatherForecast> forecastlist = null;

    //声明定位回调监听器
    private AMapLocationClientOption mLocationOption;
    private String cityname;//天气搜索的城市，可以写名称或adcode；
    private TextView citytx;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        citytx = (TextView) findViewById(R.id.city_name_weather_card);

        try {
            startLocaion();
        } catch (Exception e) {
            e.printStackTrace();
        }

        holder = findViewById(R.id.bottom_sheet);
        BottomSheetBehavior<LinearLayout> behavior = BottomSheetBehavior.from(holder);
        behavior.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View view, int newState) {
//                if (newState == BottomSheetBehavior.STATE_EXPANDED || newState == BottomSheetBehavior.STATE_HALF_EXPANDED ) {
//                    locate.setVisibility(View.GONE);
//                }else if (newState == BottomSheetBehavior.STATE_COLLAPSED) {
//                    locate.setVisibility(View.VISIBLE);
//                }
            }
            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            }
        });

        ServiceSettings.updatePrivacyShow(this,true,true);
        ServiceSettings.updatePrivacyAgree(this,true);

        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        setContentView(R.layout.activity_main);
        MapsInitializer.updatePrivacyShow(this,true,true);
        MapsInitializer.updatePrivacyAgree(this,true);


        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);
        mMapView.getMap().getUiSettings().setZoomControlsEnabled(true);//缩放按钮
        mMapView.getMap().getUiSettings().setZoomPosition(AMapOptions.ZOOM_POSITION_RIGHT_CENTER); //缩放按钮右中间
        mMapView.getMap().getUiSettings().setScaleControlsEnabled(false);//控制比例尺控件是否显示
        mMapView.getMap().getUiSettings().setCompassEnabled(false);//指南针控件

        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        //开启室内地图
        aMap.showIndoorMap(true);

        basicmap = (Button)findViewById(R.id.basicmap);
        basicmap.setOnClickListener(this);
        rsmap = (Button)findViewById(R.id.rsmap);
        rsmap.setOnClickListener(this);
        nightmap = (Button)findViewById(R.id.nightmap);
        nightmap.setOnClickListener(this);
        navimap = (Button)findViewById(R.id.navimap);
        navimap.setOnClickListener(this);
        bt_rute = (Button)findViewById(R.id.bt_rute);
        bt_poi = (Button)findViewById(R.id.bt_poi);
        bt_weather = (Button)findViewById(R.id.bt_weather);
        bt_navigation = (Button)findViewById(R.id.bt_navigation);
        ivCompass = (ImageView)findViewById(R.id.iv_compass);

//        forecasttv = (TextView) findViewById(R.id.weather_forecast);
        weather = (TextView) findViewById(R.id.weather_this_time);
        Temperature = (TextView) findViewById(R.id.temp_card);
        wind = (TextView) findViewById(R.id.wind);
        humidity = (TextView) findViewById(R.id.humidity);

        mLocationListener = new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
            }
        };
        //初始化定位
        try {
            mLocationClient = new AMapLocationClient(getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        //设置定位回调监听
        mLocationClient.setLocationListener(mLocationListener);

        //定位蓝点//
        myLocationStyle = new MyLocationStyle();//初始化定位蓝点样式类myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE);//连续定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。（1秒1次定位）如果不设置myLocationType，默认也会执行此种模式。
        myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE_NO_CENTER);//连续定位、蓝点不会移动到地图中心点，定位点依照设备方向旋转，并且蓝点会跟随设备移动。
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);

        //mapView相关监听
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                startIvCompass(cameraPosition.bearing);
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {
            }
        });
        aMap.setOnCameraChangeListener(new AMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition cameraPosition) {

                startIvCompass(cameraPosition.bearing);
            }

            @Override
            public void onCameraChangeFinish(CameraPosition cameraPosition) {

            }
        });


        // 为按钮设置点击事件监听器
        bt_rute.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建一个新的Intent来启动TargetActivity
                Intent intent = new Intent(MainActivity.this, RouteActivity.class);
                // 启动目标Activity
                startActivity(intent);
            }
        });
        bt_poi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建一个新的Intent来启动TargetActivity
                Intent intent = new Intent(MainActivity.this, PoiKeywordSearchActivity.class);
                // 启动目标Activity
                startActivity(intent);
            }
        });
        bt_weather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 创建一个新的Intent来启动TargetActivity
                Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
                // 启动目标Activity
                startActivity(intent);
            }
        });
//        bt_navigation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 创建一个新的Intent来启动TargetActivity
//                Intent intent = new Intent(MainActivity.this, NavigationActivity.class);
//                // 启动目标Activity
//                startActivity(intent);
//            }
//        });

        expandableLayout = findViewById(R.id.ly_bt);
        fabExpand = findViewById(R.id.fab);

        fabExpand.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (expandableLayout.getVisibility() == View.GONE) {
                    // 展开按钮列
                    expandableLayout.setVisibility(View.VISIBLE);
                    fabExpand.setImageResource(R.drawable.float_up);
                } else {
                    // 折叠按钮列
                    expandableLayout.setVisibility(View.GONE);
                    fabExpand.setImageResource( R.drawable.float_down);
                }
            }
        });


    }

    //指南针动画
    private void startIvCompass(float bearing) {
        bearing = 360 - bearing;
//        Log.d(TAG, "startIvCompass: " + bearing);
        rotateAnimation = new RotateAnimation(lastBearing, bearing, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        rotateAnimation.setFillAfter(true);

        ivCompass.startAnimation(rotateAnimation);
        lastBearing = bearing;
    }

    //地图模式切换监听
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.basicmap:
                aMap.setMapType(AMap.MAP_TYPE_NORMAL);// 矢量地图模式
                break;
            case R.id.rsmap:
                aMap.setMapType(AMap.MAP_TYPE_SATELLITE);// 卫星地图模式
                break;
            case R.id.nightmap:
                aMap.setMapType(AMap.MAP_TYPE_NIGHT);//夜景地图模式
                break;
            case R.id.navimap:
                aMap.setMapType(AMap.MAP_TYPE_NAVI);//导航地图模式
                break;
        }

    }
    //定位获取地区
    public void startLocaion() throws Exception {
        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationClient.setLocationListener(mLocationListener);
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
//                reporttime1.setText(weatherlive.getReportTime() + "发布");
                weather.setText(weatherlive.getWeather());
                Temperature.setText(weatherlive.getTemperature() + "°");
                wind.setText(weatherlive.getWindDirection() + "风     " + weatherlive.getWindPower() + "级");
                humidity.setText("湿度         " + weatherlive.getHumidity() + "%");
            } else {
                ToastUtil.show(this, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this, rCode);
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

            } else {
                ToastUtil.show(this, R.string.no_result);
            }
        } else {
            ToastUtil.showerror(this, rCode);
        }
    }

}




