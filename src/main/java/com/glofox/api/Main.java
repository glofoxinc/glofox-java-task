package com.glofox.api;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Entry point for the Glofox bookings API.
 *
 * <p>The skeleton starts an HTTP server on {@code :8080} with no routes
 * registered. You decide the package layout, the HTTP style, and the storage
 * approach.
 */
public final class Main {

    /**
     * Class pre-loaded into the store at startup. Candidates do not create a
     * class — they book against this one. Seed it into your store before
     * registering routes.
     */
    public static final SeedClass SEED_CLASS = new SeedClass(
            "5d2e2a9f-3c1b-4f2a-b6e0-1a2b3c4d5e6f",
            "Pilates",
            "2026-12-14",
            "2026-12-14",
            10);

    public static void main(String[] args) throws IOException {
        String addr = System.getenv("ADDR");
        if (addr == null || addr.isBlank()) {
            addr = ":8080";
        }
        final InetSocketAddress listen;
        try {
            listen = listenAddress(addr);
        } catch (IllegalArgumentException ex) {
            System.err.println(ex.getMessage());
            System.exit(1);
            return;
        }

        // TODO: initialise your store and seed SEED_CLASS into it here.
        SeedClass seed = SEED_CLASS;
        System.out.printf(
                "seeded class id=%s name=\"%s\" dates=%s..%s capacity=%d%n",
                seed.id, seed.name, seed.startDate, seed.endDate, seed.capacity);

        HttpServer server = HttpServer.create(listen, 0);

        // TODO: register your routes here.
        //
        //   POST /bookings -> book a member onto a pre-seeded class for a specific date
        //
        // See README.md for the full task brief.

        // HttpServer's default executor runs every request on one thread.
        // A virtual thread per request lets overlapping calls run concurrently.
        ExecutorService executor = Executors.newVirtualThreadPerTaskExecutor();
        server.setExecutor(executor);
        server.start();
        System.out.printf("listening on %s%n", addr);

        // The server thread is non-daemon, so returning from main does not exit.
        // The shutdown hook stops the server on Ctrl+C or SIGTERM.
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            server.stop(5);
            executor.shutdown();
        }));
    }

    private static InetSocketAddress listenAddress(String addr) {
        String trimmed = addr.trim();
        if (trimmed.startsWith(":")) {
            return new InetSocketAddress(parsePort(trimmed.substring(1), trimmed));
        }
        int colon = trimmed.lastIndexOf(':');
        if (colon < 0) {
            return new InetSocketAddress(parsePort(trimmed, trimmed));
        }
        String host = trimmed.substring(0, colon);
        int port = parsePort(trimmed.substring(colon + 1), trimmed);
        if (host.isEmpty()) {
            return new InetSocketAddress(port);
        }
        return new InetSocketAddress(host, port);
    }

    private static int parsePort(String value, String addr) {
        try {
            int port = Integer.parseInt(value);
            if (port < 1 || port > 65535) {
                throw new NumberFormatException(value);
            }
            return port;
        } catch (NumberFormatException ex) {
            throw new IllegalArgumentException("invalid ADDR: " + addr, ex);
        }
    }

    /** Values for the class that is pre-loaded into the store at startup. */
    public static final class SeedClass {
        public final String id;
        public final String name;
        public final String startDate;
        public final String endDate;
        public final int capacity;

        public SeedClass(String id, String name, String startDate, String endDate, int capacity) {
            this.id = id;
            this.name = name;
            this.startDate = startDate;
            this.endDate = endDate;
            this.capacity = capacity;
        }
    }
}
