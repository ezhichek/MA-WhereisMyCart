package de.htwberlin.whereismycart.cart;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.UUID;

import de.htwberlin.whereismycart.store.Store;
import de.htwberlin.whereismycart.location.Coordinates;

@Entity(tableName = "carts")
public class Cart {

    @PrimaryKey
    @NonNull
    private UUID id;

    @ColumnInfo(name = "store")
    @NonNull
    private Store store;

    @ColumnInfo(name = "longitude")
    @NonNull
    private double longitude;

    @ColumnInfo(name = "latitude")
    @NonNull
    private double latitude;

    @ColumnInfo(name = "status")
    @NonNull
    private CartStatus status;

    @ColumnInfo(name = "create_time")
    @NonNull
    private long createTime;

    @ColumnInfo(name = "update_time")
    @NonNull
    private long updateTime;

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Store getStore() {
        return store;
    }

    public void setStore(Store store) {
        this.store = store;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public CartStatus getStatus() {
        return status;
    }

    public void setStatus(CartStatus status) {
        this.status = status;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(long updateTime) {
        this.updateTime = updateTime;
    }

    public Coordinates getCoordinates() {
        return new Coordinates(longitude, latitude);
    }

    @Override
    public String toString() {
        return "Cart{" +
                "id=" + id +
                ", store=" + store +
                ", longitude=" + longitude +
                ", latitude=" + latitude +
                ", status=" + status +
                ", createTime=" + createTime +
                ", updateTime=" + updateTime +
                '}';
    }
}
