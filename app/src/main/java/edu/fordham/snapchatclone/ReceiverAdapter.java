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

public class ReceiverAdapter extends RecyclerView.Adapter<ReceiverViewHolder> {

    private List<ReceiveObject> usersList;
    //private Context context;

    public ReceiverAdapter(ArrayList<ReceiveObject> usersList){// Context context){
        this.usersList = usersList;
        //this.context = context;
    }
    @NonNull
    @Override
    public ReceiverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview_receiver_item,parent,false);
        ReceiverViewHolder rcv = new ReceiverViewHolder(layoutView);
        return rcv;
    }

    @Override
    public void onBindViewHolder(@NonNull ReceiverViewHolder holder, int position) {
        holder.mEmail.setText(usersList.get(position).getEmail());
        //when click in checkbox
        holder.checkBox.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                boolean receiveState = !usersList.get(holder.getLayoutPosition()).getReceive();
                usersList.get(holder.getLayoutPosition()).setReceive(receiveState);
            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }
}
