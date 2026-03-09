package com.example.passwordmanager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private final List<VaultItemEntity> mData;
    private final FragmentActivity mActivity;

    public CustomAdapter(List<VaultItemEntity> data, FragmentActivity activity) {
        this.mData = data;
        this.mActivity = activity;
    }

    public void deleteItem(int position) {
        mData.remove(position);
        notifyItemRemoved(position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.itemView.setOnClickListener(v -> {
            int position = viewHolder.getAdapterPosition();
            if (position == RecyclerView.NO_POSITION) {
                return;
            }
            VaultItemEntity item = mData.get(position);
            CustomBottomSheetDialogFragment bottomSheetDialogFragment = new CustomBottomSheetDialogFragment(item, position, this);
            bottomSheetDialogFragment.show(mActivity.getSupportFragmentManager(), bottomSheetDialogFragment.getTag());
        });
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        VaultItemEntity item = mData.get(position);
        holder.titleTextView.setText(item.name);
        holder.descriptionTextView.setText(item.getDisplaySubtitle());
        holder.imageView.setImageResource(item.iconResId);
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
