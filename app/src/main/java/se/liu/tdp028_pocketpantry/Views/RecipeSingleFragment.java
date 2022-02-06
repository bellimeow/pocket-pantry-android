package se.liu.tdp028_pocketpantry.Views;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import java.util.ArrayList;

import se.liu.tdp028_pocketpantry.Models.Ingredient;
import se.liu.tdp028_pocketpantry.Models.RecipeItem;
import se.liu.tdp028_pocketpantry.R;

public class RecipeSingleFragment extends Fragment {

    public interface OnClickListener {
        void onAddAllIngredientsClick(View v);
    }
    private MainActivity currentActivity;
    ArrayAdapter<Ingredient> adapter = null;

    public RecipeSingleFragment(MainActivity activity) {
        super(R.layout.fragment_recipe_single);
        this.currentActivity = activity;
    }

    public static RecipeSingleFragment newInstance(MainActivity activity) {
        RecipeSingleFragment fragment = new RecipeSingleFragment(activity);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Bundle bundle = getArguments();
        assert bundle != null;
        int position = bundle.getInt("Selected Recipe");
        RecipeItem recipeItem = currentActivity.getRecipesList().get(position);

        View view = inflater.inflate(R.layout.fragment_recipe_single, container, false);

        //Recipe title
        TextView recipeName = (TextView) view.findViewById(R.id.recipe_title);
        recipeName.setText(recipeItem.getRecipeName());

        //Ingredients list
        adapter = new ArrayAdapter<Ingredient>(currentActivity, R.layout.ingredient_list_item,
                recipeItem.getIngredients());
        ListView ingredientsListView = (ListView) view.findViewById(R.id.ingredients_listview);
        ingredientsListView.setAdapter(adapter);

        //Recipe description
        TextView recipeDescription = (TextView) view.findViewById(R.id.recipe_description);
        recipeDescription.setText(recipeItem.getRecipeLongDescription());

        //"Add all ingredients" button
        Button addAllIngredientsButton =
                (Button) view.findViewById(R.id.add_all_ingredients_button);
        addAllIngredientsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(), "Ingredients added to your shopping list",
                        Toast.LENGTH_SHORT).show();
                currentActivity.addIngredientsList =
                        (ArrayList<Ingredient>) recipeItem.getIngredients();
                currentActivity.onAddAllIngredientsClick(view);
            }

        });
        return view;
    }
}
