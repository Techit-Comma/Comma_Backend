package com.bitharmony.comma.album.album.scheduling;

import static org.quartz.TriggerBuilder.*;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AlbumStreamingCountTriggerService {
	public Trigger build(JobKey jobKey) {
		log.info("Album Streaming Reset trigger 설정");
		return newTrigger()
			.forJob(jobKey)
			.withIdentity(jobKey.getName() + "Trigger", jobKey.getGroup())
			.withSchedule(CronScheduleBuilder.dailyAtHourAndMinute(12, 0)) // 매일 12시에 실행
			.build();
	}
}