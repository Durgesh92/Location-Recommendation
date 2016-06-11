package test.com.myapplication;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

/**
 * Created by Durgesh on 22/04/16.
 */
public class GoogleMapActivity extends AppCompatActivity {
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    public String name;
    public String dlat;
    public String dlng;
    public String slat;
    public String slng;
    public String daddr;
    public String state;
    public String country;
    public String users;
    private RecyclerView mRecyclerView,mRecyclerView2;
    private CardAdapter mAdapter,mAdapter2;
    private RecyclerView.LayoutManager mLayoutManager,mLayoutManager2;
    public static final int INFO = 0;
    public static final int MAP = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.google_map);
        name= getIntent().getStringExtra("name");
        dlat= getIntent().getStringExtra("dlat");
        dlng= getIntent().getStringExtra("dlng");
        slat= getIntent().getStringExtra("slat");
        slng= getIntent().getStringExtra("slng");
        daddr= getIntent().getStringExtra("address");
        state= getIntent().getStringExtra("state");
        country= getIntent().getStringExtra("country");
        users= getIntent().getStringExtra("users");
        String line = "Your are travelling to \""+name+"\"\nAddress : "+daddr+", "+state+", "+country+"\nNo. of current checkins : "+users;
        String[] mDataset = {line};
        int mDataSetTypes[] = {INFO};
        //String line2 = slat+","+slng+","+dlat+","+dlng;
        //String[] mDataset2 = {line2};
        //int mDataSetTypes2[] = {MAP};

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerViewMap);
        //mRecyclerView2 = (RecyclerView) findViewById(R.id.recyclerViewMap2);
        mLayoutManager = new LinearLayoutManager(GoogleMapActivity.this);
        //mLayoutManager2 = new LinearLayoutManager(GoogleMapActivity.this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        //mRecyclerView2.setLayoutManager(mLayoutManager2);
        mAdapter = new CardAdapter(mDataset, mDataSetTypes);
        //mAdapter2 = new CardAdapter(mDataset2, mDataSetTypes2);
        mRecyclerView.setAdapter(mAdapter);
        //mRecyclerView2.setAdapter(mAdapter2);
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);
    }
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        MapFragment f1;
        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return f1.init_insert_frag(position,slat,slng,dlat,dlng,name);
            }
            return null;
        }

        @Override
        public int getCount() {
            // Show  total pages.
            return 1;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Insert";
            }
            return null;
        }
    }
}
