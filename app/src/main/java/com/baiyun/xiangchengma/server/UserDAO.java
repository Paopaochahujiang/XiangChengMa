package com.baiyun.xiangchengma.server;

import com.baiyun.xiangchengma.bean.User;

import java.util.List;
import java.util.Map;

public interface UserDAO {
    /**
     * 插入用户信息
     */
    public void insertUser(User user);

    /**
     * 删除用户
     */
    public void deleteUser(Integer id);

    /**
     * 更新用户
     */
    public void updateUser(Integer id,double latitude,double longitude);

    /**
     * 查询用户信息(同一用户对应多个轨迹)
     */
    public List<User> getAllUser();
    /*
     * 查询学生信息(一个名字对应一个学生)
     */
    public Map<String,User> getUser();

    /**\
     * 用户信息是否存在
     * @return
     */
    public boolean isExists(String name);


}
