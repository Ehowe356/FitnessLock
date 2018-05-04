package fitnesslock.designproject;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import java.util.Random;

public class RunningTracker extends FragmentActivity implements OnMapReadyCallback, LocationListener {


    public int milage = 1;
    private GoogleMap mMap;
    private LocationManager locationManager;
    private String providers;
    private final int FINE_LOCATION_PERMISSION = 9999;
    private LatLng myPosition;
    public double lat, lng;
    public Marker marker2;
    public Marker marker;
    public LatLng goToPosition;
    private Polyline polyline;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_runningtracker);
        marker = null;

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        //request permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager    .PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,             Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},FINE_LOCATION_PERMISSION);
        }


        //location manager
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        providers = locationManager.getBestProvider(new Criteria(), false);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Location location = locationManager.getLastKnownLocation(providers);
        if (location != null)
            Log.i("Log Info", "Location Achieved");
        else
            Log.i("Log Info", "Location not found");
    }


    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates(this);
    }

    protected void onResume() {
        super.onResume();



        //request permission
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,new String[] {Manifest.permission.ACCESS_FINE_LOCATION},FINE_LOCATION_PERMISSION);
        }
        locationManager.requestLocationUpdates(providers, 400, 1, (android.location.LocationListener) this);
    }
    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.getUiSettings().setScrollGesturesEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);
        mMap.getUiSettings().setZoomGesturesEnabled(false);

    }

    @Override
    public void onLocationChanged(Location location) {
        //create current location marker
        lat = location.getLatitude();
        lng = location.getLongitude();
        myPosition = new LatLng(lat, lng);
        if(marker != null) {
            marker.remove();
            marker = null;
        }

        marker = mMap.addMarker(new MarkerOptions().position(myPosition).title("My position"));
        float zoomLevel = 14.0f; //This goes up to 21
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, zoomLevel));
        if(marker2 == null) {
            goToPosition = getRandomLocation(myPosition, milage);
            marker2 = mMap.addMarker(new MarkerOptions().position
                    (goToPosition).title(" Go to location"));
            System.out.println("My position " + myPosition);
        }
        Log.i("Log info", "my position: " + myPosition + "go to location: " + goToPosition);
        //create a polyline

    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    public void setScrollGesturesEnabled (boolean enabled){

    }

    public LatLng getRandomLocation(LatLng myPosition, int radius) {
        float[] TotalDistance = new float[1];
        int indexOfNearestPointToCentre = 0;
        radius = radius * 1609;
        Location myLocation = new Location("");
        myLocation.setLatitude(myPosition.latitude);
        myLocation.setLongitude(myPosition.longitude);

        //This is to generate 10 random points
        while (true) {
            double x0 = myPosition.latitude;
            double y0 = myPosition.longitude;

            Random random = new Random();

            // Convert radius from meters to degrees
            double radiusInDegrees = radius / 111000f;

            double u = random.nextDouble();
            double v = random.nextDouble();
            double w = radiusInDegrees * Math.sqrt(u);
            double t = 2 * Math.PI * v;
            double x = w * Math.cos(t);
            double y = w * Math.sin(t);

            // Adjust the x-coordinate for the shrinking of the east-west distances
            double new_x = x / Math.cos(y0);

            double foundLatitude = new_x + x0;
            double foundLongitude = y + y0;
            LatLng randomLatLng = new LatLng(foundLatitude, foundLongitude);
            Location l1 = new Location("");
            l1.setLatitude(randomLatLng.latitude);
            l1.setLongitude(randomLatLng.longitude);
            Location.distanceBetween(myPosition.latitude, myPosition.longitude, randomLatLng.latitude, randomLatLng.longitude, TotalDistance);
            //System.out.println(randomLatLng );
                if (TotalDistance[0] <= 1609.34 && TotalDistance[0]>= 1600) {
                    System.out.println("Random " + randomLatLng );
                    System.out.println("my position" + myPosition);
                    return randomLatLng;
                }
            }
        }
    }


