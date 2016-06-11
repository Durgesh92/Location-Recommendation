package test.com.myapplication;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by Durgesh on 22/04/16.
 */
public class CardAdapter extends RecyclerView.Adapter<CardAdapter.ViewHolder> {
    public static final int INFO = 0,RADIUS = 2,RESULTS = 3, CONFIG = 1;
    private String[] mDataSet;
    private int[] mDataSetTypes;
    String slat,slng,dlat,dlng;
    static String limit,results;
    SharedPreferences sharedpreferences;
    public static final String MyPREFERENCES = "foursquare" ;
    Context context;
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v;
        if (viewType == INFO) {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.info_card, viewGroup, false);
            return new InfoViewHolder(v);
        }
        else if (viewType == RADIUS) {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.preferences_range, viewGroup, false);
            return new range_holder(v);
        }else if(viewType == RESULTS) {
            v = LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.preferences_results, viewGroup, false);
            return new results_holder(v);
        }else if(viewType == CONFIG){
            v= LayoutInflater.from(viewGroup.getContext())
                    .inflate(R.layout.current_configuration_card,viewGroup,false);
            return new config_holder(v);
        }
        return null;
    }

    @Override
    public void onBindViewHolder(final ViewHolder viewHolder, int position) {
        if (viewHolder.getItemViewType() == INFO) {
            InfoViewHolder holder = (InfoViewHolder) viewHolder;
            holder.name.setText(mDataSet[position]);
        }
        else if (viewHolder.getItemViewType() == RADIUS) {
            final range_holder rholder = (range_holder) viewHolder;
            rholder.rb.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String temp = rholder.range.getText().toString();
                    sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("RADIUS", temp);
                    editor.commit();
                }
            });

        }
        else if (viewHolder.getItemViewType() == RESULTS) {
            final results_holder reholder = (results_holder) viewHolder;
            reholder.rs.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    final String temp2 = reholder.results.getText().toString();
                    sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedpreferences.edit();
                    editor.putString("RESULTS", temp2);
                    editor.commit();
                }
            });
        }
        else if(viewHolder.getItemViewType() == CONFIG){
            final config_holder ceholder = (config_holder) viewHolder;
            sharedpreferences = context.getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
            String t1 = sharedpreferences.getString("RADIUS","");
            String t2 = sharedpreferences.getString("RESULTS","");
            ceholder.results_config.setText("No of results :"+t2);
            ceholder.range_config.setText("Range limit (meters) :"+t1);
        }
    }

    @Override
    public int getItemCount() {
        return mDataSet.length;
    }

    @Override
    public int getItemViewType(int position) {
        return mDataSetTypes[position];
    }



    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View v) {
            super(v);
        }
    }

    public class InfoViewHolder extends ViewHolder {
        TextView name;
        public InfoViewHolder(View v) {
            super(v);
            this.name = (TextView) v.findViewById(R.id.dinfo);
        }
    }
    public class range_holder extends ViewHolder{
        EditText range;
        Button rb;
        public range_holder(View v) {
            super(v);
            range = (EditText) v.findViewById(R.id.preferences_editText_01);
            rb = (Button) v.findViewById(R.id.preferences_button_01);
        }
    }

    public class results_holder extends  ViewHolder{
        EditText results;
        Button rs;
        public results_holder(View v) {
            super(v);
            results = (EditText) v.findViewById(R.id.preferences_editText_02);
            rs = (Button) v.findViewById(R.id.preferences_button_02);
        }
    }

    public class config_holder extends ViewHolder{
        TextView range_config,results_config;
        public config_holder(View v) {
            super(v);
            range_config = (TextView) v.findViewById(R.id.limit_config);
            results_config = (TextView) v.findViewById(R.id.result_config);
        }
    }
    public CardAdapter(String[] dataSet, int[] dataSetTypes) {
        mDataSet = dataSet;
        mDataSetTypes = dataSetTypes;
    }
    public CardAdapter(String[] dataSet, int[] DataSetTypes, Context context){
        mDataSet = dataSet;
        this.mDataSetTypes = DataSetTypes;
        this.context=context;
    }
}
