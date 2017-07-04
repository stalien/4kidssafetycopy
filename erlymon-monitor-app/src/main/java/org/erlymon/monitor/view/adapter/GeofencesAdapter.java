/*
 * Copyright (c) 2016, Sergey Penkovsky <sergey.penkovsky@gmail.com>
 *
 * This file is part of Erlymon Monitor.
 *
 * Erlymon Monitor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Erlymon Monitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Erlymon Monitor.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.erlymon.monitor.view.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import org.erlymon.core.model.data.Device;
import org.erlymon.core.model.data.Geofence;
import org.erlymon.core.model.data.User;
import org.erlymon.monitor.R;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 1/7/16.
 */
public class GeofencesAdapter extends RealmBaseAdapter<Geofence> implements ListAdapter {

    public GeofencesAdapter(Context context, OrderedRealmCollection<Geofence> data) {
        super(context, data);

    }

    /**
     * Реализация класса ViewHolder, хранящего ссылки на виджеты.
     */
    class ViewHolder {
        private TextView name;
        private TextView description;

        public ViewHolder(View itemView) {
            name = (TextView) itemView.findViewById(R.id.name_geofence);
            description = (TextView) itemView.findViewById(R.id.description_geofence);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GeofencesAdapter.ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_geofence,  parent, false);
            viewHolder = new GeofencesAdapter.ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (GeofencesAdapter.ViewHolder) convertView.getTag();
        }

        Geofence item = adapterData.get(position);
        viewHolder.name.setText(item.getName());
        viewHolder.description.setText(item.getDescription());
        return convertView;
    }

}