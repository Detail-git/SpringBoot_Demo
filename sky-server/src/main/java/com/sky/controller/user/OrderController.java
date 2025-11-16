package com.sky.controller.user;

import com.sky.entity.OrderDetail;
import com.sky.result.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/user/order")
public class OrderController {
    @GetMapping("/orderDetail/{id}")
    public Result<OrderDetail> getOrderById(@PathVariable Long id){

        return null;
    }
}
