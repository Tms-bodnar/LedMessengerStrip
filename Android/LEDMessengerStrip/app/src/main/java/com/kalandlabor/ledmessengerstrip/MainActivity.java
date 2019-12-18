package com.kalandlabor.ledmessengerstrip;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.os.Handler;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import adapters.CustomGridAdapter;

public class MainActivity extends AppCompatActivity {

    Context context;
    List<Button> buttonList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getApplicationContext();
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addNewMessage(view);
            }
        });
        Resources res = getResources();
        buttonsInit();
        GridView gridView = findViewById(R.id.grid_view);
        CustomGridAdapter gridAdapter = new CustomGridAdapter(MainActivity.this, buttonList);
        gridView.setAdapter(gridAdapter);

    }

    private void buttonsInit() {
        buttonList = new ArrayList<>();
        for (int i = 0; i < 10; i++){
           buttonList.add(new Button(this));
        }
        buttonList.get(0).setText("Köszi!");
        buttonList.get(1).setText("Köszönöm!");
        buttonList.get(2).setText("Szia!");
        buttonList.get(3).setText("Bocsi!");
        buttonList.get(4).setText("Bocsánat");
        buttonList.get(5).setText("Neked is szép napot!");
        buttonList.get(6).setText("Nyugi");
        buttonList.get(7).setText("Nyugalom a hosszú élet titka!");
        buttonList.get(8).setText("Ne dudálj, köszi!");
        buttonList.get(9).setText("Ne villogj, nem megy jobban!");
    }

    private void addNewMessage(View view) {
        openDialog();
    }

    private void openDialog() {
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.bluetooth_settings) {
            Toast.makeText(context, R.string.bluetooth_settings_message, Toast.LENGTH_SHORT).show();
            Intent intentOpenBluetoothSettings = new Intent();
            intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
            startActivity(intentOpenBluetoothSettings);
            return true;
        }
        if (id == R.id.faq) {
            Toast.makeText(context, "Implement faq", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void speechToText(View view) {
        Snackbar.make(view, "implement speechToText", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
    }

    public void itemClicked(int position){

    }
}

