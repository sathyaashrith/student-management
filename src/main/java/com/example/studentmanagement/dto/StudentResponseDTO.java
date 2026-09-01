package com.example.studentmanagement.dto;

public class StudentResponseDTO {

    private Long id;
    private String name;
    private Integer age;
    private String email;
    private String course;
    private String phone;
    private String city;

    public StudentResponseDTO() {
    }

    public StudentResponseDTO(Long id, String name, Integer age,
                              String email, String course,
                              String phone, String city) {

        this.id = id;
        this.name = name;
        this.age = age;
        this.email = email;
        this.course = course;
        this.phone = phone;
        this.city = city;
    }

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

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getCourse() {
        return course;
    }

    public void setCourse(String course) {
        this.course = course;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }
}