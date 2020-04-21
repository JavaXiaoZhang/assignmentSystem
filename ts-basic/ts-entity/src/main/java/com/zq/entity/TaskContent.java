package com.zq.entity;

import java.io.Serializable;
import java.util.Date;

/**
 * @author ZQ
 */
public class TaskContent implements Serializable {

    private static final long serialVersionUID = -1L;

    private Long id;

    private String username;

    private String content;

    private String contentFilename;

    private String contentFilepath;

    private String grade;

    private String isDelete;

    private Long updateUser;

    private Date updateTime;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getContentFilename() {
        return contentFilename;
    }

    public void setContentFilename(String contentFilename) {
        this.contentFilename = contentFilename;
    }

    public String getContentFilepath() {
        return contentFilepath;
    }

    public void setContentFilepath(String contentFilepath) {
        this.contentFilepath = contentFilepath;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }

    public String getIsDelete() {
        return isDelete;
    }

    public void setIsDelete(String isDelete) {
        this.isDelete = isDelete == null ? null : isDelete.trim();
    }

    public Long getUpdateUser() {
        return updateUser;
    }

    public void setUpdateUser(Long updateUser) {
        this.updateUser = updateUser;
    }

    public Date getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Date updateTime) {
        this.updateTime = updateTime;
    }
}