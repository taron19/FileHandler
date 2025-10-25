package test.files;

import java.util.Objects;

public class LocationPoint {
    private String street;
    private String city;
    private String zip;

    public LocationPoint(String street, String city, String zip) {
        this.street = street;
        this.city = city;
        this.zip = zip;
    }

    public LocationPoint() {
    }

    public String getStreet() {
        return street;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getZip() {
        return zip;
    }

    public void setZip(String zip) {
        this.zip = zip;
    }

    public void setStreet(String street) {
        this.street = street;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        LocationPoint that = (LocationPoint) object;
        return Objects.equals(street, that.street) && Objects.equals(city, that.city) && Objects.equals(zip, that.zip);
    }

    @Override
    public int hashCode() {
        return Objects.hash(street, city, zip);
    }

    @Override
    public String toString() {
        return "LocationPoint{" +
                "street='" + street + '\'' +
                ", city='" + city + '\'' +
                ", zip='" + zip + '\'' +
                '}';
    }
}
