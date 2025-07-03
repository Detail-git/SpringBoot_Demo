package com.sky.controller.user;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

@RestController("usershopController")
@Slf4j
@RequestMapping("/user/shop")
public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;

    public static final String key = "SHOP_STATUS";

    /**
     * 查询营业状态
     */
    @GetMapping("/status")
    public Result getStatus() {
        Integer status = (Integer) redisTemplate.opsForValue().get(key);
        log.info("获取店铺的营业状态:{}", status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }
}
