package com.baiyun.xiangchengma.server;

import com.baiyun.xiangchengma.bean.UserLocationInfo;

import java.util.List;
import java.util.Map;

public interface UserLocationInfoDAO {

    /**
     * 插入用户位置信息
     */
    public void insertUserLocationInfo(UserLocationInfo user);

    /**
     * 删除用户位置信息
     */
    public void deleteUserLocationInfo(Integer id);

    /**
     * 更新用户位置信息
     */
    public void updateUserLocationInfo(Integer id,double latitude,double longitude);

    /**
     * 查询用户全部轨迹信息(同一用户对应多个轨迹,保留轨迹数为100)
     */
    public List<UserLocationInfo> getAllUserLocationInfo(Integer id);

    /**
     * 查询用户最近的轨迹信息(最多查询三十个轨迹信息)
     * @return
     */
    public Map<String, UserLocationInfo> getUserLocationInfo(Integer id);


    /**\
     * 用户位置信息是否存在
     * @return
     */
    public boolean isExists(Integer id);

}
