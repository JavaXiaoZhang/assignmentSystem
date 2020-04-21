package com.zq.mapper;

import com.zq.commons.base.IBaseDao;
import com.zq.entity.ClassEntity;
import com.zq.entity.Task;
import com.zq.entity.TaskContent;
import com.zq.entity.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * @author ZQ
 */
public interface TaskMapper extends IBaseDao<Task> {
    /**
     * 根据userId查询个人任务
     * @param userId
     * @return
     */
    List<Task> queryPersonalTaskByUserId(Long userId);

    /**
     * 删除个人任务
     * @param userId
     * @param taskId
     */
    void deleteTask(Long userId, Long taskId);

    /**
     * 新增任务并返回主键
     * @param task
     * @return
     */
    void insertTaskWithReturn(Task task);

    /**
     * 根据组id查询团队任务
     * @param id
     * @return
     */
    List<Task> queryGroupTaskByGroupId(Long id);

    /**
     * 新增管理员的任务关系
     * @param userId
     * @param taskId
     */
    void insertRelationWithAdmin(Long userId, Long taskId);

    //void insertRelationWithPersonal(Long userId, Long taskId);


    /**
     * 根据团队id删除任务
     * @param groupId
     * @param userId
     */
    void deleteTaskByGroupId(Long groupId, Long userId);

    /**
     * 新增班级的任务关系
     * @param taskId
     * @param classIdArr
     * @param userId
     */
    void insertRelationWithClass(Long taskId, Long[] classIdArr, Long userId);

    /**
     * 新增成员的任务关系
     * @param taskId
     * @param memberArr
     * @param userId
     */
    void insertRelationWithUsers(Long taskId, Long[] memberArr, Long userId);

    void insertRelationWithClassmates(Long taskId, List<Long> userIdList, Long userId);

    List<Long> getUserIdByClassIds(String classIds);

    List<Task> getTaskByUserId(Long userId);

    Integer getTotalContent(Long taskId);

    List<TaskContent> queryContentListByTaskId(Long taskId);

    List<ClassEntity> getClassListByTaskId(Long taskId);

    List<Long> getUserIdListByTaskId(Long taskId);
}