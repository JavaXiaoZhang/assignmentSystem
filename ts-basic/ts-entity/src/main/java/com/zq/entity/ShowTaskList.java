package com.zq.entity;

import java.util.ArrayList;
import java.util.List;

/**
 * @author ZQ
 * @Date 2020/4/21
 */
public class ShowTaskList {
    private List<Task> doingList;
    private List<Task> waitList;
    private List<Task> doneList;

    public ShowTaskList() {
    }

    public ShowTaskList(List<Task> doingList, List<Task> waitList, List<Task> doneList) {
        this.doingList = doingList;
        this.waitList = waitList;
        this.doneList = doneList;
    }

    public List<Task> getDoingList() {
        return doingList;
    }

    public void setDoingList(List<Task> doingList) {
        this.doingList = doingList;
    }

    public List<Task> getWaitList() {
        return waitList;
    }

    public void setWaitList(List<Task> waitList) {
        this.waitList = waitList;
    }

    public List<Task> getDoneList() {
        return doneList;
    }

    public void setDoneList(List<Task> doneList) {
        this.doneList = doneList;
    }
}
