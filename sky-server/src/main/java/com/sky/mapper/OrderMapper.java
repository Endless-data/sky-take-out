package com.sky.mapper;

import com.github.pagehelper.Page;
import com.sky.dto.OrdersPageQueryDTO;
import com.sky.entity.Orders;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface OrderMapper {
    void insert(Orders orders);

    /**
     * 根据订单号查询订单信息
     *
     * @param orderNumber 订单号
     * @return 订单信息
     */
    @Select("select * from orders where number = #{orderNumber}")
    Orders getByNumber(String orderNumber);

    /**
     * 更新订单信息
     *
     * @param orders 订单信息
     */
    void update(Orders orders);

    /**
     * 分页查询订单
     *
     * @param ordersPageQueryDTO 订单分页查询参数
     * @return 订单分页结果
     */
    Page<Orders> pageQuery(OrdersPageQueryDTO ordersPageQueryDTO);

    /**
     * 根据ID查询订单
     *
     * @param id 订单ID
     * @return 订单信息
     */
    @Select("select * from orders where id = #{id}")
    Orders getById(Long id);


    /**
     * 根据状态统计订单数量
     * @param status 订单状态
     * @return 订单数量
     */
    @Select("select count(id) from orders where status = #{status}")
    Integer countStatus(Integer status);
}
