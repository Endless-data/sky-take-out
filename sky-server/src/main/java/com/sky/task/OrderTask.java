package com.sky.task;

import com.sky.entity.Orders;
import com.sky.mapper.OrderMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 定时任务类，定时处理订单状态
 */
@Component
@Slf4j
public class OrderTask {

    private final OrderMapper orderMapper;

    public OrderTask(OrderMapper orderMapper) {
        this.orderMapper = orderMapper;
    }

    /**
     * 处理超时订单的定时任务
     */
    @Scheduled(cron = "0 * * * * ?") // 每分钟执行一次
    public void processTimeoutOrders() {
        log.info("定时处理超时订单: {}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusMinutes(-15);

        // select * from orders where status = ? and order_time < (now() - 15 minutes)
        List<Orders> timeoutOrders = orderMapper.getByStatusAndOrderTime(Orders.PENDING_PAYMENT, time);

        if (timeoutOrders != null && !timeoutOrders.isEmpty()) {
            for (Orders order : timeoutOrders) {
                order.setStatus(Orders.CANCELLED);
                order.setCancelReason("超时未支付，系统自动取消");
                order.setCancelTime(LocalDateTime.now());
                orderMapper.update(order);
            }
        }
    }

    /**
     * 处理一直处于派送中的订单
     */
    @Scheduled(cron = "0 0 1 * * ?") // 每天凌晨一点触发一次
    public void processDeliveryOrders() {
        log.info("定时处理派送中的订单: {}", LocalDateTime.now());

        LocalDateTime time = LocalDateTime.now().plusHours(-1);

        // select * from orders where status = ? and delivery_time < (now() - 1 hour)
        List<Orders> deliveryOrders = orderMapper.getByStatusAndOrderTime(Orders.DELIVERY_IN_PROGRESS, time);

        if (deliveryOrders != null && !deliveryOrders.isEmpty()) {
            for (Orders order : deliveryOrders) {
                order.setStatus(Orders.COMPLETED);
                orderMapper.update(order);
            }
        }
    }
}
