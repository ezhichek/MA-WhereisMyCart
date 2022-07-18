package de.htwberlin.whereismycart.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import de.htwberlin.whereismycart.R;
import de.htwberlin.whereismycart.cart.Cart;
import de.htwberlin.whereismycart.location.Coordinates;

public class CartListItemAdapter extends RecyclerView.Adapter<CartListItemAdapter.ViewHolder> {

    private final LayoutInflater inflater;

    private final ItemClickListener clickListener;

    private List<Cart> carts = new ArrayList<>();

    private Coordinates location;

    public CartListItemAdapter(Context context, ItemClickListener clickListener) {
        this.inflater = LayoutInflater.from(context);
        this.clickListener = clickListener;
    }

    public void setCarts(List<Cart> carts) {
        final int oldSize = this.carts.size();
        final int newSize = carts.size();
        this.carts = carts;
        sort();
        if (oldSize == newSize) {
            notifyItemRangeChanged(0, carts.size());
        } else {
            notifyDataSetChanged();
        }
    }

    public void setLocation(Coordinates location) {
        if (this.location == null || !this.location.equals(location)) {
            this.location = location;
            sort();
            notifyItemRangeChanged(0, carts.size());
        }
    }

    public void remove(int index) {
        carts.remove(index);
        notifyItemRemoved(index);
    }

    private void sort() {
        if (location != null) {
            carts.sort(this::compareCarts);
        }
    }

    private int compareCarts(Cart cart1, Cart cart2) {
        int dist1 = location.distanceTo(cart1.getCoordinates());
        int dist2 = location.distanceTo(cart2.getCoordinates());
        return Integer.compare(dist1, dist2);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.cart_list_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Cart cart = carts.get(position);
        if (location != null) {
            final long dist = cart.getCoordinates().distanceTo(location);
            String distTxt = dist > 1000 ? (dist / 1000 + " km") : dist + " m";
            holder.textView.setText(distTxt);
        } else {
            holder.textView.setText("");
        }
    }

    @Override
    public int getItemCount() {
        return carts.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        final TextView textView;

        ViewHolder(View itemView) {
            super(itemView);

            textView = itemView.findViewById(R.id.cart_list_item_text);

            itemView.findViewById(R.id.cart_list_item_confirm_button).setOnClickListener(this::onConfirmButtonClicked);
            itemView.findViewById(R.id.cart_list_item_delete_button).setOnClickListener(this::onDeleteButtonClicked);
            itemView.findViewById(R.id.cart_list_item_map_button).setOnClickListener(this::onMapButtonClicked);
        }

        private void onConfirmButtonClicked(View view) {
            if (clickListener != null) {
                final int position = getAdapterPosition();
                final Cart cart = carts.get(position);
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Mark as collected")
                        .setMessage("Are you sure you want to mark as collected?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            clickListener.onConfirmClicked(cart, position);
                            dialog.dismiss();
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        }

        private void onDeleteButtonClicked(View view) {
            if (clickListener != null) {
                final int position = getAdapterPosition();
                final Cart cart = carts.get(position);
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Mark as not found")
                        .setMessage("Are you sure you want to mark as not found?")
                        .setPositiveButton("Yes", (dialog, which) -> {
                            clickListener.onDeleteClicked(cart, position);
                            dialog.dismiss();
                        })
                        .setNegativeButton("No", (dialog, which) -> dialog.dismiss())
                        .show();
            }
        }

        private void onMapButtonClicked(View view) {
            if (clickListener != null) {
                int position = getAdapterPosition();
                final Cart cart = carts.get(position);
                clickListener.onMapClicked(cart, position);
            }
        }
    }

    public interface ItemClickListener {

        void onMapClicked(Cart cart, int index);

        void onConfirmClicked(Cart cart, int index);

        void onDeleteClicked(Cart cart, int index);
    }



}
