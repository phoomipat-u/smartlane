package trigon.smartlane;
import android.app.Activity;
import android.content.SharedPreferences;
import android.support.v4.app.ActivityCompat;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.content.Intent;
import android.location.LocationManager;
import android.Manifest;
import android.content.pm.PackageManager;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    private static final int PERMISSIONS_REQUEST = 100;
    private String BUS_ID_KEY;
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    String busId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        BUS_ID_KEY = getString(R.string.bus_id_key);
        this.pref = getApplicationContext().getSharedPreferences("settings", 0); // 0 - for private mode
        this.editor = pref.edit();

        preStartTrackingService();
        setContentView(R.layout.activity_main);
    }

    public void preStartTrackingService(){

        this.busId = pref.getString(BUS_ID_KEY, null);

        //Check whether GPS tracking is enabled//
        Log.d("MAIN", "Outer " + this.busId);
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {

            Log.d("MAIN", "Inner ID: " + this.busId);

            //Check whether this app has access to the location permission//
            int permission = ContextCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION);
            //If the location permission has been granted, then start the TrackerService//
            if (permission == PackageManager.PERMISSION_GRANTED) {
                Log.d("MAIN", "Need ID: " + this.busId);
                if (this.busId != null) {
                    startTrackerService();
                }

            }
            else {
                Log.d("MAIN", "Inner2222 ID: " + this.busId);
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        PERMISSIONS_REQUEST);
            }
        }

    }

    public void recordBusId(View view){

        EditText ptBusId = findViewById(R.id.busIdText);
        String busId = ptBusId.getText().toString();
        editor.putString(BUS_ID_KEY, busId);
        editor.commit();
        preStartTrackingService();

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[]
            grantResults) {

        //If the permission has been granted...//
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

//            //...then start the GPS tracking service//
//            startService();
        } else {
            //If the user denies the permission request, then display a toast with some more information//
            Toast.makeText(this, "Please enable location services to allow GPS tracking", Toast.LENGTH_SHORT).show();
        }
    }

//Start the TrackerService//

    private void startTrackerService() {


        Intent intent = new Intent(this, TrackingService.class);
        intent.putExtra(BUS_ID_KEY, busId);
        startService(intent);

        //Notify the user that tracking has been enabled//
        Toast.makeText(this, "GPS tracking enabled", Toast.LENGTH_SHORT).show();

        //Close MainActivity//
        finish();
    }

}