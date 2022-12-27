/**
 * @project：
 * @title：UserDetailsService.java
 * @description：
 * @package com.springcloud.auth.modular.user.service
 * @author：weiyang
 * @date：2021/8/31
 * @copyright: 武汉中天云通数据科技有限责任公司  All rights reserved.
 * @version V1.0
 */
package com.system.modular.user.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.base.core.model.UserInfo;
import com.system.modular.user.model.SysUser;

public interface IUserService extends IService<SysUser> {

    UserInfo queryUserInfo(UserInfo userInfo);

    Page<UserInfo> selectUserList(Page<UserInfo> page, UserInfo user);
}
