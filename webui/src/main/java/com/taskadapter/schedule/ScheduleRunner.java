package com.taskadapter.schedule;

import com.taskadapter.connector.common.ProgressMonitorUtils;
import com.taskadapter.connector.definition.exceptions.BadConfigException;
import com.taskadapter.web.SettingsManager;
import com.taskadapter.web.uiapi.Schedule;
import com.taskadapter.web.uiapi.UIConfigStore;
import com.taskadapter.web.uiapi.UISyncConfig;
import com.taskadapter.webui.SchedulesStorage;
import com.taskadapter.webui.TALog;
import com.taskadapter.webui.pages.ExportResultsLogger;
import com.taskadapter.webui.results.ExportResultFormat;
import com.taskadapter.webui.results.ExportResultStorage;
import org.slf4j.Logger;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ScheduleRunner {
    private final static Logger log = TALog.log;

    private final UIConfigStore uiConfigStore;
    private final SchedulesStorage schedulesStorage;
    private final ExportResultStorage exportResultStorage;
    private final SettingsManager settingsManager;


    private final static int threadsNumber = 1;
    private final static int initialDelaySec = 30;
    private final static int intervalSec = 20;

    private volatile boolean allowedToRun;
    private volatile boolean currentlyBusy = false;

    public ScheduleRunner(UIConfigStore uiConfigStore, SchedulesStorage schedulesStorage,
                          ExportResultStorage exportResultStorage, SettingsManager settingsManager) {
        this.uiConfigStore = uiConfigStore;
        this.schedulesStorage = schedulesStorage;
        this.exportResultStorage = exportResultStorage;
        this.settingsManager = settingsManager;
        allowedToRun = settingsManager.schedulerEnabled();
        init();
    }

    private void init() {
        log.info("Configuring scheduler to support periodic sync. Time interval is {} sec. 'enabled' flag is: {}",
                intervalSec, allowedToRun);

        var ex = new ScheduledThreadPoolExecutor(threadsNumber);
        var task = new Runnable() {
            public void run() {
                if (allowedToRun) {
                    var schedules = schedulesStorage.getSchedules();
                    if (!schedules.isEmpty()) {
                        log.info("Found " + schedules.size() + " scheduled configs. Checking if it is time for them to run...");
                        schedules.forEach(s -> {
                            if (needToRun(s)) {
                                launchSyncLeftOrRight(s);
                            }
                        });
                    } else {
                        log.debug("Scheduler is enabled in the application settings, but there are no scheduled tasks to process. " +
                                "You can define some tasks on 'Schedules' page or disable the scheduler to avoid this message.");
                    }
                }
            }
        };
        var f = ex.scheduleAtFixedRate(task, initialDelaySec, intervalSec, TimeUnit.SECONDS);
    }

    public void stop() {
        log.info("Stopping scheduler");
        allowedToRun = false;
    }


    public void start() {
        log.info("Starting scheduler");
        allowedToRun = true;
    }

    private boolean needToRun(Schedule schedule) {
        var results = exportResultStorage.getSaveResults(schedule.getConfigId());
        if (results.isEmpty()) {
            return true;
        }
        var lastResult = results.stream()
                .max(Comparator.comparingLong(ScheduleRunner::finishTime)).get();

        var now = System.currentTimeMillis();
        var lastRanLong = lastResult.getDateStarted().getTime() + lastResult.getTimeTookSeconds() * 1000;
        var timePassedSeconds = (now - lastRanLong) / 1000;
        var needtoRun = timePassedSeconds >= (schedule.getIntervalInMinutes() * 60);
        return needtoRun;
    }

    private static long finishTime(ExportResultFormat resultFormat) {
        return resultFormat.getDateStarted().getTime() + resultFormat.getTimeTookSeconds() * 1000;
    }

    private void launchSyncLeftOrRight(Schedule s) {
        if (currentlyBusy) {
            log.info("Skipping scheduled sync for " + s + " because another sync is currently running");
        } else {
            currentlyBusy = true;
            try {
                var config = uiConfigStore.getConfig(s.getConfigId());
                if (config.isPresent()) {
                    if (s.isDirectionRight()) {
                        launchSync(config.get());
                    }

                    if (s.isDirectionLeft()) {
                        launchSync(config.get().reverse());
                    }
                } else {
                    log.error(
                            String.format("Cannot find config %1$s scheduled for execution in %2$s. Skipping it.",
                                    s.getConfigId(), s)
                    );
                }
            } finally {
                currentlyBusy = false;
            }
        }
    }

    private void launchSync(UISyncConfig c) {
        log.info("Starting scheduled export from {} to {}",
                c.getConnector1().getLabel(),
                c.getConnector2().getLabel());

        try {
            var errors = c.getConnector1().validateForLoad();
            if (errors.isEmpty()) {
                c.getConnector2().validateForSave(c.getFieldMappings());

                var loaded = UISyncConfig.loadTasks(c, 10000);
                var result = c.saveTasks(loaded, ProgressMonitorUtils.DUMMY_MONITOR);
                ExportResultsLogger.log(result, "Scheduled export completed.");
                exportResultStorage.store(result);
            } else {
                logErrors(c, errors);
            }
        } catch (BadConfigException e) {
            log.error("Config {} is scheduled for periodic export, " +
                            "but it will be skipped because it failed load or save validation: {}",
                    c.getConfigId().getId(), e);
        } catch (Exception e) {
            log.error("Config {} scheduled sync: Error {}",
                    c.getConfigId().getId(), e);
        }
    }

    private static void logErrors(UISyncConfig c, List<BadConfigException> errors) {
        log.error("Config {} is scheduled for periodic export, " +
                        "but it will be skipped because it failed load or save validation: {}",
                c.getConfigId().getId(), errors);
    }
}
