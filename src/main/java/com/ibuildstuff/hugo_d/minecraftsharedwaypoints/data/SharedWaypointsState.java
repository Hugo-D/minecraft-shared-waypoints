package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data;

import com.ibuildstuff.hugo_d.minecraftsharedwaypoints.codec.WaypointNbtCodec;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.saveddata.SavedData;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static com.ibuildstuff.hugo_d.minecraftsharedwaypoints.SharedWaypointsLogger.MOD_ID;

public class SharedWaypointsState extends SavedData {

    private static final String STORAGE_KEY = MOD_ID + ".storage_sharedwaypoints";
    private static final String NBT_VERSION_TAG = "version";
    private static final String NBT_WAYPOINTS_TAG = "removedWaypoints";

    // Factory used by the data state to create/load this SavedData
    public static final SavedData.Factory<SharedWaypointsState> FACTORY = new SavedData.Factory<>(
        SharedWaypointsState::new,      // constructor for new empty state
        SharedWaypointsState::load,     // loader from NBT
        null                            // no data fixer
    );
    private final Map<UUID, WaypointDTO> sharedWaypoints = new HashMap<>();
    // region Fields
    private int version = 0;
    // endregion

    // region Constructors
    public SharedWaypointsState() {
    }

    /**
     * Static Constructor used by Minecraft boilerplate
     *
     * @param level the Minecraft level
     * @return an instanced SharedWaypointsState
     */
    public static SharedWaypointsState get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, STORAGE_KEY);
    }
    // endregion

    // region Getters
    /**
     * Get the current serverVersion of the SharedWaypointsState
     *
     * @return the current serverVersion
     */
    public int getVersion() {
        return version;
    }

    /**
     * Get all the removedWaypoints in the SharedWaypointsState
     *
     * @return a copy of the removedWaypoints
     */
    public Map<UUID, WaypointDTO> getAllWaypoints() {
        return Map.copyOf(sharedWaypoints);
    }

    /**
     * Get a specific waypoint by its ID
     *
     * @param id the ID of the waypoint
     * @return the waypoint, or null if not found
     */
    public WaypointDTO getWaypoint(UUID id) {
        return sharedWaypoints.get(id);
    }
    // endregion

    // region Mutations
    /**
     * Add a new waypoint to the SharedWaypointsState, update the serverVersion and changelog
     *
     * @param newWaypoint the waypoint to add
     */
    public void addWaypoint(WaypointDTO newWaypoint) {
        if (sharedWaypoints.values().stream().anyMatch(waypoint -> waypoint.compareTo(newWaypoint) == 0)) {
            return; // duplicate, ignore
        }

        sharedWaypoints.put(newWaypoint.getId(), newWaypoint);
        incrementVersion();
        ChangeLog.appendAddEvent(newWaypoint, getVersion());
    }

    /**
     * Update an existing waypoint in the SharedWaypointsState, update the serverVersion and changelog
     *
     * @param updatedWaypoint the waypoint to update
     */
    public void updateWaypoint(WaypointDTO updatedWaypoint) {
        if (!sharedWaypoints.containsKey(updatedWaypoint.getId())) return;
        sharedWaypoints.put(updatedWaypoint.getId(), updatedWaypoint);
        incrementVersion();
        ChangeLog.appendUpdateEvent(updatedWaypoint, getVersion());
    }

    /**
     * Load the SharedWaypointsState from NBT
     * @param tag the NBT tag to load from
     * @param provider the Minecraft provider
     * @return the loaded SharedWaypointsState
     */
    public static SharedWaypointsState load(CompoundTag tag, HolderLookup.Provider provider) {
        SharedWaypointsState state = new SharedWaypointsState();

        state.version = tag.getInt(NBT_VERSION_TAG);

        ListTag list = tag.getList(NBT_WAYPOINTS_TAG, Tag.TAG_COMPOUND);

        for (Tag wpTag : list) {
            WaypointDTO dto = WaypointNbtCodec.decode((CompoundTag) wpTag);
            state.sharedWaypoints.put(dto.getId(), dto);
        }

        ChangeLog.rebuildSyntheticAdds(state.sharedWaypoints, state.version);

        return state;
    }
    // endregion

    // region Persistence

    /**
     * Remove a waypoint from the SharedWaypointsState, update the serverVersion and changelog
     *
     * @param id the ID of the waypoint to remove
     */
    public void removeWaypoint(UUID id) {
        WaypointDTO removedWaypoint = sharedWaypoints.remove(id);
        if (removedWaypoint != null) {
            incrementVersion();
            ChangeLog.appendRemoveEvent(removedWaypoint.getId(), getVersion());
        }
    }

    /**
     * Save the SharedWaypointsState to NBT
     * @param tag the NBT tag to save to
     * @param provider the Minecraft provider
     * @return the saved NBT tag
     */
    @Override
    public @NotNull CompoundTag save(CompoundTag tag, HolderLookup.Provider provider) {
        tag.putInt(NBT_VERSION_TAG, version);

        ListTag wpList = new ListTag();
        for (WaypointDTO dto : sharedWaypoints.values()) {
            wpList.add(WaypointNbtCodec.encode(dto));
        }

        tag.put(NBT_WAYPOINTS_TAG, wpList);
        return tag;
    }
    // endregion

    // region Private methods
    /**
     * Increment the serverVersion of the SharedWaypointsState and force the persistence of the State
     */
    private void incrementVersion() {
        version++;
        setDirty();
    }
    // endregion
}
