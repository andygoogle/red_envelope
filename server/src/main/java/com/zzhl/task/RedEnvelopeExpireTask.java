package com.zzhl.task;

import com.zzhl.config.RedEnvelopeConfig;
import com.zzhl.service.RedEnvelopeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * 红包过期处理任务
 * 5分钟发运行一次
 *
 * @author Andy
 * @date 2017-01-05
 */
//@Component("bbExpireTask")
public class RedEnvelopeExpireTask {
	private static final Logger logger = LoggerFactory.getLogger(RedEnvelopeExpireTask.class);

	@Autowired
	private RedEnvelopeService redEnvelopeService;
	@Autowired
	private RedEnvelopeConfig redEnvelopeConfig;

//	@Scheduled(fixedDelay = 5 * 60 * 1000)
    public void run() {
		// 判断是否启动数据统计任务
		if (redEnvelopeConfig.getEnableScheduledTask() == 0) {
			logger.info("红包过期处理任务, 未开启 ...");
			return;
		}

		logger.info("红包过期处理任务 start ...");
		int count = 0, page = 0, size = 100;
		try {
			long expireTime = System.currentTimeMillis() - 24 * 3600 * 1000L;
//			while (true) {
//				page++;
//				List<Integer> hbIds = hbService.getExpiresHbIds(page, size, expireTime);
//				if (hbIds == null || hbIds.size() == 0) {
//					break;
//				}
//				count = count + hbIds.size();
//				for (Integer hbid: hbIds) {
//					hbService.refund(hbid);
//				}
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}

        logger.info("红包过期处理任务, 处理总量={}, end ...", count);
    }
	
}
