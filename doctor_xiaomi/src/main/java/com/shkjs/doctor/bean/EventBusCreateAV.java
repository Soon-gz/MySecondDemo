package com.shkjs.doctor.bean;

/**
 * Created by Administrator on 2016/10/24.
 */

public class EventBusCreateAV {
    private String type;
    private String UserId;
    private String name;
    private String age;
    private String sex;
    private String head;
    private String level;

    public EventBusCreateAV(String type, String userId, String name, String age, String sex, String head, String level) {
        this.type = type;
        UserId = userId;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.head = head;
        this.level = level;
    }

    public EventBusCreateAV(String type, String userId, String name, String age, String sex, String head) {
        this.type = type;
        UserId = userId;
        this.name = name;
        this.age = age;
        this.sex = sex;
        this.head = head;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getHead() {
        return head;
    }

    public void setHead(String head) {
        this.head = head;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUserId() {
        return UserId;
    }

    public void setUserId(String userId) {
        UserId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }
}
