package com.gotaxiride.passenger.signUp;

import android.app.Activity;
import android.app.ProgressDialog;
import android.app.SearchManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.InputFilter;
import android.text.Spanned;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.gotaxiride.passenger.R;
import com.gotaxiride.passenger.api.ApiCountryCode;
import com.gotaxiride.passenger.config.General;
import com.gotaxiride.passenger.signUp.beans.Country;
import com.gotaxiride.passenger.signUp.beans.CountryList;
import com.gotaxiride.passenger.signUp.helper.RetroClient;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ListCountryActivity extends AppCompatActivity {

    private ArrayList<Country> employeeList;
    private ProgressDialog pDialog;
    private RecyclerView recyclerView;
    private CountryAdapter eAdapter;
    private Toolbar toolbar;

    public static final int LIST_COUNTRY = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_country);

        if (General.ENABLE_RTL_MODE) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
                getWindow().getDecorView().setLayoutDirection(View.LAYOUT_DIRECTION_RTL);
            }
        }

        pDialog = new ProgressDialog(ListCountryActivity.this);
        pDialog.setMessage("Loading Data.. Please wait...");
        pDialog.setIndeterminate(false);
        pDialog.setCancelable(false);
        pDialog.show();

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        initToolbar("Country Code");
        setdata();
    }



    public void initToolbar(String title) {
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setTitle(title);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        getMenuInflater().inflate(R.menu.menu_search, menu);


        // Retrieve the SearchView and plug it into SearchManager
        final SearchView searchView = (SearchView) MenuItemCompat
                .getActionView(menu.findItem(R.id.action_search));

        SearchManager searchManager = (SearchManager) this.getSystemService(this.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(this.getComponentName()));

        //changing edittext color
        EditText searchEdit = ((EditText) searchView.findViewById(android.support.v7.appcompat.R.id.search_src_text));
        searchEdit.setTextColor(Color.WHITE);
        searchEdit.setHintTextColor(Color.WHITE);
        searchEdit.setBackgroundColor(Color.TRANSPARENT);
        searchEdit.setHint("Search");

        InputFilter[] fArray = new InputFilter[2];
        fArray[0] = new InputFilter.LengthFilter(40);
        fArray[1] = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {

                for (int i = start; i < end; i++) {

                    if (!Character.isLetterOrDigit(source.charAt(i)))
                        return "";
                }


                return null;


            }
        };
        searchEdit.setFilters(fArray);
        View v = searchView.findViewById(android.support.v7.appcompat.R.id.search_plate);
        v.setBackgroundColor(Color.TRANSPARENT);

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                ArrayList<Country> filterList = new ArrayList<Country>();
                if (s.length() > 0) {
                    for (int i = 0; i < employeeList.size(); i++) {
                        if (employeeList.get(i).getName().toLowerCase().contains(s.toString().toLowerCase())) {
                            filterList.add(employeeList.get(i));
                            eAdapter.updateList(filterList);
                        }
                    }

                } else {
                    eAdapter.updateList(employeeList);
                }
                return false;
            }
        });


        return true;
    }

    private void setdata(){
        ApiCountryCode api = RetroClient.getApiService();
        Call<CountryList> call = api.getMyJSON();
        call.enqueue(new Callback<CountryList>() {
            @Override
            public void onResponse(Call<CountryList> call, Response<CountryList> response) {
                pDialog.dismiss();

                if (response.isSuccessful()) {
                    employeeList = response.body().getEmployee();
                    recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
                    eAdapter = new CountryAdapter(employeeList);
                    RecyclerView.LayoutManager eLayoutManager = new LinearLayoutManager(getApplicationContext());
                    recyclerView.setLayoutManager(eLayoutManager);
                    recyclerView.setItemAnimator(new DefaultItemAnimator());
                    recyclerView.setAdapter(eAdapter);
                }

                else {
                    Toast.makeText(ListCountryActivity.this, "Lost Conection", Toast.LENGTH_SHORT).show();
                }


                eAdapter.SetOnItemClickListener(new CountryAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(View view, int position, Country employee) {
                        Intent intent = new Intent();
                        String code = employee.getDialCode();
                        intent.putExtra("key", code);
                        setResult(Activity.RESULT_OK, intent);
                        finish();
                    }
                });
            }

            @Override
            public void onFailure(Call<CountryList> call, Throwable t) {
                pDialog.dismiss();
                Toast.makeText(ListCountryActivity.this, "Check your internet connection and try again", Toast.LENGTH_SHORT).show();
            }

        });
    }

}
