package com.example.passwordmanager;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Objects;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {
    private final List<DocumentSnapshot> mData;
    private final FragmentActivity mActivity;
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();

    public CustomAdapter(List<DocumentSnapshot> data, FragmentActivity activity) {
        this.mData = data;
        this.mActivity = activity;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.custom_list_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.itemView.setOnLongClickListener(v -> {
            PopupMenu popupMenu = new PopupMenu(mActivity, v);
            popupMenu.getMenuInflater().inflate(R.menu.context_menu, popupMenu.getMenu());
            DocumentSnapshot document = mData.get(viewHolder.getAdapterPosition());
            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.edit:
                        Intent i = new Intent(mActivity, AddPasswordActivity.class);
                        String id_edit = document.getId();
                        i.putExtra("documentId", id_edit);
                        mActivity.startActivity(i);
                        return true;
                    case R.id.delete:
                        int position = viewHolder.getAdapterPosition();
                        String id = document.getId();
                        String type = document.getString("1)Type");
                        if (mAuth.getCurrentUser() != null && type != null)
                            FirebaseFirestore.getInstance().collection("users")
                                .document(mAuth.getCurrentUser().getUid())
                                .collection(type).document(id).delete();
                        mData.remove(position);
                        notifyItemRemoved(position);
                        return true;
                    default:
                        return false;
                }
            });
            popupMenu.show();
            return true;
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
