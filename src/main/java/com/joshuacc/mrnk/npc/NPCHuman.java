package com.joshuacc.mrnk.npc;

import java.nio.charset.StandardCharsets;

import cn.nukkit.Player;
import cn.nukkit.entity.Entity;
import cn.nukkit.entity.EntityHuman;
import cn.nukkit.entity.data.FloatEntityData;
import cn.nukkit.item.Item;
import cn.nukkit.level.format.FullChunk;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;
import cn.nukkit.network.protocol.AddPlayerPacket;

public abstract class NPCHuman extends EntityHuman {

	public NPCHuman(FullChunk chunk, CompoundTag nbt) {
		super(chunk, nbt);
		this.setDataProperty(new FloatEntityData(DATA_SCALE, this.namedTag.getFloat("scale")));
	}

	@Override
	public void spawnTo(Player player) {
		if (!this.hasSpawned.containsKey(player.getLoaderId())) {
			this.hasSpawned.put(player.getLoaderId(), player);

			this.server.updatePlayerListData(this.getUniqueId(), this.getId(), this.getName(), this.getSkin(),
					new Player[] { player });

			AddPlayerPacket pk = new AddPlayerPacket();
			pk.uuid = this.getUniqueId();
			pk.username = this.getName();
			pk.entityUniqueId = this.getId();
			pk.entityRuntimeId = this.getId();
			pk.x = (float) this.x;
			pk.y = (float) this.y;
			pk.z = (float) this.z;
			pk.speedX = (float) this.motionX;
			pk.speedY = (float) this.motionY;
			pk.speedZ = (float) this.motionZ;
			pk.yaw = (float) this.yaw;
			pk.pitch = (float) this.pitch;
			this.inventory.setItemInHand(Item.fromString(this.namedTag.getString("Item")));
			pk.item = this.getInventory().getItemInHand();
			pk.metadata = this.dataProperties;
			player.dataPacket(pk);

			this.inventory.setHelmet(Item.fromString(this.namedTag.getString("Helmet")));
			this.inventory.setChestplate(Item.fromString(this.namedTag.getString("Chestplate")));
			this.inventory.setLeggings(Item.fromString(this.namedTag.getString("Leggings")));
			this.inventory.setBoots(Item.fromString(this.namedTag.getString("Boots")));

			this.inventory.sendArmorContents(player);

			this.server.removePlayerListData(this.getUniqueId(), new Player[] { player });

			super.spawnTo(player);
		}
	}
	
	public abstract void onInteract(Player player);
	
	public static Entity createEntity(String type, String name, Player player, String command, float scale)
	{
		CompoundTag nbt = nbt(player, name, command, scale);
		Entity ent = Entity.createEntity(type, player.chunk, nbt);
		ent.setNameTag(name);
		ent.setNameTagAlwaysVisible(true);
		ent.spawnToAll();
		return ent;
	}

	private static CompoundTag nbt(Player p, String name, String command, float scale) {
		CompoundTag nbt = new CompoundTag()
				.putList(new ListTag<>("Pos")
						.add(new DoubleTag("", p.x))
						.add(new DoubleTag("", p.y))
						.add(new DoubleTag("", p.z)))
				.putList(new ListTag<DoubleTag>("Motion")
						.add(new DoubleTag("", 0))
						.add(new DoubleTag("", 0))
						.add(new DoubleTag("", 0)))
				.putList(new ListTag<FloatTag>("Rotation")
						.add(new FloatTag("", (float) p.getYaw()))
						.add(new FloatTag("", (float) p.getPitch())))
				.putBoolean("Invulnerable", true)
				.putString("Command", command)
				.putString("NameTag", name)
				.putBoolean("npc", true)
				.putFloat("scale", scale);
		CompoundTag skinTag = new CompoundTag()
				.putByteArray("Data", p.getSkin().getSkinData().data)
				.putInt("SkinImageWidth", p.getSkin().getSkinData().width)
				.putInt("SkinImageHeight", p.getSkin().getSkinData().height)
				.putString("ModelId", p.getSkin().getSkinId())
				.putString("CapeId", p.getSkin().getCapeId())
				.putByteArray("CapeData", p.getSkin().getCapeData().data)
				.putInt("CapeImageWidth", p.getSkin().getCapeData().width)
				.putInt("CapeImageHeight", p.getSkin().getCapeData().height)
				.putByteArray("SkinResourcePatch", p.getSkin().getSkinResourcePatch().getBytes(StandardCharsets.UTF_8))
				.putByteArray("GeometryData", p.getSkin().getGeometryData().getBytes(StandardCharsets.UTF_8))
				.putByteArray("AnimationData", p.getSkin().getAnimationData().getBytes(StandardCharsets.UTF_8))
				.putBoolean("PremiumSkin", p.getSkin().isPremium())
				.putBoolean("PersonaSkin", p.getSkin().isPersona())
				.putBoolean("CapeOnClassicSkin", p.getSkin().isCapeOnClassic());
		nbt.putCompound("Skin", skinTag);
		nbt.putBoolean("ishuman", true);
		nbt.putString("Item", p.getInventory().getItemInHand().getName());
		nbt.putString("Helmet", p.getInventory().getHelmet().getName());
		nbt.putString("Chestplate", p.getInventory().getChestplate().getName());
		nbt.putString("Leggings", p.getInventory().getLeggings().getName());
		nbt.putString("Boots", p.getInventory().getBoots().getName());
		return nbt;
	}
}
