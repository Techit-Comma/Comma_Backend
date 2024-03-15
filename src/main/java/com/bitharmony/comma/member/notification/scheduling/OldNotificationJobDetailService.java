package com.bitharmony.comma.member.notification.scheduling;

import static org.quartz.JobBuilder.newJob;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class OldNotificationJobDetailService {
	public JobDetail build(JobKey jobKey) {
		return newJob(OldNotificationJob.class)
			.withIdentity(jobKey.getName(), jobKey.getGroup())
			.storeDurably(true)
			.build();
	}
}