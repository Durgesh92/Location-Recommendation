package test.com.myapplication;


import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by Durgesh on 02/06/16.
 */
public class Preferences extends AppCompatActivity {
    @Override
    public void onCreate(Bundle b){
        super.onCreate(b);
        setContentView(R.layout.preferences);
        int CONFIG = 1;
        int RADIUS = 2;
        int RESULTS = 3;
        int mDataSetTypes[] = {CONFIG,RADIUS,RESULTS};
        RecyclerView mRecyclerView;
        RecyclerView.LayoutManager mLayoutManager;
        CardAdapter mAdapter;
        mRecyclerView = (RecyclerView) findViewById(R.id.preferences_recycler_01);
        mLayoutManager = new LinearLayoutManager(Preferences.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        String[] mDataset = {"Hi","Bi","Fi"};
        mAdapter = new CardAdapter(mDataset,mDataSetTypes,this);
        mRecyclerView.setAdapter(mAdapter);
    }
}