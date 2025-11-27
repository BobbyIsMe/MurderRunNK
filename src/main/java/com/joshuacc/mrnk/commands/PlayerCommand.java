package com.joshuacc.mrnk.commands;

import java.nio.charset.StandardCharsets;

import com.joshuacc.mrnk.files.MRLobbyConfig;
import com.joshuacc.mrnk.lang.ConfigLang;
import com.joshuacc.mrnk.main.MRMain;
import com.joshuacc.mrnk.main.MRTeam.MapModes;

import cn.nukkit.Player;
import cn.nukkit.command.Command;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.entity.Entity;
import cn.nukkit.nbt.tag.CompoundTag;
import cn.nukkit.nbt.tag.DoubleTag;
import cn.nukkit.nbt.tag.FloatTag;
import cn.nukkit.nbt.tag.ListTag;

public class PlayerCommand extends Command {

	private MRLobbyConfig lobby;

	public PlayerCommand(MRMain main) {
		super("npcadd", "Adds necessary NPCs for players to see the map selector!");
		this.lobby = main.getMRLobbyConfig();
		this.setPermission("mr.npcadd");
		this.commandParameters.clear();
		this.commandParameters.put("default", new CommandParameter[] {
				CommandParameter.newEnum("options", new String[] {
						"normal", "escape", "unqueue", "shop", "sell"	
				})
		});
	}

	@Override
	public boolean execute(CommandSender sender, String label, String[] args) 
	{
		if(!(sender instanceof Player))
		{
			sender.sendMessage(ConfigLang.NOTPLAYER.toString());
			return false;
		}

		Player player = (Player) sender;

		if(!player.hasPermission("mr.npcadd"))
			return false;

		if(args.length == 0)
			player.sendMessage(ConfigLang.SHORTARGUEMENTS.toString());
		else if(args.length == 1)
		{
			String type = args[0];
			switch(type)
			{
			case "normal":
				lobby.addJoinNPCDetails(MapModes.NORMAL, createEntityMode(player, "openlist %p normal"));
				break;
			case "escape":
				lobby.addJoinNPCDetails(MapModes.ESCAPE, createEntityMode(player, "openlist %p escape"));
				break;
			case "unqueue":
				lobby.addJoinNPCDetails("Unqueue", createEntityLobby(ConfigLang.NPCUNQUEUE.toString(), player, "unqueue %p"));
				break;
			case "shop":
				lobby.addJoinNPCDetails("Shop", createEntityLobby(ConfigLang.NPCSHOP.toString(), player, "openlist %p shop"));
				break;
			case "sell":
				lobby.addJoinNPCDetails("Sell", createEntityLobby(ConfigLang.NPCSELL.toString(), player, "sell %p"));
				break;
			}
		}
		return true;
	}

	private Entity createEntityMode(Player player, String command)
	{
		String name = ConfigLang.NPCJOINLIST.toString();
		CompoundTag nbt = nbt(player, name, command);
		Entity ent = Entity.createEntity("NPCHuman", player.chunk, nbt);
		ent.setNameTag(name);
		ent.setNameTagAlwaysVisible(true);
		ent.spawnToAll();
		return ent;
	}
	
	private Entity createEntityLobby(String name, Player player, String command)
	{
		CompoundTag nbt = nbt(player, name, command);
		Entity ent = Entity.createEntity("NPCHuman", player.chunk, nbt);
		ent.setNameTag(name);
		ent.setNameTagAlwaysVisible(true);
		ent.spawnToAll();
		return ent;
	}

	private CompoundTag nbt(Player p, String name, String command) {
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
				.putFloat("scale", 1.5F);
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
