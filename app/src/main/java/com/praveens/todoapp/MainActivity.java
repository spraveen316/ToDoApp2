package com.praveens.todoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;

import static android.R.attr.id;
import static android.R.attr.name;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> todoItems;
    ArrayAdapter<String> aToDoAdapter;
    ListView lvItems;
    EditText etEditText;
    private final int REQUEST_CODE = 20;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        populateArrayItems();
        lvItems = (ListView) findViewById(R.id.lvItems);
        lvItems.setAdapter(aToDoAdapter);
        etEditText = (EditText) findViewById(R.id.etEditText);
        lvItems.setOnItemLongClickListener((adapterView, view, position, id) -> {
            todoItems.remove(position);
            aToDoAdapter.notifyDataSetChanged();
            writeItems();
            return true;
        });

        lvItems.setOnItemClickListener((adapterView, view, position, id) -> {
            Intent i = new Intent(this, EditItemActivity.class);
            i.putExtra("editItem", todoItems.get(position));
            i.putExtra("editItemPosition", position);
            startActivityForResult(i, REQUEST_CODE);
        });

    }

    private void readItems() {
        File fileDirs = getFilesDir();
        File file = new File(fileDirs, "todo.txt");
        try {
            todoItems = new ArrayList<String>(FileUtils.readLines(file, Charset.defaultCharset()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void writeItems() {
        File fileDirs = getFilesDir();
        File file = new File(fileDirs, "todo.txt");
        try {
            FileUtils.writeLines(file, todoItems);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void populateArrayItems() {
        todoItems = new ArrayList<String>();
        readItems();
        aToDoAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, todoItems);
    }

    public void onAddItem(View view) {
        aToDoAdapter.add(etEditText.getText().toString());
        etEditText.setText("");
        writeItems();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            String editedText = data.getExtras().getString("editedText");
            int editItemPosition = data.getExtras().getInt("editItemPosition");
            todoItems.set(editItemPosition, editedText);
            aToDoAdapter.notifyDataSetChanged();
            writeItems();
        }
    }
}
