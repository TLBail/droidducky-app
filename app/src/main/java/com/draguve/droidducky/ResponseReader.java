package com.draguve.droidducky;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Debug;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.util.Arrays;

public class ResponseReader extends AppCompatActivity {

    String fileName;
    String filePath;
    Boolean canEdit;
    String scriptType;

    EditText responseTextBox;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_response_reader);

        Intent callingIntent = getIntent();
        fileName = callingIntent.getExtras().getString("fileName", null);
        filePath = callingIntent.getExtras().getString("filePath", null);
        canEdit = callingIntent.getExtras().getBoolean("canEdit",false);
        scriptType = callingIntent.getExtras().getString("scripttype",null);


        //If cant edit need to hide the elements where we have the save and run code
        if(!canEdit){
            findViewById(R.id.canEdit).setVisibility(View.INVISIBLE);
        }

        if(fileName == null || filePath == null){
            onBackPressed();
        }

        //Read the file
        StringBuilder text = new StringBuilder();
        try{
            File file = new File(filePath);
            if(file.exists()) {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();
            }else{
                //this is a new file
                file.createNewFile();
            }
        }catch (Exception e ){
            e.printStackTrace();
        }


        responseTextBox = findViewById(R.id.response_read);
        EditText nameBox = findViewById(R.id.response_name);

        nameBox.setText(fileName);

        if(!canEdit){
            JSONObject json=null;
            String jsonText="";
            try{
                json = new JSONObject(text.toString());
                jsonText = json.toString(4);
            }catch(Exception e){
                e.printStackTrace();
            }
            responseTextBox.setText(jsonText);
            responseTextBox.setKeyListener(null);
            nameBox.setKeyListener(null);
        }else{
           responseTextBox.setText(text.toString());
        }

        responseTextBox.setHorizontallyScrolling(true);
        responseTextBox.setHorizontalScrollBarEnabled(true);
        responseTextBox.setVerticalScrollBarEnabled(true);

        //Set what happens when the buttons are pressed
        final Button saveButton = findViewById(R.id.save);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveScript(filePath);
            }
        });

        final Button saveAsButton = findViewById(R.id.saveas);
        saveAsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SaveAs(filePath);
            }
        });

        final Button runButton = findViewById(R.id.runcode);
        runButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(scriptType.equals("duckyscript")){
                    final int result = 1;
                    Intent executeIntent = new Intent(getApplicationContext(), ExecuterActivity.class);
                    executeIntent.putExtra("fileName", fileName);
                    executeIntent.putExtra("filePath", filePath);
                    executeIntent.putExtra("scripttype","duckyscript");
                    startActivityForResult(executeIntent, result);
                }else if(scriptType.equals("js")){
                    final int result = 1;
                    Intent executerIntent = new Intent(getApplicationContext(), JSExecuterActivity.class);
                    executerIntent.putExtra("filePath", filePath);
                    startActivityForResult(executerIntent, result);
                }

            }
        });


        final Toolbar toolbar = findViewById(R.id.response_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("View File");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


    }

    public void goBackToSelector() {
        Intent goingBack = new Intent();
        setResult(RESULT_OK, goingBack);
        finish();
    }

    void saveScript(final String SaveLocation) {
        if (canEdit) {
            new MaterialDialog.Builder(this)
                    .title("Do you want to overwrite the script")
                    .positiveText("Save Script")
                    .negativeText("Cancel")
                    .neutralText("Don't Save")
                    .onPositive(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            writeFile(SaveLocation,responseTextBox.getText().toString());
                            goBackToSelector();
                        }
                    })
                    .onNeutral(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            goBackToSelector();
                        }
                    })
                    .onNegative(new MaterialDialog.SingleButtonCallback() {
                        @Override
                        public void onClick(MaterialDialog dialog, DialogAction which) {
                            dialog.dismiss();
                        }
                    })
                    .show();
        }
    }

    void SaveAs(String filePath){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("new FileName");

        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(filePath);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                writeFile(input.getText().toString(),responseTextBox.getText().toString());
                goBackToSelector();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    void writeFile(String filePath, String data) {

        File outFile = new File(filePath);
        FileOutputStream out;
        try {
            out = new FileOutputStream(outFile, false);
            byte[] contents = data.getBytes();
            out.write(contents);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        goBackToSelector();
        return true;
    }

    @Override
    public void onBackPressed() {
        goBackToSelector();
    }
}
