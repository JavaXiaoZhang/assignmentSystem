package com.zq.entity;

import com.zq.annotation.LogPoint;

import java.io.Serializable;

@LogPoint("类上的注解")
public class Student extends Teacher implements Serializable {
    static {
        System.out.println("Student 静态代码块。。。");
    }

    private String a = "a";
    private static String b = "b";
    private String c = "c";

    @LogPoint("构造方法切点")
    public Student() {
        System.out.println("Student 构造方法。。。");
    }

    @LogPoint(value = "自定义注解")
    public void doSth(){
        System.out.println("Student do sth...");
    }
}
