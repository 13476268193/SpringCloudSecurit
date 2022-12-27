package com.system.modular.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.system.modular.system.model.SysResource;

import java.util.List;

/**
 * @author gitegg
 */
public interface IResourceService extends IService<SysResource> {

    List<SysResource> queryMenuTreeByUserId(Long id);
}
