package com.example.yuanpeiyu.vpntest;

import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.*;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.*;
import android.telephony.*;
import android.telephony.gsm.GsmCellLocation;
import android.util.Log;
import android.view.*;
import android.widget.TextView;

import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.utils.CoordinateConverter;

import java.util.List;

/**
 * 功能描述：通过手机信号获取基站信息
 * # 通过TelephonyManager 获取lac:mcc:mnc:cell-id
 * # MCC，Mobile Country Code，移动国家代码（中国的为460）；
 * # MNC，Mobile Network Code，移动网络号码（中国移动为0，中国联通为1，中国电信为2）；
 * # LAC，Location Area Code，位置区域码；
 * # CID，Cell Identity，基站编号；
 * # BSSS，Base station signal strength，基站信号强度。
 * @author android_ls
 */
public class GSMCellLocationActivity extends AppCompatActivity {

    private static final String TAG = "GSMCellLocationActivity";
    private static final int REQUEST_LOCATION_MIN_TIME = 10 * 1000;
    private static final int REQUEST_LOCATION_MIN_DISTANCE = 50;
    private static final long SUITABLE_MILLIONTIME = 30 * 1000;
    private static final long SUITABLE_NANOIME = 30 * 1000 * 1000 * 1000l;

    private View mRootView;
    private Toolbar mToolBar;
    private DrawerLayout mDrawerView;
    private ActionBarDrawerToggle mDrawerToggle;
    private CollapsingToolbarLayout mCollapsingToolbarLayout;
    private RecyclerView mRecycleView;
    /*private PhoneStateListener mPhoneStateListener = new PhoneStateListener() {
        @Override
        public void onCellInfoChanged(List<CellInfo> cellInfo) {
            super.onCellInfoChanged(cellInfo);
            Log.d(TAG, "onCellInfoChanged");
            getCellIds();
        }

        @Override
        public void onCellLocationChanged(CellLocation location) {
            super.onCellLocationChanged(location);
            Log.d(TAG, "onCellLocationChanged");
            getCellIds();
        }
    };*/

    private LocationListener mGpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "mGpsLocationListener onLocationChanged");
//            getLocationByGps();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            if (LocationManager.GPS_PROVIDER.equals(provider)) {
//                getLocationByGps();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            if (LocationManager.GPS_PROVIDER.equals(provider)) {
//                getLocationByGps();
            }
        }
    };

    private LocationListener mMainGpsLocationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            Log.d(TAG, "mMainGpsLocationListener onLocationChanged");
//            getLocationByGps();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {
            if (LocationManager.GPS_PROVIDER.equals(provider)) {
//                getLocationByGps();
            }
        }

        @Override
        public void onProviderDisabled(String provider) {
            if (LocationManager.GPS_PROVIDER.equals(provider)) {
//                getLocationByGps();
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.main);
        mRootView = findViewById(R.id.roo_view);
        mToolBar = (Toolbar) findViewById(R.id.tooBar);

        setSupportActionBar(mToolBar);
        mDrawerView = (DrawerLayout) findViewById(R.id.drawer_view);
        mDrawerToggle = new ActionBarDrawerToggle(this, mDrawerView, R.string.app_name, R.string.app_name);
        mDrawerToggle.syncState();
        mDrawerView.setDrawerListener(mDrawerToggle);

        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mCollapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolBar);
        mCollapsingToolbarLayout.setTitle("Test");
//        TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
//        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CELL_INFO);
//        mTelephonyManager.listen(mPhoneStateListener, PhoneStateListener.LISTEN_CELL_LOCATION);
        // 获取基站信息
        FloatingActionButton button = (FloatingActionButton) findViewById(R.id.button1);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /*getCellIds();
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        registLocationListener();
                    }
                }).start();*/
                /*Intent intent = new Intent(GSMCellLocationActivity.this, TestMainActivity.class);
                intent.putExtra("test", "aaaaaaaaaaaaaaaaaaaa");
                startActivity(intent);*/
                showSnackBar();
//                getLocationByGps();
            }
        });
