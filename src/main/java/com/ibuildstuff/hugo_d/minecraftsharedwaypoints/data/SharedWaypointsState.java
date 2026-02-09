package com.ibuildstuff.hugo_d.minecraftsharedwaypoints.data;

import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.server.level.ServerLevel;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static com.ibuildstuff.hugo_d.minecraftsharedwaypoints.SharedWaypointsLogger.MOD_ID;

public class SharedWaypointsState extends SavedData {

    private static final String STORAGE_KEY = MOD_ID + ".storage_sharedwaypoints";
    private final List<SharedWaypointsEntry> shared = new ArrayList<>();

    // Factory used by the data storage to create/load this SavedData
    public static final SavedData.Factory<SharedWaypointsState> FACTORY = new SavedData.Factory<>(
        SharedWaypointsState::new,      // constructor for new empty state
        SharedWaypointsState::load,     // loader from NBT
        null                            // no data fixer
    );

    public SharedWaypointsState() {
        // empty initial state
    }

    public static SharedWaypointsState get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(FACTORY, STORAGE_KEY);
    }

    public static SharedWaypointsState load(CompoundTag tag, HolderLookup.Provider provider) {
        SharedWaypointsState state = new SharedWaypointsState();
        // TODO: deserialize
        return state;
    }

    @Override
    public @NotNull CompoundTag save(CompoundTag compoundTag, HolderLookup.Provider provider) {
        // TODO: serialize
        return compoundTag;
    }

    public void add(SharedWaypointsEntry entry) {
        shared.add(entry);
        setDirty();
    }

    public List<SharedWaypointsEntry> getAll() {
        return List.copyOf(shared);
    }
}


