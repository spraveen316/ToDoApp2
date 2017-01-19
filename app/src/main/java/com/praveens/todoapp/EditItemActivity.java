package com.praveens.todoapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import static android.content.ContentValues.TAG;
import static android.webkit.ConsoleMessage.MessageLevel.LOG;
import static com.praveens.todoapp.R.id.etEditText;

public class EditItemActivity extends AppCompatActivity {

    EditText etEditText;
    int editItemPosition;
    long editItemId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        String editItemText = getIntent().getStringExtra("editItemText");
        super.onCreate(savedInstanceState);

        editItemPosition = getIntent().getIntExtra("editItemPosition", 0);
        setContentView(R.layout.activity_edit_item);

        etEditText = (EditText) findViewById(R.id.etEditItemText);
        etEditText.setText(editItemText);
        etEditText.setSelection(etEditText.getText().length());

        editItemId = getIntent().getLongExtra("editItemId", 0);

    }

    public void onEditItem(View view) {
        String editedText = etEditText.getText().toString();
        Intent data = new Intent();
        data.putExtra("editedText", editedText);
        data.putExtra("editItemPosition", editItemPosition);
        data.putExtra("editItemId", editItemId);
        Log.d(TAG, "in onEditItem, editedText=" + editedText + ", editItemPosition=" + editItemPosition + ", editItemId=" + editItemId);
        setResult(RESULT_OK, data);
        this.finish();
    }
}
