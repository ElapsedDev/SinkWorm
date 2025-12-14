package dev.elapsed.sinkworm.database.serializer;

import dev.elapsed.sinkworm.database.Configurations;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.logging.Level;
import java.util.logging.Logger;

public class DiscUtil {

    private static final HashMap<String, Lock> locks = new HashMap<>();

    public static byte[] readBytes(File file) throws IOException {
        int length = (int) file.length();
        byte[] output = new byte[length];
        InputStream in = new FileInputStream(file);
        int offset = 0;
        while (offset < length) {
            offset += in.read(output, offset, (length - offset));
        }
        in.close();

        return output;
    }

    public static void writeBytes(File file, byte[] bytes) throws IOException {
        FileOutputStream out = new FileOutputStream(file);
        out.write(bytes);
        out.close();
    }

    public static void write(File file, String content) throws IOException {
        writeBytes(file, utf8(content));
    }

    public static String read(File file) throws IOException {
        return utf8(readBytes(file));
    }

    public static boolean writeCatch(File file, String content, boolean  sync) {
        String name = file.getName();
        final Lock lock;

        if (locks.containsKey(name)) {
            lock = locks.get(name);
        } else {
            ReadWriteLock rwl = new ReentrantReadWriteLock();
            lock = rwl.writeLock();
            locks.put(name, lock);
        }

        if (sync) {
            lock.lock();
            try {
                Logger.getLogger(Configurations.LOGGER_TITLE).log(Level.INFO, "Saving Cached Memory to the Database");
                file.createNewFile();
                writeBytes(file, content.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                lock.unlock();
            }
        } else {
            CompletableFuture.runAsync(() -> {
                lock.lock();
                try {
                    write(file, content);
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    lock.unlock();
                }
            });
        }

        return true;
    }

    public static String readCatch(File file) {
        try {
            return read(file);
        } catch (IOException e) {
            return null;
        }
    }

    public static byte[] utf8(String string) {
        return string.getBytes(StandardCharsets.UTF_8);
    }

    public static String utf8(byte[] bytes) {
        return new String(bytes, StandardCharsets.UTF_8);
    }

}