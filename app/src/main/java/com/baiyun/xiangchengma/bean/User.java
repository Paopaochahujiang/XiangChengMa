package com.baiyun.xiangchengma.bean;


/**
 * 用户信息表
 */
public class User {

    private Integer id;
    private String name;
    private String password;

    public User(Integer id, String name) {
        this.id = id;
        this.name = name;
    }

    public User(String name) {

        this.name = name;
    }

    public User() {
    }

    public User(Integer id, String name, String password) {
        this.id = id;
        this.name = name;
        this.password = password;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", name='" + name + '\'' +
                +
                '}';
    }
}