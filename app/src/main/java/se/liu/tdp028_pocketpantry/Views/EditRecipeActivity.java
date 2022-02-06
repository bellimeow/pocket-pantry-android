package se.liu.tdp028_pocketpantry.Views;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import se.liu.tdp028_pocketpantry.Models.Ingredient;
import se.liu.tdp028_pocketpantry.Models.RecipeItem;
import se.liu.tdp028_pocketpantry.R;

public class EditRecipeActivity extends AppCompatActivity {

    ArrayAdapter<Ingredient> addedIngredientsAdapter = null;
    ArrayAdapter spinnerAdapter = null;
    ArrayList<Ingredient> addedIngredients = new ArrayList<>();
    private Button addRecipeButton;
    private Button addIngredientButton;
    public EditRecipeActivity() { super(R.layout.new_recipe); }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_recipe);

        //Recipe Title
        EditText recipeTitle = (EditText) findViewById(R.id.edit_recipe_name);

        //Ingredients added
        ListView addedIngredientsListView = (ListView) findViewById(R.id.ingredients_listview);
        addedIngredientsAdapter = new ArrayAdapter<Ingredient>(this, R.layout.ingredient_list_item,
                addedIngredients);
        addedIngredientsListView.setAdapter(addedIngredientsAdapter);

        //Add ingredient
        EditText ingredientName = (EditText) findViewById(R.id.type_ingredient);
        EditText amountAdded = (EditText) findViewById(R.id.amount_ingredient);

        //Measurement unit spinner
        String[] measurementUnits = getResources().getStringArray(R.array.measurement_unit_array);
        Spinner unitSpinner = (Spinner) findViewById(R.id.measurement_unit_spinner);
        unitSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        spinnerAdapter = new ArrayAdapter<>(this,
                R.layout.support_simple_spinner_dropdown_item, measurementUnits);
        spinnerAdapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        unitSpinner.setAdapter(spinnerAdapter);

        //Add ingredients to recipe
        addIngredientButton = (Button) findViewById(R.id.add_ingredient_button);
        addIngredientButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String ingredientType = ingredientName.getText().toString();
                double amount = Double.parseDouble(amountAdded.getText().toString());
                Ingredient.Unit unit =
                        Ingredient.Unit.fromString(unitSpinner.getSelectedItem().toString());
                Ingredient ingredient = new Ingredient(ingredientType, amount, unit);

                addedIngredients.add(ingredient);

                addedIngredientsAdapter.notifyDataSetChanged();
            }
        });

        // Instance of short recipe description
        EditText recipeShortDescriptionText = (EditText) findViewById(R.id.edit_recipe_short_description);

        // Instance of long recipe description
        EditText recipeLongDescriptionText = (EditText) findViewById(R.id.recipe_description);

        // Add new recipe
        addRecipeButton = (Button) findViewById(R.id.add_recipe_button);
        addRecipeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean correctUserInput = true;

                // Check if title is missing for the recipe
                if (recipeTitle.getText().length() < 0) {
                    correctUserInput = false;
                    recipeTitle.setError("Missing title");
                }
                // Check if recipe are missing ingredients
                if (addedIngredients.isEmpty()) {
                    correctUserInput = false;
                    ingredientName.setError("You must add at least one ingredient");
                }

                if (correctUserInput) {
                    String recipeName = recipeTitle.getText().toString();
                    String recipeShortDescription = recipeShortDescriptionText.getText().toString();
                    String recipeLongDescription = recipeLongDescriptionText.getText().toString();
                    RecipeItem recipeItem = new RecipeItem(recipeName, recipeShortDescription,
                            recipeLongDescription, addedIngredients);

                    //Send recipe object to MainActivity
                    Intent intent = new Intent();
                    intent.putExtra("RecipeItem", recipeItem);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }

}
