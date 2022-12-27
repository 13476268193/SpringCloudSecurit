package com.security.filter;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class SecurityGlobalFilter implements WebFilter {

    @SneakyThrows
    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        ServerHttpResponse response = exchange.getResponse();

        String token = request.getHeaders().getFirst("Authorization");
        if (StrUtil.isEmpty(token)) {
            JSONObject jsonObject = setResultErrorMsg(401, "登录失效");
            DataBuffer buffer = response.bufferFactory().wrap(jsonObject.toString().getBytes());
            return response.writeWith(Mono.just(buffer));
        }
        // 解析JWT获取jti，以jti为key判断redis的黑名单列表是否存在，存在则拦截访问
        token = token.replace("Bearer ", "");
        String payload;
        try {
            payload = JWSObject.parse(token).getPayload().toString();
        } catch (Exception e) {
            JSONObject jsonObject = setResultErrorMsg(401, "登录失效");
            DataBuffer buffer = response.bufferFactory().wrap(jsonObject.toString().getBytes());
            return response.writeWith(Mono.just(buffer));
        }
        if (StrUtil.isEmpty(payload)) {
            JSONObject jsonObject = setResultErrorMsg(401, "登录失效");
            DataBuffer buffer = response.bufferFactory().wrap(jsonObject.toString().getBytes());
            return response.writeWith(Mono.just(buffer));
        }

        //给header里面添加值
        JSONObject jsonObject = JSONUtil.parseObj(payload);
        String base64 = String.valueOf(Base64.encode(jsonObject.toString()));
        ServerHttpRequest tokenRequest = exchange.getRequest().mutate().header("token", base64).build();
        ServerWebExchange build = exchange.mutate().request(tokenRequest).build();
        return chain.filter(build);
    }

    private JSONObject setResultErrorMsg(Integer code, String msg) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.set("code", code);
        jsonObject.set("message", msg);
        return jsonObject;
    }
}

