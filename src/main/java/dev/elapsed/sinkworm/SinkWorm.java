package dev.elapsed.sinkworm;

import dev.elapsed.sinkworm.database.Configurations;
import dev.elapsed.sinkworm.database.QueryDatabase;
import dev.elapsed.sinkworm.database.data.QueryData;
import dev.elapsed.sinkworm.database.serializer.Persist;
import dev.elapsed.sinkworm.modules.CorsFilter;
import dev.elapsed.sinkworm.modules.honepot.*;
import dev.elapsed.sinkworm.modules.queries.IPDetailResponse;
import dev.elapsed.sinkworm.modules.queries.IndexResponse;
import dev.elapsed.sinkworm.modules.queries.SummaryResponse;
import dev.elapsed.sinkworm.modules.queries.TimelineResponse;
import lombok.Getter;
import spark.Request;
import spark.Response;

import java.util.EnumMap;
import java.util.Scanner;
import java.util.UUID;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import static spark.Spark.*;

public class SinkWorm {

    private static volatile SinkWorm instance;

    @Getter
    private Persist persist;
    @Getter
    RouteHandler routeHandler;

    @Getter
    private EnumMap<RouteModules, RouteResponse> modules;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) {

        SinkWorm.getInstance().initialize();
    }

    public static SinkWorm getInstance() {
        if (instance == null) {
            synchronized (SinkWorm.class) {
                if (instance == null) {
                    instance = new SinkWorm();
                }
            }
        }
        return instance;
    }

    public void initialize() {

        persist = new Persist();
        // Additional initialization logic can be added here
        QueryDatabase.load();
        Configurations.load();

        setupConfiguration();
        setupModules();
        setupRoutes();
        setupShutdownListener();
        scheduleDatabaseSaves();
    }

    public void setupConfiguration() {
        port(Configurations.SERVER_PORT);
        threadPool(Configurations.MAX_THREADS, Configurations.MIN_THREADS, Configurations.IDLE_TIMEOUT);
    }

    public void setupModules() {

        /**
         * Module Ideas:
         *
         * - Enhance the FakeLoginModule to simulate more complex login behaviors.
         * - Add a CaptchaModule that serves fake CAPTCHA challenges.
         * - Implement a HoneypotModule that creates hidden fields in forms to trap bots.
         * - Develop a UserAgentModule to analyze and respond based on User-Agent strings.
         * - WebsocketModule to handle and log websocket connections.
         * - Savage introduced me a ssh playground where the user can connect and assume it's a real ssh server but it's just logging their attempts.
         */

        this.modules = new EnumMap<>(RouteModules.class);

        this.modules.put(RouteModules.FAKE_LOGIN, new FakeLoginModule());
        this.modules.put(RouteModules.ROBOT_TEXT, new RobotTextModule());
        this.modules.put(RouteModules.GENERIC_RESPONSE, new QueryPathModule());

    }

    public void setupRoutes() {

        routeHandler = new RouteHandler();

        new CorsFilter().apply();

        path("/sinkworm-api/v1", () -> {
            get("/summary", new SummaryResponse());
            get("/index", new IndexResponse());
            get("/ip/:address", new IPDetailResponse());
            get("/timeline", new TimelineResponse());
        });

        get("/*", routeHandler);
        post("/*", routeHandler);
        put("/*", routeHandler);
        delete("/*", routeHandler);

        notFound(routeHandler);
    }


    private void shutdownServer(boolean isShutdownHook) {
        try {
            Logger.getLogger(Configurations.LOGGER_TITLE).info("[Shutdown] Initiating server shutdown...");
            QueryDatabase.save();

            Logger.getLogger(Configurations.LOGGER_TITLE).info("[Shutdown] Stopping Spark server...");
            stop();
            scheduler.shutdown();

            Logger.getLogger(Configurations.LOGGER_TITLE).info("[Shutdown] Server has been stopped.");
            if (!isShutdownHook) {
                System.exit(0);
            }
        } catch (Exception e) {
            Logger.getLogger(Configurations.LOGGER_TITLE).severe("[Shutdown] Error: " + e.getMessage());
        }
    }

    private void scheduleDatabaseSaves() {
        scheduler.scheduleAtFixedRate(() -> {
            try {
                Configurations.load();
                QueryDatabase.save();
            } catch (Exception e) {
                Logger.getLogger(Configurations.LOGGER_TITLE).severe("[Auto-Save] Error: " + e.getMessage());
            }
        }, 20, 60, TimeUnit.SECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> shutdownServer(true)));
    }

    private void setupShutdownListener() {
        new Thread(() -> {
            Scanner scanner = new Scanner(System.in);
            while (true) {
                String input = scanner.nextLine();
                if ("stop".equalsIgnoreCase(input)) {
                    shutdownServer(false);
                }
            }
        }).start();
    }


    public QueryData registerQuery(Request request, Response response) {

        QueryData queryData = QueryDatabase.QUERIES.get(request.ip());

        if (queryData == null) {

            queryData = new QueryData();
            queryData.setAddress(request.ip());
            queryData.setFirstConnection(System.currentTimeMillis());
        }

        queryData.recordQueryPath(request.pathInfo());
        queryData.recordConnection();

        UUID uuid = UUID.randomUUID();

        for (String keys : request.queryParams()) {
            queryData.addMetaData(uuid.toString(), keys, request.queryParams(keys));
        }

        QueryDatabase.QUERIES.put(request.ip(), queryData);
        return queryData;
    }


}