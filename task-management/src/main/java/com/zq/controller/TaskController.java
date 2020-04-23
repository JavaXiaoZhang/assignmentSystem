package com.zq.controller;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import com.zq.commons.constant.WEditorResultConstant;
import com.zq.commons.pojo.ResultBean;
import com.zq.commons.pojo.WEditorResultBean;
import com.zq.entity.*;
import com.zq.feign.IClassService;
import com.zq.service.IGroupService;
import com.zq.service.ITaskInfoService;
import com.zq.service.ITaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author ZQ
 * @Date 2020/2/29
 */
@Controller
@RequestMapping("task")
public class TaskController {

    private Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private ITaskService taskService;

    @Autowired
    private ITaskInfoService taskInfoService;

    @Autowired
    private IGroupService groupService;

    @Autowired
    private IClassService classService;

    @Autowired
    private FastFileStorageClient client;

    @Autowired
    private RedisTemplate redisTemplate;

    @Value("${image.server}")
    private String imgServer;

    @GetMapping("index/{userId}")
    public String index(@PathVariable("userId") Long userId, ModelMap modelMap){
        User user = taskService.getUser(userId);

//        Map<String, List<ClassEntity>> map = new HashMap<>(1);
//        map.put("classList", classList);
//        ObjectMapper objectMapper = new ObjectMapper();
//        String json = null;
//        try {
//            json = objectMapper.writeValueAsString(classList);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
        modelMap.put("userId",userId);
//        modelMap.put("classList", classList);
        //modelMap.put("classList", JSONArray.toJSON(classList));
        modelMap.put("role",user.getRole());
        return "index";
    }

    @RequestMapping("logout")
    public String logout(HttpServletResponse response){
        Cookie cookie = new Cookie("userToken", null);
        cookie.setMaxAge(0);
        response.addCookie(cookie);
        return "redirect:http://localhost:9081/sso/sso/index.html";
    }

    @GetMapping("getClassList/{userId}")
    @ResponseBody
    public List<ClassEntity> getClassList(@PathVariable Long userId){
        //User user = taskService.getUser(userId);
        //根据username查询classList
        List<ClassEntity> classListByUserId = classService.getClassListByUserId(userId);
        log.info("classList:{}",classListByUserId);
        return classListByUserId;
    }

    @GetMapping("queryUserId/{userId}/{str}")
    @ResponseBody
    public List<Long> queryUserId(@PathVariable Long userId, @PathVariable String str){
        //根据str检索username
        return taskService.queryUserId(userId, str);
    }

