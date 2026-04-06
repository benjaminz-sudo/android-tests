package com.example.tp2_1;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class UserAdapter extends ArrayAdapter<User> {
    public UserAdapter(Context context, int resource, ArrayList<User> users )
    {
        super(context,resource, users);
    }
    @Override
    public View getView(int position, View view, ViewGroup parent)
    {
        view = LayoutInflater.from(getContext()).inflate(R.layout.custom_item_list,null, false);
        ImageView image = view.findViewById(R.id.user);
        TextView text1 = view.findViewById(R.id.firstNameLabel);
        TextView text2 = view.findViewById(R.id.lastNameLabel);
        User user = getItem(position);
        text1.setText(user.firstName);
        text2.setText(user.lastName);
        image.setImageResource(user.photo);
        return view;
    }
}
