package com.wave39.simpletodo;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.wave39.simpletodo.Data.TodoItemDatabaseHelper;
import com.wave39.simpletodo.Model.ListItem;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    ArrayList<ListItem> items;
    ArrayAdapter<ListItem> itemsAdapter;
    ListView lvItems;

    private final int EDIT_TEXT_CODE = 2209;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        lvItems = (ListView)findViewById(R.id.lvItems);
        readItems();
        itemsAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, items);
        lvItems.setAdapter(itemsAdapter);
        setupListViewListener();
    }

    private void setupListViewListener() {
        lvItems.setOnItemLongClickListener(
                new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> adapter, View item, int pos, long id) {
                        items.remove(pos);
                        itemsAdapter.notifyDataSetChanged();
                        writeItems();
                        return true;
                    }
                }
        );

        lvItems.setOnItemClickListener(
                new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapter, View item, int pos, long id) {
                        editItem(pos);
                    }
                }
        );
    }

    private void editItem(int position) {
        Intent i = new Intent(MainActivity.this, EditItemActivity.class);
        i.putExtra("itemPosition", position);
        i.putExtra("itemText", items.get(position).listItemString);
        startActivityForResult(i, EDIT_TEXT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_TEXT_CODE) {
            String itemText = data.getExtras().getString("itemText");
            int itemPosition = data.getExtras().getInt("itemPosition", 0);
            ListItem newListItem = new ListItem(itemText);
            items.set(itemPosition, newListItem);
            itemsAdapter.notifyDataSetChanged();
            writeItems();
        }
    }

    public void onAddItem(View v) {
        EditText etNewItem = (EditText)findViewById(R.id.etNewItem);
        String itemText = null;
        if (etNewItem != null) {
            itemText = etNewItem.getText().toString();
        }

        if (itemText != null && itemText.length() > 0) {
            ListItem newListItem = new ListItem(itemText);
            itemsAdapter.add(newListItem);
            etNewItem.setText("");
            writeItems();
        }
    }

    private void readItems() {
        TodoItemDatabaseHelper databaseHelper = TodoItemDatabaseHelper.getInstance(this);
        items = databaseHelper.getAllListItems();
    }

    private void writeItems() {
        TodoItemDatabaseHelper databaseHelper = TodoItemDatabaseHelper.getInstance(this);
        databaseHelper.deleteAllListItems();
        ListItem.reorder(items);
        for (ListItem item : items) {
            databaseHelper.addOrUpdateListItem(item);
        }
    }

}
