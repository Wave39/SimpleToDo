package com.wave39.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;

public class EditItemActivity extends AppCompatActivity {
    int itemPosition;
    String itemText;
    EditText etItemText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        etItemText = (EditText)findViewById(R.id.etItem);
        itemText = getIntent().getStringExtra("itemText");
        itemPosition = getIntent().getIntExtra("itemPosition", 0);
        etItemText.setText(itemText);
    }

    public void onSaveItem(View v) {
        String itemText = etItemText.getText().toString();
        if (itemText.length() > 0) {
            Intent data = new Intent();
            data.putExtra("itemText", itemText);
            data.putExtra("itemPosition", itemPosition);
            setResult(RESULT_OK, data);
            finish();
        }
    }

}
