package com.chromaclypse.villagermod;

import java.nio.ByteBuffer;
import java.util.Optional;
import java.util.UUID;

import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Villager;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import org.bukkit.plugin.Plugin;

public class VillagerTracker {

	private final NamespacedKey TRACKED_DATA;
	private final VillagerSerializer serializer = new VillagerSerializer();
	
	private static long globalTickTime() {
		return System.currentTimeMillis() / (1000 / 20);
	}
	
	private static long spawnTime(Entity entity) {
		return globalTickTime() - entity.getTicksLived();
	}
	
	public VillagerTracker(Plugin handle) {
		TRACKED_DATA = new NamespacedKey(handle, "origin_uuid");
	}
	
	private boolean hasTrackedUUID(Entity entity) {
		return entity.getPersistentDataContainer().has(TRACKED_DATA, serializer);
	}
	
	private Data getTrackedData(Entity entity) {
		return entity.getPersistentDataContainer().get(TRACKED_DATA, serializer);
	}
	
	private void setTrackedData(Entity entity, Data data) {
		entity.getPersistentDataContainer().set(TRACKED_DATA, serializer, data);
	}
	
	public Optional<Data> getTrackedVillagerUUID(Entity entity) {
		if(!hasTrackedUUID(entity)) {
			if(!(entity instanceof Villager)) {
				return Optional.empty();
			}
			
			setTrackedData(entity, new Data(entity.getUniqueId(), spawnTime(entity)));
		}
		
		return Optional.of(getTrackedData(entity));
	}
	
	public boolean trackVillagerTransformation(Data villagerData, Entity newEntity) {
		if(hasTrackedUUID(newEntity)) {
			return false;
		}
		
		setTrackedData(newEntity, villagerData);
		return true;
	}
	
	public static class Data {
		private final UUID uuid;
		private final long spawnTicks;
		
		public Data(UUID uuid, long spawnTicks) {
			this.uuid = uuid;
			this.spawnTicks = spawnTicks;
		}
		
		public UUID getTrackedUUID() {
			return uuid;
		}
		
		public long getTradeTimer() {
			return globalTickTime() - spawnTicks;
		}
	}
	
	private static class VillagerSerializer implements PersistentDataType<byte[], Data> {

		@Override
		public Class<byte[]> getPrimitiveType() {
			return byte[].class;
		}

		@Override
		public Class<Data> getComplexType() {
			return Data.class;
		}

		@Override
		public byte[] toPrimitive(Data complex, PersistentDataAdapterContext context) {
			ByteBuffer buffer = ByteBuffer.allocate(20);
			buffer.putLong(complex.uuid.getMostSignificantBits());
			buffer.putLong(complex.uuid.getLeastSignificantBits());
			buffer.putLong(complex.spawnTicks);
			
			return buffer.array();
		}

		@Override
		public Data fromPrimitive(byte[] primitive, PersistentDataAdapterContext context) {
			ByteBuffer buffer = ByteBuffer.wrap(primitive);
			
			return new Data(new UUID(buffer.getLong(0), buffer.getLong(1)), buffer.getLong(2));
		}
	}
}
