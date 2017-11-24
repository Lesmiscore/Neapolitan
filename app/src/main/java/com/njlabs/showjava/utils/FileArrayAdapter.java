package com.njlabs.showjava.utils;

import android.content.*;
import android.graphics.drawable.*;
import android.view.*;
import android.widget.*;

import com.nao20010128nao.Neapolitan.*;
import com.njlabs.showjava.modals.*;

import org.apache.commons.io.*;

import java.util.*;

public class FileArrayAdapter extends ArrayAdapter<Item> {

    private final Context context;
    private final int id;
    private final List<Item> items;

    public FileArrayAdapter(Context context, int textViewResourceId,
                            List<Item> objects) {
        super(context, textViewResourceId, objects);
        this.context = context;
        id = textViewResourceId;
        items = objects;
    }

    public Item getItem(int i) {
        return items.get(i);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if (v == null) {
            LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(id, null);
        }

        final Item o = items.get(position);
        if (o != null) {

            TextView filenameView = v.findViewById(R.id.file_name);
            TextView fileSizeView = v.findViewById(R.id.file_size);

            ImageView fileIconView = v.findViewById(R.id.file_icon);

            Drawable image = context.getResources().getDrawable(o.getImage());

            if (FilenameUtils.getExtension(o.getPath()).equals("png") || FilenameUtils.getExtension(o.getPath()).equals("jpg")) {
                image = Drawable.createFromPath(o.getPath());
            }

            fileIconView.setImageDrawable(image);

            filenameView.setText(o.getName());
            fileSizeView.setText(o.getData());
        }
        return v;
    }
}
