package test.com.myapplication;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.drive.Contents;
import com.google.android.gms.location.LocationListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {  //AppCompatActivity

    EditText search_et;
    Button search_b;
    GPSTracker gps;
    String url="";
    double longitude;
    double latitude;
    ProgressDialog pDialog;
    ArrayList<LinkedHashMap<String, String>> contactList=null;
    ArrayList<LinkedHashMap<String, String>> sortedContactList=null;
    LinkedHashMap<String,Integer> sortHashMap = null;
    Parsing html;
    String CLIENT_ID = "UY2IQAOMNYERHBWOP1RBK4S3LP503LRBEO54O2AA5IARO1XM";
    String CLIENT_SECRET = "JG550GHQ4CGKZAVDS4JXFM5H5M2NJSOPYLHP4OL2FE2C3FJX";
    private List<Venue> venueList = new ArrayList<>();
    private RecyclerView recyclerView;
    String val;
    private VenueAdapter mAdapter;
    int usageCount=0;
    ArrayList<String> reverseKey=null;
    ArrayList<Integer> reverseValue=null;
    String splitSymbol="@";

    protected LocationManager locationManager;
    protected LocationListener locationListener;
    protected Context context;
    String lat;
    String provider;
    protected String latitudeX,longitudeX;
    protected boolean gps_enabled,network_enabled;
    public static final String MyPREFERENCES = "foursquare" ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(shouldAskPermission())
        {
            String[] perms = {"android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.INTERNET",
                    "test.com.myapplication.permission.MAPS_RECEIVE",
                    "android.permission.WRITE_EXTERNAL_STORAGE",
                    "android.permission.ACCESS_COARSE_LOCATION",
                    "android.permission.STORAGE",
                    "android.permission.READ_EXTERNAL_STORAGE",
                    "com.google.android.providers.gsf.permission.READ_GSERVICES"
                    };
            int permsRequestCode = 200;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                requestPermissions(perms, permsRequestCode);
            }
        }

        final SharedPreferences loc_pref = getApplicationContext().getSharedPreferences("location",Context.MODE_PRIVATE);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this,R.style.MyDialogTheme);
        builder.setTitle("How do you want to be located?");
        builder.setMessage("GPS : to get your current location\nPrevious : to get your last known location\nManual : to set location manually");
        //Button One : Yes
        builder.setPositiveButton("Manually", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Toast.makeText(MainActivity.this, "Yes button Clicked!", Toast.LENGTH_LONG).show();
            }
        });
        //Button Two : No
        builder.setNegativeButton("Previous", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                latitude = Double.parseDouble(loc_pref.getString("LOCATION_LAT", String.valueOf(0.0)));
                longitude = Double.parseDouble(loc_pref.getString("LOCATION_LNG", String.valueOf(0.0)));
                dialog.cancel();
            }
        });
        //Button Three : Neutral
        builder.setNeutralButton("GPS", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                gps = new GPSTracker(MainActivity.this);
                if (gps.canGetLocation()) {
                    latitude = gps.getLatitude();
                    longitude = gps.getLongitude();
                    System.out.println("latitude ==> "+latitude+" & longitute ==> "+longitude);
                    Toast.makeText(getApplicationContext(),"location success",Toast.LENGTH_SHORT).show();
                } else {
                    System.out.println("GPS not enabled");
                    gps.showSettingsAlert();
                }
                dialog.cancel();
            }
        });


        AlertDialog diag = builder.create();
        diag.show();
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        mAdapter = new VenueAdapter(venueList);
        RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
        recyclerView.setAdapter(mAdapter);
        search_b = (Button)findViewById(R.id.button);
        search_et = (EditText)findViewById(R.id.editText);
        final SharedPreferences pref = getApplicationContext().getSharedPreferences(MyPREFERENCES,Context.MODE_PRIVATE);
        /*
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Contents.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null && activeNetwork.isConnected();
        if(!isConnected){
            AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.MyDialogTheme);
            builder.setTitle("Connectivity status");
            builder.setMessage("You are roaming and connected to internet..");
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        */

        search_b.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(latitude == 0.0 && longitude == 0.0){
                    latitude = 21.1619600;
                    longitude = 72.7850740;
                    Toast.makeText(getApplicationContext(),"Default latlng are selected",Toast.LENGTH_SHORT).show();
                }
                System.out.println("latitude ==> "+latitude+" & longitute ==> "+longitude);
                SharedPreferences.Editor editor = loc_pref.edit();
                editor.putString("LOCATION_LAT",String.valueOf(latitude));
                editor.putString("LOCATION_LNG",String.valueOf(longitude));
                val = search_et.getText().toString();
                System.out.println("val : "+val);
                final String range = pref.getString("RADIUS", "");
                final String results = pref.getString("RESULTS","");
                System.out.println("range :"+range);
                System.out.println("results :"+results);
                url="https://api.foursquare.com/v2/venues/search?client_id="+CLIENT_ID+"&client_secret="+CLIENT_SECRET+"&v=20160407+&ll="+latitude+","+longitude+"&query="+val+"&limit="+results+"&radius="+range;
                System.out.println("processed url is : "+url);
                try {
                    if(usageCount>0){
                       venueList.clear();
                        mAdapter.notifyDataSetChanged();
                        usageCount=0;
                    }
                    usageCount++;
                    new GetContacts().execute();
                }
                catch(Exception e){
                }
            }
        });
        recyclerView.addOnItemTouchListener(new RecyclerTouchListener(getApplicationContext(), recyclerView, new ClickListener() {
            @Override
            public void onClick(View view, int position) {
                Venue venue = venueList.get(position);
                Snackbar snackbar = Snackbar
                        .make(view, venue.getName()+" is selected & position : "+position, Snackbar.LENGTH_LONG);
                snackbar.show();
                String part[] = reverseKey.get(position).toString().split(splitSymbol);
                Intent intent = new Intent(MainActivity.this, GoogleMapActivity.class);
                intent.putExtra("name",part[1]);
                intent.putExtra("dlat",part[3]);
                intent.putExtra("dlng",part[4]);
                intent.putExtra("slat",String.valueOf(latitude));
                intent.putExtra("slng",String.valueOf(longitude));
                intent.putExtra("address",part[2]);
                intent.putExtra("state",part[8]);
                intent.putExtra("country",part[9]);
                intent.putExtra("users",part[10]);
                startActivity(intent);
            }
            @Override
            public void onLongClick(View view, int position) {

            }
        }));
    }

    @Override
    public void onLocationChanged(Location location) {
        latitude=Double.parseDouble(String.valueOf(location.getLatitude()));
        longitude=Double.parseDouble(String.valueOf(location.getLongitude()));
        System.out.println("onLocationChanged lat::"+latitude+" lng"+longitude);
    }
    public interface ClickListener {
        void onClick(View view, int position);

        void onLongClick(View view, int position);
    }

    public static class RecyclerTouchListener implements RecyclerView.OnItemTouchListener {

        private GestureDetector gestureDetector;
        private MainActivity.ClickListener clickListener;

        public RecyclerTouchListener(Context context, final RecyclerView recyclerView, final MainActivity.ClickListener clickListener) {
            this.clickListener = clickListener;
            gestureDetector = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onSingleTapUp(MotionEvent e) {
                    return true;
                }

                @Override
                public void onLongPress(MotionEvent e) {
                    View child = recyclerView.findChildViewUnder(e.getX(), e.getY());
                    if (child != null && clickListener != null) {
                        clickListener.onLongClick(child, recyclerView.getChildPosition(child));
                    }
                }
            });
        }

        @Override
        public boolean onInterceptTouchEvent(RecyclerView rv, MotionEvent e) {

            View child = rv.findChildViewUnder(e.getX(), e.getY());
            if (child != null && clickListener != null && gestureDetector.onTouchEvent(e)) {
                clickListener.onClick(child, rv.getChildPosition(child));
            }
            return false;
        }

        @Override
        public void onTouchEvent(RecyclerView rv, MotionEvent e) {
        }

        @Override
        public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

        }
    }
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Hold on, retrieving data...");
            pDialog.setCancelable(false);
            pDialog.show();
        }

        @Override
        protected Void doInBackground(Void... arg0) {
            html = new Parsing(MainActivity.this);
            html.JsonParse(url);
            contactList = new ArrayList<LinkedHashMap<String, String>>();
            contactList.clear();
            contactList=html.contactList;
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            int size = contactList.size();
            LinkedHashMap<String,Integer> temp = new LinkedHashMap<>();
            HashMap<String,Integer> zc = new HashMap<>();
            for(int i=0;i<size;i++){
                String id=contactList.get(i).get("id");
                String names=contactList.get(i).get("names");
                String address=contactList.get(i).get("address");
                String lat=contactList.get(i).get("lat");
                String lng=contactList.get(i).get("lng");
                String distance=contactList.get(i).get("distance");
                String postalCode=contactList.get(i).get("postalCode");
                String city=contactList.get(i).get("city");
                String state=contactList.get(i).get("state");
                String country=contactList.get(i).get("country");
                String checkIn=contactList.get(i).get("checkIn");
                String usersCount=contactList.get(i).get("usersCount");
                String compoundKey = id+splitSymbol+names+splitSymbol+address+splitSymbol+lat+splitSymbol+lng+splitSymbol+distance+splitSymbol+postalCode+splitSymbol+city+splitSymbol+state+splitSymbol+country+splitSymbol+usersCount;
                int valueCheckIn=Integer.parseInt(checkIn);
                zc.put(compoundKey,valueCheckIn);
            }
            sortHashMap = sortHashMapByValues(zc);
            ArrayList<String> key = new ArrayList<>();
            ArrayList<Integer> value = new ArrayList<>();
            Iterator izc = sortHashMap.entrySet().iterator();
            while (izc.hasNext()){
                String Result[] = izc.next().toString().split("=");
                key.add(Result[0]);
                value.add(Integer.parseInt(Result[1]));
            }
            reverseKey = new ArrayList<>();
            reverseValue = new ArrayList<>();
            for(int s=size-1;s>=0;s--){
                reverseKey.add(key.get(s));
                reverseValue.add(value.get(s));
            }
            if (pDialog.isShowing())
                pDialog.dismiss();
                if(contactList.size()==0) {
                Toast.makeText(getApplicationContext(),"No results",Toast.LENGTH_SHORT).show();
                    Snackbar snackbar = Snackbar
                            .make(recyclerView, "Please increase the search range in setting", Snackbar.LENGTH_LONG);
                    snackbar.show();
                }
            for(int l=0;l<size;l++){
                String part[] = reverseKey.get(l).toString().split(splitSymbol);
                String name=part[1];
                String address=part[2];
                String checkin=reverseValue.get(l).toString();
                String lat=part[3];
                String lon=part[4];
                Venue venue = new Venue(name,address,checkin,lat,lon);
                venueList.add(venue);
                mAdapter.notifyDataSetChanged();
            }
        }
    }
    private boolean shouldAskPermission(){

        return(Build.VERSION.SDK_INT>Build.VERSION_CODES.LOLLIPOP_MR1);

    }
    @Override
    public void onRequestPermissionsResult(int permsRequestCode, String[] permissions, int[] grantResults){

        switch(permsRequestCode){

            case 200:
                boolean ACCESS_FINE_LOCATION = grantResults[0]== PackageManager.PERMISSION_GRANTED;
                boolean INTERNET = grantResults[1]== PackageManager.PERMISSION_GRANTED;
                boolean MAPS_RECEIVE = grantResults[2]== PackageManager.PERMISSION_GRANTED;
                boolean WRITE_EXTERNAL_STORAGE = grantResults[3]== PackageManager.PERMISSION_GRANTED;;
                boolean ACCESS_COARSE_LOCATION = grantResults[4]== PackageManager.PERMISSION_GRANTED;;
                boolean STORAGE = grantResults[5]== PackageManager.PERMISSION_GRANTED;;
                boolean READ_EXTERNAL_STORAGE = grantResults[6]== PackageManager.PERMISSION_GRANTED;;
                boolean READ_GSERVICES = grantResults[7]== PackageManager.PERMISSION_GRANTED;;
                System.out.println("ReadExternalStorageAccepted : "+ACCESS_FINE_LOCATION);
                System.out.println("WriteSettingAccepted : "+INTERNET);
                System.out.println("MAPS_RECEIVE : "+MAPS_RECEIVE);
                System.out.println("WRITE_EXTERNAL_STORAGE : "+WRITE_EXTERNAL_STORAGE);
                System.out.println("ACCESS_COARSE_LOCATION : "+ACCESS_COARSE_LOCATION);
                System.out.println("STORAGE : "+STORAGE);
                System.out.println("READ_EXTERNAL_STORAGE : "+READ_EXTERNAL_STORAGE);
                System.out.println("READ_GSERVICES : "+READ_GSERVICES);
                break;
        }

    }
    @TargetApi(Build.VERSION_CODES.M)
    private boolean hasPermission(String permission){

        if(shouldAskPermission()){

            return(checkSelfPermission(permission)==PackageManager.PERMISSION_GRANTED);
        }

        return true;

    }
    public LinkedHashMap<String, Integer> sortHashMapByValues(
            HashMap<String, Integer> passedMap) {
        List<String> mapKeys = new ArrayList<>(passedMap.keySet());
        List<Integer> mapValues = new ArrayList<>(passedMap.values());
        Collections.sort(mapValues);
        Collections.sort(mapKeys);
        LinkedHashMap<String, Integer> sortedMap =
                new LinkedHashMap<>();

        Iterator<Integer> valueIt = mapValues.iterator();
        while (valueIt.hasNext()) {
            int val = valueIt.next();
            Iterator<String> keyIt = mapKeys.iterator();

            while (keyIt.hasNext()) {
                String key = keyIt.next();
                int comp1 = passedMap.get(key);
                int comp2 = val;

                if (comp1==comp2) {
                    keyIt.remove();
                    sortedMap.put(key, val);
                    break;
                }
            }
        }
        return sortedMap;
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);//Menu Resource, Menu
        //return true;
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item1:
                Intent i =new Intent(MainActivity.this,Preferences.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}

