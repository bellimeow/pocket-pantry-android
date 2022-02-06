package se.liu.tdp028_pocketpantry.ViewModels;

import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import androidx.core.widget.CompoundButtonCompat;

import com.google.android.material.textfield.TextInputEditText;

import java.util.ArrayList;
import java.util.Objects;

import se.liu.tdp028_pocketpantry.R;

public class ShoppingListAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final ArrayList<String> items;
    private final ArrayList<String> deletedItems;
    private ShoppingListAdapter arrayAdapter;
    private boolean checkedState = false;

    public ShoppingListAdapter(Activity context, ArrayList<String> primaryList,
                               ArrayList<String> secondaryList, boolean checkedState) {
        super(context, R.layout.shopping_list_item, primaryList);
        this.context = context;
        this.items = primaryList;
        this.deletedItems = secondaryList;
        this.checkedState = checkedState;
    }

    public void setArrayAdapter(ShoppingListAdapter adapter) {
        this.arrayAdapter = adapter;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.shopping_list_item, null, true);
        TextInputEditText textInput = (TextInputEditText) rowView.findViewById(R.id.item_text);
        textInput.setText(items.get(position));
        textInput.setInputType(InputType.TYPE_CLASS_TEXT);
        items.set(position, items.get(position));
        textInput.setImeActionLabel("Custom text", KeyEvent.KEYCODE_ENTER);

        // Edit selected field when "Enter" button is pressed and save it to the items list.
        textInput.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event)
            {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) &&
                        (keyCode == KeyEvent.KEYCODE_ENTER))
                {
                    String editedInput = Objects.requireNonNull(textInput.getText()).toString();
                    items.set(position, editedInput);
                    return true;
                }
                return false;
            }
        });

        CheckBox checkBox = rowView.findViewById(R.id.checkbox_item);
        checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final boolean isChecked = ((CheckBox)view).isChecked();
                if (!checkedState) {
                Toast.makeText(context, "Item " + items.get(position) + " added",
                        Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, "Item " + items.get(position) + " removed",
                            Toast.LENGTH_SHORT).show();
                }

                // Add checked item to Ticked items
                String deleted_item = items.get(position);
                items.remove(position);
                deletedItems.add(deleted_item);

                // Update both listviews
                arrayAdapter.notifyDataSetChanged();
                notifyDataSetChanged();
            }
        });

        if (checkedState) {
            textInput.setPaintFlags(textInput.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
            textInput.setTextColor(Color.GRAY);
            checkBox.setChecked(true);
            int states[][] = {{android.R.attr.state_checked}, {}};
            int colors[] = {Color.GRAY, Color.GREEN};
            CompoundButtonCompat.setButtonTintList(checkBox, new ColorStateList(states, colors));
        }

        return rowView;
    }
}