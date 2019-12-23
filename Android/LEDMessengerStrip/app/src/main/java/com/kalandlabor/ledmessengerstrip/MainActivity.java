package com.kalandlabor.ledmessengerstrip;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.loader.content.AsyncTaskLoader;

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
import android.widget.TextView;
import android.widget.Toast;

import java.lang.ref.WeakReference;
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
    private final int REQ_CODE_BLUETOOTH = 200;
    String textToSend;
    static BluetoothMessenger btm;
    final int ADD_TYPE = 1;
    final int ERROR_TYPE = 2;
    final int DEV_ERROR_TYPE = 3;

    public static MyBluetoothTask btt = null;

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
        btt = new MyBluetoothTask(MainActivity.this);
        btt.execute();
    }



    @Override
    protected void onResume(){
        super.onResume();
        btt.restartMyBluetoothTask();
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
        openDialog(ADD_TYPE);
    }

    private void openDialog( int type) {
        if(type == ADD_TYPE) {
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
        }if (type == ERROR_TYPE){
            LayoutInflater factory = LayoutInflater.from(this);
            final View dialogView = factory.inflate(R.layout.check_bluetooth_dialog, null);
            final AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.setView(dialogView);
            dialogView.findViewById(R.id.OK_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btt.cancel(true);
                    btt.restartMyBluetoothTask();
                    dialog.dismiss();
                }
            });
            dialogView.findViewById(R.id.Cancel_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    finishAndRemoveTask();
                }
            });
            dialog.show();
        }if (type == DEV_ERROR_TYPE) {
            LayoutInflater factory = LayoutInflater.from(this);
            final View dialogView = factory.inflate(R.layout.check_bluetooth_dialog, null);
            final AlertDialog dialog = new AlertDialog.Builder(this).create();
            dialog.setView(dialogView);
            TextView tv = dialogView.findViewById(R.id.error_dialog_text);
            tv.setText(R.string.bluetooth_settings_message);
            dialogView.findViewById(R.id.OK_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    btt.cancel(true);
                    openBluetoothSettings();
                    dialog.dismiss();
                }
            });
            dialogView.findViewById(R.id.Cancel_btn).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    finishAndRemoveTask();
                }
            });
            dialog.show();
        }
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
            openBluetoothSettings();
            return true;
        }
        if (id == R.id.faq) {
            Toast.makeText(context, "Implement faq", Toast.LENGTH_SHORT).show();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void openBluetoothSettings(){
        Intent intentOpenBluetoothSettings = new Intent();
        intentOpenBluetoothSettings.setAction(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS);
        startActivityForResult(intentOpenBluetoothSettings, REQ_CODE_BLUETOOTH);
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
            case REQ_CODE_BLUETOOTH: {
                btt.cancel(true);
                btt.restartMyBluetoothTask();
                break;
            }
        }
    }

    private void sendToBluetooth(String textToSend) {
        if(btm.getClientSocket() != null) {
            btm.sendMessage(textToSend);
        } else {
            openDialog(ERROR_TYPE);
        }
    }

    public void itemClicked(int position){
        Toast.makeText(context, R.string.sent + textToSend, Toast.LENGTH_SHORT);
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

    static class MyBluetoothTask extends AsyncTask<Void, Void, String> {
        private final WeakReference<MainActivity> weakActivity;
        ProgressDialog progressDialog;
        final int ERROR_TYPE = 2;
        final int DEV_ERROR_TYPE = 3;
        MyBluetoothTask myBtt = null;

        MyBluetoothTask(MainActivity myActivity) {
            this.weakActivity = new WeakReference<>(myActivity);
        }

        @Override
        public String doInBackground(Void... params) {
                Log.d("xxx", "Run doInBackground");
                return btm.initBluetooth();
        }

        @Override
        public void onPostExecute(String result) {
            MainActivity activity = weakActivity.get();
            if (activity == null
                    || activity.isFinishing()
                    || activity.isDestroyed()) {
                return;
            } else {
                if (result.equals("BLUETOOTH ERROR") && !isCancelled()) {
                    Log.d("xxx", "dev NOK");
                    progressDialog.dismiss();
                    activity.openDialog(DEV_ERROR_TYPE);
                } else if (result.equals("clientsocket Error") && ! isCancelled()){
                    Log.d("xxx", "socket NOK");
                    progressDialog.dismiss();
                    activity.openDialog(ERROR_TYPE);
                } else {
                    Log.d("xxx", "Bluetooth OK");
                    progressDialog.dismiss();
                }
            }
        }

        @Override
        protected void onPreExecute() {
            myBtt = this;
            progressDialog = ProgressDialog.show(weakActivity.get(),
                    weakActivity.get().getResources().getString(R.string.bluetooth_check),
                    "");
        }

        @Override
        protected void onCancelled() {
            super.onCancelled();
            myBtt = null;
        }

        public void restartMyBluetoothTask(){
            Log.d("xxx", "MybluetoothTask restart");
            myBtt = new MyBluetoothTask(weakActivity.get());
            myBtt.execute();
        }
    }
}


