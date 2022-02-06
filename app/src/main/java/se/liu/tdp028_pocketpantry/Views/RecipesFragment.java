package se.liu.tdp028_pocketpantry.Views;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;

import se.liu.tdp028_pocketpantry.Models.RecipeItem;
import se.liu.tdp028_pocketpantry.R;
import se.liu.tdp028_pocketpantry.ViewModels.RecipesListAdapter;

public class RecipesFragment extends Fragment implements RecipesListAdapter.OnClickListener{

    public interface OnClickListener {
        void onAddRecipeClick(View v);
    }
    private final MainActivity currentActivity;
    RecipesListAdapter adapter;
    ArrayList<RecipeItem> testRecipesList = new ArrayList<>();

    public RecipesFragment(MainActivity activity) {
        super(R.layout.fragment_recipes);
        this.currentActivity = activity;
    }

    public static RecipesFragment newInstance(MainActivity activity) {
        RecipesFragment fragment = new RecipesFragment(activity);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
        }
        testRecipesList = currentActivity.getRecipesList();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_recipes, container, false);
        ListView recipeListView = (ListView) view.findViewById(R.id.recipes_listview);

        adapter = new RecipesListAdapter(currentActivity, this, testRecipesList);
        recipeListView.setAdapter(adapter);

        Button addRecipe = (Button) view.findViewById(R.id.add_new_recipe);
        addRecipe.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentActivity.onAddRecipeClick(view);
            }
        });

        return view;
    }

    @Override
    public void onRecipeItemClick(View v, int position) {
        Bundle bundle = new Bundle();
        bundle.putInt("Selected Recipe", position);
        currentActivity.swapBottomNavigationFragment("Recipe Single Fragment", bundle);

    }

    public void updateRecipeListAdapter() {
        adapter.notifyDataSetChanged();
    }
}