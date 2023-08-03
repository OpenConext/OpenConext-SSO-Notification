package nl.kennisnet.services.web.service.jobs;

import nl.kennisnet.services.web.config.CacheConfig;
import nl.kennisnet.services.web.service.CacheHashService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

@Component
public class DataServicesUpdateRunner implements Runnable {

    private static final Logger LOGGER = LoggerFactory.getLogger(DataServicesUpdateRunner.class);

    private static final String ALREADY_RUNNING = "DataServicesUpdateRunner is already running";

    private static final String UPDATE_FAILED = "Cache evicts failed: {}";

    private static final String UPDATE_FINISHED = "Cache evicts finished";

    private static final Lock execLock = new ReentrantLock();

    private static boolean running = false;

    private static String lastDataServicesCacheHash = "";

    private final CacheHashService cacheHashService;

    private final CacheConfig cacheConfig;

    public DataServicesUpdateRunner(CacheHashService cacheHashService, CacheConfig cacheConfig) {
        this.cacheHashService = cacheHashService;
        this.cacheConfig = cacheConfig;
    }

    @Override
    @Scheduled(cron = "${dataservices.fetchCacheHash.cronSchedule:-}")
    public void run() {
        if (running || !execLock.tryLock()) {
            LOGGER.warn(ALREADY_RUNNING);
            return;
        }

        // Set the lock and execute the reaper
        running = true;
        // General exception to ensure that we do not create a deadlock for the scheduled runner
        try {
            execute();
        } catch (Error | Exception e) {
            LOGGER.error(UPDATE_FAILED, e.getMessage());
        }

        // Release the lock
        running = false;
        execLock.unlock();
    }

    private void execute() {
        String dataServicesCacheHash = cacheHashService.fetchCacheHash();
        if (dataServicesCacheHash.equals(lastDataServicesCacheHash)
                || (!lastDataServicesCacheHash.isEmpty() && dataServicesCacheHash.isEmpty())) {
            return;
        }

        LOGGER.info("Changes since last update detected, evicting caches");
        cacheConfig.cacheEvict();
        lastDataServicesCacheHash = dataServicesCacheHash;

        LOGGER.info(UPDATE_FINISHED);
    }

}