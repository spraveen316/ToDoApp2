package com.praveens.todoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static com.praveens.todoapp.R.id.etEditText;

public class EditItemActivity extends AppCompatActivity {

    EditText etEditText;
    int editItemPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String editItem = getIntent().getStringExtra("editItem");
        super.onCreate(savedInstanceState);

        editItemPosition = getIntent().getIntExtra("editItemPosition", 0);
        setContentView(R.layout.activity_edit_item);

        etEditText = (EditText) findViewById(R.id.etEditItemText);
        etEditText.setText(editItem);
        etEditText.setSelection(etEditText.getText().length());

    }

    public void onEditItem(View view) {
        String editedText = etEditText.getText().toString();
        Intent data = new Intent();
        data.putExtra("editedText", editedText);
        data.putExtra("editItemPosition", editItemPosition);
        setResult(RESULT_OK, data);
        this.finish();
    }
}
