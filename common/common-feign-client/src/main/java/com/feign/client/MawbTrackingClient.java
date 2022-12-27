package com.feign.client;

import com.base.core.model.UserInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import static com.base.core.constant.SecurityConstants.FROM;

@FeignClient(value = "business-service", contextId = "mawbTracking-service")
public interface MawbTrackingClient {

    @GetMapping("/mawbTracking/getMawbTracking")
    public Object getMawbTracking(@RequestParam("mawb") String mawb) ;

}
