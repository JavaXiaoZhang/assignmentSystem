package com.zq.entity;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

/**
 *
 * @author ZQ
 */
public class Task implements Serializable {

    private static final long serialVersionUID = 5077670175092807127L;

    private Long id;

    private String taskName;

    private String taskDesc;

    private String taskType;

    private String choiceMode;

    private String startTime;

    private String deadTime;

    private String finishTime;

    private String filename;

    private String filepath;

    private String isDelete;

    private Long updateUser;

    private Date updateTime;

    private Long[] memberArr;

    private Long[] classIdArr;

    private String grade;

    private List<ClassEntity> classList;

    public List<ClassEntity> getClassList() {
        return classList;
    }

    public void setClassList(List<ClassEntity> classList) {
        this.classList = classList;
    }

    //0为进行中，1为已结束
    private String statusShow;

    private String isAdmin;

    private String reviewState;

    private TaskContent content;

    private List<TaskContent> unReviewedTaskContentList;

    private List<TaskContent> reviewedTaskContentList;

    public String getChoiceMode() {
        return choiceMode;
    }

    public void setChoiceMode(String choiceMode) {
        this.choiceMode = choiceMode;
    }

    public TaskContent getContent() {
        return content;
    }

    public void setContent(TaskContent content) {
        this.content = content;
    }

    public String getReviewState() {
        return reviewState;
    }

    public void setReviewState(String reviewState) {
        this.reviewState = reviewState;
    }

    public List<TaskContent> getUnReviewedTaskContentList() {
        return unReviewedTaskContentList;
    }

    public void setUnReviewedTaskContentList(List<TaskContent> unReviewedTaskContentList) {
        this.unReviewedTaskContentList = unReviewedTaskContentList;
    }

    public List<TaskContent> getReviewedTaskContentList() {
        return reviewedTaskContentList;
    }

    public void setReviewedTaskContentList(List<TaskContent> reviewedTaskContentList) {
        this.reviewedTaskContentList = reviewedTaskContentList;
    }

    public String getIsAdmin() {
        return isAdmin;
    }

    public void setIsAdmin(String isAdmin) {
        this.isAdmin = isAdmin;
        if ("1".equals(isAdmin)){
            try {
                statusShow = ( new SimpleDateFormat("yyyy-MM-dd HH:mm").parse(deadTime).compareTo(new Date())==1?"0":"1");
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
    }

    public String getGrade() {
        return grade;
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public String getStatusShow() {
        return statusShow;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName == null ? null : taskName.trim();
    }

    public String getTaskDesc() {
        return taskDesc;
    }

    public void setTaskDesc(String taskDesc) {
        this.taskDesc = taskDesc == null ? null : taskDesc.trim();
    }

    public String getTaskType() {
        return taskType;
    }

    public void setTaskType(String taskType) {
        this.taskType = taskType == null ? null : taskType.trim();
    }

    public String getDeadTime() {
        return deadTime;
    }

    public void setDeadTime(String deadTime) {
        this.deadTime = deadTime == null ? null : deadTime.trim();
    }
    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime == null ? null : startTime.trim();
    }

    public String getFinishTime() {
        return finishTime;
    }

    public void setFinishTime(String finishTime) {
        this.finishTime = finishTime == null ? null : finishTime.trim();
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename == null ? null : filename.trim();
    }

    public String getFilepath() {
        return filepath;
    }

    public void setFilepath(String filepath) {
        this.filepath = filepath == null ? null : filepath.trim();
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

    public Long[] getMemberArr() {
        return memberArr;
    }

    public void setMemberArr(Long[] memberArr) {
        this.memberArr = memberArr;
    }

    public Long[] getClassIdArr() {
        return classIdArr;
    }

    public void setClassIdArr(Long[] classIdArr) {
        this.classIdArr = classIdArr;
    }

    @Override
    public String toString() {
        return "Task{" +
                "id=" + id +
                ", taskName='" + taskName + '\'' +
                ", taskDesc='" + taskDesc + '\'' +
                ", taskType='" + taskType + '\'' +
                ", deadTime='" + deadTime + '\'' +
                ", startTime='" + startTime + '\'' +
                ", finishTime='" + finishTime + '\'' +
                ", filename='" + filename + '\'' +
                ", filepath='" + filepath + '\'' +
                ", isDelete='" + isDelete + '\'' +
                ", updateUser=" + updateUser +
                ", updateTime=" + updateTime +
                ", memberArr=" + Arrays.toString(memberArr) +
                ", classIdArr=" + Arrays.toString(classIdArr) +
                '}';
    }
}