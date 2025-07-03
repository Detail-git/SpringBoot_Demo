package com.sky.controller.admin;

import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RestController("adminshopController")
@Slf4j
@RequestMapping("/admin/shop")
public class ShopController {

    @Autowired
    private RedisTemplate redisTemplate;

    public static final String key = "SHOP_STATUS";

    /**
     * 查询营业状态
     */
    @GetMapping("/status")
    public Result getStatus(){
        Integer status = (Integer) redisTemplate.opsForValue().get(key);
        log.info("获取店铺的营业状态:{}",status == 1 ? "营业中" : "打烊中");
        return Result.success(status);
    }

    /**
     * 设置营业状态
     * @param status
     * @return
     */
    @PutMapping("/{status}")
    public Result setStatus(@PathVariable Integer status){
        log.info("设置店铺营业状态:{}",status == 1 ? "营业中" : "打烊中");
        redisTemplate.opsForValue().set(key, status);

        return Result.success();
    }
}
