package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {
    /**
     * 根据菜品id来查询对应的套餐id
     */
    //select setmeal_id from setmeal_dish where dish_id in (1,2,3,4)
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 根据套餐id删除套餐和菜品的关联
     * @param id
     */
    @Delete("DELETE from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long id);

    /**
     * 根据套餐ids批量删除套餐与菜品的关联
     * @param ids
     */
    void deleteSetmealsById(List<Long> ids);

    /**
     * 批量保存套餐和菜品的关联关系
     * @param setmealDishes
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据id来查询对应的菜品信息
     * @param setmealId
     * @return
     */
    @Select("select * from setmeal_dish where setmeal_id = #{setmealId}")
    List<SetmealDish> getDishById(Long setmealId);

}
