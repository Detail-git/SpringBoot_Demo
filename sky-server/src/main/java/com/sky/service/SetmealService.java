package com.sky.service;


import com.sky.entity.Setmeal;
import com.sky.vo.DishItemVO;

import java.util.List;

public interface SetmealService {
    /**
     *动态条件查寻套餐数据
     * @param setmeal
     * @return
     */
    List<Setmeal> list(Setmeal setmeal);

    /**
     * 根据套餐id查询菜品选项
     * @param id
     * @return
     */
    List<DishItemVO> getDishItemById(Long id);
}
