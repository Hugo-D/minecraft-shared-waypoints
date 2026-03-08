package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.persistence;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.data.ClientSharedWaypointsState;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.data.LocalWaypointOverride;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.data.LocalWaypointOverridesState;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.observer.ClientSharedWaypointListener;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.client.observer.LocalWaypointOverrideListener;
import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data.WaypointDTO;

import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class ClientSharedWaypointsPersistence {

    private static final String FOLDER_NAME = "shared_waypoints";
    private static final String FILE_NAME_FORMAT = "server_%s.json";

    private static Path rootDir;
    private static String currentServerId;

    public static void initialize(Path configDir) {
        rootDir = configDir.resolve(FOLDER_NAME);
        try {
            Files.createDirectories(rootDir);
        } catch (Exception ignored) {
        }
        ClientSharedWaypointsIndex.load(rootDir);

        ClientSharedWaypointListener.INSTANCE.subscribe(ClientSharedWaypointsPersistence::onSharedWaypointsChanged);
        LocalWaypointOverrideListener.INSTANCE.subscribe(ClientSharedWaypointsPersistence::onOverridesChanged);
    }

    public static void setCurrentServer(String serverId) {
        currentServerId = serverId;
    }

    private static Path resolveServerFile(String serverId) {
        String fileName = ClientSharedWaypointsIndex.getServerFile(serverId);
        if (fileName == null) {
            fileName = String.format(FILE_NAME_FORMAT, serverId.replace(":", "_"));
            ClientSharedWaypointsIndex.setServerFile(serverId, fileName);
            ClientSharedWaypointsIndex.save(rootDir);
        }
        return rootDir.resolve(fileName);
    }

    public static PersistedServerData loadServer(String serverId) {
        Path file = resolveServerFile(serverId);
        if (!Files.exists(file)) return new PersistedServerData();

        try (Reader r = Files.newBufferedReader(file)) {
            PersistedServerData data = new Gson().fromJson(r, PersistedServerData.class);
            return data != null ? data : new PersistedServerData();
        } catch (Exception e) {
            return new PersistedServerData();
        }
    }

    public static void saveServer(String serverId, PersistedServerData data) {
        Path file = resolveServerFile(serverId);
        try (Writer w = Files.newBufferedWriter(file)) {
            new GsonBuilder().setPrettyPrinting().create().toJson(data, w);
        } catch (Exception ignored) {
        }
    }

    private static void onSharedWaypointsChanged(ClientSharedWaypointListener.Event event) {
        if (currentServerId == null) return;

        PersistedServerData data = loadServer(currentServerId);
        data.sharedWaypoints = new HashMap<>(ClientSharedWaypointsState.getAll());
        data.clientVersion = ClientSharedWaypointsState.getClientVersion();
        saveServer(currentServerId, data);
    }

    private static void onOverridesChanged(LocalWaypointOverrideListener.Event event) {
        if (currentServerId == null) return;

        PersistedServerData data = loadServer(currentServerId);
        data.localOverrides = new HashMap<>(LocalWaypointOverridesState.getAll());
        saveServer(currentServerId, data);
    }

    public static final class PersistedServerData {
        public int clientVersion = 0;
        public Map<UUID, WaypointDTO> sharedWaypoints = new HashMap<>();
        public Map<UUID, LocalWaypointOverride> localOverrides = new HashMap<>();
    }
}
