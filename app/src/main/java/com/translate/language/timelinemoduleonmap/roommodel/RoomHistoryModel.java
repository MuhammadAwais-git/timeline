package com.translate.language.timelinemoduleonmap.roommodel;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.translate.language.timelinemoduleonmap.roomdatabase.MyLatLngLocation;
import com.translate.language.timelinemoduleonmap.roomdatabase.type_converter.LatlngTypeLocationConverter;
import com.translate.language.timelinemoduleonmap.roomdatabase.type_converter.LatlngTypeLocationConverterHistory;

import java.util.List;

/*@Entity(tableName = "RoomHistoryModel")
@TypeConverters(LatlngTypeLocationConverterHistory.class)
public class RoomHistoryModel {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Integer id;

    @ColumnInfo(name = "date")
    public String date;
    @TypeConverters(LatlngTypeLocationConverterHistory.class)
    @ColumnInfo(name = "list")
    public List<MyLatLngLocation> list;

    public RoomHistoryModel() {

    }

    public RoomHistoryModel(String date, List<MyLatLngLocation> list) {
        this.date = date;
        this.list = list;
    }*/
import android.os.Parcel;
import android.os.Parcelable;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;
import com.translate.language.timelinemoduleonmap.roomdatabase.MyLatLngLocation;
import com.translate.language.timelinemoduleonmap.roomdatabase.type_converter.LatlngTypeLocationConverterHistory;

import java.util.List;

@Entity(tableName = "RoomHistoryModel")
@TypeConverters(LatlngTypeLocationConverterHistory.class)
public class RoomHistoryModel implements Parcelable {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Integer id;

    @ColumnInfo(name = "date")
    public String date;

    @TypeConverters(LatlngTypeLocationConverterHistory.class)
    @ColumnInfo(name = "list")
    public List<MyLatLngLocation> list;

    public RoomHistoryModel() {

    }

    public RoomHistoryModel(String date, List<MyLatLngLocation> list) {
        this.date = date;
        this.list = list;
    }

    protected RoomHistoryModel(Parcel in) {
        id = in.readInt();
        date = in.readString();
        list = in.createTypedArrayList(MyLatLngLocation.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(date);
        dest.writeTypedList(list);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<RoomHistoryModel> CREATOR = new Creator<RoomHistoryModel>() {
        @Override
        public RoomHistoryModel createFromParcel(Parcel in) {
            return new RoomHistoryModel(in);
        }

        @Override
        public RoomHistoryModel[] newArray(int size) {
            return new RoomHistoryModel[size];
        }
    };


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public List<MyLatLngLocation> getList() {
        return list;
    }

    public void setList(List<MyLatLngLocation> list) {
        this.list = list;
    }
}
