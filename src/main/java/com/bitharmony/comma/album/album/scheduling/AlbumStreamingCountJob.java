package com.bitharmony.comma.album.album.scheduling;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.stereotype.Component;

import com.bitharmony.comma.album.album.service.AlbumService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@RequiredArgsConstructor
public class AlbumStreamingCountJob implements Job {

	private final AlbumService albumService;

	@Override
	public void execute(JobExecutionContext context) {
		albumService.resetStreamingCounts();
	}
}
