package edu.fordham.snapchatclone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.List;

public class StoryAdapter extends RecyclerView.Adapter<StoryViewHolder> {

    private List<StoryObject> usersList;
    //private Context context;

    public StoryAdapter(ArrayList<StoryObject> usersList){// Context context){
        this.usersList = usersList;
        //this.context = context;
    }
    @NonNull
    @Override
    public StoryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_story_item,parent,false);
        StoryViewHolder rcv = new StoryViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull StoryViewHolder holder, int position) {
        holder.mEmail.setText(usersList.get(position).getEmail());
        holder.mEmail.setTag(usersList.get(position).getUid());
        holder.mLayout.setTag(usersList.get(position).getChatOrStory());
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }
}
