package se.liu.tdp028_pocketpantry.Models;

import android.widget.ArrayAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import se.liu.tdp028_pocketpantry.Models.Ingredient;

public class RecipeItem implements Serializable {

    private String recipeName;
    private String recipeShortDescription;
    private String recipeLongDescription;
    private List<Ingredient> ingredients;


    public RecipeItem(String recipeName, String recipeShortDescription) {
        this.recipeName = recipeName;
        this.recipeShortDescription = recipeShortDescription;
    }

    public RecipeItem() {
        ingredients= new ArrayList<>();
    }

    public RecipeItem(Map<String, Object> recipeData) {
        recipeName = (String) recipeData.get("recipeName");
        recipeShortDescription = (String) recipeData.get("recipeShortDescription");
        recipeLongDescription = (String) recipeData.get("recipeLongDescription");

        ingredients = new ArrayList<>();
        ArrayList<Map<String, Object>> ingredientData
                = (ArrayList<Map<String, Object>>)recipeData.get("ingredients");

        for (Map<String, Object> ingredient : ingredientData ) {
            ingredients.add(new Ingredient(ingredient));
        }
    }

    public RecipeItem(String recipeName, String recipeShortDescription,
                      String recipeLongDescription, List<Ingredient> ingredients) {
        this.recipeName = recipeName;
        this.recipeShortDescription = recipeShortDescription;
        this.recipeLongDescription = recipeLongDescription;
        this.ingredients = ingredients;
    }

    public void setRecipeName(String name) {
        recipeName = name;
    }

    public void setRecipeShortDescription(String name) {
        recipeShortDescription = name;
    }

    public void setRecipeLongDescription(String name) {
        recipeLongDescription = name;
    }

    public String getRecipeName() {
        return recipeName;
    }

    public String getRecipeShortDescription() {
        return recipeShortDescription;
    }

    public String getRecipeLongDescription() {
        return recipeLongDescription;
    }

    public List<Ingredient> getIngredients() {
        return ingredients;
    }

}
