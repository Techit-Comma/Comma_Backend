package com.bitharmony.comma.album.album.service;

import static org.quartz.JobKey.*;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.springframework.stereotype.Service;

import com.bitharmony.comma.album.album.scheduling.AlbumStreamingCountJobDetailService;
import com.bitharmony.comma.album.album.scheduling.AlbumStreamingCountTriggerService;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlbumServiceRegular {
	private final Scheduler scheduler;
	private final AlbumStreamingCountJobDetailService jobDetailService;
	private final AlbumStreamingCountTriggerService triggerService;

	@PostConstruct
	public void init() {
		resetStreamingCountsRegularly();
	}

	public void resetStreamingCountsRegularly() {
		JobKey jobKey = makeJobKey("ResetStreamingCounts");

		JobDetail jobDetail = makeJobDetail(jobKey);
		Trigger trigger = makeTrigger(jobKey);

		makeSchedule(jobDetail, trigger);
	}

	private JobKey makeJobKey(String jobName) {
		return jobKey(jobName, "AlbumService");
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