package com.sky.mapper;

import com.sky.entity.SetmealDish;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface SetmealDishMapper {

    /**
     * 根据菜品ids查询对应的套餐ids
     * @param dishIds 菜品ids
     * @return 套餐ids
     */
    // select setmeal_id from setmeal_dish where dish_id in (?,?,?)
    List<Long> getSetmealIdsByDishIds(List<Long> dishIds);

    /**
     * 批量插入套餐菜品关系数据
     * @param setmealDishes 套餐菜品关系数据
     */
    void insertBatch(List<SetmealDish> setmealDishes);

    /**
     * 根据套餐id删除对应的套餐菜品关系数据
     * @param setmealId 套餐id
     */
    @Delete("delete from setmeal_dish where setmeal_id = #{setmealId}")
    void deleteBySetmealId(Long setmealId);

    /**
     * 根据套餐id查询对应的套餐菜品关系数据
     * @param id 套餐id
     * @return 套餐菜品关系数据
     */
    @Select("select * from setmeal_dish where setmeal_id = #{id}")
    List<SetmealDish> getBySetmealId(Long id);
}
