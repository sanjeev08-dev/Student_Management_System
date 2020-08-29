package com.example.studentmanagementsystem.Adopter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.studentmanagementsystem.Model.Students;
import com.example.studentmanagementsystem.R;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

import de.hdodenhof.circleimageview.CircleImageView;

public class StudentsAdaptor extends FirestoreRecyclerAdapter<Students, StudentsAdaptor.StudentsHolder> {

    private Context mContext;
    private CircleImageView image;

    public StudentsAdaptor(@NonNull FirestoreRecyclerOptions<Students> options) {
        super(options);
    }

    @Override
    protected void onBindViewHolder(@NonNull StudentsHolder holder, int position, @NonNull Students model) {
        holder.itemView.setTag(R.string.call, model.getPhone());
        holder.itemView.setTag(R.string.email, model.getEmail());
        holder.name.setText(model.getName());
        Glide.with(mContext).load(model.getImageURL()).into(image);

    }

    @NonNull
    @Override
    public StudentsHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        mContext = parent.getContext();
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_view_item, parent, false);
        return new StudentsHolder(view);
    }

    class StudentsHolder extends RecyclerView.ViewHolder {

        TextView name;

        StudentsHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.name);
            image = itemView.findViewById(R.id.image);
        }
    }
}
