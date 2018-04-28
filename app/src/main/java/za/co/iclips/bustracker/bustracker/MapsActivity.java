package za.co.iclips.bustracker.bustracker;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.os.Vibrator;

import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationUtils;
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
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.tasks.OnSuccessListener;

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
    public TextView textView;
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
        this.loading_text = (TextView) findViewById(R.id.loading_text);
        this.btn_connect = (ImageButton) findViewById(R.id.btn_connect);
        this.txtSelectBusOutput = (TextView) findViewById(R.id.txtSelectBusOutput);
        this.selectBusID = (ListView) findViewById(R.id.selectBusID);
        this.admin_entrance = (LinearLayout) findViewById(R.id.admin_entrance);
        this.track_my_bus_layout = (LinearLayout) findViewById(R.id.track_my_bus_layout);
        this.view_flipper_layout = (RelativeLayout) findViewById(R.id.view_flipper_layout);
        this.v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        this.username = "";

        showViewFlipper();

        initMap();

        this.mContext = this;
    }

    /* ==== Life Cycle Methods - Start === */

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

                MapsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        loading_text.setText(
                                MapsActivity.this.getApplicationContext().getResources().getString(
                                        R.string.init_app));
                    }
                });
                new Timer().schedule(new TimerTask() {
                    @Override
                    public void run() {
                        new Timer().schedule(new TimerTask() {
                            @Override
                            public void run() {
                                MapsActivity.this.runOnUiThread(new Runnable() {
                                    @Override
                                    public void run() {
                                        loading_text.setText("");
                                    }
                                });
                            }
                        }, 3000);
                    }
                }, 7450);
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

    /*=============== Maps - Start ===============*/

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

