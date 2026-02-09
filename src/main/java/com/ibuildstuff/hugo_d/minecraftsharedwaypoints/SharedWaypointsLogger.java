package com.ibuildstuff.hugo_d.minecraftsharedwaypoints;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SharedWaypointsLogger {
    public static final String MOD_ID = "sharedwaypoints";

    private static final String PREFIX = "[Shared Waypoints] ";
    private static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static void info(String msg, Object... args) {
        LOGGER.info(PREFIX + msg, args);
    }

    public static void warn(String msg, Object... args) {
        LOGGER.warn(PREFIX + msg, args);
    }

    public static void error(String msg, Object... args) {
        LOGGER.error(PREFIX + msg, args);
    }

    public static void debug(String msg, Object... args) {
        LOGGER.debug(PREFIX + msg, args);
    }
}
