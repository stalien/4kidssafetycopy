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
import android.widget.ListAdapter;
import android.widget.QuickContactBadge;
import android.widget.TextView;

import org.erlymon.core.model.data.Device;
import org.erlymon.monitor.R;

import io.realm.OrderedRealmCollection;
import io.realm.RealmBaseAdapter;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 1/7/16.
 */
public class DevicesAdapter extends RealmBaseAdapter<Device> implements ListAdapter {

    public DevicesAdapter(Context context, OrderedRealmCollection<Device> data) {
        super(context, data);
    }

    /**
     * Реализация класса ViewHolder, хранящего ссылки на виджеты.
     */
    class ViewHolder {
        private GridLayout layout;
        private QuickContactBadge quickContactBadge;
        private TextView name;
        private TextView identifier;
        private TextView sim;
        public ViewHolder(View itemView) {
            layout = (GridLayout) itemView.findViewById(R.id.layout);
            quickContactBadge = (QuickContactBadge) itemView.findViewById(R.id.quickContactBadge);
            name = (TextView) itemView.findViewById(R.id.name);
            identifier = (TextView) itemView.findViewById(R.id.identifier);
            sim = (TextView) itemView.findViewById(R.id.sim);
        }
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_device,  parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Device item = adapterData.get(position);
        viewHolder.name.setText(item.getName());
        viewHolder.identifier.setText(item.getUniqueId());
 //       viewHolder.layout.setBackgroundResource(getStatusColorId(item.getStatus()));
        viewHolder.quickContactBadge.setBackgroundResource(getStatusColorId(item.getStatus()));
        return convertView;
    }

    private int getStatusColorId(String status) {
        switch (status) {
            case "online": return R.color.colorOnlineStatus;
            case "offline": return R.color.colorOfflineStatus;
            case "unknown": return R.color.colorUnknownStatus;
            default: return R.color.colorUnknownStatus;
        }
    }
}