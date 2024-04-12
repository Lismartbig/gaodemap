package com.smart.gaodemap.base.map;

import android.app.Activity;
import android.os.Bundle;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.maps.AMap;
import com.amap.api.maps.MapView;
import com.amap.api.maps.MapsInitializer;
import com.amap.api.maps.model.MyLocationStyle;
import com.amap.api.services.core.ServiceSettings;
import com.smart.gaodemap.R;

public class OriginalMapActivity extends Activity {
    MapView mMapView = null;
    MyLocationStyle myLocationStyle;
    //初始化地图控制器对象
    AMap aMap;
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明定位回调监听器

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        ServiceSettings.updatePrivacyShow(this,true,true);
        ServiceSettings.updatePrivacyAgree(this,true);

        // 设置为true表示启动显示定位蓝点，false表示隐藏定位蓝点并不进行定位，默认是false。
        setContentView(R.layout.activity_map);
        MapsInitializer.updatePrivacyShow(this,true,true);
        MapsInitializer.updatePrivacyAgree(this,true);
        //获取地图控件引用
        mMapView = (MapView) findViewById(R.id.map);
        //在activity执行onCreate时执行mMapView.onCreate(savedInstanceState)，创建地图
        mMapView.onCreate(savedInstanceState);

        if (aMap == null) {
            aMap = mMapView.getMap();
        }
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
        myLocationStyle.interval(2000); //设置连续定位模式下的定位间隔，只在连续定位模式下生效，单次定位模式下不会生效。单位为毫秒。
        aMap.setMyLocationStyle(myLocationStyle);//设置定位蓝点的Style
        aMap.getUiSettings().setMyLocationButtonEnabled(true);//设置默认定位按钮是否显示，非必需设置。
        aMap.setMyLocationEnabled(true);
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //在activity执行onDestroy时执行mMapView.onDestroy()，销毁地图
        mMapView.onDestroy();
    }
    @Override
    protected void onResume() {
        super.onResume();
        //在activity执行onResume时执行mMapView.onResume ()，重新绘制加载地图
        mMapView.onResume();
    }
    @Override
    protected void onPause() {
        super.onPause();
        //在activity执行onPause时执行mMapView.onPause ()，暂停地图的绘制
        mMapView.onPause();
    }
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        //在activity执行onSaveInstanceState时执行mMapView.onSaveInstanceState (outState)，保存地图当前的状态
        mMapView.onSaveInstanceState(outState);
    }
}


//public class MainActivity extends AppCompatActivity implements AMapLocationListener {
//
//    //请求权限码
//    private static final int REQUEST_PERMISSIONS = 9527;
//    //声明AMapLocationClient类对象
//    public AMapLocationClient mLocationClient = null;
//    //声明AMapLocationClientOption对象
//    public AMapLocationClientOption mLocationOption = null;
//    //内容
//    private TextView tvContent;
//
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//        //初始化控件
//        tvContent = findViewById(R.id.tv_content);
//
//        //初始化定位
//        initLocation();
//        //检测安卓版本
//        checkingAndroidVersion();
//    }
//    /**
//     * 检查Android版本
//     */
//    private void checkingAndroidVersion() {
//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
//            //Android6.0及以上先获取权限再定位
//            requestPermission();
//        }else {
//            //Android6.0以下直接定位
//            mLocationClient.startLocation();
//        }
//    }
//
//    /**
//     * 动态请求权限
//     */
//    @AfterPermissionGranted(REQUEST_PERMISSIONS)
//    private void requestPermission() {
//        String[] permissions = {
//                Manifest.permission.ACCESS_COARSE_LOCATION,
//                Manifest.permission.ACCESS_FINE_LOCATION,
//                Manifest.permission.READ_PHONE_STATE,
//                Manifest.permission.WRITE_EXTERNAL_STORAGE
//        };
//
//        if (EasyPermissions.hasPermissions(this, permissions)) {
//            //true 有权限 开始定位
//            showMsg("已获得权限，可以定位啦！");
//            //启动定位
//            mLocationClient.startLocation();
//        } else {
//            //false 无权限
//            EasyPermissions.requestPermissions(this, "需要权限", REQUEST_PERMISSIONS, permissions);
//        }
//    }
//
//    /**
//     * 请求权限结果
//     * @param requestCode
//     * @param permissions
//     * @param grantResults
//     */
//    @Override
//    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
//        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
//        //设置权限请求结果
//        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
//    }
//
//    /**
//     * Toast提示
//     * @param msg 提示内容
//     */
//    private void showMsg(String msg){
//        Toast.makeText(this,msg, Toast.LENGTH_SHORT).show();
//    }
//
//    /**
//     * 初始化定位
//     */
//    private void initLocation() {
//        //初始化定位
//        try {
//            mLocationClient = new AMapLocationClient(getApplicationContext());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (mLocationClient != null) {
//            //设置定位回调监听
//            mLocationClient.setLocationListener(this);
//            //初始化AMapLocationClientOption对象
//            mLocationOption = new AMapLocationClientOption();
//            //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
//            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
//            //获取最近3s内精度最高的一次定位结果：
//            //设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
//            mLocationOption.setOnceLocationLatest(true);
//            //设置是否返回地址信息（默认返回地址信息）
//            mLocationOption.setNeedAddress(true);
//            //设置定位请求超时时间，单位是毫秒，默认30000毫秒，建议超时时间不要低于8000毫秒。
//            mLocationOption.setHttpTimeOut(20000);
//            //关闭缓存机制，高精度定位会产生缓存。
//            mLocationOption.setLocationCacheEnable(false);
//            //给定位客户端对象设置定位参数
//            mLocationClient.setLocationOption(mLocationOption);
//        }
//    }
//
//    /**
//     * 接收异步返回的定位结果
//     *
//     * @param aMapLocation
//     */
//    @Override
//    public void onLocationChanged(AMapLocation aMapLocation) {
//        if (aMapLocation != null) {
//            if (aMapLocation.getErrorCode() == 0) {
//                //地址
//                String address = aMapLocation.getAddress();
//                tvContent.setText(address == null ? "无地址" : address);
//            } else {
//                tvContent.setText(aMapLocation.getErrorCode());
//                //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
//                Log.e("AmapError", "location Error, ErrCode:"
//                        + aMapLocation.getErrorCode() + ", errInfo:"
//                        + aMapLocation.getErrorInfo());
//            }
//        }
//    }
//
//
//}