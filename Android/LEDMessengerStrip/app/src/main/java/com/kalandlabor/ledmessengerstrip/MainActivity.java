package com.kalandlabor.ledmessengerstrip;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.preference.PreferenceManager;
import android.speech.RecognizerIntent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import adapters.CustomGridAdapter;
import managers.BluetoothMessenger;

public class MainActivity extends AppCompatActivity {

    public Context context;
    List<Button> buttonList;
    EditText newButtonsText;
    GridView gridView;
    CustomGridAdapter gridAdapter;
    SharedPreferences sPrefs;
    List<String> buttonTexts;
    private final int REQ_CODE_SPEECH_INPUT = 100;
    String textToSend;
    BluetoothMessenger btm;


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
        buttonList = new ArrayList<>();
        buttonTexts= new ArrayList<>();
        addNewButton(getResources().getString(R.string.speech_button));
        gridView = findViewById(R.id.grid_view);
        gridAdapter = new CustomGridAdapter(MainActivity.this, buttonList);
        gridView.setAdapter(gridAdapter);
        btm = new BluetoothMessenger();
    }

    @Override
    protected void onResume(){
        super.onResume();
        sPrefs = PreferenceManager.getDefaultSharedPreferences(context);
        Set<String> buttonTextSet = new HashSet<>();
        buttonTextSet =  sPrefs.getStringSet("buttonText", buttonTextSet);
        buttonTexts.addAll(buttonTextSet);
        for ( String text : buttonTexts) {
            addNewButton(text);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = sPrefs.edit();
        Set<String> buttonTextSet = new HashSet<>(buttonTexts);
        buttonTextSet.addAll(buttonTexts);
        editor.putStringSet("buttonText",buttonTextSet).apply();
    }

    private void addNewMessage(View view) {
        openDialog();
    }

    private void openDialog() {
        LayoutInflater factory = LayoutInflater.from(this);
        final View dialogView = factory.inflate(R.layout.add_message_dialog, null);
        final AlertDialog dialog = new AlertDialog.Builder(this).create();
        dialog.setView(dialogView);
        dialogView.findViewById(R.id.new_button_text).requestFocus();
        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE);
        dialogView.findViewById(R.id.save_new_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newButtonsText = (EditText) dialogView.findViewById(R.id.new_button_text);
                buttonTexts.add(newButtonsText.getText().toString());
                addNewButton(newButtonsText.getText().toString());
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    private void addNewButton(String text) {
        Button b =new Button(context);
        b.setText(text);
        buttonList.add(b);
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
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hu-HU");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,
                "Mondd az üzeneted!");
        try {
            startActivityForResult(intent, REQ_CODE_SPEECH_INPUT);
        } catch (ActivityNotFoundException a) {
            a.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case REQ_CODE_SPEECH_INPUT: {
                Log.d("xxx", "activity result");
                if (resultCode == RESULT_OK && null != data) {
                    ArrayList<String> result = data
                            .getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
                    textToSend = result.get(0);
                    sendToBluetooth(textToSend);
                }
                break;
            }
        }
    }

    private void sendToBluetooth(String textToSend) {
        btm.sendMessage(textToSend);
    }

    public void itemClicked(int position){
        Snackbar.make(getWindow().getCurrentFocus(), "Elküldted: " + buttonList.get(position).getText(), Snackbar.LENGTH_LONG)
                .setAction("Action", null).show();
        sendToBluetooth(buttonList.get(position).getText().toString());
    }


    public boolean removeItem(Button button) {
        for ( Button b: buttonList ) {
            if(b.getText() == button.getText()){
                buttonList.remove(b);
                Log.d("xxx","item remove " + button.getText());
                gridAdapter.notifyDataSetChanged();
                return removeButtonText(button);
            }
        }

        return true;
    }

    private boolean removeButtonText(Button button) {
        for (String s : buttonTexts) {
            if (s.equals(button.getText())) {
                buttonTexts.remove(s);
                return true;
            }
        }
        return false;
    }
}


