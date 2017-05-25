package com.example.musab.chatapp;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by Musab on 5/18/2017.
 */

public class Adapter_User_names extends ArrayAdapter<String> {
private final Activity context;
CheckBox check;
        TextView names;
        ArrayList<String> users = new ArrayList<String>();
        ArrayList<String> selected=new ArrayList<>();
public Adapter_User_names(Activity context, ArrayList<String> users) {
        super(context, R.layout.adapter_user_names, users);
        // TODO Auto-generated constructor stub

        this.context=context;
        this.users = users;
        }

public View getView(final int position, View view, ViewGroup parent) {


    LayoutInflater inflater = context.getLayoutInflater();
    final View rowView = inflater.inflate(R.layout.adapter_user_names, null, true);
    final ViewHolder holder = new ViewHolder();
    holder.check = (CheckBox) rowView.findViewById(R.id.check_box);
    holder.names = (TextView) rowView.findViewById(R.id.names);
    holder.names.setText(users.get(position));
    holder.check.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (holder.check.isChecked()) {
                selected.add(users.get(position));
                Log.i("Selected",selected.toString());
            } else if (!holder.check.isChecked()) {
                for (int i = 0; i < selected.size(); i++) {
                    if (selected.get(i).equals(holder.names.getText())) {
                        selected.remove(i);
                    }
                }
            }

        }
    });

    return rowView;
}
    public ArrayList<String> Users_Selected(){
        return selected;
    }
    static class ViewHolder {
        private TextView names;
        private CheckBox check;
    }
}
