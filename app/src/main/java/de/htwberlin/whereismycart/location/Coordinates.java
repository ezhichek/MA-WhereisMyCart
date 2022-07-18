package de.htwberlin.whereismycart.location;

import android.location.Location;

import java.util.Objects;

public class Coordinates {

    private final double longitude;

    private final double latitude;

    public Coordinates(Location location) {
        this(location.getLongitude(), location.getLatitude());
    }

    public Coordinates(double longitude, double latitude) {
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public double getLatitude() {
        return latitude;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        Coordinates that = (Coordinates)o;
        return toLong(that.longitude) == toLong(longitude) && toLong(that.latitude) == toLong(latitude);
    }

    @Override
    public int hashCode() {
        return Objects.hash(toLong(longitude), toLong(latitude));
    }

    public int distanceTo(Coordinates coordinates) {
        final Location location1 = new Location("Location 1");
        location1.setLongitude(getLongitude());
        location1.setLatitude(getLatitude());

        final Location location2 = new Location("Location 2");
        location2.setLongitude(coordinates.getLongitude());
        location2.setLatitude(coordinates.getLatitude());

        return (int)location1.distanceTo(location2);
    }

    private static long toLong(double d) {
        return (long)d * 1000000;
    }

}
