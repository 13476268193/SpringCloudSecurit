package com.system.modular.system.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.enums.ResourceEnum;
import com.google.common.collect.Lists;
import com.system.modular.system.dao.ResourceMapper;
import com.system.modular.system.dto.QueryUserResourceDTO;
import com.system.modular.system.model.SysResource;
import com.system.modular.system.service.IResourceService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author gitegg
 */
@Slf4j
@Service
@RequiredArgsConstructor(onConstructor_ = @Autowired)
public class ResourceServiceImpl extends ServiceImpl<ResourceMapper, SysResource> implements IResourceService {


    @Override
    public List<SysResource> queryMenuTreeByUserId(Long userId) {
        QueryUserResourceDTO queryUserResourceDTO = new QueryUserResourceDTO();
        queryUserResourceDTO.setUserId(userId);
        queryUserResourceDTO.setResourceTypeList(Lists.newArrayList(ResourceEnum.MENU.getCode()));
        List<SysResource> resourceList = this.baseMapper.queryResourceByUserId(queryUserResourceDTO);
        Map<Long, SysResource> resourceMap = new HashMap<>();
        List<SysResource> menus = this.assembleResourceTree(resourceList, resourceMap);
        return menus;
    }

    /**
     * 组装子父级目录
     *
     * @param resourceList
     * @param resourceMap
     * @return
     */
    private List<SysResource> assembleResourceTree(List<SysResource> resourceList, Map<Long, SysResource> resourceMap) {
        List<SysResource> menus = new ArrayList<>();
        for (SysResource resource : resourceList) {
            resourceMap.put(resource.getId(), resource);
        }
        for (SysResource resource : resourceList) {
            Long treePId = resource.getParentId();
            SysResource resourceTree = resourceMap.get(treePId);
            if (null != resourceTree && !resource.equals(resourceTree)) {
                List<SysResource> nodes = resourceTree.getChildren();
                if (null == nodes) {
                    nodes = new ArrayList<>();
                    resourceTree.setChildren(nodes);
                }
                nodes.add(resource);
            } else {
                menus.add(resource);
            }
        }
        return menus;
    }

}
