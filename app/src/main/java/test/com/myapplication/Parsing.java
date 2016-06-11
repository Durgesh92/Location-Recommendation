package test.com.myapplication;

import android.content.Context;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.LinkedHashMap;

public class Parsing {

	Context context;
    Parsing(Context context){
        this.context=context;
    }
	ArrayList<LinkedHashMap<String, String>> contactList;

	public void JsonParse(String URL1){
		contactList = new ArrayList<LinkedHashMap<String, String>>();
		String logoLink=null;
        try {
            JsonParser jParser = new JsonParser();            // instantiate our json parser
            String yourJsonStringUrl = URL1;            // set your json string url here
            System.out.println("Value of URL at Parsing class is "+URL1);
            JSONArray dataJsonArr = null;            // contacts JSONArray
            JSONObject json = jParser.getJSONFromUrl(URL1);            // get json string from url
            JSONObject responseData = json.getJSONObject("response");            // get the array of users
            dataJsonArr = responseData.getJSONArray("venues");
			int len=dataJsonArr.length();            // loop through all users
            System.out.println("JOSAN Array length : " + dataJsonArr.length());
            //dbHelper db = new dbHelper(context,null,null,1);
            for (int i = 0; i < len; i++) {
                JSONObject c = dataJsonArr.getJSONObject(i);
                String id="",names="",address="",lat="",lng="",distance="",postalCode="",city="",state="",country="",checkIn="",usersCount="";
                if(c.has("id")){
                    id = c.getString("id");
                }
                if(c.has("name")){
                    names = c.getString("name");
                }
				JSONObject location=c.getJSONObject("location");
                if(location.has("address")){
                    address=location.getString("address");
                }
                if(location.has("lat")){
                    lat=location.getString("lat");
                }
                if (location.has("lng")){
                    lng=location.getString("lng");
                }
                if(location.has("distance")){
                    distance=location.getString("distance");
                }
                if(location.has("postalCode")){
                    postalCode=location.getString("postalCode");
                }
                if(location.has("city")){
                    city=location.getString("city");
                }
                if(location.has("state")){
                    state=location.getString("state");
                }
                if(location.has("country")){
                    country=location.getString("country");
                }
                JSONObject stats=c.getJSONObject("stats");
                if(stats.has("checkinsCount")){
                    checkIn=stats.getString("checkinsCount");
                }
                if(stats.has("usersCount")){
                    usersCount=stats.getString("usersCount");
                }

				LinkedHashMap<String, String> value = new LinkedHashMap<String, String>();
                if(!id.isEmpty()) {
                    value.put("id", id);
                }else {
                    value.put("id", "N.A.");
                }
                if(!names.isEmpty()){
				    value.put("names", names);
                }else {
                    value.put("names", "N.A.");
                }
                if(!address.isEmpty()){
				    value.put("address", address);
                }else {
                    value.put("address", "N.A.");
                }
                if(!lat.isEmpty()){
                    value.put("lat", lat);
                }else {
                    value.put("lat", "N.A.");
                }
                if(!lng.isEmpty()){
				    value.put("lng", lng);
                }else {
                    value.put("lng", "N.A.");

                }
                if(!distance.isEmpty()){
				    value.put("distance", distance);
                }else {
                    value.put("distance", "N.A.");
                }
                if(!postalCode.isEmpty()){
				    value.put("postalCode", postalCode);
                }else {
                    value.put("postalCode", "N.A.");
                }
                if(!city.isEmpty()){
				    value.put("city", city);
                }else {
                    value.put("city", "N.A.");
                }
                if(!state.isEmpty()){
				    value.put("state", state);
                }else {
                    value.put("state", "N.A.");
                }
                if(!country.isEmpty()){
				    value.put("country", country);
                }else {
                    value.put("country", "N.A.");
                }
                if(!checkIn.isEmpty()){
                    value.put("checkIn",checkIn);
                }else {
                    value.put("checkIn", "N.A.");
                }
                if(!usersCount.isEmpty()){
                    value.put("usersCount",usersCount);
                }else {
                    value.put("usersCount", "N.A.");
                }
                //System.out.println("Value aded in contact list is :" + value);
				contactList.add(value);

                int p1 = Integer.valueOf(checkIn);
                int p2 = Integer.valueOf(usersCount);
                //db.add(id,p1,p2);
			}
            System.out.println("Size of contact list : " + contactList.size());
        } catch (JSONException e) {
            e.printStackTrace();
        }
		catch(Exception e){
		}
	}


	public class JsonParser {
	 
	    final String TAG = "JsonParser.java";
	 
	     InputStream is = null;
	     JSONObject jObj = null;
	     String json = "";
	    public JSONObject getJSONFromUrl(String ul) {
	    try{
			URL url = new URL(ul);
	        URLConnection connection = url.openConnection();
	        String line;
	        StringBuilder builder = new StringBuilder();
	        BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
	        while((line = reader.readLine()) != null) {
	        builder.append(line);
            }
            json = builder.toString();
	        } catch (Exception e) {
	            Log.e(TAG, "Error converting result " + e.toString());
	        }
	        try {
	            jObj = new JSONObject(json);	        // try parse the string to a JSON object
            }
			catch (JSONException e)
			{
	            Log.e(TAG, "Error parsing json data " + e.toString());
	        }
			catch(Exception e){
	        	
	        }
	        return jObj;	        // return JSON String
        }
	}
}