//        getLocationByGps();
//        registMainLocationListener();

        /*mRecycleView = (RecyclerView) findViewById(R.id.recycle_view);
//        mRecycleView.setLayoutManager(new StaggeredGridLayoutManager(2, OrientationHelper.VERTICAL));
        mRecycleView.setLayoutManager(new LinearLayoutManager(this));
        mRecycleView.setItemAnimator(new DefaultItemAnimator());
        mRecycleView.setAdapter(new NormalRecyclerViewAdapter(this));*/
    }

    private void showSnackBar() {
        final Snackbar snackbar = Snackbar.make(mRootView, "点击快乐", Snackbar.LENGTH_LONG);
        snackbar.setAction("UnDo", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
        snackbar.show();
    }

    /*@Override
    public void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        mDrawerToggle.syncState();
    }*/

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
//        mDrawerToggle.onConfigurationChanged(newConfig);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (mDrawerToggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }


    private void getCellIds() {
        TelephonyManager mTelephonyManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

        // 返回值MCC + MNC
        String operator = mTelephonyManager.getNetworkOperator();
        int mcc = -1;
        int mnc = -1;
        if (operator.length() > 3) {
            mcc = Integer.parseInt(operator.substring(0, 3));
            mnc = Integer.parseInt(operator.substring(3));
        }

        List<CellInfo> cInfos = mTelephonyManager.getAllCellInfo();
        if (cInfos != null) {
            StringBuffer sb1 = new StringBuffer("getAllCellInfo总数 : " + cInfos.size() + "\n");
            for (CellInfo info1 : cInfos) { // 根据邻区总数进行循环
                sb1.append(" LAC : " + info1.toString());
                sb1.append("\n");
            }

            Log.i(TAG, " getAllCellInfo:" + sb1.toString());
        } else {
            Log.i(TAG, " getAllCellInfo: cinfos is null");
        }


        // 中国移动和中国联通获取LAC、CID的方式
        GsmCellLocation location = (GsmCellLocation) mTelephonyManager.getCellLocation();
        if (location != null) {
            int lac = location.getLac();
            int cellId = location.getCid();

            Log.i(TAG, " MCC = " + mcc + "\t MNC = " + mnc + "\t LAC = " + lac + "\t CID = " + cellId);
        } else {
            Log.i(TAG, " 获取邻区基站信息: location is null");
        }

        // 中国电信获取LAC、CID的方式
                /*CdmaCellLocation location1 = (CdmaCellLocation) mTelephonyManager.getCellLocation();
                lac = location1.getNetworkId();
                cellId = location1.getBaseStationId();
                cellId /= 16;*/

        // 获取邻区基站信息
        List<NeighboringCellInfo> infos = mTelephonyManager.getNeighboringCellInfo();
        StringBuffer sb = new StringBuffer("getNeighboringCellInfo总数 : " + infos.size() + "\n");
        for (NeighboringCellInfo info1 : infos) { // 根据邻区总数进行循环
            sb.append(" LAC : " + info1.getLac()); // 取出当前邻区的LAC
            sb.append(" CID : " + info1.getCid()); // 取出当前邻区的CID
            sb.append(" BSSS : " + (-113 + 2 * info1.getRssi()) + "\n"); // 获取邻区基站信号强度
        }

        Log.i(TAG, " getNeighboringCellInfo:" + sb.toString());
    }

    /*private synchronized void registLocationListener() {
        LocationManager locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, REQUEST_LOCATION_MIN_TIME,
                REQUEST_LOCATION_MIN_DISTANCE, mGpsLocationListener, Looper.getMainLooper());
    }

    private synchronized void registMainLocationListener() {
        LocationManager locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, REQUEST_LOCATION_MIN_TIME,
                REQUEST_LOCATION_MIN_DISTANCE, mMainGpsLocationListener, Looper.getMainLooper());
    }*/

    private void getLocationByGps() {
        LocationManager locMan = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Location loc = locMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (loc != null) {
            double lat = loc.getLatitude();
            double lng = loc.getLongitude();
            float accuracy = loc.getAccuracy();
            LatLng sourceLatLng = new LatLng(lat, lng);
            convert(sourceLatLng);
            boolean timeSuitable = false;
            long time = 0;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                time = loc.getElapsedRealtimeNanos();
                timeSuitable = SystemClock.elapsedRealtimeNanos() - time <= SUITABLE_NANOIME;
                Log.d(TAG, "------------------ time is " + time
                        + ", current time is " + SystemClock.elapsedRealtimeNanos());
            } else {
                time = loc.getTime();
                timeSuitable = System.currentTimeMillis() - time <= SUITABLE_MILLIONTIME;
                Log.d(TAG, "------------------ time is " + time
                        + ", current time is " + System.currentTimeMillis());
            }
            Log.d(TAG, "------------------ lat is " + lat + ", lng is " + lng
                    + ", accuracy is " + accuracy + ", timeSuitable is " + timeSuitable);
        } else {
            Log.d(TAG, "------------------ Location return null");
        }
    }

    private void convert(LatLng sourceLatLng) {
        CoordinateConverter converter  = new CoordinateConverter();
        converter.from(CoordinateConverter.CoordType.GPS);
        converter.coord(sourceLatLng);
        LatLng desLatLng = converter.convert();
        Log.d(TAG, "------------------after lat is " + desLatLng.latitude + ", lng is " + desLatLng.longitude
                );
    }

    class NormalRecyclerViewAdapter extends RecyclerView.Adapter<NormalRecyclerViewAdapter.ViewHolder> {

        public NormalRecyclerViewAdapter(Context context) {
        }

        @Override
        public NormalRecyclerViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            ViewHolder holder = new ViewHolder(
                    LayoutInflater.from(GSMCellLocationActivity.this)
                    .inflate(R.layout.recycler_item, null));
            return holder;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            holder.tv.setText("This is item " + position);
        }

        @Override
        public int getItemCount() {
            return 30;
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            TextView tv;
            public ViewHolder(View itemView) {
                super(itemView);
                tv = (TextView) itemView.findViewById(R.id.item);
            }
        }
    }
}
