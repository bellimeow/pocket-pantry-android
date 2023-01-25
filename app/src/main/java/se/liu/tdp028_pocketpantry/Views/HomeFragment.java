package se.liu.tdp028_pocketpantry.Views;

import android.content.res.Resources;
import android.os.Bundle;

import androidx.appcompat.widget.SwitchCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.GridLayout;
import android.widget.GridView;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;

import se.liu.tdp028_pocketpantry.Models.RecipeItem;
import se.liu.tdp028_pocketpantry.R;
import se.liu.tdp028_pocketpantry.ViewModels.RecipesListAdapter;

public class HomeFragment extends Fragment implements RecipesListAdapter.OnClickListener {

    public interface OnClickListener {
        void onSignInClick(View v);
    }

    MainActivity currentActivity;
    private Button signIn;
    private TextView usernameWelcomeText;
    private TextView ingredientCount;
    private RecipesListAdapter adapter = null;

    public HomeFragment(MainActivity activity) {
        currentActivity = (activity);
    }

    public static HomeFragment newInstance(MainActivity activity) {
        HomeFragment fragment = new HomeFragment(activity);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        currentActivity = (MainActivity) getActivity();

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Username display when logged in
        usernameWelcomeText = (TextView) view.findViewById(R.id.logged_in_username);

        // Sign in button
        signIn = (Button) view.findViewById(R.id.sign_in_button);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            signIn.setText("SIGN OUT");
            usernameWelcomeText.setText("Welcome " + FirebaseAuth.getInstance()
                    .getCurrentUser().getDisplayName());
        }
        signIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentActivity.onSignInClick(view);
            }
        });

        // Shopping list item count
        ingredientCount = (TextView) view.findViewById(R.id.amount_ingredients_in_shopping_list);

        if (currentActivity.getShoppingList().isEmpty()) {
            ingredientCount.setVisibility(View.GONE);
        } else {
            ingredientCount.setVisibility(View.VISIBLE);
            setShoppingListItemCount();
        }
        GridView recipeDisplayGrid = (GridView) view.findViewById(R.id.recipe_display_home);
        adapter = new RecipesListAdapter(currentActivity,this,
                currentActivity.getRecipesList());
        recipeDisplayGrid.setAdapter(adapter);

        return view;
        }

        public void setSignInButtonState ( boolean isLoggedIn){
            if (signIn != null) {
                signIn.setText(isLoggedIn ? "SIGN OUT" : "SIGN IN");
            }
        }

        public void setUsernameWelcomeText (String username, boolean signedIn){
            if (usernameWelcomeText != null ) {
                usernameWelcomeText.setText("Welcome " + username);
            }
            if (!signedIn) {
                usernameWelcomeText.setText(username);
            }
        }

        public void setShoppingListItemCount() {
            if (ingredientCount != null) {
                String ingredientCountMessage = getString(R.string.you_have_x_ingredients,
                        currentActivity.getShoppingList().size());
                ingredientCount.setText(ingredientCountMessage);
            }
        }

        @Override
        public void onRecipeItemClick(View v, int position) {
            Bundle bundle = new Bundle();
            bundle.putInt("Selected Recipe", position);
            currentActivity.swapBottomNavigationFragment("Recipe Single Fragment", bundle);
        }

        public void updateRecipeListAdapter() { adapter.notifyDataSetChanged(); }
}
