package net.nutchi.cmdon;

import net.nutchi.cmdon.listener.PlayerListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public final class CmdOn extends JavaPlugin {
    Map<Event, List<String>> cmdsMap = new EnumMap<>(Event.class);

    @Override
    public void onEnable() {
        register();

        saveDefaultConfig();
        saveEmptyCmds();
        loadCmds();
    }

    @Override
    public void onDisable() {
    }

    private void register() {
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(this), this);

        getCommand("cmdon").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && args[0].equals("reload")) {
            loadCmds();
            sender.sendMessage("reloaded!");
            return true;
        } else {
            return false;
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Stream.of("reload").filter(s -> s.startsWith(args[0])).collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    private void loadCmds() {
        reloadConfig();
        for (Event event : Event.values()) {
            cmdsMap.put(event, getConfig().getStringList("cmds." + event));
        }
    }

    private void saveEmptyCmds() {
        for (Event event : Event.values()) {
            if (!getConfig().contains("cmds." + event, true)) {
                getConfig().set("cmds." + event, new ArrayList<String>());
            }
        }
        saveConfig();
    }

    public void execCmd(Event event, Function<String, String> replacer) {
        List<String> cmds = cmdsMap.get(event);
        if (cmds != null) {
            cmds.forEach(c ->
                    getServer().dispatchCommand(getServer().getConsoleSender(), replacer.apply(c))
            );
        }
    }
}
