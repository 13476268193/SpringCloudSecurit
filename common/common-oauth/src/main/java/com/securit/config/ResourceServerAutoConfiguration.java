/*
 * Copyright (c) 2020 pig4cloud Authors. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.securit.config;

import com.feign.client.AuthorizationClient;
import com.feign.client.UserDetailsClient;
import com.securit.component.PermissionService;
import com.securit.token.BearerTokenExtractor;
import com.securit.token.CustomOpaqueTokenIntrospector;
import com.securit.token.ResourceAuthExceptionEntryPoint;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.server.resource.introspection.OpaqueTokenIntrospector;

/**
 * @author lengleng
 * @date 2022-06-02
 */
@RequiredArgsConstructor
@Configuration
public class ResourceServerAutoConfiguration {

	/**
	 * 鉴权具体的实现逻辑
	 * @return （#pms.xxx）
	 */
	@Bean("pms")
	public PermissionService permissionService() {
		return new PermissionService();
	}

	/**
	 * 请求令牌的抽取逻辑
	 * @param urlProperties 对外暴露的接口列表
	 * @return BearerTokenExtractor
	 */
	@Bean
	public BearerTokenExtractor bearerTokenExtractor(PermitAllUrlProperties urlProperties) {
		return new BearerTokenExtractor(urlProperties);
	}

	/**
	 * 资源服务器异常处理
	 * @return ResourceAuthExceptionEntryPoint
	 */
	@Bean
	public ResourceAuthExceptionEntryPoint resourceAuthExceptionEntryPoint() {
		return new ResourceAuthExceptionEntryPoint();
	}

	/**
	 * 资源服务器toke内省处理器
	 * @param authorizationService token存储实现
	 * @param userDetailsService 用户信息查询服务
	 * @return OpaqueTokenIntrospector
	 */
	@Bean
	public OpaqueTokenIntrospector opaqueTokenIntrospector(AuthorizationClient authorizationService, UserDetailsClient userDetailsService) {
		return new CustomOpaqueTokenIntrospector(authorizationService, userDetailsService);
	}


}
