package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class ClientSharedWaypointsIndex {

    private static final String INDEX_FILE = "index.json";
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    private static IndexData data = new IndexData();

    private ClientSharedWaypointsIndex() {
    }

    public static void load(Path rootDir) {
        Path file = rootDir.resolve(INDEX_FILE);
        if (!Files.exists(file)) return;

        try (Reader r = Files.newBufferedReader(file)) {
            IndexData loaded = GSON.fromJson(r, IndexData.class);
            if (loaded != null) data = loaded;
        } catch (Exception ignored) {
        }
    }

    public static void save(Path rootDir) {
        Path file = rootDir.resolve(INDEX_FILE);
        try (Writer w = Files.newBufferedWriter(file)) {
            GSON.toJson(data, w);
        } catch (Exception ignored) {
        }
    }

    public static String getServerFile(String serverId) {
        return data.servers.get(serverId);
    }

    public static void setServerFile(String serverId, String fileName) {
        data.servers.put(serverId, fileName);
    }

    private static final class IndexData {
        Map<String, String> servers = new HashMap<>();
    }
}
