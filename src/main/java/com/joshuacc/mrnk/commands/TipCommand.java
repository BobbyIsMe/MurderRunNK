package com.joshuacc.mrnk.commands;

import cn.nukkit.command.defaults.VanillaCommand;

import cn.nukkit.Player;
import cn.nukkit.Server;
import cn.nukkit.command.CommandSender;
import cn.nukkit.command.data.CommandParamType;
import cn.nukkit.command.data.CommandParameter;
import cn.nukkit.lang.TranslationContainer;
import cn.nukkit.utils.TextFormat;

import java.util.Locale;


public class TipCommand extends VanillaCommand {

    public TipCommand() {
        super("tip", "%nukkit.command.tip.description", "%nukkit.command.tip.usage");
        this.setPermission("nukkit.command.tip");

        this.commandParameters.clear();
        this.commandParameters.put("tip", new CommandParameter[]{
                new CommandParameter("player", CommandParamType.TARGET, false),
                new CommandParameter("tip", new String[]{"tip"}),
                new CommandParameter("tipText", CommandParamType.STRING, false)
        });
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!this.testPermission(sender)) {
            return true;
        }
        if (args.length < 2) {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }

        Player player = Server.getInstance().getPlayerExact(args[0].replace("@s", sender.getName()));
        if (player == null) {
            sender.sendMessage(new TranslationContainer(TextFormat.RED + "%commands.generic.player.notFound"));
            return true;
        }

        if (args.length == 3) {
            switch (args[1].toLowerCase(Locale.ROOT)) {
                case "tip":
                    player.sendTip(args[2]);
                    break;
                default:
                    sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
                    return false;
            }
        } else {
            sender.sendMessage(new TranslationContainer("commands.generic.usage", this.usageMessage));
            return false;
        }

        return true;
    }
}