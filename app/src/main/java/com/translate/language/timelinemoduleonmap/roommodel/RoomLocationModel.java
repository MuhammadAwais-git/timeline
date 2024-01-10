package com.translate.language.timelinemoduleonmap.roommodel;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.translate.language.timelinemoduleonmap.roomdatabase.MyLatLngLocation;
import com.translate.language.timelinemoduleonmap.roomdatabase.type_converter.LatlngTypeLocationConverter;

import java.util.List;

@Entity(tableName = "RoomLocationModel")
public class RoomLocationModel {

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    public Integer id;

    @ColumnInfo(name = "date")
    public String date;
    @TypeConverters(LatlngTypeLocationConverter.class)
    @ColumnInfo(name = "list")
    public List<MyLatLngLocation> list;

    public RoomLocationModel() {

    }

    public RoomLocationModel(String date, List<MyLatLngLocation> list) {
        this.date = date;
        this.list = list;
    }

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
