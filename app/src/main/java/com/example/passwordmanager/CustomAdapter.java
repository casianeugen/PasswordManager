package com.example.passwordmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Objects;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private final List<DocumentSnapshot> mData;
    private final FragmentActivity mActivity;

    public CustomAdapter(List<DocumentSnapshot> data, FragmentActivity activity) {
        this.mData = data;
        this.mActivity = activity;
    }

    public void deleteItem(int position) {
        // Delete the item from your dataset
        mData.remove(position);

        // Notify the adapter that an item has been removed at the specified position
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.itemView.setOnClickListener(v -> {
            DocumentSnapshot document = mData.get(viewHolder.getAdapterPosition());
            CustomBottomSheetDialogFragment bottomSheetDialogFragment = new CustomBottomSheetDialogFragment(document, viewHolder.getAdapterPosition(), this);
            Bundle args = new Bundle();
            args.putInt("position", viewHolder.getAdapterPosition());
            bottomSheetDialogFragment.setArguments(args);
            bottomSheetDialogFragment.show(mActivity.getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot document = mData.get(position);
        String title = document.getString("3)Name");
        String description = document.getString("5)Username");

        if (Objects.equals(description, null))
            description = "Credit Card";
        int imageUrl = Objects.requireNonNull(document.getLong("2)Icon")).intValue();

        holder.titleTextView.setText(title);
        holder.descriptionTextView.setText(description);
        holder.imageView.setImageResource(imageUrl);
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView titleTextView;
        public TextView descriptionTextView;
        public ImageView imageView;

        public ViewHolder(View view) {
            super(view);
            titleTextView = view.findViewById(R.id.title);
            descriptionTextView = view.findViewById(R.id.subtitle);
            imageView = view.findViewById(R.id.image_view);
        }
    }
}
