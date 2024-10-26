package com.explorex.zero.job;

import com.explorex.zero.constants.ActiveConstant;
import com.explorex.zero.util.RedisUtils;
import com.explorex.zero.util.ZookeeperUtils;
import com.xxl.job.core.biz.model.ReturnT;
import com.xxl.job.core.handler.annotation.XxlJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.concurrent.TimeUnit;

@Component
public class ActiveUpXxlJob {
    private static Logger logger = LoggerFactory.getLogger(ActiveUpXxlJob.class);

    @Autowired
    private ZookeeperUtils zookeeperUtils;
    @Autowired
    private RedisUtils redisUtils;

    //定时任务，每天00:00执行一次，将活动id存到缓存中去[预热]
    @XxlJob("ActiveUpJobHandler")
    public ReturnT<String> demoJobHandler(String param) throws Exception {
        logger.info("ActiveUpJobHandler start, param:{}", param);
        //1.计算当前时间戳距离今年开始的日期天数
        // 获取当前日期
        LocalDate currentDate = LocalDate.now(ZoneId.systemDefault());

        // 获取今年的1月1日
        LocalDate startOfYear = LocalDate.of(currentDate.getYear(), 1, 1);

        // 计算当前日期与今年1月1日之间的天数差
        long daysSinceStartOfYear = ChronoUnit.DAYS.between(startOfYear, currentDate);
        String Key = ActiveConstant.ACTIVE_UP_KEY + daysSinceStartOfYear;
        //2.从zookeeper中拿到活动_时间戳和活动id的
        String value = null;
        if(zookeeperUtils.exists("/explorex/active/"+Key)) {
            byte[] bytes = zookeeperUtils.readNodeData("/explorex/active/" + Key);
            value = new String(bytes);
        }

        //3.将活动id存到缓存中去
        String RedisKey = ActiveConstant.CURRENT_ACTIVE_KEY;
        if(value!=null&&value.isBlank()) {
            redisUtils.set(RedisKey,value,ActiveConstant.ACTIVE_UP_TIME, TimeUnit.DAYS);
        }else{
            redisUtils.set(RedisKey,value,ActiveConstant.DEFAULT_ACTIVE_ID, TimeUnit.DAYS);
        }

        return ReturnT.SUCCESS;
    }
}