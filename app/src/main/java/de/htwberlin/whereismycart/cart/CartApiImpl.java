package de.htwberlin.whereismycart.cart;

import android.util.Log;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import de.htwberlin.whereismycart.store.Store;

public class CartApiImpl implements CartApi {

    private static final String TAG = "de.htwberlin.whereismycart.cart.CartApiImpl";

    private static final String ID = "id";
    private static final String STORE = "store";
    private static final String LONGITUDE = "longitude";
    private static final String LATITUDE = "latitude";
    private static final String STATUS =  "status";
    private static final String CREATE_TIME = "createTime";
    private static final String UPDATE_TIME = "updateTime";

    private static final String CARTS = "carts";

    @Override
    public void findAllCartsByStore(Store store,  Consumer<List<Cart>> consumer) {
        FirebaseFirestore.getInstance()
                .collection(CARTS)
                .whereEqualTo("store", store.name())
                .get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        final List<Cart> updates = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            final Cart cart = fromMap(document.getData());
                            updates.add(cart);
                        }
                        consumer.accept(updates);
                    } else {
                        Log.d(TAG, "Error getting documents: ", task.getException());
                    }
                });
    }

    @Override
    public void updateCart(Cart cart) {
        FirebaseFirestore.getInstance()
                .collection(CARTS)
                .document(cart.getId().toString())
                .set(toMap(cart))
                .addOnSuccessListener(aVoid -> Log.d(TAG, "DocumentSnapshot successfully written!"))
                .addOnFailureListener(e -> Log.e(TAG, "Error writing document", e));
    }

    private static Map<String, Object> toMap(Cart cart) {
        final Map<String, Object> map = new HashMap<>();
        map.put(ID, cart.getId().toString());
        map.put(STORE, cart.getStore());
        map.put(LONGITUDE, cart.getLongitude());
        map.put(LATITUDE, cart.getLatitude());
        map.put(STATUS, cart.getStatus());
        map.put(CREATE_TIME, cart.getCreateTime());
        map.put(UPDATE_TIME, cart.getUpdateTime());
        return map;
    }

    private static Cart fromMap(Map<String, Object> map) {
        final Cart cart = new Cart();
        cart.setId(UUID.fromString((String)map.get(ID)));
        cart.setStore(Optional.ofNullable((String)map.get(STORE)).map(Store::valueOf).orElse(null));
        cart.setLongitude(Optional.ofNullable((Double)map.get(LONGITUDE)).orElse(0d));
        cart.setLatitude(Optional.ofNullable((Double)map.get(LATITUDE)).orElse(0d));
        cart.setStatus(Optional.ofNullable((String)map.get(STATUS)).map(CartStatus::valueOf).orElse(null));
        cart.setCreateTime(Optional.ofNullable((Long)map.get(CREATE_TIME)).orElse(0L));
        cart.setUpdateTime(Optional.ofNullable((Long)map.get(UPDATE_TIME)).orElse(0L));
        return cart;
    }
}
