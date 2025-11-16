package com.sky.controller.admin;

import com.sky.dto.SetmealDTO;
import com.sky.dto.SetmealPageQueryDTO;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.SetmealService;

import com.sky.vo.SetmealVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("adminSetmealController")
@RequestMapping("/admin/setmeal")
@Slf4j
public class SetmealController {
    @Autowired
    private SetmealService setmealService;

    /**
     * 起售or禁售套餐
     * @param id
     * @return
     */
    @PostMapping("status/{status}")
    public Result UseOrBan(@PathVariable Integer status, Long id){
        log.info("起售或禁售套餐:{},{}",status, id);
        setmealService.UserOrBan(status, id);
        return Result.success();
    }

    /**
     * 批量删除套餐
     */
    @DeleteMapping
    public Result<Long> deleteSetmeal(@RequestParam List<Long> ids){
//        @RequestParam这个注解的唯一核心作用是：
//        告诉 Spring MVC，这个参数要从「URL 的 Query 参数（?id=xxx）」中获取。
//        它和参数是单个Long id还是集合List<Long> ids无关，只和「参数来源」有关。
        log.info("批量删除套餐:{}", ids);
        setmealService.deleteSetmeal(ids);
        return Result.success();
    }

    /**
     * 根据id查询套餐，用于修改套餐页面回显数据
     */
    @GetMapping("{id}")
    public Result<SetmealVO> getById(@PathVariable Long id){
        log.info("根据id查询套餐：{}", id);
        SetmealVO byIdWithMeal = setmealService.getByIdWithMeal(id);
        return Result.success(byIdWithMeal);
    }

    /**
     * 套餐的分页查询
     */
    @GetMapping("page")
    public Result<PageResult> page(SetmealPageQueryDTO setmealPageQueryDTO){
        log.info("套餐分页查询:{}",setmealPageQueryDTO);
        PageResult pageResult = setmealService.pageQureuy(setmealPageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     * 修改套餐
     */
    @PutMapping
    public Result<SetmealDTO> updateSetmeal(@RequestBody SetmealDTO setmealDTO){
        //为什么这里不用变量接收参数再返回？
        /**
         * 在当前场景中，服务层方法可能是 void，或者是直接修改传入的 DTO
         * 传入的 setmealDTO 经过服务层处理后，已经是 “包含更新后信息” 的对象了，所以可以直接返回这个被修改过的 DTO。
         *如果服务层方法有返回值，比如返回一个新的、从数据库查询出来的 SetmealDTO（确保是最新的数据库数据），这时候就需要用变量接收返回值，再返回。
         */
        log.info("修改套餐:{}", setmealDTO);
        setmealService.updateSetmeal(setmealDTO);
        return Result.success();
    }

    /**
     * 新增套餐
     */
    @PostMapping
    public Result addSetmeal(@RequestBody SetmealDTO setmealDTO){
        log.info("新增套餐:{}",setmealDTO);
        setmealService.addSetmeal(setmealDTO);
        return Result.success();
    }
}
