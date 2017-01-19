package com.praveens.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.praveens.todoapp.model.Todo;
import com.praveens.todoapp.sqllite.TodoItemDatabaseHelper;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

import static android.R.attr.id;
import static android.content.ContentValues.TAG;

public class MainActivity extends AppCompatActivity {

    List<Todo> todoItems;
    ArrayAdapter<Todo> aToDoAdapter;
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
            removeItem(position);
            aToDoAdapter.notifyDataSetChanged();
            return true;
        });

        lvItems.setOnItemClickListener((adapterView, view, position, id) -> {
            Intent i = new Intent(this, EditItemActivity.class);
            i.putExtra("editItemId", todoItems.get(position).id);
            i.putExtra("editItemText", todoItems.get(position).text);
            i.putExtra("editItemPosition", position);
            startActivityForResult(i, REQUEST_CODE);
        });

    }

    private void readItems() {
        TodoItemDatabaseHelper dbHelper = TodoItemDatabaseHelper.getInstance(this);
        todoItems = dbHelper.getAllTodos();
    }

    private void removeItem(int removeItemPosition) {
        TodoItemDatabaseHelper dbHelper = TodoItemDatabaseHelper.getInstance(this);
        dbHelper.deleteTodo(todoItems.get(removeItemPosition));
        todoItems.remove(removeItemPosition);
    }

    private long writeItem(Todo todo) {
        TodoItemDatabaseHelper dbHelper = TodoItemDatabaseHelper.getInstance(this);
        return dbHelper.addTodo(todo);
    }

    private long updateItem(Todo todo) {
        TodoItemDatabaseHelper dbHelper = TodoItemDatabaseHelper.getInstance(this);
        return dbHelper.updateToDo(todo);
    }

    public void populateArrayItems() {
        todoItems = new ArrayList<Todo>();
        readItems();
        aToDoAdapter = new ArrayAdapter<Todo>(this, android.R.layout.simple_list_item_1, todoItems);
    }

    public void onAddItem(View view) {
        if (StringUtils.isBlank(etEditText.getText().toString())) {
            Toast.makeText(this, "Cannot add empty item", Toast.LENGTH_SHORT).show();
            return;
        }
        Todo addItem = new Todo(etEditText.getText().toString());
        addItem.id = writeItem(addItem);
        aToDoAdapter.add(addItem);
        etEditText.setText("");
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            String editedText = data.getExtras().getString("editedText");
            int editItemPosition = data.getExtras().getInt("editItemPosition");
            long editItemId = data.getExtras().getLong("editItemId");
            Log.d(TAG, "in onActivityResult, editedText=" + editedText + ", editItemPosition=" + editItemPosition + ", editItemId=" + editItemId);
            if (StringUtils.isBlank(editedText)) {
                // if edited item is blank it is treated as a remove
                removeItem(editItemPosition);
            } else {
                TodoItemDatabaseHelper dbHelper = TodoItemDatabaseHelper.getInstance(this);
                Todo addItem = dbHelper.getTodo(editItemId);
                addItem.text = editedText;
                addItem.id = updateItem(addItem);
                todoItems.set(editItemPosition, addItem);
            }
            aToDoAdapter.notifyDataSetChanged();
        }
    }
}
