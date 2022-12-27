package com.system.modular.system.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.system.modular.system.dao.RoleResourceMapper;
import com.system.modular.system.model.SysResource;
import com.system.modular.system.model.RoleResource;
import com.system.modular.system.service.IResourceService;
import com.system.modular.system.service.IRoleResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * @author gitegg
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class RoleResourceServiceImpl extends ServiceImpl<RoleResourceMapper, RoleResource>
        implements IRoleResourceService {

    private final IResourceService resourceService;


    @Override
    public List<SysResource> queryResourceByRoleId(Long roleId) {
        LambdaQueryWrapper<RoleResource> ew = new LambdaQueryWrapper<>();
        ew.eq(RoleResource::getRoleId, roleId);
        List<RoleResource> roleResourceList = this.list(ew);
        if (!CollectionUtils.isEmpty(roleResourceList)) {
            List<Long> resourceIds = new ArrayList<>();
            for (RoleResource roleResource : roleResourceList) {
                resourceIds.add(roleResource.getResourceId());
            }
            List<SysResource> resourceList = resourceService.listByIds(resourceIds);
            return resourceList;
        } else {
            return null;
        }
    }

}
