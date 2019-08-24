package org.mcezario.diff.http;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

public class TestJson {

    @NotNull
    private String id;

    @NotEmpty
    private String name;

    @Min(value=18, message="Age should not be less than 18")
    private int age;

    @NotNull
    private Brand brand;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id=id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name=name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age=age;
    }

    public Brand getBrand() {
        return brand;
    }

    public void setBrand(Brand brand) {
        this.brand=brand;
    }

    enum Brand {VISA}

}
