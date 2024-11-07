package com.example.lab1;
public class DTO {
    private String id;  // Chuyển id từ int thành String
    private String name;
    private String date;
    private String brand;

    public DTO(String brand, String date, String id, String name) {
        this.brand = brand;
        this.date = date;
        this.id = id;
        this.name = name;
    }

    public DTO() {
        // Constructor mặc định
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return "DTO{" +
                "brand='" + brand + '\'' +
                ", id='" + id + '\'' +  // Sửa id thành String
                ", name='" + name + '\'' +
                ", date='" + date + '\'' +
                '}';
    }
}
