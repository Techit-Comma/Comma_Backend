package com.bitharmony.comma.member.notification.scheduling;

import static org.quartz.TriggerBuilder.newTrigger;

import lombok.extern.slf4j.Slf4j;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobKey;
import org.quartz.Trigger;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class OldNotificationTriggerService {
	public Trigger build(JobKey jobKey) {
		return newTrigger()
			.forJob(jobKey)
			.withIdentity(jobKey.getName() + "Trigger", jobKey.getGroup())
			.withSchedule(CronScheduleBuilder.monthlyOnDayAndHourAndMinute(1, 12, 0)) // 매월 1일, 12시에 실행
			.build();
	}
}