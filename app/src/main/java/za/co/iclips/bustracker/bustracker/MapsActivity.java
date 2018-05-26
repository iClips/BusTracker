package za.co.iclips.bustracker.bustracker;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Vibrator;

import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
import android.view.animation.LinearInterpolator;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONException;
import org.json.JSONObject;

import io.socket.client.IO;
import io.socket.client.Socket;
import io.socket.emitter.Emitter;

import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.annotation.TargetApi;
import android.os.Build;
import android.util.Property;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import static com.google.android.gms.maps.model.LatLngBounds.*;

public class MapsActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    private List<String> simulatedPath = new ArrayList<>();
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    public ArrayAdapter<String> adapter;
    private LinearLayout admin_entrance;
    Button btnAdminOnly;
    Button btnSubmit;
    Double lat, lng;
    private ImageButton btn_connect;
    private String bus_id;
    public AlphaAnimation buttonClick = new AlphaAnimation(1.0f, 0.8f);
    private final GestureDetector detector = new GestureDetector(new MapsActivity.SwipeGestureDetector());
    public TextView edtBusID;
    public boolean is_bus = false;
    public boolean is_connected = false;
    public TextView loading_text, txtSelectBusOutput;
    private LocationManager locationManager;
    private Context mContext;
    public GoogleMap mMap;
    private ViewFlipper mViewFlipper;
    public ListView selectBusID;
    private Socket socket;
    public TextView textView, txt_connect_btn;
    private LinearLayout track_my_bus_layout;
    private String username;
    public Timer simBusTimer;
    private boolean hasSimTimerStarted = false;
    public Vibrator v = null;
    private RelativeLayout view_flipper_layout;
    // before loop:
    List<Marker> markers = new ArrayList<Marker>();
    private FusedLocationProviderClient mFusedLocationClient;
    RelativeLayout mapsLayout = null;

    Marker simuatedBusMarker;
    int sCount = 0;
    boolean fwd = true;

    //PICKUP POINTS lat,lng,
    static final LatLng MARKER_PICKUP_POINT_FLAMINK_674 =
            new LatLng(-34.018940055845874, 22.477776837893657);
    static final LatLng MARKER_PICKUP_POINT_VALK_672=
            new LatLng(-34.02054649372225, 22.475554650497997);
    static final LatLng MARKER_PICKUP_POINT_VALK_671=
            new LatLng(-34.0206843266813,22.475786996853913);
    static final LatLng MARKER_PICKUP_POINT_FLAMINK_673=
            new LatLng(-34.01845951975393, 22.477742955868507);
    static final LatLng MARKER_PICKUP_POINT_MARKET_554 =
            new LatLng(-33.971390237009516,22.446705340926542);

    private Map<Marker, Map<String, Object>> marker_pickups = new HashMap<>();
    private Map<String, Object> markers_pickups_dm = new HashMap<>();
    ImageButton pickups = null;

    int mIndex, msCount = 0;
    PolylineOptions ploCDDPacs_route;

    /* ==== Life Cycle Methods - Start === */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        //hide the top level action bar
        ActionBar actionBar = getSupportActionBar();
        actionBar.hide();

        //instaite all vieclient here in alphabetical order
        this.btnAdminOnly = (Button) findViewById(R.id.btn_admin_only);
        this.btnSubmit = (Button) findViewById(R.id.btnSubmit);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        this.edtBusID = (TextView) findViewById(R.id.edtBusID);
        this.txt_connect_btn = (TextView) findViewById(R.id.txt_connect_btn);
        this.loading_text = (TextView) findViewById(R.id.loading_text);
        this.btn_connect = (ImageButton) findViewById(R.id.btn_connect);
        this.txtSelectBusOutput = (TextView) findViewById(R.id.txtSelectBusOutput);
        this.selectBusID = (ListView) findViewById(R.id.selectBusID);
        this.admin_entrance = (LinearLayout) findViewById(R.id.admin_entrance);
        this.track_my_bus_layout = (LinearLayout) findViewById(R.id.track_my_bus_layout);
        this.view_flipper_layout = (RelativeLayout) findViewById(R.id.view_flipper_layout);
        pickups = (ImageButton) findViewById(R.id.pickups);
        pickups.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                view.startAnimation(buttonClick);

                MapsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (lat != null && lng != null) {
                            doAnimateCameraToPickUps(true);
                        }
                    }
                });

                return false;
            }
        });
        this.v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        this.username = "";

        showViewFlipper();

        initMap();

        this.mContext = this;
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (this.is_connected) {
            try {
                this.socket.close();
            } catch (Exception ex) {
                setTextViewText(ex.getMessage());
            }
        }
    }

    /* ==== Life Cycle Methods - End === */

    /*=============== Splash Screen - Start ===============*/

    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_in));
                    mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.left_out));
                    mViewFlipper.showNext();
                    return true;
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    mViewFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.right_in));
                    mViewFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.right_out));
                    mViewFlipper.showPrevious();
                    return true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            return false;
        }
    }

    private void showViewFlipper() {
        this.mViewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);

        this.mViewFlipper.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                detector.onTouchEvent(motionEvent);
                return false;
            }
        });

        this.mViewFlipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MapsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        toggleViewFlipperPlay();
                    }
                });
            }
        });
        playViewFlipper();

        MapsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (view_flipper_layout.getVisibility() == View.GONE) {
                    view_flipper_layout.setVisibility(View.VISIBLE);
                }

                loading_text.setText(MapsActivity.this.getApplicationContext().getResources().getString(
                                        R.string.init_app));

                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        MapsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                loading_text.setText("Opening...");
                            }
                        });
                    }
                }, 5000);
            }
        });
    }

    private void toggleViewFlipperPlay() {
        if (this.mViewFlipper.isFlipping()) {
            pauseViewFlipper();
        } else {
            playViewFlipper();
        }
    }
    private void playViewFlipper () {
        this.mViewFlipper.setAutoStart(true);
        this.mViewFlipper.setFlipInterval(4000);
        this.mViewFlipper.startFlipping();
    }

    private void pauseViewFlipper () {
        this.mViewFlipper.stopFlipping();
    }

    /*=============== Splash Screen - End   ===============*/

    /*#$#$#$#$#$#$#$#$#$#$#$#$#$#$#$# Maps - Start #$#$#$#$#$#$#$#$#$#$#$#$#$#$#$#*/

    private void initMap() {
        mapsLayout = (RelativeLayout) findViewById(R.id.mapLayout);

        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationManager = (LocationManager) getApplicationContext()
                .getSystemService(Context.LOCATION_SERVICE);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                10000,
                0, locationListenerGPS);
        isLocationEnabled();
    }

    LocationListener locationListenerGPS = new LocationListener() {
        @Override
        public void onLocationChanged(final Location location) {
            if (location != null) {
                lat = location.getLatitude();
                lng = location.getLongitude();
                for (int i = 0; i < markers.size(); i++) {
                    final int f = i;
                    if (markers.get(i).getTag() == "Global Marker") {
                        // Setting latitude and longitude for the marker
                        markers.get(i).setPosition(new LatLng(lat, lng));
                        final int j = i;
                        break;
                    }
                }

                //Create new bounds object
                final LatLngBounds.Builder builder = new LatLngBounds.Builder();
                for (Marker marker : markers) {
                    LatLng geoCode = new LatLng(
                            marker.getPosition().latitude, marker.getPosition().longitude);
                    builder.include(geoCode);
                }
                Display display = getWindowManager().getDefaultDisplay();
                final int width = display.getWidth();
                final int height = display.getHeight();

                if (is_connected && is_bus) {
                    final JSONObject json = new JSONObject();
                    try {
                        json.put("id", bus_id);
                        json.put("lat", lat);
                        json.put("lng", lng);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    MapsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                socket.emit("broadcast location",
                                        json.toString());
                            } catch (Exception e2) {
                                v.vibrate(2500);
                                MapsActivity.this.setTextViewText(e2.getMessage());
                            }
                        }
                    });
                }
            } else {
                MapsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        setTextViewText("Location unavailable.");
                    }
                });
            }
        }

        @Override
        public void onStatusChanged(String s, int i, Bundle bundle) {

        }

        @Override
        public void onProviderEnabled(String s) {
        }

        @Override
        public void onProviderDisabled(String s) {
//            displayPromptForEnablingGPS(MapsActivity.this);
        }
    };

    private void isLocationEnabled() {

        if(!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            AlertDialog.Builder alertDialog=new AlertDialog.Builder(MapsActivity.this);
            alertDialog.setTitle("Enable Location");
            alertDialog.setMessage("Your locations setting is not enabled. Please enabled it in settings menu.");
            alertDialog.setPositiveButton("Location Settings", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    Intent intent=new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                }
            });
            alertDialog.setNegativeButton("Cancel", new DialogInterface.OnClickListener(){
                public void onClick(DialogInterface dialog, int which){
                    dialog.cancel();
                }
            });
            AlertDialog alert=alertDialog.create();
            try {
                alert.show();
            } catch (Exception ex) {
                setTextViewText(ex.getMessage());
            }
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        if (ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(
                        this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
            MapsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if (view_flipper_layout.getVisibility() == View.VISIBLE) {
                        view_flipper_layout.setVisibility(View.GONE);
                    }
                }
            });
        } else {
            mMap.setMyLocationEnabled(true);
        }
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mFusedLocationClient.getLastLocation()
            .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(final Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        LatLng locs = new LatLng(location.getLatitude(),
                                location.getLongitude());

                        Marker marker = MapsActivity.this.mMap.addMarker(
                                new MarkerOptions().position(locs)
                                .title("Global Marker").draggable(true).visible(false));
                        marker.setTag("Global Marker");
                        markers.add(marker);

                        MapsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                final Builder builder = new LatLngBounds.Builder();
                                for (Marker marker : markers) {
                                    LatLng geoCode = new LatLng(
                                            marker.getPosition().latitude,
                                            marker.getPosition().longitude);
                                    builder.include(geoCode);
                                }
                                // Move camera.
                                zoomMapToLatLngBounds(mapsLayout, mMap, builder.build());
                    }
                        });
                    }
                }
            });

        //set pickup points
        implementPickups();

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);

        //hide splash screen
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                MapsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (view_flipper_layout.getVisibility() == View.VISIBLE) {
                            view_flipper_layout.setVisibility(View.GONE);
                        }

                        //move the camera closer
                        if (lat != null && lng != null) {
                            doAnimateCameraToPickUps(true);
                        }
                    }
                });
            }
        }, 5000);
    }

    @Override
    public boolean onMarkerClick(final Marker marker) {
        if (marker_pickups.containsKey(marker)) {
            Map markers_pickups_dm = (Map) marker_pickups.get(marker);
            final String title = (String) markers_pickups_dm.get("title");

            final String TITLE = (String) markers_pickups_dm.get("title");
            MapsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    setTextViewText(TITLE);
                    v.vibrate(130);
                }
            });
        } else {
            setTextViewText(marker.getTitle());
        }
        return false;
    }

    private void zoomMapToLatLngBounds(final RelativeLayout layout,final GoogleMap mMap, final LatLngBounds bounds){

        ViewTreeObserver vto = layout.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                layout.getViewTreeObserver().removeGlobalOnLayoutListener(this);

            }
        });

    }

    //PICKUPS
    void implementPickups() {
        BitmapDescriptor icon =
                BitmapDescriptorFactory.fromResource(
                        R.drawable.bus_stop);

        //Valk 671
        LatLng locs = new LatLng(MARKER_PICKUP_POINT_VALK_671.latitude,
                MARKER_PICKUP_POINT_VALK_671.longitude);

        Marker marker = MapsActivity.this.mMap.addMarker(
                new MarkerOptions()
                        .position(locs)
                        .title("Valk 671")
                        .icon(icon));
        markers_pickups_dm.put("title", "Valk 671");
        markers_pickups_dm.put("latitude", MARKER_PICKUP_POINT_VALK_671.latitude);
        markers_pickups_dm.put("longitude", MARKER_PICKUP_POINT_VALK_671.longitude);
        marker_pickups.put(marker, markers_pickups_dm);

        //Valk 672
        locs = new LatLng(MARKER_PICKUP_POINT_VALK_672.latitude,
                MARKER_PICKUP_POINT_VALK_672.longitude);

        marker = MapsActivity.this.mMap.addMarker(
                new MarkerOptions()
                        .position(locs)
                        .title("Valk 672")
                        .icon(icon));
        markers_pickups_dm.put("title", "Valk 672");
        markers_pickups_dm.put("latitude", MARKER_PICKUP_POINT_VALK_672.latitude);
        markers_pickups_dm.put("longitude", MARKER_PICKUP_POINT_VALK_672.longitude);
        marker_pickups.put(marker, markers_pickups_dm);

        //Flamink 673
        locs = new LatLng(MARKER_PICKUP_POINT_FLAMINK_673.latitude,
                MARKER_PICKUP_POINT_FLAMINK_673.longitude);

        marker = MapsActivity.this.mMap.addMarker(
                new MarkerOptions()
                        .position(locs)
                        .title("Flamink 673")
                        .icon(icon));
        markers_pickups_dm.put("title", "Flamink673");
        markers_pickups_dm.put("latitude", MARKER_PICKUP_POINT_FLAMINK_673.latitude);
        markers_pickups_dm.put("longitude", MARKER_PICKUP_POINT_FLAMINK_673.longitude);
        marker_pickups.put(marker, markers_pickups_dm);

        //674
        locs = new LatLng(MARKER_PICKUP_POINT_FLAMINK_674.latitude,
                MARKER_PICKUP_POINT_FLAMINK_674.longitude);

        marker = MapsActivity.this.mMap.addMarker(
                new MarkerOptions()
                        .position(locs)
                        .title("Flamink 674")
                        .icon(icon));
        markers_pickups_dm.put("title", "Flamink 674");
        markers_pickups_dm.put("latitude", MARKER_PICKUP_POINT_FLAMINK_674.latitude);
        markers_pickups_dm.put("longitude", MARKER_PICKUP_POINT_FLAMINK_674.longitude);
        marker_pickups.put(marker, markers_pickups_dm);

        //MARKER_PICKUP_POINT_MARKET_554
        locs = new LatLng(MARKER_PICKUP_POINT_MARKET_554.latitude,
                MARKER_PICKUP_POINT_MARKET_554.longitude);

        marker = MapsActivity.this.mMap.addMarker(
                new MarkerOptions()
                        .position(locs)
                        .title("MARKET 554")
                        .icon(icon));
        markers_pickups_dm.put("title", "MARKET_554");
        markers_pickups_dm.put("latitude", MARKER_PICKUP_POINT_MARKET_554.latitude);
        markers_pickups_dm.put("longitude", MARKER_PICKUP_POINT_MARKET_554.longitude);
        marker_pickups.put(marker, markers_pickups_dm);

        //go to the closest pickups witht the camera
        //doAnimateCameraToPickUps();
    }

    public void animatePickups(final View view) {
        view.startAnimation(buttonClick);

        if (lat != null && lng != null) {
            doAnimateCameraToPickUps(false);
        }
    }

    void doAnimateCameraToPickUps(boolean bln_all) {
        //find the device's marker location

        boolean blnAtLeastOne = false;
        //only bound around close by pickups
        final LatLngBounds.Builder builder = new LatLngBounds.Builder();

        float[] results = new float[1];
        Location.distanceBetween(MARKER_PICKUP_POINT_VALK_672.latitude,
                MARKER_PICKUP_POINT_VALK_672.longitude,
                lat, lng, results);

        if (bln_all) {
            blnAtLeastOne = true;
            builder.include(new LatLng(
                    MARKER_PICKUP_POINT_VALK_672.latitude,
                    MARKER_PICKUP_POINT_VALK_672.longitude));
        } else if (results[0] < 1000)  {
            blnAtLeastOne = true;
            builder.include(new LatLng(
                    MARKER_PICKUP_POINT_VALK_672.latitude,
                    MARKER_PICKUP_POINT_VALK_672.longitude));
        }

        Location.distanceBetween(MARKER_PICKUP_POINT_VALK_671.latitude,
                MARKER_PICKUP_POINT_VALK_671.longitude,
                lat, lng, results);
        if (bln_all) {
            blnAtLeastOne = true;
            builder.include(new LatLng(
                    MARKER_PICKUP_POINT_VALK_671.latitude,
                    MARKER_PICKUP_POINT_VALK_671.longitude));
        } else
        if (results[0] < 1000) {
            blnAtLeastOne = true;
            builder.include(new LatLng(
                    MARKER_PICKUP_POINT_VALK_671.latitude,
                    MARKER_PICKUP_POINT_VALK_671.longitude));
        }

        Location.distanceBetween(MARKER_PICKUP_POINT_FLAMINK_673.latitude,
                MARKER_PICKUP_POINT_FLAMINK_673.longitude,
                lat, lng, results);
        if (bln_all) {
            blnAtLeastOne = true;
            builder.include(new LatLng(
                    MARKER_PICKUP_POINT_FLAMINK_673.latitude,
                    MARKER_PICKUP_POINT_FLAMINK_673.longitude));
        } else if (results[0] < 1000) {
            blnAtLeastOne = true;
            builder.include(new LatLng(
                    MARKER_PICKUP_POINT_FLAMINK_673.latitude,
                    MARKER_PICKUP_POINT_FLAMINK_673.longitude));
        }

        Location.distanceBetween(MARKER_PICKUP_POINT_FLAMINK_674.latitude,
                MARKER_PICKUP_POINT_FLAMINK_674.longitude,
                lat, lng, results);
        if (bln_all) {
            blnAtLeastOne = true;
            builder.include(new LatLng(
                    MARKER_PICKUP_POINT_FLAMINK_674.latitude,
                    MARKER_PICKUP_POINT_FLAMINK_674.longitude));
        } else if (results[0] < 1000) {
            blnAtLeastOne = true;
            builder.include(new LatLng(
                    MARKER_PICKUP_POINT_FLAMINK_674.latitude,
                    MARKER_PICKUP_POINT_FLAMINK_674.longitude));
        }

        Location.distanceBetween(MARKER_PICKUP_POINT_MARKET_554.latitude,
                MARKER_PICKUP_POINT_MARKET_554.longitude,
                lat, lng, results);
        if (bln_all) {
            blnAtLeastOne = true;
            builder.include(new LatLng(
                    MARKER_PICKUP_POINT_MARKET_554.latitude,
                    MARKER_PICKUP_POINT_MARKET_554.longitude));
        } else if (results[0] < 1000) {
            blnAtLeastOne = true;
            builder.include(new LatLng(
                    MARKER_PICKUP_POINT_MARKET_554.latitude,
                    MARKER_PICKUP_POINT_MARKET_554.longitude));
        }

        //include local location
        if (lat != null && lng != null) {
            builder.include(new LatLng(lat,lng));
        }
        if (blnAtLeastOne) {
            if (!bln_all) {
                setTextViewText("Showing bus stops near you...");
            } else {
                setTextViewText("Showing all bus stops...");
            }

            new Timer().schedule(new TimerTask() {
                @Override
                public void run() {
                    MapsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            LatLngBounds bounds = builder.build();

                            int padding = 30; // offset from edges of the map in pixels

                            // Move camera.
                            Display display = getWindowManager().getDefaultDisplay();
                            int width = display.getWidth();
                            int height = display.getHeight();
                            mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                                    bounds, width, height,padding));
                        }
                    });
                }
            }, 2000);

