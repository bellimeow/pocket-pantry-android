package se.liu.tdp028_pocketpantry.Models;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreDatabase {

    private final static String SHOPPING_LIST_COLLECTION_KEY = "shoppingList";
    private final static String UNCHECKED_OBJECT_KEY = "uncheckedItem";
    private final static String CHECKED_OBJECT_KEY = "checkedItem";
    private final static String RECIPES_LIST_COLLECTION_KEY = "recipesList";
    private final static String RECIPES_OBJECT_KEY = "recipes";

    private static FirestoreDatabase instance;
    private FirebaseFirestore db;

    public FirestoreDatabase() {
        db = FirebaseFirestore.getInstance();
    }

    public static FirestoreDatabase getInstance() {
        if (instance == null) {
            instance = new FirestoreDatabase();
        }
        return instance;
    }

    public Task<Void> saveShoppingListData(ArrayList<String> uncheckedList,
                                     ArrayList<String> checkedList ) {
        Map<String, Object> shoppingListItems = new HashMap<>();
        shoppingListItems.put(UNCHECKED_OBJECT_KEY, uncheckedList);
        shoppingListItems.put(CHECKED_OBJECT_KEY, checkedList);

        String userId = currentUser().getUid();

        Task<Void> addedDocRef =
                db.collection(SHOPPING_LIST_COLLECTION_KEY).document(userId).set(shoppingListItems);

        addedDocRef.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "ShoppingListData added with ID:");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding ShoppingListData", e);
                    }
                });
        return addedDocRef;
    }

    public Task<DocumentSnapshot> getShoppingListData() {
        String userId = currentUser().getUid();

        Task<DocumentSnapshot> docRef = db.collection(SHOPPING_LIST_COLLECTION_KEY).document(userId).get();

        return docRef;
    }

    public Task<Void> saveRecipesListData(ArrayList<RecipeItem> recipesList) {
        Map<String, Object> recipesListItems = new HashMap<>();
        recipesListItems.put(RECIPES_OBJECT_KEY, recipesList);

        String userId = currentUser().getUid();

        Task<Void> addedDocRef =
                db.collection(RECIPES_LIST_COLLECTION_KEY).document(userId).set(recipesListItems);

        addedDocRef.addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "RecipesListData added with ID: ");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error adding RecipesListData", e);
                    }
                });
        return addedDocRef;
    }

    public Task<DocumentSnapshot> getRecipesListData() {
        String userId = currentUser().getUid();

        Task<DocumentSnapshot> docRef = db.collection(RECIPES_LIST_COLLECTION_KEY).document(userId).get();

        return docRef;
    }

    public FirebaseUser currentUser() {
        FirebaseUser currentFirebaseUser = FirebaseAuth.getInstance().getCurrentUser() ;
        return currentFirebaseUser;
    }
}
