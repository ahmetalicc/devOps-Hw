package org.sau.devopsproject2.Entity;

import jakarta.persistence.*;


@Entity
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;
    @Column(name = "name")
    private String name;
    @Column(name = "address")
    private String address;

    @Column(name = "image_url", length = 1024)
    private String img_url;

    public Person() {
    }

    public Person(String name, String address, String img_url) {
        this.name = name;
        this.address = address;
        this.img_url = img_url;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getImg_url(){return img_url;}

    public void  setImg_url(String img_url){this.img_url = img_url;}

    @Override
    public String toString() {
        return "Person{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", address='" + address + '\'' +
                ", img_url='" + img_url + '\'' +
                '}';
    }
}
