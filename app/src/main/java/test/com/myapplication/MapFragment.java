package test.com.myapplication;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.LinearInterpolator;
import android.widget.Toast;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;

/**
 * Created by Durgesh on 20/02/16.
 */
public class MapFragment extends Fragment implements RoutingListener {
    private android.support.v4.app.FragmentTransaction transaction;
    private GoogleMap googleMap;
    private MapView mMapView;
    private static  LatLng POINTER_ONE; //= new LatLng(19.0544, 72.8406);
    private static  LatLng POINTER_TWO; //= new LatLng(18.9750, 72.8258);
    private ArrayList<Polyline> polylines;
    private int[] colors = new int[]{R.color.primary_dark,R.color.primary,R.color.primary_light,R.color.accent,R.color.primary_dark_material_light};
    private String serverKey = "AIzaSyCAtPOtdSNXGw5ErR81LFJ6KSxuHR8rcyo";
    int[] markerColors = new int[]{(int) BitmapDescriptorFactory.HUE_CYAN, (int) BitmapDescriptorFactory.HUE_VIOLET, (int) BitmapDescriptorFactory.HUE_ORANGE, (int) BitmapDescriptorFactory.HUE_ROSE, (int) BitmapDescriptorFactory.HUE_YELLOW};
    int count =0;
    public StringBuilder str;
    public int i = 0;
    int fragVal;
    Double slat;
    Double slng;
    Double dlat;
    Double dlng;
    String distance;
    String  duration;
    int GlobalRoute;
    LatLng p1,p2;
    ArrayList<String> dis = new ArrayList<>();
    ArrayList<String> dur = new ArrayList<>();
    ArrayList<ArrayList<String>> points = new ArrayList<>();
    ProgressDialog pDialog;
    ArrayList<Integer> idi = new ArrayList<>();
    ArrayList<Integer> idu = new ArrayList<>();
    ArrayList<LatLng> ill = new ArrayList<>();
    ArrayList<ArrayList<Integer>> cidi = new ArrayList<>();
    ArrayList<ArrayList<Integer>> cidu = new ArrayList<>();
    ArrayList<ArrayList<LatLng>> cill = new ArrayList<>();
    LatLng sourceLatLng;
    LatLng destinationLatLng;
    ArrayList<LatLng> latLngArrayList = new ArrayList<>();
    ArrayList<ArrayList<LatLng>> pathLatLng = new ArrayList<>();
    int c1=0,c2=0,c3,c4;
    String address;
    int MaxSpeed = 70;
    int MinSpeed = 10;
    public static MapFragment init_insert_frag (int val,String slat,String slng, String dlat, String dlng, String daddr){
        MapFragment insert_frag = new MapFragment();
        Bundle args = new Bundle();
        args.putInt("val", val);
        args.putString("slat",slat);
        args.putString("slng",slng);
        args.putString("dlat",dlat);
        args.putString("dlng",dlng);
        args.putString("daddr",daddr);
        insert_frag.setArguments(args);
        return insert_frag;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragVal = getArguments() != null ? getArguments().getInt("val") : 1;
        slat = Double.parseDouble(getArguments().getString("slat"));
        slng = Double.parseDouble(getArguments().getString("slng"));
        dlat = Double.parseDouble(getArguments().getString("dlat"));
        dlng = Double.parseDouble(getArguments().getString("dlng"));
        address = getArguments().getString("daddr");
        //System.out.println("MapFragment :: onCreate called");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.map_layout, container,
                false);
        //System.out.println("MapFragment :: onCreateView called");
        POINTER_ONE = new LatLng(slat,slng);
        POINTER_TWO = new LatLng(dlat,dlng);
        polylines = new ArrayList<>();
        mMapView = (MapView) v.findViewById(R.id.mapView);
        mMapView.onCreate(savedInstanceState);
        mMapView.onResume();// needed to get the map to display immediately
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }

        googleMap = mMapView.getMap();
        googleMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
        googleMap.setTrafficEnabled(true);
        /*
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(POINTER_ONE.latitude, POINTER_ONE.longitude)).zoom(14).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        */
        Polyline line = googleMap.addPolyline(new PolylineOptions()
                .add(POINTER_ONE, POINTER_TWO)
                .width(2)
                .color(Color.RED));

        Double distance = CalculationByDistance(POINTER_ONE, POINTER_TWO);
        //System.out.println("Direct distance :: "+distance);
        Routing routing = new Routing.Builder()
                .travelMode(Routing.TravelMode.DRIVING)
                .withListener(this)
                .waypoints(POINTER_ONE, POINTER_TWO)
                .alternativeRoutes(true)
                .build();
        routing.execute();
        return v;
    }
    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        int Radius = 6371;// radius of earth in Km
        double lat1 = StartP.latitude;
        double lat2 = EndP.latitude;
        double lon1 = StartP.longitude;
        double lon2 = EndP.longitude;
        double dLat = Math.toRadians(lat2 - lat1);
        double dLon = Math.toRadians(lon2 - lon1);
        double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                + Math.cos(Math.toRadians(lat1))
                * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                * Math.sin(dLon / 2);
        double c = 2 * Math.asin(Math.sqrt(a));
        double valueResult = Radius * c;
        double km = valueResult / 1;
        DecimalFormat newFormat = new DecimalFormat("####");
        int kmInDec = Integer.valueOf(newFormat.format(km));
        double meter = valueResult % 1000;
        int meterInDec = Integer.valueOf(newFormat.format(meter));
        Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                + " Meter   " + meterInDec);
        return Radius * c;
    }
    @Override
    public void onResume() {
        super.onResume();
        mMapView.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
        mMapView.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mMapView.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mMapView.onLowMemory();
    }

    @Override
    public void onRoutingFailure(RouteException e) {
        //Toast.makeText(getActivity().getApplicationContext()," "+e,Toast.LENGTH_SHORT).show();
        System.out.println("MapFragment : onRoutingFailure :: "+e.getMessage());
    }

    @Override
    public void onRoutingStart() {
        //Toast.makeText(getActivity().getApplicationContext()," Routing Started",Toast.LENGTH_SHORT).show();
        System.out.println("MapFragment : onRoutingStart ");
    }

    @Override
    public void onRoutingSuccess(ArrayList<com.directions.route.Route> route, int shortestRouteIndex)
    {
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(POINTER_ONE.latitude, POINTER_ONE.longitude)).zoom(16).build();
        googleMap.animateCamera(CameraUpdateFactory
                .newCameraPosition(cameraPosition));
        if(polylines.size()>0) {
            for (Polyline poly : polylines) {
                poly.remove();
            }
        }
        ArrayList<String> t1 = new ArrayList<>();
        String temp;
        GlobalRoute = route.size();
        for(int i=0; i<route.size();i++){

            System.out.println("********************STARTING ROUTE "+"["+i+"]"+"**************************\n");
            //System.out.println("Route getPoints: "+route.get(i).getPoints());
            System.out.println("Route getDistanceText: "+route.get(i).getDistanceValue());
            System.out.println("Route getDurationText: "+route.get(i).getDurationValue());
            System.out.println("Route getEndAddressText: "+route.get(i).getEndAddressText());
            System.out.println("********************ENDING ROUTE "+"["+i+"]"+"****************************\n");

            temp = route.get(i).getPoints().toString().replaceAll("lat/lng:","");
            //System.out.println("added coordinates:: "+temp);
            t1.add(i,temp);
            points.add(i,t1);
        }

        polylines = new ArrayList<>();
        //add route(s) to the map.
        for (int i = 0; i <route.size(); i++) {

            //In case of more than 5 alternative routes
            int colorIndex = i % colors.length;
            PolylineOptions polyOptions = new PolylineOptions();
            polyOptions.color(getResources().getColor(colors[colorIndex]));
            polyOptions.width(12 + i * 3);
            polyOptions.addAll(route.get(i).getPoints());
            polyOptions.geodesic(true);
            polyOptions.describeContents();
            Polyline polyline = googleMap.addPolyline(polyOptions);
            polylines.add(polyline);
            //Toast.makeText(getActivity().getApplicationContext(),"Route "+ (i+1) +": distance - "+ route.get(i).getDistanceValue()+": duration - "+ route.get(i).getDurationValue(),Toast.LENGTH_SHORT).show();
            str = new StringBuilder();
            str.append("Route : " + (i + 1) + " Distance : " + route.get(i).getDistanceText() + " Duration : " + route.get(i).getDurationText() + "\n");
            System.out.println(str.toString());
        }

        // Start marker
        MarkerOptions options = new MarkerOptions();
        options.position(POINTER_ONE);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED));
        options.title("Source");
        googleMap.addMarker(options);

        // End marker
        options = new MarkerOptions();
        options.position(POINTER_TWO);
        options.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN));
        options.title("Destination");
        googleMap.addMarker(options);
        HashMap hm = new HashMap();
        //Toast.makeText(getActivity().getApplicationContext(),"Route Marked",Toast.LENGTH_SHORT).show();
        ArrayList<String> decode = points.get(0);
        ArrayList<LatLng> lt1;
        for (int i=0; i<route.size(); i++){
            String s1 = decode.get(i).toString().replaceAll("\\[","").replaceAll("\\]","");
            String s2[] = s1.split(", ");
            lt1 = new ArrayList<>();
            for(int k=0; k<s2.length; k++){
                String latlng[] = s2[k].split(",");
                String templat = latlng[0].replaceAll("\\(","");
                String templng = latlng[1].replaceAll("\\)","");
                Double lat = Double.parseDouble(templat);
                Double lng = Double.parseDouble(templng);
                LatLng x = new LatLng(lat,lng);
                //latLngArrayList.add(k,x);
                lt1.add(k,x);
                markPoints(x,count);
            }
            pathLatLng.add(i,lt1);
            System.out.println("************* Added Markers on Route ["+(i+1)+"]  *************");
            count++;
        }
        System.out.println("pathLatLng.size() : "+pathLatLng.size());
        System.out.println("pathLatLng.isEmpty() "+pathLatLng.isEmpty());
        /*
        new executeEventThread().start();
        try {
            new executeEventThread().join();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        */
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),R.style.MyDialogTheme);
        builder.setTitle("Analysis Results");
        if(GlobalRoute==1){
            builder.setMessage("only 1 path is available to reach "+address);
        }else {
            builder.setMessage(GlobalRoute+" path's are available to reach "+address);
        }
        builder.setPositiveButton("Process Data",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // positive button logic
                        Toast.makeText(getActivity(),"positive button pressed",Toast.LENGTH_SHORT).show();
                        long k = getSpeed();
                        System.out.println("speed "+k);
                        for (int i=0; i<GlobalRoute; i++){
                            System.out.println("pathLatLng ["+i+"] : "+pathLatLng.get(i)+"\n");
                        }
                        distanceExecutor dexe = new distanceExecutor();
                        dexe.start();
                    }
                });
        builder.setNegativeButton("Exit",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // negative button logic
                        //setAnimation(googleMap,arrayListArrayList.get(0));
                    }
                });

        AlertDialog dialog = builder.create();
        // display dialog
        dialog.show();

    }

    @Override
    public void onRoutingCancelled() {
        System.out.println("onRoutingCancelled");
    }

    /*
    public class executeEventThread extends Thread{
        @Override
        public void run(){

            intermediateDistanceAndDuration idd = new intermediateDistanceAndDuration();
            try {
                idd.execute().get();
            } catch (InterruptedException e) {
                System.out.println("ERROR---InterruptedException :"+e.getMessage() );
            } catch (ExecutionException e) {
                System.out.println("ERROR---ExecutionException :"+e.getMessage() );
            }
        }
    }
    */

    public void markPoints(LatLng temp, int c){
        MarkerOptions options = new MarkerOptions();
        options.position(temp);
        options.icon(BitmapDescriptorFactory.defaultMarker(markerColors[c]));
        options.title(temp.latitude+","+temp.longitude);
        googleMap.addMarker(options);
    }

    public void setAnimation(GoogleMap myMap, final List<LatLng> directionPoint) {

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(new LatLng(directionPoint.get(0).latitude, directionPoint.get(0).longitude)).zoom(16).tilt(60).build();
        Bitmap icon = resizeMapIcons(256,256);
        Marker marker = myMap.addMarker(new MarkerOptions()
                .icon(BitmapDescriptorFactory.fromBitmap(icon))
                .position(directionPoint.get(0))
                .snippet("you are here!")
                .flat(false));
        //myMap.animateCamera(CameraUpdateFactory.newLatLngZoom(directionPoint.get(0), 16));
        myMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        animateMarker(myMap, marker, directionPoint, false);
    }

    public Bitmap resizeMapIcons(int width, int height){
        Bitmap imageBitmap = BitmapFactory.decodeResource(this.getResources(), R.drawable.car);
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, width, height, false);
        return resizedBitmap;
    }

    private  void animateMarker(GoogleMap myMap, final Marker marker, final List<LatLng> directionPoint,
                                      final boolean hideMarker) {
        final Handler handler = new Handler();
        final long start = SystemClock.uptimeMillis();
        Projection proj = myMap.getProjection();
        final long duration = 30000;
        final Interpolator interpolator = new LinearInterpolator();
        handler.post(new Runnable() {
            int i = 0;
            @Override
            public void run() {
                long elapsed = SystemClock.uptimeMillis() - start;
                System.out.println("start : "+start);
                System.out.println("elapsed : "+elapsed);
                float t = interpolator.getInterpolation((float) elapsed / duration);
                //float t = interpolator.getInterpolation((float) 2.5);
                System.out.println("t : "+t);
                if (i < directionPoint.size())
                    marker.setPosition(directionPoint.get(i));
                i++;
                if (t < 1.0) {
                    // Post again 16ms later.
                    handler.postDelayed(this, 16);
                } else {
                    if (hideMarker) {
                        marker.setVisible(false);
                    } else {
                        marker.setVisible(true);
                    }
                }
            }
        });
    }

    public class distanceExecutor extends Thread{

        @Override
        public void run(){
            for (int m=0; m<GlobalRoute; m++) {
                ArrayList<LatLng> test = pathLatLng.get(m);
                for (int n = 0; n < test.size(); n++) {
                    distanceWrapper dw = new distanceWrapper(test.get(n), POINTER_TWO);
                    synchronized (dw) {
                        dw.start();
                        try {
                            dw.join();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            System.out.println("dw.join exception : " + e.getMessage().toString());
                        }
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    public class distanceWrapper extends Thread{
        LatLng src,dest;
        @Override
        public void run(){
            new distanceHelper().execute(src,dest);
        }
        distanceWrapper(LatLng src, LatLng dest){
            this.src = src;
            this.dest = dest;
        }
    }
    public class distanceHelper extends AsyncTask <LatLng, Void, Void> implements RoutingListener{

        LatLng src,dest;
        @Override
        protected void onPreExecute(){
        }
        @Override
        protected Void doInBackground(LatLng... params) {
            //System.out.println("p1 : "+p1);
            //System.out.println("p2 : "+p2);
            this.src=params[0];
            this.dest=params[1];
            Routing routing = new Routing.Builder()
                    .travelMode(Routing.TravelMode.DRIVING)
                    .withListener(this)
                    .waypoints(src, dest)
                    .alternativeRoutes(true)
                    .build();
            routing.execute();
            return null;
        }
        @Override
        public void onRoutingFailure(RouteException e) {
            //System.out.println("distanceHelper ::onRoutingFailure : "+e.getMessage().toString());
        }

        @Override
        public void onRoutingStart() {
            //System.out.println("distanceHelper ::onRoutingStart");
        }

        @Override
        public void onRoutingSuccess(ArrayList<com.directions.route.Route> arrayList, int i) {
            distance = String.valueOf(arrayList.get(0).getDistanceValue());
            duration = String.valueOf(arrayList.get(0).getDurationValue());
            //idi.add(Integer.valueOf(distance));
            //idu.add(Integer.parseInt(duration));
            //Log.d("Durgesh","distance ==>"+distance);
            //Log.d("Durgesh","duration ==> "+duration);
            //System.out.println("p1 :: "+src+" distance :: "+distance+" duration :: " +duration);
            System.out.println("source : "+src);
            System.out.println("destination : "+dest);
            //distance value is in meters
            //divide duration value with 60 to get duration is seconds
            Double d1= Double.parseDouble(distance);
            Double d2= Double.parseDouble(duration);
            System.out.println("\t\tdistance value: "+distance+" text : "+arrayList.get(0).getDistanceText()+" conversion :"+d1/1000);
            System.out.println("\t\tduration value: "+duration+" text : "+arrayList.get(0).getDurationText()+" conversion :"+d2/60);
            Double d = Double.parseDouble(distance);
            Double t = Double.parseDouble(duration);
            Double s = d1/d2;
            System.out.println("\t\tRequired speed is : "+s+"m/s = "+(s*18)/5+"kmph");
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void onRoutingCancelled() {
            //System.out.println("distanceHelper ::onRoutingCancelled");
        }
    }
    /*
    public class intermediateDistanceAndDuration extends AsyncTask<Void,Void,Void>{

        @Override
        protected void onPreExecute(){
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                System.out.println("error :: in Thread.sleep "+e.getMessage());
            }
            //pDialog = new ProgressDialog(getActivity());
            //pDialog.setMessage("Hold on, extracting waypoints...");
            //pDialog.setCancelable(false);
            //pDialog.show();
            System.out.println("dialog shown");
            ArrayList<String> decode = points.get(0);
            for (int i=0; i<GlobalRoute; i++){
                System.out.println("************* Route ["+(i+1)+"] details *************");
                String s1 = decode.get(i).toString().replaceAll("\\[","").replaceAll("\\]","");
                //System.out.println("s1 : "+s1);
                String s2[] = s1.split(", ");
                for (int p=0; p<s2.length; p++){
                    System.out.println("s2 : "+s2[p]);
                }
                for(int k=0; k<s2.length; k++){
                    String latlng[] = s2[k].split(",");
                    String templat = latlng[0].replaceAll("\\(","");
                    String templng = latlng[1].replaceAll("\\)","");
                    Double lat = Double.parseDouble(templat);
                    Double lng = Double.parseDouble(templng);
                    //System.out.println("lat :: "+lat);
                    //System.out.println("lng :: "+lng);
                    LatLng x = new LatLng(lat,lng);
                    //System.out.println("s2[] :: "+s2[k]);
                    p2 = new LatLng(dlat,dlng);
                    p1 = x;
                    distanceHelper dh = new distanceHelper();
                    LatLng arr[] = new LatLng[]{p1,p2};
                    //System.out.println("dh init");
                    //System.out.println("about to call get");
                    dh.execute(arr);
                    try {
                        Void v = dh.get();
                        Thread.sleep(500);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
                    //System.out.println("dh.exe called");
                    ill.add(k,p1);
                }
                System.out.println("no of markers in "+i+" location :: "+s2.length);
                cidu.add(i,idu);
                cidi.add(i,idi);
                cill.add(i,ill);
                System.out.println("befor clearing buffers");
                System.out.println("idu.size() : "+idu.size());
                System.out.println("idi.size() : "+idi.size());
                System.out.println("ill.size() : "+ill.size());
                idu.clear();
                idi.clear();
                ill.clear();
                System.out.println("after clearing buffers");
                System.out.println("idu.size() : "+idu.size());
                System.out.println("idi.size() : "+idi.size());
                System.out.println("ill.size() : "+ill.size());
                count++;
            }
        }
        @Override
        protected Void doInBackground(Void... params) {

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            //pDialog.dismiss();
            System.out.println("***** distance and duration arraylist *****");
            System.out.println("cidu.size() : "+cidu.size());
            System.out.println("cidi.size() : "+cidi.size());
            System.out.println("cill.size() : "+cill.size());
            for(int i=0; i<cidu.size(); i++){
                System.out.println("**************START["+i+"]**************");
                ArrayList<Integer> t1= cidu.get(i);
                ArrayList<Integer> t2= cidi.get(i);
                ArrayList<LatLng> t3= cill.get(i);
                System.out.println("cidu.get("+i+") = "+t1.toString());
                System.out.println("cidi.get("+i+") = "+t2.toString());
                System.out.println("cill.get("+i+") = "+t3.toString());
                System.out.println("**************END["+i+"]**************");
            }
        }
    }
*/
    long getSpeed(){
        return Math.round((Math.random()*(MaxSpeed-MinSpeed)+MinSpeed)/10)*10;
    }
}