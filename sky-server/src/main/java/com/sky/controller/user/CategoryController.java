package com.sky.controller.user;

import com.sky.entity.Category;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("UserCategoryController")
@RequestMapping("/user/category")
@Slf4j
public class CategoryController {
    @Autowired
    private CategoryService categoryService;
    /**
     * 查询分类
     * 根据type的值查询，为1时查询菜品
     * 为2时查询套餐，否则查询全部
     */
    @GetMapping("/list")
    public Result<List<Category>> list(Integer type){
        List<Category> list = categoryService.list(type);
        return Result.success(list);
    }
}
