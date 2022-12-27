package com.system.modular.system.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.system.modular.system.dto.QueryUserResourceDTO;
import com.system.modular.system.model.SysResource;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * <p>
 * 权限表 Mapper 接口
 * </p>
 *
 * @author gitegg
 * @since 2018-05-19
 */
public interface ResourceMapper extends BaseMapper<SysResource> {

    /**
     * 查询用户权限资源
     * @param queryUserResourceDTO
     * @return
     */
    List<SysResource> queryResourceByUserId(@Param("userResource") QueryUserResourceDTO queryUserResourceDTO);

}
