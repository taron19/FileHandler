package test.files;

import com.fasterxml.jackson.annotation.JsonProperty;


import java.util.Arrays;
import java.util.Objects;

public class Person {

    private String name;
    private int age;
    @JsonProperty("isStudent")
    private boolean isStudent;
    private String[] courses;
    private LocationPoint address;

    public Person(String name, int age, boolean isStudent, String[] courses, LocationPoint address) {
        this.name = name;
        this.age = age;
        this.isStudent = isStudent;
        this.courses = courses;
        this.address = address;
    }

    public Person() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @JsonProperty("isStudent")
    public boolean isStudent() {
        return isStudent;
    }

    @JsonProperty("isStudent")
    public void setStudent(boolean student) {
        isStudent = student;
    }

    public String[] getCourses() {
        return courses;
    }

    public void setCourses(String[] courses) {
        this.courses = courses;
    }

    public LocationPoint getAddress() {
        return address;
    }

    public void setAddress(LocationPoint address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object object) {

        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        Person person = (Person) object;
        return age == person.age && isStudent == person.isStudent && Objects.equals(name, person.name) && Objects.deepEquals(courses, person.courses) && Objects.equals(address, person.address);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, age, isStudent, Arrays.hashCode(courses), address);
    }

    @Override
    public String toString() {
        return "Person{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", isStudent=" + isStudent +
                ", courses=" + Arrays.toString(courses) +
                ", address=" + address +
                '}';
    }
}
