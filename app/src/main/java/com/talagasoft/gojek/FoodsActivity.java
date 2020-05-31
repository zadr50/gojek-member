package com.talagasoft.gojek;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AdapterView;
import android.widget.ListView;

import com.talagasoft.gojek.adapter.ItemMasterAdapter;
import com.talagasoft.gojek.controller.ItemMasterController;
import com.talagasoft.gojek.model.ItemMaster;

public class FoodsActivity extends AppCompatActivity {

    /**
     * The {@link PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link FragmentStatePagerAdapter}.
     */
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private int mTabIndex=0;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    private ViewPager mViewPager;
    static SharedPreferences mSetting;
    private int mDepositAmount=0;
    static String _supp_code, _supp_name, mNoHp,_type_item;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foods);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        _supp_code = getIntent().getStringExtra("supp_code");
        _supp_name = getIntent().getStringExtra("supp_name");
        _type_item=getIntent().getStringExtra("type");

        mSetting = getSharedPreferences("setting_gojek", Context.MODE_PRIVATE);
        mNoHp=mSetting.getString("no_hp","");
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent("com.talagasoft.oc_member.FoodsCartActivity");
                intent.putExtra("supp_code",_supp_code);
                intent.putExtra("no_hp",mNoHp);
                startActivity(intent);
            }
        });
        final ItemMasterController vItem=new ItemMasterController(getBaseContext());
        vItem.downloadUpdate();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_foods, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        public PlaceholderFragment() {
        }

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_foods, container, false);
            ListView listView = (ListView) rootView.findViewById(R.id.lstData);
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    ItemMaster item = (ItemMaster) adapterView.getAdapter().getItem(i);

                    //Toast.makeText(getContext(),"selected item index " + item.getItemName(),Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor editor = mSetting.edit();
                    editor.putString("item_no",item.getItemNo());
                    editor.putString("item_name",item.getItemName());
                    editor.putInt("item_price",item.getHarga());
                    editor.putFloat("item_lat",item.getLat());
                    editor.putFloat("item_lon",item.getLon());
                    editor.putString("item_icon",item.getIconFile());
                    editor.commit();

                    startActivity(new Intent("com.talagasoft.oc_member.ItemOrderActivity"));
                }
            });
            String sJenis="food";
            int i=getArguments().getInt(ARG_SECTION_NUMBER);
            if(i==1){
                sJenis="food";
            } else if(i==2){
                sJenis="beverage";
            } else {
                sJenis="other";
            }

            Log.d("tabIndex", "i="+i);

            listView.setAdapter(new ItemMasterAdapter(getContext(),sJenis,_supp_code));
            return rootView;
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).

            return PlaceholderFragment.newInstance(position + 1);
        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "FOODS";
                case 1:
                    return "BEVERAGES";
                case 2:
                    return "OTHERS";
            }
            return null;
        }
    }
}
