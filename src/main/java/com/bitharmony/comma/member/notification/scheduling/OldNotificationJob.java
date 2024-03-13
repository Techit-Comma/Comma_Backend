package com.bitharmony.comma.member.notification.scheduling;

import com.bitharmony.comma.album.album.service.AlbumService;
import com.bitharmony.comma.member.notification.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.PersistJobDataAfterExecution;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@DisallowConcurrentExecution
@PersistJobDataAfterExecution
@RequiredArgsConstructor
public class OldNotificationJob implements Job {

	private final NotificationService notificationService;

	@Override
	public void execute(JobExecutionContext context) {
		notificationService.removeOldNotification();
	}
}
