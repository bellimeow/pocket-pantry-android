package se.liu.tdp028_pocketpantry.ViewModels;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import androidx.annotation.NonNull;
import java.util.List;

import se.liu.tdp028_pocketpantry.R;
import se.liu.tdp028_pocketpantry.Models.RecipeItem;

public class RecipesListAdapter extends ArrayAdapter<RecipeItem>  {

    public interface OnClickListener {
        void onRecipeItemClick(View v, int position);

    }
    private final Activity context;
    private final OnClickListener listener;
    private final List<RecipeItem> items;

    public RecipesListAdapter(Activity context, @NonNull OnClickListener listener,
                              @NonNull List<RecipeItem> objects) {
        super(context, R.layout.recipe_list_item, objects);
        this.context = context;
        this.listener = listener;
        this.items = objects;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.fragment_recipe_single_card, null, true);

        TextView recipeName = (TextView) rowView.findViewById(R.id.recipe_name);
        RecipeItem recipeItem = items.get(position);
        String name = recipeItem.getRecipeName();
        recipeName.setText(name);

        TextView recipeShortDescription =
                (TextView) rowView.findViewById(R.id.recipe_short_description);
        recipeShortDescription.setText(items.get(position).getRecipeShortDescription());

        rowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                listener.onRecipeItemClick(view, position);
            }
        });

        return rowView;
    }

}
