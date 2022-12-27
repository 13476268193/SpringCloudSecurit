/**
 * @project：
 * @title：UserServiceImpl.java
 * @description：
 * @package com.springcloud.auth.modular.user.service.impl
 * @author：weiyang
 * @date：2021/9/11
 * @copyright: 武汉中天云通数据科技有限责任公司  All rights reserved.
 * @version V1.0
 */
package com.system.modular.user.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.base.core.exception.BusinessException;
import com.base.core.model.UserInfo;
import com.system.modular.system.model.SysResource;
import com.system.modular.system.service.IRoleResourceService;
import com.system.modular.user.dao.UserMapper;
import com.system.modular.user.model.SysUser;
import com.system.modular.user.service.IUserService;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, SysUser> implements IUserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private IRoleResourceService resourceService;

    @Override
    public UserInfo queryUserInfo(UserInfo sysUser) {
        UserInfo userInfo = userMapper.queryUserInfo(sysUser);

        if (null == userInfo) {
            throw new BusinessException("用户不存在");
        }

        String roleIds = userInfo.getRoleIds();
        //组装角色ID列表，用于Oatuh2和Gateway鉴权
        if (!org.springframework.util.StringUtils.isEmpty(roleIds)) {
            String[] roleIdsArray = roleIds.split(",");
            userInfo.setRoleIdList(Arrays.asList(roleIdsArray));
        }

        String roleKeys = userInfo.getRoleKeys();
        //组装角色key列表，用于前端页面鉴权
        if (!org.springframework.util.StringUtils.isEmpty(roleKeys)) {
            String[] roleKeysArray = roleKeys.split(",");
            userInfo.setRoleKeyList(Arrays.asList(roleKeysArray));
        }

//        String dataPermissionTypes = userInfo.getDataPermissionTypes();
//        // 获取用户的角色数据权限级别
//        if (!org.springframework.util.StringUtils.isEmpty(dataPermissionTypes)) {
//            String[] dataPermissionTypeArray = dataPermissionTypes.split(",");
//            userInfo.setDataPermissionTypeList(Arrays.asList(dataPermissionTypeArray));
//        }

//        String organizationIds = userInfo.getOrganizationIds();
//        // 获取用户机构数据权限列表
//        if (!org.springframework.util.StringUtils.isEmpty(organizationIds)) {
//            String[] organizationIdArray = organizationIds.split(",");
//            userInfo.setOrganizationIdList(Arrays.asList(organizationIdArray));
//        }

        // 获取用户机构数据权限列表
        if (!CollectionUtils.isEmpty(userInfo.getResourceKeyList())) {
            userInfo.getResourceKeyList().add("message.read");
//            userInfo.setOrganizationIdList(userInfo.getResourceKeyList());
        }

//        String[] resources = {};
        if(!CollectionUtils.isEmpty(userInfo.getRoleIdList())) {
            List<String> resourceList = userInfo.getRoleIdList().stream().map(Long::parseLong)
                    .map(resourceService::queryResourceByRoleId)
                    .flatMap(Collection::stream).map(SysResource::getResourceKey).distinct().collect(Collectors.toList());
            userInfo.setResourceKeyList(resourceList);
//            resources = resourceSet.toArray(new String[resourceSet.size()]);
        }
        // 查询用户菜单树，用于页面展示
//        List<Resource> menuTree = resourceService.queryMenuTreeByUserId(userInfo.getId());
//        userInfo.setMenuTree(menuTree);

        return userInfo;
    }


    @Override
    public Page<UserInfo> selectUserList(Page<UserInfo> page, UserInfo user) {
        Page<UserInfo> pageUserInfo = userMapper.selectUserList(page, user);
        return pageUserInfo;
    }

}

