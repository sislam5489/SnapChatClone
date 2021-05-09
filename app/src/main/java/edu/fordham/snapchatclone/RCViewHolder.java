package edu.fordham.snapchatclone;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

public class RCViewHolder extends RecyclerView.ViewHolder {
    public TextView mEmail;
    public Button mFollow;


    public RCViewHolder(@NonNull View itemView) {
        super(itemView);
        mEmail = itemView.findViewById(R.id.email);
        mFollow = itemView.findViewById(R.id.follow);
    }
}
