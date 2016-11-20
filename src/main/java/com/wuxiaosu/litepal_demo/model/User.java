package com.wuxiaosu.litepal_demo.model;

import org.litepal.crud.DataSupport;

/**
 * Created by Su on 2016/11/17.
 */

public class User extends DataSupport {
    private int id;
    private String name;
    private String sex;
    private String age;

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

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getAge() {
        return age;
    }

    public void setAge(String age) {
        this.age = age;
    }
}
