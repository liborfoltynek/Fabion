package com.fotolibb.fabion;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Libb on 28.10.2017.
 */

public class FabionEventBaseAdapter
        extends BaseAdapter {
    Context context;
    List<FabionEvent> rowItems;
    FabionUser fabionUser;

    public FabionEventBaseAdapter(Context context, List<FabionEvent> items, FabionUser fabionUser) {
        this.context = context;
        this.rowItems = items;
        this.fabionUser = fabionUser;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder hld = null;
        LayoutInflater lInflater = (LayoutInflater)
                context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = lInflater.inflate(R.layout.list_item, null);
            hld = new ViewHolder();
            hld.txtTime = (TextView) convertView.findViewById(R.id.evDetailTime);
            hld.txtSubject = (TextView) convertView.findViewById(R.id.evDetailSubject);
            hld.imageUser = (ImageView) convertView.findViewById(R.id.obrazek);
            hld.imageDelete = (ImageView) convertView.findViewById(R.id.imDelete);
            hld.txtLogin = (TextView) convertView.findViewById(R.id.evDetailLogin);
            convertView.setTag(hld);
        } else {
            hld = (ViewHolder) convertView.getTag();
        }
        FabionEvent polozka = (FabionEvent) getItem(position);
        hld.txtTime.setText(polozka.getTimeFrom() + " - " + polozka.getTimeTo());
        hld.txtSubject.setText(polozka.getSubject());
        hld.txtLogin.setText(polozka.getLogin());
        hld.imageUser.setImageResource(polozka.getImage());

        if (polozka.getLogin().equals(fabionUser.Login)) {
            hld.imageDelete.setVisibility(View.VISIBLE);
            hld.imageDelete.setContentDescription(Integer.toString(polozka.getId()));
        } else {
            hld.imageDelete.setVisibility(View.GONE);
        }

        return convertView;
    }

    @Override
    public int getCount() {
        return rowItems.size();
    }

    @Override
    public Object getItem(int position) {
        return rowItems.get(position);
    }

    @Override
    public long getItemId(int position) {
        return rowItems.indexOf(getItem(position));
    }

    private class ViewHolder {
        ImageView imageUser;
        ImageView imageDelete;
        TextView txtSubject;
        TextView txtLogin;
        TextView txtTime;
    }
}