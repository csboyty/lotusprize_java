package com.zhongyi.lotusprize.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

/**
 * 用来删除临时文件,删除 currentTime-fileCreateTime >= 3hours
 * 
 */

@Component
public class SystemTempFileSweep {
	private final Logger LOG = LoggerFactory.getLogger(SystemTempFileSweep.class);

	private ScheduledExecutorService runner;

	@PostConstruct
	public void init() {
		runner = Executors.newSingleThreadScheduledExecutor();
		Runnable task = new SweepTask(LotusprizeLocalFiles.instance()
				.tempStorageDir(), 3 * 60 * 60 * 1000L);
		runner.scheduleAtFixedRate(task, 0, 3L, TimeUnit.HOURS);
	}

	@PreDestroy
	public void destroy() {
		runner.shutdownNow();
	}

	private class SweepTask implements Runnable {
		private final File dir;
		private final long deadlineInMills;

		public SweepTask(File dir, long deadlineInMills) {
			this.dir = dir;
			this.deadlineInMills = deadlineInMills;
		}

		public void run() {
			final long cutoff = System.currentTimeMillis() - deadlineInMills;
			Path path = dir.toPath();
			try {
				Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
					public FileVisitResult visitFile(Path subPath,
							BasicFileAttributes attrs) throws IOException {
						FileVisitResult result = FileVisitResult.CONTINUE;
						if (attrs.lastModifiedTime().toMillis() < cutoff) {
							if (Files.isDirectory(subPath)) {
								result = FileVisitResult.SKIP_SUBTREE;
							}
							Files.delete(subPath);
						}
						return result;
					}
				});
			} catch (IOException e) {
				LOG.error("", e);
			}
		}
	}

}