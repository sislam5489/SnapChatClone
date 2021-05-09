package edu.fordham.snapchatclone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import org.w3c.dom.Text;

//implement View Onclick Listener to let user click view
public class StoryViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    public TextView mEmail;
    public LinearLayout mLayout;

    public StoryViewHolder(@NonNull View itemView) {
        super(itemView);
        itemView.setOnClickListener(this);
        mEmail = itemView.findViewById(R.id.email);
        mLayout= itemView.findViewById(R.id.layout);
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(view.getContext(),DisplayImage.class);
        //Bundle b = new Bundle();
        intent.putExtra("userId",mEmail.getTag().toString());
        intent.putExtra("chatOrStory",mLayout.getTag().toString());

        //b.putString("userId",mEmail.getTag().toString());
      //  b.putString("chatOrStory",mLayout.getTag().toString());
   //     intent.putExtras(b);
        view.getContext().startActivity(intent);
    }
}
