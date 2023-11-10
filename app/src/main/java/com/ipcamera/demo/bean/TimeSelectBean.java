package com.ipcamera.demo.bean;

import com.massky.sraum.User;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TimeSelectBean implements Comparable<TimeSelectBean>,Serializable {

    private String name; //姓名

    private int age; // 年龄


    public TimeSelectBean(String name, int age) {
        this.name = name;
        this.age = age;
    }

    // getter && setter
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

    @Override
    public String toString() {
        return "User [name=" + name + ", age=" + age + "]";
    }

    @Override
    public int compareTo(TimeSelectBean user) {           //重写Comparable接口的compareTo方法，
        return this.age - user.getAge();// 根据年龄升序排列，降序修改相减顺序即可
    }
}

//程序运行结果：根据年龄升序排列
//
//        [User [name=张三, age=5], User [name=陈十七, age=17], User [name=王五, age=19], User [name=李四, age=30]]
//
//        3、匿名内部类实现排序

