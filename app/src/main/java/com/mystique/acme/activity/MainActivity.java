package com.mystique.acme.activity;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.mystique.acme.MainApplication;
import com.mystique.acme.R;
import com.mystique.acme.rest.IO;
import com.mystique.acme.rest.InternetConnectionListener;
import com.santalu.emptyview.EmptyView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity implements InternetConnectionListener {

    private FloatingActionButton fabRefresh;
    private EmptyView emptMain;
    private TextView tvMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        MainApplication.getInstance().setInternetConnectionListener(this);

        //View initialization
        initViews();

        //Call to our api to get the data
        fetchData();

        fabRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fetchData();
            }
        });
    }

    private void initViews() {
        fabRefresh = findViewById(R.id.fab);
        emptMain = findViewById(R.id.emptMain);
        tvMain = findViewById(R.id.tvMain);
    }


    public void fetchData() {
        new IO().getFortuneQuote(emptMain, tvMain);
    }

    @Override
    public void onInternetUnavailable() {
        //Show error view in case there's no internet
        emptMain.error()
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        fetchData();
                    }
                }).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        MainApplication.getInstance().removeInternetConnectionListener();
    }
}
