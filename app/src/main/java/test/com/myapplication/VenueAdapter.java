package test.com.myapplication;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

/**
 * Created by Durgesh on 21/04/16.
 */
public class VenueAdapter extends RecyclerView.Adapter<VenueAdapter.MyViewHolder>{
    //private ArrayList<LinkedHashMap<String, String>> contactList;
    private List<Venue> contactList;
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.custom_list, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Venue venue=contactList.get(position);
        //System.out.println("venude.getName : "+venue.getName().toString());
        holder.name.setText(venue.getName().trim());
        holder.address.setText(venue.getAddress().trim());
        holder.checkin.setText(venue.getCheckin().trim());
    }

    @Override
    public int getItemCount() {
        return contactList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        private TextView name,address,checkin;
        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.Vname);
            address = (TextView) itemView.findViewById(R.id.Vaddress);
            checkin = (TextView) itemView.findViewById(R.id.checkin);
        }
    }

    public VenueAdapter(List<Venue> contactList){
        this.contactList=contactList;
    }


}
