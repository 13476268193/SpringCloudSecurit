package com.system.modular.system.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.system.modular.system.model.SysResource;
import com.system.modular.system.model.RoleResource;

import java.util.List;

/**
 * @ClassName: IRoleService
 * @Description: 角色相关操作接口
 * @author gitegg
 * @date
 */
public interface IRoleResourceService extends IService<RoleResource> {

    List<SysResource> queryResourceByRoleId(Long roleId);
}