    /**
     * 添加任务
     * @return
     */
    @PostMapping("insertTask")
    public String insertTask(Task task, @RequestParam(value = "members", required = false) String members,
                             @RequestParam(value = "classId[]", required = false) String classIds, MultipartFile file){
        if (task.getTaskType().equals("0")){
            //0是团队任务
            if (task.getChoiceMode().equals("0")){
                //指派班级
                String[] classIdStringArr = classIds.split(",");
                Long[] classIdArr = new Long[classIdStringArr.length];
                for (int i = 0; i < classIdStringArr.length; i++) {
                    classIdArr[i] = Long.valueOf(classIdStringArr[i]);
                }
                task.setClassIdArr(classIdArr);
            }else {
                //指派人员
                String[] memberArr = members.split(",");
                Long[] userIdArr = new Long[memberArr.length];
                for (int i = 0; i < memberArr.length; i++) {
                    userIdArr[i] = Long.valueOf(memberArr[i]);
                }
                task.setMemberArr(userIdArr);
            }
        }

        String filename = file.getOriginalFilename();
        if (!StringUtils.isEmpty(filename)) {
            try {
                String extName = filename.substring(filename.lastIndexOf(".") + 1);
                StorePath storePath = client.uploadFile(null, file.getInputStream(), file.getSize(), extName);
                String filePath = new StringBuilder(imgServer).append(storePath.getFullPath()).toString();
                //将属性添加到task对象中
                task.setFilename(filename);
                task.setFilepath(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        log.info("task:{}",task.toString());
        taskService.insertTask(task);
        return "redirect:http://localhost:9081/task-management/task/index/" + task.getUpdateUser();
    }

    /**
     * 添加任务
     * @return
     */
    @PostMapping("modifyTask")
    public String modifyTask(Task task, @RequestParam(value = "members", required = false) String members,
                             @RequestParam(value = "classId[]", required = false) String classIds, MultipartFile file){
        if (task.getTaskType().equals("0")){
            //0是团队任务
            if (task.getChoiceMode().equals("0")){
                //指派班级
                String[] classIdStringArr = classIds.split(",");
                Long[] classIdArr = new Long[classIdStringArr.length];
                for (int i = 0; i < classIdStringArr.length; i++) {
                    classIdArr[i] = Long.valueOf(classIdStringArr[i]);
                }
                task.setClassIdArr(classIdArr);
            }else {
                //指派人员
                String[] memberArr = members.split(",");
                Long[] userIdArr = new Long[memberArr.length];
                for (int i = 0; i < memberArr.length; i++) {
                    userIdArr[i] = Long.valueOf(memberArr[i]);
                }
                task.setMemberArr(userIdArr);
            }
        }

        String filename = file.getOriginalFilename();
        if (!StringUtils.isEmpty(filename)) {
            try {
                String extName = filename.substring(filename.lastIndexOf(".") + 1);
                StorePath storePath = client.uploadFile(null, file.getInputStream(), file.getSize(), extName);
                String filePath = new StringBuilder(imgServer).append(storePath.getFullPath()).toString();
                //将属性添加到task对象中
                task.setFilename(filename);
                task.setFilepath(filePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        //删除旧的
        taskService.deleteByPrimaryKey(task.getId());
        //插入新的
        taskService.insertTask(task);
        return "redirect:http://localhost:9081/task-management/task/index/" + task.getUpdateUser();
    }

    @GetMapping("getTaskByUserId/{userId}")
    @ResponseBody
    public ShowTaskList getTaskByUserId(@PathVariable Long userId){
        List<Task> taskList = taskService.getTaskByUserId(userId);
        log.info("taskList.size:{}",taskList.size());
        if (taskList.size() == 0){
            return null;
        }
        //老师
        //showStatus：0为进行中，1为已结束
        //已结束再分：为1已结束时，判断有无待批阅，有则是待批阅，无则是已完成
        //如果isAdmin为1，那么就获取contentList
        //如果isAdmin为0，那么就获取content
        List<Task> doingList = new ArrayList<>();
        List<Task> waitList = new ArrayList<>();
        List<Task> doneList = new ArrayList<>();
        if ("1".equals(taskList.get(0).getIsAdmin())) {
            for (Task task : taskList) {
                //查询出班级或成员
                if ("0".equals(task.getChoiceMode())){
                    List<ClassEntity> classList = taskService.getClassListByTaskId(task.getId());
                    task.setClassList(classList);
                }else {
                    List<Long> userIdList = taskService.getUserIdListByTaskId(task.getId());
                    Long[] userIds = userIdList.toArray(new Long[userIdList.size()]);
                    task.setMemberArr(userIds);
                }
                //查询出所有的contentList
                List<TaskContent> contentList = taskService.queryContentListByTaskId(task.getId());
                log.info("contentList:{}",contentList);
                //检查List分数是否为null，有分数则已批阅，待批阅list为空则是已完成
                List<TaskContent> unReviewedList = new ArrayList<>();
                List<TaskContent> reviewedList = new ArrayList<>();
                for (TaskContent taskContent : contentList) {
                    if (taskContent == null || taskContent.getContent()==null ||taskContent.getGrade() == null) {
                        unReviewedList.add(taskContent);
                    } else {
                        reviewedList.add(taskContent);
                    }
                }
                //设置task的viewState属性
                task.setReviewedTaskContentList(reviewedList);
                task.setUnReviewedTaskContentList(unReviewedList);

                if ("0".equals(task.getStatusShow())) {
                    doingList.add(task);
                } else {
                    //Integer total = taskService.getTotalContent(task.getId());
                    if (reviewedList.size()>0) {
                        task.setReviewState("0");
                        waitList.add(task);
                    } else {
                        task.setReviewState("1");
                        doneList.add(task);
                    }
                }
            }
        }
        //学生：
        //content不为null则已提交
        //已结束再分：为1已提交时，分数为null则是待批阅，分数不为null则是已完成
        if ("0".equals(taskList.get(0).getIsAdmin())) {
            for (Task task : taskList) {
                if (task.getContent()==null) {
                    doingList.add(task);
                } else {
                    //查询待批阅content，检查分数
                    if (StringUtils.isEmpty(task.getGrade())){
                        waitList.add(task);
                    }else {
                        doingList.add(task);
                    }
                }
            }
        }
        redisTemplate.opsForValue().set("task:"+userId, taskList);
        return new ShowTaskList(doingList,waitList,doneList);
    }

    @GetMapping("getTaskById/{taskId}/{userId}")
    @ResponseBody
    public Task getTaskById(@PathVariable Long taskId, @PathVariable Long userId){
        List<Task> taskList = (List<Task>) redisTemplate.opsForValue().get("task:" + userId);
        for (Task task : taskList) {
            if (taskId.equals(task.getId())){
                return task;
            }
        }
        return null;
    }

    @DeleteMapping("delTaskById/{taskId}/{userId}")
    @ResponseBody
    public void delTaskById(@PathVariable("userId") Long userId, @PathVariable("taskId") Long taskId){
        taskService.deleteTask(userId, taskId);
    }

    /*=====================================================*/
    @GetMapping("url/{url}")
    public String url(@PathVariable String url){
        return url;
    }

    /**
     * 添加任务
     * @param name
     * @param userId
     * @return
     */
    /*@PostMapping("insertTaskWithReturn/{name}/{desc}/{type}/{groupId}/{userId}")
    @ResponseBody
    public Long insertPersonalTaskWithReturn(@PathVariable("name") String name, @PathVariable("desc") String desc,
                                             @PathVariable("userId") Long userId, @PathVariable String type, @PathVariable Long groupId){
        return taskService.insertTaskWithReturn(new Task(null,name,desc,type,groupId,null,userId,null));
    }*/

    @PostMapping("insertGroupWithReturn/{name}/{userId}")
    @ResponseBody
    public Long insertGroupWithReturn(@PathVariable("name") String name, @PathVariable("userId") Long userId){
        return groupService.insertGroupWithReturn(name, userId);
    }

//    @GetMapping("queryGroupTask/{userId}/{groupId}")
//    @ResponseBody
//    public Task queryGroupTask(@PathVariable Long userId, @PathVariable Long groupId){
//        return taskService.queryGroupTask(userId,groupId);
//    }

    @PutMapping("modifyGroupName/{groupId}/{groupName}/{userId}")
    @ResponseBody
    public void modifyGroupName(@PathVariable Long groupId, @PathVariable String groupName, @PathVariable Long userId){
        groupService.modifyGroupName(groupId, groupName, userId);
    }

    @GetMapping("delTask/{taskId}/{userId}")
    public String deleteTask(@PathVariable("userId") Long userId, @PathVariable("taskId") Long taskId){
        taskService.deleteTask(userId, taskId);
        //跳转到首页
        return "redirect:http://localhost:9081/task-management/task/index/"+userId;
    }

    @PutMapping("delGroup/{groupId}/{userId}")
    @ResponseBody
    public void delGroup(@PathVariable("userId") Long userId, @PathVariable("groupId") Long groupId){
        groupService.deleteGroup(userId, groupId);
    }

    /**
     * 根据用户id查询用户的个人任务
     * @param userId
     * @return
     */
    @GetMapping("queryPersonalTaskByUserId/{userId}")
    @ResponseBody
    public List<Task> queryPersonalTaskByUserId(@PathVariable("userId") Long userId){
        return taskService.queryPersonalTaskByUserId(userId);
    }

    /**
     * 根据用户id查询对应group
     * @param userId
     * @return
     */
    @GetMapping("queryGroupByUserId/{userId}")
    @ResponseBody
    public List<Group> queryGroupByUserId(@PathVariable("userId") Long userId){
        List<Group> groups = groupService.queryGroupByUserId(userId);
        for (Group group : groups){
            //查询groupId对应的团队任务
            group.setTaskList(taskService.queryGroupTaskByGroupId(group.getId()));
        }
        return groups;
    }

    /**
     * 根据groupId查询组内用户
     * @param groupId
     * @param modelMap
     * @return
     */
    @GetMapping("queryGroupInfoByGroupId/{groupId}/{userId}")
    public String queryGroupInfoById(@PathVariable("groupId") Long groupId, @PathVariable Long userId, ModelMap modelMap){
        Group group = groupService.queryGroupInfoByGroupId(groupId,userId);
        //跳转到group详情界面
        modelMap.put("group", group);
        modelMap.put("userId",userId);
        return "groupinfo";
    }

    @PostMapping("addGroupUser/{groupId}/{username}/{updateUser}")
    @ResponseBody
    public void addGroupUser(@PathVariable Long groupId, @PathVariable String username, @PathVariable Long updateUser){
        groupService.addGroupUser(groupId,username,updateUser);
    }

    @PutMapping("modifyIsAdmin/{groupId}/{userId}/{updateUser}")
    @ResponseBody
    public void modifyIsAdmin(@PathVariable Long groupId, @PathVariable Long userId, @PathVariable Long updateUser){
        groupService.modifyIsAdmin(groupId, userId, updateUser);
    }

    @PutMapping("delUserById/{groupId}/{userId}/{updateUser}")
    @ResponseBody
    public void delUserById(@PathVariable Long groupId, @PathVariable Long userId, @PathVariable Long updateUser){
        groupService.delUserById(groupId, userId, updateUser);
    }

    /**
     * 根据任务id查询任务详情
     * @param taskId
     * @param userId
     * @param modelMap
     * @return 跳转到任务详情界面
     */
    @GetMapping("queryTaskInfoById/{taskId}/{isAdmin}/{userId}")
    public String queryTaskInfoById(@PathVariable("taskId") Long taskId, @PathVariable String isAdmin,
                                    @PathVariable Long userId, ModelMap modelMap){
        //查询出TaskInfo的list
        List<TaskInfo> taskInfoList = taskInfoService.queryTaskInfoById(taskId);
        //根据taskId查询groupId
        Long groupId = groupService.queryGroupIdByTaskId(taskId);

        //添加userList属性，分类list
        List<TaskInfo> todoList = new ArrayList<>();
        List<TaskInfo> doingList = new ArrayList<>();
        List<TaskInfo> doneList = new ArrayList<>();
        List<TaskInfo> fileList = new ArrayList<>();
        for (TaskInfo taskInfo: taskInfoList) {
            if ("Todo".equals(taskInfo.getStatus())){
                todoList.add(taskInfo);
            }else if ("Doing".equals(taskInfo.getStatus())){
                doingList.add(taskInfo);
            }else if ("Done".equals(taskInfo.getStatus())){
                doneList.add(taskInfo);
            }else if ("File".equals(taskInfo.getStatus())){
                fileList.add(taskInfo);
            }
        }
        //跳转到个人任务详情界面
        modelMap.put("userId",userId);
        modelMap.put("taskId",taskId);
        modelMap.put("isAdmin",isAdmin);
        modelMap.put("groupId",groupId);
        modelMap.put("todoList",todoList);
        modelMap.put("doingList",doingList);
        modelMap.put("doneList",doneList);
        modelMap.put("fileList",fileList);

        return "taskinfo";
    }

    @PostMapping("addTaskInfo")
    @ResponseBody
    public void addTaskInfo(TaskInfo taskInfo){
        log.info("entered");
        taskInfoService.insertSelective(taskInfo);
    }

    @RequestMapping("getTaskInfoById/{taskInfoId}/{isAdmin}/{userId}/{taskId}")
    public String getTaskInfoById(@PathVariable Long taskInfoId, @PathVariable String isAdmin,
                                  @PathVariable Long userId, @PathVariable Long taskId, ModelMap modelMap){
        TaskInfo taskInfo = taskInfoService.getTaskInfoById(taskId, taskInfoId);
        modelMap.put("taskInfo", taskInfo);
        modelMap.put("isAdmin", isAdmin);
        modelMap.put("userId", userId);
        modelMap.put("taskId", taskId);
        return "taskinfo-modal";
    }

    @PostMapping("addUserToTaskInfo/{taskInfoId}/{username}/{updateUser}/{isAdmin}/{taskId}")
    public String addUserToTaskInfo(@PathVariable Long taskInfoId, @PathVariable String username,
                                    @PathVariable Long updateUser, @PathVariable String isAdmin, @PathVariable Long taskId){
        taskInfoService.addUserToTaskInfo(taskInfoId, username, updateUser, taskId);
        return "redirect:http://localhost:9081/task-management/task/getTaskInfoById/"+taskInfoId+"/"+isAdmin+"/"+updateUser+"/"+taskId;
    }

    @PutMapping("modifyDeadtime/{taskInfoId}/{deadTime}/{updateUser}/{isAdmin}/{taskId}")
    public String modifyDeadtime(@PathVariable Long taskInfoId, @PathVariable String deadTime,
                               @PathVariable Long updateUser, @PathVariable String isAdmin, @PathVariable Long taskId){
        taskInfoService.modifyDeadtime(taskInfoId, deadTime, updateUser, taskId);
        return "redirect:http://localhost:9081/task-management/task/getTaskInfoById/"+taskInfoId+"/"+isAdmin+"/"+updateUser+"/"+taskId;
    }

    @PutMapping("modifyIsFinished/{taskInfoId}/{isFinished}/{updateUser}/{isAdmin}/{taskId}")
    public String modifyIsFinished(@PathVariable Long taskInfoId, @PathVariable String isFinished,
                               @PathVariable Long updateUser, @PathVariable String isAdmin, @PathVariable Long taskId){
        taskInfoService.modifyIsFinished(taskInfoId, isFinished, updateUser, taskId);
        return "redirect:http://localhost:9081/task-management/task/getTaskInfoById/"+taskInfoId+"/"+isAdmin+"/"+updateUser+"/"+taskId;
    }

    @PutMapping("modifyContentIsFinished/{taskContentId}/{taskInfoId}/{isFinished}/{updateUser}/{isAdmin}/{taskId}")
    public String modifyContentIsFinished(@PathVariable Long taskContentId, @PathVariable Long taskInfoId, @PathVariable String isFinished,
                                          @PathVariable Long updateUser, @PathVariable String isAdmin, @PathVariable Long taskId){
        taskInfoService.modifyContentIsFinished(taskContentId, taskInfoId, isFinished, updateUser, taskId);
        return "redirect:http://localhost:9081/task-management/task/getTaskInfoById/"+taskInfoId+"/"+isAdmin+"/"+updateUser+"/"+taskId;
    }

    @PostMapping("addTaskContent/{taskInfoId}/{content}/{updateUser}/{isAdmin}/{taskId}")
    public String addTaskContent(@PathVariable Long taskInfoId, @PathVariable String content,
                                 @PathVariable Long updateUser, @PathVariable String isAdmin, @PathVariable Long taskId){
        taskInfoService.addTaskContent(taskInfoId, content, updateUser, taskId);
        return "redirect:http://localhost:9081/task-management/task/getTaskInfoById/"+taskInfoId+"/"+isAdmin+"/"+updateUser+"/"+taskId;
    }

    @PostMapping("upload")
    @ResponseBody
    public WEditorResultBean batchUpload(MultipartFile[] files){
        String[] data = new String[files.length];
        try {
            for (int i=0;i<files.length;i++) {
                String originalFilename = files[i].getOriginalFilename();
                String extName = originalFilename.substring(originalFilename.lastIndexOf(".") + 1);
                StorePath storePath = client.uploadImageAndCrtThumbImage(files[i].getInputStream(), files[i].getSize(), extName, null);
                StringBuilder stringBuilder = new StringBuilder(imgServer).append(storePath.getFullPath());
                data[i] = stringBuilder.toString();
            }
            //返回之后将图片添加到评论
            return new WEditorResultBean(WEditorResultConstant.OK,data);
        }catch (IOException e) {
            e.printStackTrace();
            return new WEditorResultBean(WEditorResultConstant.ERROR,null);
        }
    }

    @PostMapping(value = {"addComment/{taskInfoId}/{comment}/{updateUser}/{isAdmin}/{taskId}","addComment/{taskInfoId}/{updateUser}/{isAdmin}/{taskId}"})
    public String addComment(@PathVariable Long taskInfoId, @PathVariable(required = false) String comment,
                             @PathVariable Long updateUser, @PathVariable String isAdmin, @PathVariable Long taskId,
                             @RequestParam(required = false) String img){
        taskInfoService.addCommentImg(taskInfoId, comment, img, updateUser, taskId);
        return "redirect:http://localhost:9081/task-management/task/getTaskInfoById/"+taskInfoId+"/"+isAdmin+"/"+updateUser+"/"+taskId;
    }

    @PutMapping("modifyDesc/{taskInfoId}/{desc}/{updateUser}/{isAdmin}/{taskId}")
    public String modifyDesc(@PathVariable Long taskInfoId, @PathVariable String desc,
                                 @PathVariable Long updateUser, @PathVariable String isAdmin, @PathVariable Long taskId){
        taskInfoService.modifyDesc(taskInfoId, desc, updateUser, taskId);
        return "redirect:http://localhost:9081/task-management/task/getTaskInfoById/"+taskInfoId+"/"+isAdmin+"/"+updateUser+"/"+taskId;
    }

    @DeleteMapping("delContent/{taskInfoId}/{taskContentId}/{updateUser}/{taskId}")
    public String delContent(@PathVariable Long taskInfoId, @PathVariable Long taskContentId,
                             @PathVariable Long updateUser,  @PathVariable Long taskId){
        taskInfoService.delContent(taskInfoId, taskContentId, updateUser, taskId);
        return "redirect:http://localhost:9081/task-management/task/getTaskInfoById/"+taskInfoId+"/1/"+updateUser+"/"+taskId;
    }

    @DeleteMapping("delComment/{taskInfoId}/{taskCommentId}/{updateUser}/{taskId}")
    public String delComment(@PathVariable Long taskInfoId, @PathVariable Long taskCommentId,
                             @PathVariable Long updateUser,  @PathVariable Long taskId){
        taskInfoService.delComment(taskInfoId, taskCommentId, updateUser, taskId);
        return "redirect:http://localhost:9081/task-management/task/getTaskInfoById/"+taskInfoId+"/1/"+updateUser+"/"+taskId;
    }

    @DeleteMapping("delTaskInfo/{taskInfoId}/{updateUser}/{taskId}")
    @ResponseBody
    public void delTaskInfo(@PathVariable Long taskInfoId,
                             @PathVariable Long updateUser,  @PathVariable Long taskId){
        taskInfoService.delTaskInfo(taskInfoId, updateUser, taskId);
    }

    @PutMapping("modifyStatus/{taskInfoId}/{status}/{updateUser}/{taskId}")
    @ResponseBody
    public void modifyStatus(@PathVariable Long taskInfoId, @PathVariable String status,
                             @PathVariable Long updateUser, @PathVariable Long taskId){
        taskInfoService.modifyStatus(taskInfoId, status, updateUser, taskId);
    }

    @GetMapping("getUserListByTaskInfoId/{taskInfoId}")
    @ResponseBody
    public ResultBean getUserListByTaskInfoId(@PathVariable Long taskInfoId){
        return taskInfoService.getUserListByTaskInfoId(taskInfoId);
    }
}
