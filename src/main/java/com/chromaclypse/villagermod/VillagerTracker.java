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

	private final NamespacedKey TRACKED_UUID;
	private final PersistentUUID serializer = new PersistentUUID();
	
	public VillagerTracker(Plugin handle) {
		TRACKED_UUID = new NamespacedKey(handle, "origin_uuid");
	}
	
	private boolean hasTrackedUUID(Entity entity) {
		return entity.getPersistentDataContainer().has(TRACKED_UUID, serializer);
	}
	
	private UUID getTrackedUUID(Entity entity) {
		return entity.getPersistentDataContainer().get(TRACKED_UUID, serializer);
	}
	
	private void setTrackedUUID(Entity entity, UUID uuid) {
		entity.getPersistentDataContainer().set(TRACKED_UUID, serializer, uuid);
	}
	
	public Optional<UUID> getTrackedVillagerUUID(Entity entity) {
		if(!hasTrackedUUID(entity)) {
			if(!(entity instanceof Villager)) {
				return Optional.empty();
			}
			
			setTrackedUUID(entity, entity.getUniqueId());
		}
		
		return Optional.of(getTrackedUUID(entity));
	}
	
	public boolean trackVillagerTransformation(UUID villagerUUID, Entity newEntity) {
		if(hasTrackedUUID(newEntity)) {
			return false;
		}
		
		setTrackedUUID(newEntity, villagerUUID);
		return true;
	}
	
	private static class PersistentUUID implements PersistentDataType<byte[], UUID> {

		@Override
		public Class<byte[]> getPrimitiveType() {
			return byte[].class;
		}

		@Override
		public Class<UUID> getComplexType() {
			return UUID.class;
		}

		@Override
		public byte[] toPrimitive(UUID complex, PersistentDataAdapterContext context) {
			ByteBuffer buffer = ByteBuffer.allocate(16);
			buffer.putLong(complex.getMostSignificantBits());
			buffer.putLong(complex.getLeastSignificantBits());
			return buffer.array();
		}

		@Override
		public UUID fromPrimitive(byte[] primitive, PersistentDataAdapterContext context) {
			ByteBuffer buffer = ByteBuffer.wrap(primitive);
			
			return new UUID(buffer.getLong(0), buffer.getLong(1));
		}
	}
}
