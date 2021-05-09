package edu.fordham.snapchatclone;

import android.view.View;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ReceiverViewHolder extends RecyclerView.ViewHolder {
    public TextView mEmail;
    public CheckBox checkBox;
    public ReceiverViewHolder(@NonNull View itemView) {
        super(itemView);
        mEmail = itemView.findViewById(R.id.email);
        checkBox = itemView.findViewById(R.id.receive);
    }
}
