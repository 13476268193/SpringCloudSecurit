package com.system.modular.user.service.impl;///**
// * @project：
// * @title：ClientDetailsServiceImpl.java
// * @description：
// * @package com.springcloud.auth.modular.user.service.impl
// * @author：weiyang
// * @date：2021/9/13
// * @copyright: 武汉中天云通数据科技有限责任公司  All rights reserved.
// * @version V1.0
// */
//package com.security.oauth.modular.user.service.impl;
//
//import lombok.SneakyThrows;
//import org.springframework.security.oauth2.provider.ClientDetails;
//import org.springframework.security.oauth2.provider.client.JdbcClientDetailsService;
//
//import javax.sql.DataSource;
//
//public class ClientDetailsServiceImpl extends JdbcClientDetailsService {
//
//    public ClientDetailsServiceImpl(DataSource dataSource) {
//        super(dataSource);
//    }
//
//    /**
//     * 缓存客户端信息
//     *
//     * @param clientId 客户端id
//     */
//    @Override
//    @SneakyThrows
//    public ClientDetails loadClientByClientId(String clientId) {
//        return super.loadClientByClientId(clientId);
//    }
//
//
//
//}
//
