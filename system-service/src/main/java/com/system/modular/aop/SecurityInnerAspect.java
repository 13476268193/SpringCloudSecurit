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

package com.system.modular.aop;

import cn.hutool.core.util.StrUtil;
import com.base.core.annotation.Inner;
import com.base.core.constant.SecurityConstants;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;

/**
 * @author lengleng
 * @date 2022-06-04
 *
 * 服务间接口不鉴权处理逻辑
 */
@Slf4j
@Component
@Aspect
//@RequiredArgsConstructor
//@Order(200)
public class SecurityInnerAspect implements Ordered {

//	@Autowired
//	private HttpServletRequest request;

	@SneakyThrows
	@Around("@within(inner) || @annotation(inner)")
	public Object around(ProceedingJoinPoint point, Inner inner) {
		// 实际注入的inner实体由表达式后一个注解决定，即是方法上的@Inner注解实体，若方法上无@Inner注解，则获取类上的
		if (inner == null) {
			Class<?> clazz = point.getTarget().getClass();
			inner = AnnotationUtils.findAnnotation(clazz, Inner.class);
		}
		//获取RequestAttributes
		RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
		//从获取RequestAttributes中获取HttpServletRequest的信息
		HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);

		String header = request.getHeader(SecurityConstants.FROM);
		if (inner.value() && !StrUtil.equals(SecurityConstants.FROM_IN, header)) {
			log.warn("访问接口 {} 没有权限", point.getSignature().getName());
			throw new AccessDeniedException("Access is denied");
		}
		return point.proceed();
	}

	@Pointcut(value = "@annotation(com.base.core.annotation.Inner)")
	public void pointCutInner() {
	}


	@Pointcut("execution(* com.system.*..controller.*.*(..))")
	public void pointCutController() {
	}

	/**
	 * 环绕通知 @Around  ， 当然也可以使用 @Before (前置通知)  @After (后置通知)
	 *
	 * @param point
	 * @return
	 */
	//@Around注解后添加切点方法名，进行关联
	@Around("pointCutInner()")
	//参数ProceedingJoinPoint的作用：
	//其他切面编程使用JoinPoint就可以了，ProceedingJoinPoint继承了JoinPoint类，增加了proceed方法。
	//环绕通知=前置+目标方法执行+后置通知，proceed方法就是用于启动目标方法执行的
	public Object around(ProceedingJoinPoint point) throws Throwable {
		Signature signature = point.getSignature();
		MethodSignature methodSignature = (MethodSignature) signature;
		Method method = methodSignature.getMethod();
		if (method != null) {
			Inner inner = method.getAnnotation(Inner.class);
			// 获取注解的属性值
			if (inner == null) {
				Class<?> clazz = point.getTarget().getClass();
				inner = AnnotationUtils.findAnnotation(clazz, Inner.class);
			}
			//获取RequestAttributes
			RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
			//从获取RequestAttributes中获取HttpServletRequest的信息
			HttpServletRequest request = (HttpServletRequest) requestAttributes.resolveReference(RequestAttributes.REFERENCE_REQUEST);

			String header = request.getHeader(SecurityConstants.FROM);
			if (inner.value() && !StrUtil.equals(SecurityConstants.FROM_IN, header)) {
				log.warn("访问接口 {} 没有权限", point.getSignature().getName());
				throw new AccessDeniedException("Access is denied");
			}
		}
		return point.proceed();
	}

	@Override
	public int getOrder() {
		return Ordered.HIGHEST_PRECEDENCE + 1;
	}

}
