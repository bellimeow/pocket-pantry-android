package se.liu.tdp028_pocketpantry.Views;

import static android.content.ContentValues.TAG;

import androidx.activity.OnBackPressedCallback;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.FirebaseAuthUIActivityResultContract;
import com.firebase.ui.auth.IdpResponse;
import com.firebase.ui.auth.data.model.FirebaseAuthUIAuthenticationResult;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import se.liu.tdp028_pocketpantry.Models.FirestoreDatabase;
import se.liu.tdp028_pocketpantry.Models.Ingredient;
import se.liu.tdp028_pocketpantry.Models.RecipeItem;
import se.liu.tdp028_pocketpantry.R;

public class MainActivity extends AppCompatActivity implements HomeFragment.OnClickListener,
        RecipeSingleFragment.OnClickListener, RecipesFragment.OnClickListener {

    public final static String UNCHECKED_OBJECT_KEY = "uncheckedItem";
    public final static String CHECKED_OBJECT_KEY = "checkedItem";

    public final static String SHARED_PREFERENCES_KEY = "sharedPreferences";

    private BottomNavigationView bottomNavigationView;

    private ShoppingListFragment shoppingListFragment;
    private HomeFragment homeFragment;
    private RecipesFragment recipesFragment;
    private RecipeSingleFragment recipeSingleFragment;

    private ActivityResultLauncher<Intent> signInLauncher;

    ArrayList<String> shoppingList = new ArrayList<>();

    public ArrayList<String> getShoppingList() {
        return shoppingList;
    }

    ArrayList<String> deletedItems = new ArrayList<>();
    public ArrayList<String> getDeletedItems() {
        return deletedItems;
    }

    ArrayList<RecipeItem> recipesList = new ArrayList<>();
    public ArrayList<RecipeItem> getRecipesList() {
        return recipesList;
    }

    public ArrayList<Ingredient> addIngredientsList = new ArrayList<>();


    public MainActivity(){
        super(R.layout.activity_main);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (shoppingListFragment == null) {
            shoppingListFragment = ShoppingListFragment.newInstance(this);
        }
        if (homeFragment == null) {
            homeFragment = HomeFragment.newInstance(this);
        }
        if (recipesFragment == null) {
            recipesFragment = RecipesFragment.newInstance(this);
        }
        if (recipeSingleFragment == null) {
            recipeSingleFragment = RecipeSingleFragment.newInstance(this);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                    .add(R.id.fragment_container_view, homeFragment, null)
                    .commit();
        }

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                setBottomNavigationVisibility(true);
                FragmentManager manager = getSupportFragmentManager();
                if (homeFragment.isVisible()) {

                    new AlertDialog.Builder(MainActivity.this)
                            .setMessage("Are you sure you want to exit?")
                            .setCancelable(false)
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    saveShoppingListItemsToSharedPreferences();
                                    MainActivity.this.finish();
                                }
                            })
                            .setNegativeButton("No", null)
                            .show();
                } else {
                    manager.popBackStack();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnNavigationItemSelectedListener(bottomNavigationMethod);

        FloatingActionButton shoppingListFab = findViewById(R.id.shopping_list_FAB);
        shoppingListFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggleShoppingListFragment("Shopping List", false);
            }
        });

        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            loadFirestoreData(new FirestoreCallback(){
                @Override
                public void onCallback(ArrayList<String> list) {
                    Log.d(TAG, "Firestore data retrieved");
                    homeFragment.setShoppingListItemCount();
                    homeFragment.updateRecipeListAdapter();
                }
            });
        } else if (savedInstanceState != null ) {
            ArrayList<String> list = savedInstanceState.getStringArrayList(MainActivity.UNCHECKED_OBJECT_KEY);
            if (list != null) {
                shoppingList.clear();
                shoppingList.addAll(list);
            }
            list = savedInstanceState.getStringArrayList(MainActivity.CHECKED_OBJECT_KEY);
            if (list != null) {
                deletedItems.clear();
                deletedItems.addAll(list);
            }
        } else {
            getSharedPreferences();
        }

        signInLauncher = registerForActivityResult(
                new FirebaseAuthUIActivityResultContract(),
                new ActivityResultCallback<FirebaseAuthUIAuthenticationResult>() {
                    @Override
                    public void onActivityResult(FirebaseAuthUIAuthenticationResult result) {
                        onSignInResult(result);
                    }
                }
        );
        //testData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && data.hasExtra("RecipeItem")) {
            recipesList.add((RecipeItem) data.getSerializableExtra("RecipeItem"));
            recipesFragment.updateRecipeListAdapter();
            FirestoreDatabase.getInstance().saveRecipesListData(recipesList);
        }
    }

    private void testData() {
        List<Ingredient> ingredients = new ArrayList<>();
        Ingredient i1 = new Ingredient("Mjölk", 3, Ingredient.Unit.DL);
        Ingredient i2 = new Ingredient("Köttbullar", 500, Ingredient.Unit.G);
        Ingredient i3 = new Ingredient("Pasta", 1, Ingredient.Unit.PCS);
        ingredients.add(i1);
        ingredients.add(i2);
        ingredients.add(i3);

        RecipeItem r1 = new RecipeItem("Köttbullar och Pasta",
                "Short Description, Short DescriptionShort Short Description",
                "Long DescriptionLong DescriptionLong DescriptionLong " +
                        "DescriptionLong DescriptionLong DescriptionLong DescriptionLong " +
                        "DescriptionLong DescriptionLong DescriptionLong DescriptionLong " +
                        "DescriptionLong DescriptionLong DescriptionLong DescriptionLong " +
                        "DescriptionLong DescriptionLong DescriptionLong DescriptionLong " +
                        "DescriptionLong DescriptionLong DescriptionLong DescriptionLong " +
                        "DescriptionLong Description",  ingredients);

        List<Ingredient> ingredients2 = new ArrayList<>();
        Ingredient i12 = new Ingredient("Äpplen", 3, Ingredient.Unit.PCS);
        Ingredient i22 = new Ingredient("Mjöl", 1, Ingredient.Unit.KG);
        Ingredient i32 = new Ingredient("Smör", 250, Ingredient.Unit.G);
        ingredients2.add(i12);
        ingredients2.add(i22);
        ingredients2.add(i32);

        RecipeItem r2 = new RecipeItem("Äppelpaj",
                "Short Description, Short DescriptionShort Short Description",
                "Long DescriptionLong DescriptionLong DescriptionLong " +
                        "DescriptionLong DescriptionLong DescriptionLong DescriptionLong " +
                        "DescriptionLong DescriptionLong DescriptionLong DescriptionLong " +
                        "DescriptionLong DescriptionLong DescriptionLong DescriptionLong " +
                        "DescriptionLong DescriptionLong DescriptionLong DescriptionLong " +
                        "DescriptionLong DescriptionLong DescriptionLong DescriptionLong " +
                        "DescriptionLong Description",  ingredients2);

        recipesList.add(r1);
        recipesList.add(r2);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirestoreDatabase database = FirestoreDatabase.getInstance();
            database.saveShoppingListData(shoppingList, deletedItems);
            database.saveRecipesListData(recipesList);
        } else {
            outState.putStringArrayList(MainActivity.UNCHECKED_OBJECT_KEY, shoppingList);
            outState.putStringArrayList(MainActivity.CHECKED_OBJECT_KEY, shoppingList);
            saveShoppingListItemsToSharedPreferences();
        }
    }

    private void loadFirestoreData(FirestoreCallback firestoreCallback) {
        FirestoreDatabase instance = FirestoreDatabase.getInstance();

        instance.getShoppingListData().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot shoppingListData = task.getResult();

                    Map<String, Object> data = shoppingListData.getData();

                    if (data != null) {

                        shoppingList.clear();
                        deletedItems.clear();
                        shoppingList
                                .addAll((ArrayList<String>) data.get(MainActivity.UNCHECKED_OBJECT_KEY));
                        deletedItems
                                .addAll((ArrayList<String>) data.get(MainActivity.CHECKED_OBJECT_KEY));


                        firestoreCallback.onCallback(shoppingList);
                        homeFragment.setShoppingListItemCount();
                        homeFragment.updateRecipeListAdapter();
                    }

                } else {
                    Log.w(TAG,"Error getting currentActivity.getShoppingList()Data.",
                            task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error getting RecipesListData", e);
            }
        });

        instance.getRecipesListData().addOnCompleteListener(
                new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {

                    DocumentSnapshot recipeListData = task.getResult();

                    recipesList.clear();

                    Map<String, Object> data = recipeListData.getData();

                    if (data != null) {

                        for (Map<String, Object> recipe :
                                (ArrayList<Map<String, Object>>) data.get("recipes")) {
                            RecipeItem recipeItem = new RecipeItem(recipe);

                            recipesList.add(recipeItem);
                        }
                    }

                } else {
                    Log.w(TAG,"Error getting getRecipesListData.",
                            task.getException());
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error getting RecipesListData", e);
            }
        });
    }

    private interface FirestoreCallback {
        void onCallback(ArrayList<String> list);
    }

    private final BottomNavigationView.OnNavigationItemSelectedListener bottomNavigationMethod = new
            BottomNavigationView.OnNavigationItemSelectedListener() {
                @SuppressLint("NonConstantResourceId")
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()){
                        case R.id.page_home:
                            swapBottomNavigationFragment("Home", null);
                            break;
                        case R.id.page_recipes:
                            swapBottomNavigationFragment("Recipes", null);
                            break;
                        case R.id.page_pantry:
                            //swapToFragment("Pantry", true);
                            break;
                    }
                    return true;
                }
            };

    public void swapBottomNavigationFragment(String tag, Bundle bundle) {

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = null;

        switch (tag) {
            case "Home":
                fragment = homeFragment;
                break;
            case "Recipes":
                fragment = recipesFragment;

                break;
            case "Recipe Single Fragment":
                fragment = recipeSingleFragment;
                fragment.setArguments(bundle);
            default:

        }
        transaction.setReorderingAllowed(true);
        transaction.replace(R.id.fragment_container_view, fragment, tag);
        transaction.addToBackStack(null);
        transaction.commit();

        setBottomNavigationVisibility(true);
    }

    private void toggleShoppingListFragment(String tag, boolean navigationVisibility) {
        FragmentManager manager = getSupportFragmentManager();

        if (shoppingListFragment != null && shoppingListFragment.isVisible()) {
            manager.popBackStack();
            setBottomNavigationVisibility(true);
        }
         else {
            FragmentTransaction transaction = manager.beginTransaction();
            transaction.setCustomAnimations(
                    R.anim.slide_in,  // enter
                    R.anim.fade_out,  // exit
                    R.anim.fade_in,   // popEnter
                    R.anim.slide_out  // popExit
            );
            transaction.setReorderingAllowed(true);
            transaction.replace(R.id.fragment_container_view, shoppingListFragment, tag);
            transaction.addToBackStack(null);
            transaction.commit();
            setBottomNavigationVisibility(navigationVisibility);
        }
    }

    public void setBottomNavigationVisibility(boolean visible) {
        bottomNavigationView.setVisibility(visible? View.VISIBLE : View.INVISIBLE);
    }

    public void saveToSharedPreferences(ArrayList<String> list,
                                               String key) {
        Set<String> set =
                new HashSet<>(list);

        SharedPreferences prefs = this.getSharedPreferences(SHARED_PREFERENCES_KEY,
                Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putStringSet(key, set);
        editor.apply();
    }

    public void getSharedPreferences() {
        SharedPreferences prefs = this.getSharedPreferences(SHARED_PREFERENCES_KEY,
                Context.MODE_PRIVATE);

        this.getShoppingList().clear();
        this.getShoppingList()
                .addAll(prefs.getStringSet(UNCHECKED_OBJECT_KEY, new HashSet<>()));

        this.getDeletedItems().clear();
        this.getDeletedItems()
                .addAll(prefs.getStringSet(CHECKED_OBJECT_KEY, new HashSet<>()));
    }

    public void saveShoppingListItemsToSharedPreferences() {
        saveToSharedPreferences(this.getShoppingList(), UNCHECKED_OBJECT_KEY);
        saveToSharedPreferences(this.getDeletedItems(), CHECKED_OBJECT_KEY);
    }

    private void onSignInResult(FirebaseAuthUIAuthenticationResult result) {
        IdpResponse response = result.getIdpResponse();
        if (result.getResultCode() == RESULT_OK) {
            homeFragment.setSignInButtonState(true);

            // Successfully signed in
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            homeFragment.setUsernameWelcomeText(user.getDisplayName(), true);
            homeFragment.setShoppingListItemCount();
            homeFragment.updateRecipeListAdapter();



            loadFirestoreData(new FirestoreCallback(){
                @Override
                public void onCallback(ArrayList<String> list) {
                    homeFragment.setShoppingListItemCount();
                    homeFragment.updateRecipeListAdapter();
                }
            });
        } else {
            Toast toast = Toast.makeText(homeFragment.getContext(),
                    "Sign in failed", Toast.LENGTH_SHORT);
        }
    }

    @Override
    public void onSignInClick(View v) {
        if (FirebaseAuth.getInstance().getCurrentUser() == null) {
            List<AuthUI.IdpConfig> providers = Arrays.asList(
                    new AuthUI.IdpConfig.GoogleBuilder().build(),
                    new AuthUI.IdpConfig.EmailBuilder().build());

            // Create and launch sign-in intent
            Intent signInIntent = AuthUI.getInstance()
                    .createSignInIntentBuilder()
                    .setAvailableProviders(providers)
                    .build();

            signInLauncher.launch(signInIntent);
        } else {
            FirestoreDatabase.getInstance()
                    .saveShoppingListData(shoppingList,
                            deletedItems);
            AuthUI.getInstance()
                    .signOut(this)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast toast = Toast.makeText(homeFragment.getContext(),
                                    "You signed out", Toast.LENGTH_SHORT);
                            toast.show();

                            //Clear shopping list items when user logs out.
                            shoppingList.clear();
                            deletedItems.clear();
                            recipesList.clear();

                            homeFragment.updateRecipeListAdapter();
                            homeFragment.setUsernameWelcomeText("", false);
                            homeFragment.setSignInButtonState(false);
                            homeFragment.setShoppingListItemCount();

                        }
                    });
        }
    }

    @Override
    public void onAddRecipeClick(View v) {
        if (FirebaseAuth.getInstance().getCurrentUser() != null) {
        Intent intent = new Intent(this, EditRecipeActivity.class);
        startActivityForResult(intent, 0);
        } else {
            Toast toast = Toast.makeText(recipesFragment.getContext(),
                    "You need to sign in to add a recipe", Toast.LENGTH_SHORT);
            toast.show();
        }
    }

    @Override
    public void onAddAllIngredientsClick(View v) {
        combineIngredientsToShoppingList();
    }

    private void combineIngredientsToShoppingList() {

        Pattern pattern = Pattern.compile("([\\d.,]+)\\s*(\\w+)\\s(.+)");

        // Check if shopping list item can be converted to ingredient
        ArrayList<Object> list = new ArrayList<>();
        for (String s : shoppingList) {
            Matcher m = pattern.matcher(s);
            if (m.groupCount() == 3 && m.find()) {
                String name = m.group(3);
                Ingredient.Unit unit = Ingredient.Unit.fromString(m.group(2));
                double amount = Double.parseDouble(m.group(1));
                list.add(new Ingredient(name, amount, unit));
            }
            else {
                list.add(s);
            }
        }

        list.addAll(addIngredientsList);

        // Get first ingredient for comparison
        for (int i = 0; i < list.size(); ++i) {
            Object o = list.get(i);
            if (o instanceof Ingredient) {
                Ingredient ingredient = (Ingredient)o;
                // Compare rest of the list with first ingredient
                for (int j = i + 1; j < list.size(); ++j) {
                    o = list.get(j);
                    if (o instanceof Ingredient) {
                        Ingredient nextIngredient = (Ingredient)o;
                        // Compare name of ingredients
                        if (ingredient.getName().equals(nextIngredient.getName())) {
                            double newAmount = ingredient.getAmount();
                            newAmount += nextIngredient.getUnit().convert(ingredient.getUnit(),
                                    nextIngredient.getAmount());
                            ingredient.setAmount(newAmount);
                            // Remove consumed ingredient
                            list.remove(j);
                            --j;
                        }
                    }
                }
            }
        }

        shoppingList.clear();
        for (Object o : list) {
            shoppingList.add(o.toString());
        }

    }
}
