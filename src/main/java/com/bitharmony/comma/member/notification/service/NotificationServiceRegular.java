package com.bitharmony.comma.member.notification.service;

import static org.quartz.JobKey.jobKey;

import com.bitharmony.comma.album.album.scheduling.AlbumStreamingCountJobDetailService;
import com.bitharmony.comma.album.album.scheduling.AlbumStreamingCountTriggerService;
import com.bitharmony.comma.member.notification.scheduling.OldNotificationJobDetailService;
import com.bitharmony.comma.member.notification.scheduling.OldNotificationTriggerService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class NotificationServiceRegular {
	private final Scheduler scheduler;
	private final OldNotificationJobDetailService jobDetailService;
	private final OldNotificationTriggerService triggerService;

	@PostConstruct
	public void init() {
		resetStreamingCountsRegularly();
	}

	public void resetStreamingCountsRegularly() {
		JobKey jobKey = jobKey("RemoveOldNotifications", "NotificationService");

		JobDetail jobDetail = makeJobDetail(jobKey);
		Trigger trigger = makeTrigger(jobKey);

		makeSchedule(jobDetail, trigger);
	}

	private JobDetail makeJobDetail(JobKey jobKey){
		return jobDetailService.build(jobKey);
	}

	private Trigger makeTrigger(JobKey jobKey) {
		return triggerService.build(jobKey);
	}

	private void makeSchedule(JobDetail jobDetail, Trigger trigger) {
		try {
			scheduler.scheduleJob(jobDetail, trigger);
		} catch (SchedulerException e) {
			throw new RuntimeException();
		}
	}
}