package com.zq.service.impl;

import com.zq.commons.base.BaseServiceImpl;
import com.zq.commons.base.IBaseDao;
import com.zq.entity.ClassEntity;
import com.zq.entity.Task;
import com.zq.entity.TaskContent;
import com.zq.entity.User;
import com.zq.mapper.TaskMapper;
import com.zq.mapper.UserMapper;
import com.zq.service.ITaskService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ZQ
 * @Date 2020/2/29
 */
@Service
public class TaskServiceImpl extends BaseServiceImpl<Task> implements ITaskService {

    @Autowired
    private TaskMapper taskMapper;

    @Autowired
    private UserMapper userMapper;

//    private Map<String, Task> map = new ConcurrentHashMap<>();

    @Override
    public IBaseDao<Task> getBaseDao() {
        return taskMapper;
    }

    @Override
    public List<Task> queryPersonalTaskByUserId(Long userId) {
        return taskMapper.queryPersonalTaskByUserId(userId);
    }

    @Override
    public void deleteTask(Long userId, Long taskId) {
        taskMapper.deleteTask(userId, taskId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = RuntimeException.class)
    public void insertTask(Task task) {
        //插入任务并返回主键
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String currentTime = dateFormat.format(new Date());
        task.setStartTime(currentTime);
        taskMapper.insertTaskWithReturn(task);
        //将创建者与任务绑定
        Long taskId = task.getId();
        Long userId = task.getUpdateUser();
        taskMapper.insertRelationWithAdmin(userId, taskId);

        //根据任务类型将任务id与userId或班级绑定
        Long[] classIdArr = task.getClassIdArr();
        if (classIdArr !=null){
            //班级任务
            taskMapper.insertRelationWithClass(taskId, classIdArr, userId);
            //再给班级成员添加任务关系
            StringBuilder stringBuilder = new StringBuilder("(");
            for (int i = 0; i < classIdArr.length; i++) {
                stringBuilder.append(classIdArr[i]);
                if ( i != classIdArr.length-1){
                    stringBuilder.append(",");
                }
            }
            stringBuilder.append(")");
            List<Long> userIdList = taskMapper.getUserIdByClassIds(stringBuilder.toString());
            taskMapper.insertRelationWithClassmates(taskId, userIdList, userId);
        }
        if (task.getMemberArr()!=null) {
            //团队任务
            taskMapper.insertRelationWithUsers(taskId, task.getMemberArr(), userId);
        }
        //将对象存入ConcurrentHashMap
//        String key = "task:" + userId + ":" + task.getGroupId();
//        task.setId(taskId);
//        map.put(key,task);

        //return taskId;
    }

    @Override
    public List<Task> queryGroupTaskByGroupId(Long id) {
        return taskMapper.queryGroupTaskByGroupId(id);
    }

    @Override
    public User getUser(Long userId) {
        return userMapper.selectByPrimaryKey(userId);
    }

    @Override
    public List<Long> queryUserId(Long userId, String str) {
        return userMapper.queryUserId(userId, str);
    }

    @Override
    public List<Task> getTaskByUserId(Long userId) {
        return taskMapper.getTaskByUserId(userId);
    }

    @Override
    public Integer getTotalContent(Long taskId) {
        return taskMapper.getTotalContent(taskId);
    }

    @Override
    public List<TaskContent> queryContentListByTaskId(Long taskId) {
        return taskMapper.queryContentListByTaskId(taskId);
    }

    @Override
    public List<ClassEntity> getClassListByTaskId(Long taskId) {
        return taskMapper.getClassListByTaskId(taskId);
    }

    @Override
    public List<Long> getUserIdListByTaskId(Long taskId) {
        return taskMapper.getUserIdListByTaskId(taskId);
    }

//    @Override
//    public Task queryGroupTask(Long userId, Long groupId) {
//        String key = "task:" + userId+":"+groupId;
//        Task task;
//        do {
//            task = map.get(key);
//        }while (task==null);
//        return task;
//    }
}
