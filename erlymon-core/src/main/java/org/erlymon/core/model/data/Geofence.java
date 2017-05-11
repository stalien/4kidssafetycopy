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
package org.erlymon.core.model.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;

import java.util.Date;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;


public class Geofence extends RealmObject implements Parcelable {
    @PrimaryKey
    private Long id;

    @Since(3.0)
    @SerializedName("name")
    @Expose
    private String name;

    @Since(3.0)
    @SerializedName("description")
    @Expose
    private String description;

    @Since(3.3)
    @SerializedName("area")
    @Expose
    private String area;

    @Since(3.4)
    @SerializedName("calendarId")
    @Expose
    private Long calendarId;


    public Geofence() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getArea() {
        return area;
    }

    public void setArea(String area) { this.area = area; }

    public Long getCalendarId() {
        return calendarId;
    }

    public void setCalendarId(Long calendarId) {
        this.calendarId = calendarId;
    }


    protected Geofence(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readLong();
        name = in.readString();
        description = in.readString();
        area = in.readString();
        calendarId = in.readByte() == 0x00 ? null : in.readLong();

    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(id);
        }
        dest.writeString(name);
        dest.writeString(description);
        dest.writeString(area);
        if (calendarId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(calendarId);
        }
    }

    @SuppressWarnings("unused")
    public static final Creator<Geofence> CREATOR = new Creator<Geofence>() {
        @Override
        public Geofence createFromParcel(Parcel in) {
            return new Geofence(in);
        }

        @Override
        public Geofence[] newArray(int size) {
            return new Geofence[size];
        }
    };

    @Override
    public String toString() {
        return "Geofence{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", area='" + area + '\'' +
                ", calendarId=" + calendarId +
                '}';
    }
}
