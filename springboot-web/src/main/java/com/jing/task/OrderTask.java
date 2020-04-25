package com.jing.task;

import com.example.demo.entity.OmsOrder;
import com.example.demo.service.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * @author Admin
 * @title: OrderTask
 * @projectName demo
 * @description: TODO
 * @date 2020/3/28 10:04
 */
@Component
@EnableScheduling
public class OrderTask {
    private final Logger LOG = LoggerFactory.getLogger(OrderTask.class);
    @Resource
    private OrderService orderService;

    @Scheduled(cron = "0/5 * * * * ?")
    public void work() {
        LOG.info(Thread.currentThread().getName());
    }

    @Scheduled(cron = "0/30 * * * * ?")
    public void checkUnpaidOrder() {
        LOG.info("开始检查未付款单据 = ");
        long beginTime = System.currentTimeMillis();
        List<OmsOrder> unpaidOrderList = orderService.getUnpaidOrderList();
        for (OmsOrder orderInfo : unpaidOrderList) {
            orderService.checkExpireOrder(orderInfo);
        }
        long costtime = System.currentTimeMillis() - beginTime;
        LOG.info("开始检查完毕未付款单据 = 共消耗" + costtime);
    }


    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(5);
        return taskScheduler;
    }

}
