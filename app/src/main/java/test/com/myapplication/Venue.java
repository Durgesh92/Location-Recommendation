package test.com.myapplication;

/**
 * Created by Durgesh on 21/04/16.
 */
public class Venue {
    private String name,address,lat,lon,checkin;
    public Venue(){

    }
    public Venue(String name, String address, String checkin, String lat, String lon){
        this.name=name;
        this.address=address;
        this.checkin=checkin;
        this.lat=lat;
        this.lon=lon;
    }
    public String getName(){
        return this.name;
    }
    public void setName(String name){
     }
    public String getAddress(){
        return this.address;
    }
    public void setAddress(String address){
        this.address=address;
    }
    public String getCheckin(){
        return this.checkin;
    }
    public void setCheckin(String checkin){
        this.checkin=checkin;
    }
    public String getLat(){
        return this.lat;
    }
    public void setLat(String lat){
        this.lat=lat;
    }
    public String getLon(){
        return this.lon;
    }
    public void setLon(String lon){
        this.lon=lon;
    }
}