//                mMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
//                    @Override
//                    public void onMapLoaded() {
//                        //Add markers here
//                        MapsActivity.this.mMap.animateCamera(
//                                CameraUpdateFactory.newLatLngBounds(
//                                        builder.build(), width, height,20));
//                        mMap.setOnMapLoadedCallback(null);
//                    }
//                });

                if (MapsActivity.this.is_connected && MapsActivity.this.is_bus) {
                    final JSONObject json = new JSONObject();
                    try {
                        json.put("id", MapsActivity.this.bus_id);
                        json.put("lat", MapsActivity.this.lat);
                        json.put("lng", MapsActivity.this.lng);
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
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setTiltGesturesEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);
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

                                setTextViewText("Last location activated.");
                            }
                        });
                    }
                }
            });

        //set pickup points
        implementPickups();

        // Set a listener for marker click.
        mMap.setOnMarkerClickListener(this);

        //open the layout
        MapsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                MapsActivity.this.view_flipper_layout.setVisibility(View.GONE);
            }
        });
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

    public static void displayPromptForEnablingGPS ( final Activity activity){

        final AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        final String action = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
        final String message = activity.getString(R.string.notify_location_importance);

        builder.setMessage(message)
                .setPositiveButton("Yes",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                activity.startActivity(new Intent(action));
                                d.dismiss();
                            }
                        })
                .setNegativeButton("No",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface d, int id) {
                                d.dismiss();
                            }
                        });
        builder.create().show();
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

    public void animatePickups(View view) {
        view.startAnimation(buttonClick);

        doAnimateCameraToPickUps();
    }

    private void doAnimateCameraToPickUps() {
        MapsActivity.this.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                //find the device's marker location
                if (lat != null && lng != null) {
                    //only bound around close by pickups
                    LatLngBounds.Builder builder = new LatLngBounds.Builder();

                    float[] results = new float[1];
                    Location.distanceBetween(MARKER_PICKUP_POINT_VALK_672.latitude,
                            MARKER_PICKUP_POINT_VALK_672.longitude,
                            lat, lng, results);
                    if (results[0] < 1000) {
                        builder.include(new LatLng(
                                MARKER_PICKUP_POINT_VALK_672.latitude,
                                MARKER_PICKUP_POINT_VALK_672.longitude));
                    }

                    Location.distanceBetween(MARKER_PICKUP_POINT_VALK_671.latitude,
                            MARKER_PICKUP_POINT_VALK_671.longitude,
                            lat, lng, results);
                    if (results[0] < 1000) {
                        builder.include(new LatLng(
                                MARKER_PICKUP_POINT_VALK_671.latitude,
                                MARKER_PICKUP_POINT_VALK_671.longitude));
                    }

                    Location.distanceBetween(MARKER_PICKUP_POINT_FLAMINK_673.latitude,
                            MARKER_PICKUP_POINT_FLAMINK_673.longitude,
                            lat, lng, results);
                    if (results[0] < 1000) {
                        builder.include(new LatLng(
                                MARKER_PICKUP_POINT_FLAMINK_673.latitude,
                                MARKER_PICKUP_POINT_FLAMINK_673.longitude));
                    }

                    Location.distanceBetween(MARKER_PICKUP_POINT_FLAMINK_674.latitude,
                            MARKER_PICKUP_POINT_FLAMINK_674.longitude,
                            lat, lng, results);
                    if (results[0] < 1000) {
                        builder.include(new LatLng(
                                MARKER_PICKUP_POINT_FLAMINK_674.latitude,
                                MARKER_PICKUP_POINT_FLAMINK_674.longitude));
                    }

                    Location.distanceBetween(MARKER_PICKUP_POINT_MARKET_554.latitude,
                            MARKER_PICKUP_POINT_MARKET_554.longitude,
                            lat, lng, results);
                    if (results[0] < 1000) {
                        builder.include(new LatLng(
                                MARKER_PICKUP_POINT_MARKET_554.latitude,
                                MARKER_PICKUP_POINT_MARKET_554.longitude));
                    }

                    LatLngBounds bounds = builder.build();

                    int padding = 30; // offset from edges of the map in pixels

                    // Move camera.
                    Display display = getWindowManager().getDefaultDisplay();
                    int width = display.getWidth();
                    int height = display.getHeight();
                    mMap.animateCamera(CameraUpdateFactory.newLatLngBounds(
                            bounds, width, height,padding));

                    setTextViewText("Moving camera over pickups closest to you.");        
                } else {
                    setTextViewText("Switch on your location to enable this feature.");
                }
            }
        });
    }

    //BUSSES
    private void listSimulatedBusses() {
        List<String> list = new ArrayList<>();
        list.add("Simulated Path - New Dawn Park CBD");

        adapter = new ArrayAdapter<String>(MapsActivity.this,
                android.R.layout.simple_spinner_dropdown_item, list);
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
                                    }

                                    txtSelectBusOutput.setText(
                                "Showing the location of this bus on the map.");
                                    new Timer().schedule(new TimerTask() {
                                        @Override
                                        public void run() {
                                            MapsActivity.this.runOnUiThread(new Runnable() {
                                                @Override
                                                public void run() {
                                                    txtSelectBusOutput.setText("");
                                                }
                                            });
                                        }
                                    }, 5000);
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
        simulatedPath.add("-33.95790007595538,22.45765500149969");
        simulatedPath.add("-33.95871878131521,22.456789989092385");
        simulatedPath.add("-33.96103247125933,22.45502375445608");
        simulatedPath.add("-33.966442700576934,22.450854260542428");
        simulatedPath.add("-33.971852585766754,22.4465989359403");
        simulatedPath.add("-33.97701301076217,22.442300695993936");
        simulatedPath.add("-33.98231546602098,22.44100653014425");
        simulatedPath.add("-33.99200085466251,22.443146836703136");
        simulatedPath.add("-33.99797838390587,22.445279192869975");
        simulatedPath.add("-34.01412984099146,22.450894397442653");
        simulatedPath.add("-34.01501914917223,22.450966817086055");
        simulatedPath.add("-34.016725487621645,22.459176723604514");
        simulatedPath.add("-34.016940023050296,22.461288627927615");
        simulatedPath.add("-34.017011165692566,22.461641003136947");
        simulatedPath.add("-34.0171267723591,22.461970244293525");
        simulatedPath.add("-34.01733130684497,22.462460417990997");
        simulatedPath.add("-34.01759809021638,22.463336829786613");
        simulatedPath.add("-34.0177403743383,22.463978883569553");
        simulatedPath.add("-34.0178293017934,22.4644063606313");
        simulatedPath.add("-34.01800715642411,22.46543297613175");
        simulatedPath.add("-34.017953800074025,22.466182318275287");
        simulatedPath.add("-34.0179004436904,22.466652710681274");
        simulatedPath.add("-34.01781151630983,22.467799019759013");
        simulatedPath.add("-34.01798937097781,22.46878439629586");
        simulatedPath.add("-34.01814054715259,22.469478417878463");
        simulatedPath.add("-34.0183717572521,22.470281404202296");
        simulatedPath.add("-34.01855850341118,22.4708268984607");
        simulatedPath.add("-34.019172095041235,22.471136358325793");
        simulatedPath.add("-34.01979457477132,22.470973749404266");
        simulatedPath.add("-34.01984792996434,22.471830379908397");
        simulatedPath.add("-34.01970564937509,22.47240806067498");
        simulatedPath.add("-34.01932326910968,22.472985741441562");
        simulatedPath.add("-34.01920766543451,22.473541964536025");
        simulatedPath.add("-34.01936773201973,22.474173289482906");
        simulatedPath.add("-34.019714541918894,22.474804614429786");
        simulatedPath.add("-34.02018584540801,22.47506043011458");
        simulatedPath.add("-34.02052375950641,22.47525187278302");
        simulatedPath.add("-34.02060379159582,22.475518417303874");
        simulatedPath.add("-34.02066603872432,22.47573131764443");
        simulatedPath.add("-34.02068382360978,22.47586911613257");
        simulatedPath.add("-34.02066603872432,22.476124931817367");
        simulatedPath.add("-34.02047480792917,22.476577274501324");
        simulatedPath.add("-34.02047837398308,22.476579096502178");
        simulatedPath.add("-34.02041168048192,22.476712368762605");
        simulatedPath.add("-34.020344986928365,22.476845641023033");
        simulatedPath.add("-34.0203005245302,22.476925269103162");
        simulatedPath.add("-34.02027829332239,22.47701562601935");
        simulatedPath.add("-34.020242723377784,22.4771167117716");
        simulatedPath.add("-34.02019826092606,22.47718024659764");
        simulatedPath.add("-34.02010488970163,22.47723305258762");
        simulatedPath.add("-34.019935931986815,22.47730731624972");
        simulatedPath.add("-34.019731403776795,22.47740840200197");
        simulatedPath.add("-34.01949575109769,22.47747730124604");
        simulatedPath.add("-34.01930011441427,22.47757838699829");
        simulatedPath.add("-34.01906001423198,22.47765265066039");
        simulatedPath.add("-34.01885993022793,22.477753736412637");
        simulatedPath.add("-34.01866429207909,22.477822635656707");
        simulatedPath.add("-34.01842419009831,22.47781643304836");
        simulatedPath.add("-34.01794398409936,22.477778043931835");
        simulatedPath.add("-34.017139188324585,22.47772892597925");
        simulatedPath.add("-34.0163788493955,22.47755642641198");
        simulatedPath.add("-34.01605870465112,22.477201536631696");
        simulatedPath.add("-34.015911971239824,22.477029037064426");
        simulatedPath.add("-34.01587639946554,22.476695604956262");
        simulatedPath.add("-34.01585861357282,22.476372901684158");
        simulatedPath.add("-34.01583638120168,22.475964367723577");
        simulatedPath.add("-34.01586518045307,22.475792784716532");
        simulatedPath.add("-34.01597634221286,22.475507632370636");
        simulatedPath.add("-34.016123075512915,22.475136649336264");
        simulatedPath.add("-34.016145307808976,22.4750553448755");
        simulatedPath.add("-34.01618977238364,22.474931125070498");
        simulatedPath.add("-34.01625646920197,22.47472643899505");
        simulatedPath.add("-34.0163987555726,22.474397533114598");
        simulatedPath.add("-34.01646989866847,22.47418211820309");
        simulatedPath.add("-34.01655882745449,22.47400425421779");
        simulatedPath.add("-34.01661663111543,22.47378883930628");
        simulatedPath.add("-34.016652202579536,22.473503686960385");
        simulatedPath.add("-34.01669222045882,22.473272178794787");
        simulatedPath.add("-34.01671889903454,22.47300311970298");
        simulatedPath.add("-34.01676780973491,22.472755518283293");
        simulatedPath.add("-34.01681227398352,22.472540103371784");
        simulatedPath.add("-34.01685672820884,22.472287137534067");
        simulatedPath.add("-34.01690564882979,22.47202880727832");
        simulatedPath.add("-34.016932327338445,22.47187240096514");
        simulatedPath.add("-34.016981237915836,22.471635528381512");
        simulatedPath.add("-34.01700346998716,22.471436206724093");
        simulatedPath.add("-34.01706571975589,22.471183240886376");
        simulatedPath.add("-34.01708350539572,22.470946368302748");
        simulatedPath.add("-34.017132415886,22.470645122702763");
        simulatedPath.add("-34.01717687994364,22.470440436627314");
        simulatedPath.add("-34.01719911196372,22.470241114969895");
        simulatedPath.add("-34.01724802238741,22.470052522148535");
        simulatedPath.add("-34.017399199882455,22.469863929327175");
        simulatedPath.add("-34.017505913246254,22.46978262486641");
        simulatedPath.add("-34.017697107687624,22.469690591569588");
        simulatedPath.add("-34.01796833627266,22.469598558272764");
        simulatedPath.add("-34.01814174425812,22.46953334706609");
        simulatedPath.add("-34.01811951248491,22.46934475424473");
        simulatedPath.add("-34.018026138973845,22.46889330493991");
        simulatedPath.add("-34.01795499718218,22.468629610266134");
        simulatedPath.add("-34.01789274806557,22.46834982233827");
        simulatedPath.add("-34.01784828438291,22.46817195835297");
        simulatedPath.add("-34.01779048156062,22.467902899261162");
        simulatedPath.add("-34.01779492793296,22.467639204587385");
        simulatedPath.add("-34.01781715979117,22.46721650131528");
        simulatedPath.add("-34.0178571771213,22.466999162461207");
        simulatedPath.add("-34.01786606985877,22.46676765429561");
        simulatedPath.add("-34.01791942626401,22.466412764515326");
        simulatedPath.add("-34.01792387262959,22.46610079007928");
        simulatedPath.add("-34.01795055081822,22.465890739585802");
        simulatedPath.add("-34.01796833627266,22.465594858403847");
        simulatedPath.add("-34.01800390717036,22.46530434163992");
        simulatedPath.add("-34.017972782635695,22.465046011384175");
        simulatedPath.add("-34.017937211724956,22.46476622345631");
        simulatedPath.add("-34.01783939164358,22.464454249020264");
        simulatedPath.add("-34.01779492793296,22.464271020616934");
        simulatedPath.add("-34.01774157144948,22.46397513943498");
        simulatedPath.add("-34.01764375114265,22.46351832571213");
        simulatedPath.add("-34.01754148433772,22.46312588500564");
        simulatedPath.add("-34.017497020471055,22.46291047009413");
        simulatedPath.add("-34.01741253906025,22.462652139838383");
        simulatedPath.add("-34.01729693278289,22.462409902836725");
        simulatedPath.add("-34.017185772752335,22.462167665835068");
        simulatedPath.add("-34.01701680922712,22.46171085211222");
        simulatedPath.add("-34.01696345225455,22.461377420004055");
        simulatedPath.add("-34.016883416722824,22.46107080998604");
        simulatedPath.add("-34.016682087429345,22.46033012866974");
        simulatedPath.add("-34.016626507029706,22.460147319361567");
        simulatedPath.add("-34.016615390945404,22.460042294114828");
        simulatedPath.add("-34.016626507029706,22.459897035732865");
        simulatedPath.add("-34.016628730246396,22.45968472212553");
        simulatedPath.add("-34.01666207848967,22.459416082128882");
        simulatedPath.add("-34.016695426719835,22.459211815148592");
        simulatedPath.add("-34.01672989100637,22.458991454914212");
        simulatedPath.add("-34.0167598999277,22.458789870142937");
        simulatedPath.add("-34.01675100707435,22.45847831480205");
        simulatedPath.add("-34.01672655172285,22.458338420838118");
        simulatedPath.add("-34.01667764099871,22.458126107230783");
        simulatedPath.add("-34.01657759624792,22.457699216902256");
        simulatedPath.add("-34.016470881717126,22.457435941323638");

        simBusTimer = new Timer();
        int time = 4000;

        simBusTimer.schedule(new TimerTask() {
            @Override
            public void run() {

                if (sCount == simulatedPath.size() - 1) {
                    sCount = 0;
                }

                final String[] parts = simulatedPath.get(sCount).split("\\,");

                MapsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        //BUS ONE
                        LatLng locs = new LatLng(Double.parseDouble(parts[0]),
                                Double.parseDouble(parts[1]));

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
                            simuatedBusMarker = MapsActivity.this.mMap.addMarker(
                                    new MarkerOptions().position(locs)
                                            .title("Bus One")
                                            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
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
                        LatLng lla = null, llb = null;
                        for (int i = 0; i < markers.size(); i++) {
                            if (markers.get(i).getTag() == "Bus One") {
                                lla = new LatLng(markers.get(i).getPosition().latitude,
                                        markers.get(i).getPosition().longitude);
                            }
                            if (markers.get(i).getTag() == "Global Marker") {
                                llb = new LatLng(markers.get(i).getPosition().latitude,
                                        markers.get(i).getPosition().longitude);
                            }
                        }
                        if (lla != null && llb != null) {
                            Location.distanceBetween(lla.latitude, lla.longitude,
                                    llb.latitude, llb.longitude, results);

                            if (results[0] < 1000) {
                                setTextViewText("The bus is " + String.valueOf((int)results[0])
                                        + " meters from you.");
                            }
                        }
                    }
                });

                sCount++;
            }
        },1000, time);
        hasSimTimerStarted = true;
    }

    /*=============== Maps - End ===============*/

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

    public void trackBus (View view){
        view.startAnimation(buttonClick);

        if (track_my_bus_layout.getVisibility() == View.VISIBLE) {
            hideTrackMyBus();
        } else {
            showTrackMyBus();

            if (is_connected) {
                showTrackMyBus();

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
            setTextViewText(getResources().getString(R.string.connecting));
            connectToServer();
        } else {
            setTextViewText(this.getResources().getString(R.string.connected));
        }
    }

    public void connectToServer() {
        try {
            socket = IO.socket(getResources().getString(R.string.server_url));
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

                        MapsActivity.this.setTextViewText(
                                MapsActivity.this.getResources().getString(R.string.connected));
                        if (MapsActivity.this.view_flipper_layout.getVisibility() == View.VISIBLE) {
                            MapsActivity.this.view_flipper_layout.setVisibility(View.GONE);
                        }
                    }
                });

                MapsActivity.this.is_connected = true;
                try {
                    if (MapsActivity.this.username.equals("")) {
                        int value = new Random().nextInt(50);
                        MapsActivity.this.username = value + "_" + new SimpleDateFormat(
                                "HHmmss").format(Calendar.getInstance().getTime());
                    }
                    MapsActivity.this.socket.emit("add user", MapsActivity.this.username);
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
                final String[] list = String.valueOf(args[0]).split("\\|");

                if (list.length > 0) {
                    MapsActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            for (String item: list) {
                                adapter.insert(item, adapter.getCount());
                            }

                            adapter.notifyDataSetChanged();

//                            selectBusID.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//                                @Override
//                                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                                    final int itemPosition = i;
//
//                                    String itemValue = (String) MapsActivity.this.selectBusID
//                                            .getItemAtPosition(itemPosition);
//                                    if (MapsActivity.this.is_connected) {
//                                        try {
//                                            MapsActivity.this.socket.emit(
//                                                    "request bus location", itemValue);
//                                            MapsActivity.this.hideTrackMyBus();
//                                        } catch (Exception e) {
//                                            MapsActivity.this.setTextViewText(e.getMessage());
//                                        }
//                                    }
//
//                                    MapsActivity.this.hideTrackMyBus();
//                                }
//                            });
                        }
                    });


                    MapsActivity.this.v.vibrate(100);
                }
            }

        }).on("message", new Emitter.Listener() {
            public void call(Object... args) {
                final String message = String.valueOf(args[0]);
                MapsActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MapsActivity.this.getApplicationContext(), message,
                                Toast.LENGTH_LONG).show();
                        MapsActivity.this.v.vibrate(100);

                        if (message.contains("currently no busses")) {
                            MapsActivity.this.txtSelectBusOutput.setText(message);
                        }
                    }
                });
            }
        }).on("bus id enabled", new Emitter.Listener() {
            public void call(Object... args) {
                final String message = String.valueOf(args[0]);
                MapsActivity.this.is_bus = true;
                MapsActivity.this.runOnUiThread(new Runnable() {
                    public void run() {
                        MapsActivity.this.setTextViewText(message);
                        MapsActivity.this.hideAdminOnly();
                        MapsActivity.this.btnSubmit.setText("Bus Tracking Enabled");
                        MapsActivity.this.btnSubmit.setEnabled(false);
                        MapsActivity.this.v.vibrate(100);
                    }
                });
            }
        }).on("bus location updated", new Emitter.Listener() {
            public void call(Object... args) {
                MapsActivity.this.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        MapsActivity.this.btnAdminOnly.startAnimation(
                                MapsActivity.this.buttonClick);
                    }
                });
            }
        }).on("set bus location", new Emitter.Listener() {
            public void call(Object... args) {
                final String[] loc = String.valueOf(args[0]).split("\\|"); //ID|LAT|LNG

                boolean have_marker = false;
                for (int i = 0; i < markers.size(); i++) {
                    if (markers.get(i).getTag() == loc[0]) {
                        // Setting latitude and longitude for the marker
                        markers.get(i).setPosition(
                                new LatLng(Double.parseDouble(loc[1]),
                                        Double.parseDouble(loc[2])));
                        have_marker = true;
                        break;
                    }
                }

                //if the ID is new add a market to the map
                if (!have_marker) {
                    //cancel the simulated Bus One
                    simBusTimer.cancel();
                    hasSimTimerStarted = false;
                    for (int i = 0; i < markers.size(); i++) {
                        if (markers.get(i).getTag() == "Bus One") {
                            // Setting latitude and longitude for the marker
                            markers.remove(i);
                            simuatedBusMarker.setVisible(false);
                            break;
                        }
                    }

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
                        mMap.moveCamera(CameraUpdateFactory.newLatLngBounds(
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
                        btn_connect.setImageResource(R.drawable.connect_white);                    }
                });
                MapsActivity.this.setTextViewText(getString(R.string.disconnected));
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