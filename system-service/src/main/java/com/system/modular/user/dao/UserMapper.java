package com.system.modular.user.dao;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.base.core.model.UserInfo;
import com.system.modular.user.model.SysUser;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 用户表 Mapper 接口
 * </p>
 *
 */
public interface UserMapper extends BaseMapper<SysUser> {


    /**
     * 查询用户信息
     * @param userInfo
     * @return
     */
    UserInfo queryUserInfo(@Param("param") UserInfo userInfo);

    /**
     * 查询用户信息
     * @param userInfo
     * @return
     */
    Page<UserInfo> selectUserList(Page<UserInfo> page, @Param("user") UserInfo userInfo);

}
