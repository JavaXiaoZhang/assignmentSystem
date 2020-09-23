package com.zq.entity;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Teacher implements Serializable {
    private Integer age;

    static {
        System.out.println("teacher 静态代码块。。。");
    }

    public Teacher() {
        System.out.println("Teacher 构造方法。。。");
    }

    public Teacher(Integer age) {
        this.age = age;
    }

    public Integer getAge() {
        return age;
    }

    public static void main(String[] args) {
        Teacher[] teachers = new Teacher[]{new Teacher(20),new Teacher(30),new Teacher(40)};
        Stream<Teacher> stream = Stream.of(teachers);
        List<Integer> integerList = stream.filter((p) -> p.age > 20).map(Teacher::getAge).collect(Collectors.toList());
        System.out.println(Arrays.toString(integerList.toArray()));
    }
}
