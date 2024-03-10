package com.bitharmony.comma.album.album.scheduling;

import static org.quartz.JobBuilder.*;

import org.quartz.JobDetail;
import org.quartz.JobKey;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
@Component
public class AlbumStreamingCountJobDetailService {
	public JobDetail build(JobKey jobKey) {
		return newJob(AlbumStreamingCountJob.class)
			.withIdentity(jobKey.getName(), jobKey.getGroup())
			.storeDurably(true)
			.build();
	}
}