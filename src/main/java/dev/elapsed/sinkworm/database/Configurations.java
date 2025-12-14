package dev.elapsed.sinkworm.database;

import dev.elapsed.sinkworm.SinkWorm;

import java.util.Set;

public class Configurations {

    private static transient Configurations instance = new Configurations();

    public static final String WEBHOOK_URL = "";
    public static final String ALERT_BOT_USERNAME = "BotSniffer";
    public static final String ALERT_BOT_AVATAR_URL = "";

    public static final Set<String> TRUSTED_IPS = Set.of("");
    public static String LOGGER_TITLE = "[SinkWorm]";
    public static int SERVER_PORT = 8080;
    public static int MAX_THREADS = 5;
    public static int MIN_THREADS = 1;
    public static int IDLE_TIMEOUT = 30000;

    public static void save() {
        SinkWorm.getInstance().getPersist().save(instance, "config");
    }

    public static void load() {
        SinkWorm.getInstance().getPersist().loadOrSaveDefault(instance, Configurations.class, "config");
    }


}
