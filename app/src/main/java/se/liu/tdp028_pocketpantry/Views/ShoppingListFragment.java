package se.liu.tdp028_pocketpantry.Views;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import se.liu.tdp028_pocketpantry.R;
import se.liu.tdp028_pocketpantry.ViewModels.ShoppingListAdapter;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShoppingListFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShoppingListFragment extends Fragment {

    MainActivity currentActivity;
    ShoppingListAdapter adapter = null;
    ShoppingListAdapter adapterDeletedItems = null;

    public ShoppingListFragment(MainActivity activity) {
        super(R.layout.fragment_shopping_list);
        currentActivity = (activity);
    }

    public static ShoppingListFragment newInstance(MainActivity activity) {
        ShoppingListFragment fragment = new ShoppingListFragment(activity);
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping_list, container, false);
        adapter =
                new ShoppingListAdapter(this.getActivity(), currentActivity.getShoppingList(),
                        currentActivity.getDeletedItems(), false );
        adapterDeletedItems =
                new ShoppingListAdapter(this.getActivity(), currentActivity.getDeletedItems(),
                        currentActivity.getShoppingList(), true);

        adapter.setArrayAdapter(adapterDeletedItems);
        adapterDeletedItems.setArrayAdapter(adapter);

        ListView items_list_view = (ListView) view.findViewById(R.id.shopping_listView);
        ListView deleted_items_list_view =
                (ListView) view.findViewById(R.id.deleted_items_listview);

        items_list_view.setAdapter(adapter);
        deleted_items_list_view.setAdapter(adapterDeletedItems);

        final Button buttonAdd = (Button) view.findViewById(R.id.add_button1);
        final EditText editText = (EditText) view.findViewById(R.id.item_editText);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (editText.getText() != null) {
                    currentActivity.getShoppingList().add(editText.getText().toString());
                    editText.setText("");
                    adapter.notifyDataSetChanged();
                }
            }
        });        return view;
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
    }

}