//                String adHoc = "";
//                String[] parts = adHoc.split("\n");
//                int c = 1;
//                adHoc= "";
//                for (String line: parts
//                     ) {
//                    if (c % 4 == 0) {
//                        adHoc += line + "\n";
//                    }
//                    c++;
//                }
//                writeToFile(adHoc);

        } else {
            setTextViewText(
                    "No bus stops found near you within a 1 kilometer radius");
        }
    }

    public void writeToFile(String data)
    {
        // Get the directory for the user's public pictures directory.
        final File path =
                Environment.getExternalStoragePublicDirectory
                        (
                                //Environment.DIRECTORY_PICTURES
                                Environment.DIRECTORY_DCIM + "/Bus Tracker/"
                        );

        // Make sure the path directory exists.
        if(!path.exists())
        {
            // Make it, if it doesn't exit
            path.mkdirs();
        }

        final File file = new File(path, "gps_polyline.txt");

        // Save your stream, don't forget to flush() it before closing it.

        try
        {
            file.createNewFile();
            FileOutputStream fOut = new FileOutputStream(file);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(data);

            myOutWriter.close();

            fOut.flush();
            fOut.close();

            setTextViewText("file is created with the coordinates. path is ");
        }
        catch (IOException e)
        {
            Log.e("Exception", "File write failed: " + e.toString());
        }
    }

    //BUSSES
    private void listSimulatedBusses() {
        List<String> list = new ArrayList<>();
        list.add("Simulated - New Dawn Park CBD");

        if (adapter == null) {
            adapter = new ArrayAdapter<String>(MapsActivity.this,
                    android.R.layout.simple_spinner_dropdown_item, list);
        }
        if (list.size() > 0) {
            MapsActivity.this.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    selectBusID.setAdapter(adapter);

                    selectBusID.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> adapterView,
                                                View view, final int i, long l) {
                            MapsActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if (i == 0) {
                                        setSimulatedPath();
                                        //animateSimBusOne();
                                    }
                                }
                            });
                        }
                    });
                }
            });


            MapsActivity.this.v.vibrate(100);
        }
    }

    private void setSimulatedPath() {
        if (hasSimTimerStarted) {
            setTextViewText("The simulated bus is being tracked.");
            return;
        }

        //inistantiate a instance cbd pacs polyline
        new Thread(new Runnable() {
            public void run(){
                setTextViewText("Building a route for Simulated CDB Pacalstdorp...");

                ploCDDPacs_route = Polylines.getPolylineCBDPacaltsdorp();

                startSBTomerSchedule(ploCDDPacs_route);
            }

            private void startSBTomerSchedule(final PolylineOptions ploCDDPacs_route) {
                setTextViewText("Starting bus route for ...");

                simBusTimer = new Timer();
                int time = 500;

                simBusTimer.schedule(new TimerTask() {
                    @Override
                    public void run() {

                        if (sCount == ploCDDPacs_route.getPoints().size() - 1) {
                            sCount = 0;
                            simBusTimer.cancel();
                            return;
                        }

                        MapsActivity.this.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                //BUS ONE
                                LatLng locs = ploCDDPacs_route.getPoints().get(sCount);
                                boolean blnFound = false;
                                for (int i = 0; i < markers.size(); i++) {
                                    if (markers.get(i).getTag() == "Bus One") {
                                        // Setting latitude and longitude for the marker
                                        markers.get(i).setPosition(locs);

                                        blnFound = true;
                                        break;
                                    }
                                }

                                if (!blnFound) {
                                    BitmapDescriptor icon =
                                            BitmapDescriptorFactory.fromResource(
                                                    R.drawable.bus_marker);
                                    simuatedBusMarker = MapsActivity.this.mMap.addMarker(
                                            new MarkerOptions().position(locs)
                                                    .title("Bus One")
                                                    .icon(icon));
                                    simuatedBusMarker.setTag("Bus One");
                                    markers.add(simuatedBusMarker);
                                }

                                final Builder builder = new LatLngBounds.Builder();
                                for (Marker marker : markers) {
                                    LatLng geoCode = new LatLng(
                                            marker.getPosition().latitude,
                                            marker.getPosition().longitude);
                                    builder.include(geoCode);
                                }
                                // Move camera.
                                Display display = getWindowManager().getDefaultDisplay();
                                int width = display.getWidth();
                                int height = display.getHeight();
                                mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                                        builder.build(), width, height,40));

                                //get the distance between local location and remote location
                                float[] results = new float[1];
                                LatLng llb = null;
                                for (int i = 0; i < markers.size(); i++) {
                                    //current location
                                    if (markers.get(i).getTag() == "Global Marker") {

                                        llb = new LatLng(markers.get(i).getPosition().latitude,
                                                markers.get(i).getPosition().longitude);
                                    }
                                }
                                for (int i = 0; i < markers.size(); i++) {
                                    if (markers.get(i).getTag() != "Global Marker") {
                                        if (locs != null && llb != null) {
                                            Location.distanceBetween(locs.latitude,
                                                    locs.longitude,
                                                    llb.latitude, llb.longitude, results);

                                            if (results[0] < 1000 && results[0] % 10 == 0) {
                                                setTextViewText(markers.get(i).getTitle() + " is "
                                                        + String.valueOf((int)results[0])
                                                        + " meters from you.");

                                                if (results[0] < 130 && results[0] % 10 == 0) {
                                                    v.vibrate(25);
                                                }
                                            } else if (results[0] < 50) {
                                                setTextViewText(markers.get(i).getTitle() + " is "
                                                        + String.valueOf((int)results[0])
                                                        + " meters from you.");

                                            }
                                        } else {
                                            if (locs == null) {
                                                setTextViewText("locs is null");
                                            }
                                            if (llb == null) {
                                                setTextViewText("llb is null");
                                            }
                                        }
                                    }
                                }
                            }
                        });

                        sCount++;
                    }
                },1000, time);
                hasSimTimerStarted = true;
            }
        }).start();
    }

    /*#$#$#$#$#$#$#$#$#$#$#$#$#$#$#$# Maps - End #$#$#$#$#$#$#$#$#$#$#$#$#$#$#$#*/

    /*============= Views Events - Start ==================*/
    public void adminOnly (View view){
        view.startAnimation(buttonClick);

        if (admin_entrance.getVisibility() == View.VISIBLE) {
            hideAdminOnly();
        } else {
            showAdminOnly();
        }
    }

    public void adminSubmit (View view){
        view.startAnimation(this.buttonClick);

        if (this.is_connected) {
            this.bus_id = String.valueOf(this.edtBusID.getText());
            if (this.bus_id.contains("|")) {
                setTextViewText(MapsActivity.this.getResources().getString(
                    R.string.restricted_characters));
            } else if (this.bus_id.length() == 0) {
                setTextViewText(MapsActivity.this.getResources().getString(
                        R.string.admin_submit_error));
                this.v.vibrate(500);
            } else {
                try {
                    this.socket.emit("set bus id", this.bus_id);
                } catch (Exception e) {
                    setTextViewText(e.getMessage());
                }
                return;
            }
        }
        setTextViewText(MapsActivity.this.getResources().getString(R.string.not_connected_yet));
        this.v.vibrate(500);
    }

    //track bus
    public void trackBus (View view){
        view.startAnimation(buttonClick);

        if (track_my_bus_layout.getVisibility() == View.VISIBLE) {
            hideTrackMyBus();
        } else {
            showTrackMyBus();

            if (is_connected) {
                if (track_my_bus_layout.getVisibility() == View.GONE) {
                    showTrackMyBus();
                }

                try {
                    this.socket.emit("get bus list", "");
                } catch (Exception e) {
                    setTextViewText(e.getMessage());
                }
            } else {
                setTextViewText(getResources().getString(R.string.not_connected_yet));
                v.vibrate(500);
            }
        }
    }

    /*============= Views onClick - End ====================*/

    /*============= SOCKET - Starts =============*/
    public void connect(View view) {
        view.startAnimation(buttonClick);
        if (!is_connected) {
            txt_connect_btn.setText(getResources().getString(R.string.connecting));
            connectToServer();
        } else {
            setTextViewText(this.getResources().getString(R.string.connected));
        }
    }

    public void connectToServer() {
        try {
            IO.Options opts = new IO.Options();
            opts.timeout = 10000;
            opts.reconnection = false;
            socket = IO.socket(getResources().getString(R.string.server_url), opts);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        socket.on(Socket.EVENT_CONNECT, new Emitter.Listener() {
            public void call(Object... args) {
                MapsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //set the connected icon
                        btn_connect.setImageResource(R.drawable.connected_white);

                        MapsActivity.this.txt_connect_btn.setText(
                                MapsActivity.this.getResources().getString(R.string.connected));
                    }
                });

                MapsActivity.this.is_connected = true;
                try {
                    if (MapsActivity.this.username.equals("")) {
                        int value = new Random().nextInt(50);
                        MapsActivity.this.username = value + "_" + new SimpleDateFormat(
                                "HHmmss").format(Calendar.getInstance().getTime());
                    }
                    MapsActivity.this.socket.emit("add user",
                            MapsActivity.this.username);
                } catch (Exception e) {
                    MapsActivity.this.setTextViewText(e.getMessage());
                    MapsActivity.this.socket.disconnect();
                }
                MapsActivity.this.v.vibrate(100);
            }
        }).on("user added", new Emitter.Listener() {

            public void call(Object... args) {
                final String msg = String.valueOf(args[0]);
                MapsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MapsActivity.this.view_flipper_layout.setVisibility(View.GONE);
                        MapsActivity.this.setTextViewText(msg);
                    }
                });
                MapsActivity.this.v.vibrate(100);
            }
        }).on("set bus list", new Emitter.Listener() {
            public void call(Object... args) {
                final String[] list1 = String.valueOf(args[0]).split("\\|");
                final List<String> list = new ArrayList<>();
                list.add("Simulated Path - New Dawn Park CBD");
                for (String item: list1) {
                    list.add(item);
                }

                if (list.size() > 0) {
                    MapsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.clear();

                            for (String item: list) {
                                adapter.insert(item, adapter.getCount());
                            }

                            adapter.notifyDataSetChanged();
                        }
                    });

                    MapsActivity.this.v.vibrate(50);
                }
            };

        }).on("message", new Emitter.Listener() {
            public void call(Object... args) {
                final String message = String.valueOf(args[0]);
                MapsActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MapsActivity.this.getApplicationContext(), message,
                                Toast.LENGTH_LONG).show();
                        MapsActivity.this.v.vibrate(100);

                        if (message.contains("currently no busses")) {
                            txtSelectBusOutput.setText(message);
                        }
                    }
                });
            }
        }).on("bus id enabled", new Emitter.Listener() {
            public void call(Object... args) {
                final String message = String.valueOf(args[0]);
                is_bus = true;
                runOnUiThread(new Runnable() {
                    public void run() {
                        setTextViewText(message);
                        hideAdminOnly();
                        btnSubmit.setText("Bus Tracking Enabled");
                        btnSubmit.setEnabled(false);
                        v.vibrate(100);
                    }
                });
            }
        }).on("bus location updated", new Emitter.Listener() {
            public void call(Object... args) {
                MapsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        btnAdminOnly.startAnimation(buttonClick);
                    }
                });
            }
        }).on("set bus location", new Emitter.Listener() {
            public void call(Object... args) {
                setTextViewText(String.valueOf(args[0]));
                v.vibrate(25);

                //ID|LAT|LNG
                final String[] loc = String.valueOf(args[0]).split("\\|");

                boolean have_marker = false;
                for (int i = 0; i < markers.size(); i++) {
                    if (markers.get(i).getTag() == loc[0]) {
                        // Setting latitude and longitude for the marker
                        markers.get(i).setPosition(
                                new LatLng(Double.parseDouble(loc[1]),
                                        Double.parseDouble(loc[2])));
                        have_marker = true;
                        setTextViewText("updating location of " + loc[0]);

                        break;
                    }
                }

                //if the ID is new add a marker to the map
                if (!have_marker) {
                    //cancel the simulated Bus One
//                    simBusTimer.cancel();
//                    hasSimTimerStarted = false;
//                    for (int i = 0; i < markers.size(); i++) {
//                        if (markers.get(i).getTitle() == "Bus One") {
//                            // Setting latitude and longitude for the marker
//                            markers.remove(i);
//                            simuatedBusMarker.setVisible(false);
//                            setTextViewText("simulated bus tracking disabled");
//                            break;
//                        }
//                    }

                    MapsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            BitmapDescriptor icon =
                                    BitmapDescriptorFactory.fromResource(
                                            R.drawable.bus_marker);

                            Marker mark = MapsActivity.this.mMap.addMarker(
                                    new MarkerOptions().position(
                                    new LatLng(Double.parseDouble(loc[1]),
                                            Double.parseDouble(loc[2])))
                                    .title(loc[0])
                                    .icon(icon)
                            );
                            mark.setTag(String.valueOf(loc[0]));
                            markers.add(mark);
                        }
                    });
                    setTextViewText(loc[0] + " bus marker is now visible on the map.");
                }

                MapsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Builder builder = new Builder();
                        for (Marker marker : markers) {
                            builder.include(marker.getPosition());
                        }

                        Display display = getWindowManager().getDefaultDisplay();
                        int width = display.getWidth();
                        int height = display.getHeight();
                        mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                                builder.build(), width, height,20));
                    }
                });
            }
        }).on(Socket.EVENT_DISCONNECT, new Emitter.Listener() {
            public void call(Object... args) {
                MapsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //set the connect icon
                        btn_connect.setImageResource(R.drawable.connect_white);
                        txt_connect_btn.setText(
                                MapsActivity.this.getResources().getString(R.string.connect));

                        is_connected = false;
                    }
                });
                setTextViewText(getString(R.string.disconnected));
            }
        }).on(Socket.EVENT_CONNECT_TIMEOUT, new Emitter.Listener() {
            public void call(Object... args) {
                MapsActivity.this.setTextViewText(
                        MapsActivity.this.getResources().getString(R.string.disconnected_timeout));
            }
        });

        socket.connect();
    }

    /*============= SOCKET - End ===============*/

    /*============= View Helpers - Start ===========*/

    public void hideSelectBusWindow(View v) {
        v.startAnimation(buttonClick);

        hideTrackMyBus();
    }

    public void showTrackMyBus () {
        this.track_my_bus_layout.setVisibility(View.VISIBLE);

        listSimulatedBusses();
    }

    public void hideTrackMyBus () {
        this.track_my_bus_layout.setVisibility(View.GONE);
    }

    public void showAdminOnly () {
        this.admin_entrance.setVisibility(View.VISIBLE);
    }

    public void hideAdminOnlyWindow(View v) {
        v.startAnimation(buttonClick);

        hideAdminOnly();
    }

    public void hideAdminOnly () {
        this.admin_entrance.setVisibility(View.GONE);
    }

    public void setTextViewText ( final String str){
        System.out.println(str);
        runOnUiThread(new Runnable() {
            public void run() {
                if (MapsActivity.this.textView != null) {
                    MapsActivity.this.textView.setText(str);
                }
                Toast.makeText(MapsActivity.this.getApplicationContext(),
                        str, Toast.LENGTH_LONG).show();
            }
        });
    }

    /*============= View Helpers - End ===========*/
}