package com.smart.gaodemap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.view.View.OnClickListener;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;


import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.ServiceSettings;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smart.gaodemap.mark.MarkerActivity;
import com.smart.gaodemap.poisearch.PoiKeywordSearchActivity;
import com.smart.gaodemap.route.RouteActivity;

public class MainActivity extends Activity implements OnClickListener{

    private BottomSheetBehavior bottomSheetBehavior;
    MapView mMapView = null;
    MyLocationStyle myLocationStyle;
    LinearLayout holder;
    //初始化地图控制器对象
    AMap aMap;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;

    private Button basicmap;
    private Button rsmap;
    private Button nightmap;
    private Button navimap;
    private Button bt_mark;
    private Button bt_poi;
    private LinearLayout expandableLayout;
    private FloatingActionButton fabExpand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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
        mMapView.getMap().getUiSettings().setZoomControlsEnabled(false);
        mMapView.getMap().getUiSettings().setScaleControlsEnabled(true);//控制比例尺控件是否显示
        mMapView.getMap().getUiSettings().setCompassEnabled(true);


        if (aMap == null) {
            aMap = mMapView.getMap();
        }

        basicmap = (Button)findViewById(R.id.basicmap);
        basicmap.setOnClickListener(this);
        rsmap = (Button)findViewById(R.id.rsmap);
        rsmap.setOnClickListener(this);
        nightmap = (Button)findViewById(R.id.nightmap);
        nightmap.setOnClickListener(this);
        navimap = (Button)findViewById(R.id.navimap);
        navimap.setOnClickListener(this);
        bt_mark = (Button)findViewById(R.id.bt_mark);
        bt_poi = (Button)findViewById(R.id.bt_poi);

        AMapLocationListener mLocationListener = new AMapLocationListener() {
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


        // 为按钮设置点击事件监听器
        bt_mark.setOnClickListener(new View.OnClickListener() {
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
}


