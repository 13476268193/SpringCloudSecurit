package com.system.modular.user.model.dto;

import com.base.core.model.UserInfo;
import com.system.modular.system.model.SysResource;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;
import org.springframework.beans.BeanUtils;

import java.util.List;


@Data
public class UserInfoDto extends UserInfo {

    @ApiModelProperty(value = "前端展示的用户菜单树")
    private List<SysResource> menuTree;

    public UserInfoDto(UserInfo userInfo) {
        BeanUtils.copyProperties(userInfo, this);
    }

}
