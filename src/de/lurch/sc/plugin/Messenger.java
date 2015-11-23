package de.lurch.sc.plugin;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

public class Messenger {

	private File file = new File(SupportChat.getInstance().getDataFolder(), "messages.yml");
	private FileConfiguration config = new YamlConfiguration();
	private boolean useDisplayName;

	public Messenger() {
		reload();
	}

	public void sendMessage(Player sender, String pathTo) {
		sender.sendMessage(getMessage("Prefix") + getMessage(pathTo));
	}

	public void sendMessage(Player player, String pathTo, Player target) {
		if (useDisplayName)
			player.sendMessage(getMessage("Prefix") + getMessage(pathTo).replace("%player%", target.getDisplayName()));
		else
			player.sendMessage(getMessage("Prefix") + getMessage(pathTo).replace("%player%", target.getName()));
	}

	public String getMessage(String pathTo) {
		String message = config.getString(pathTo);
		return ChatColor.translateAlternateColorCodes('&', message);
	}

	public boolean useDisplayName() {
		return useDisplayName;
	}

	public void reload() {
		if (!file.exists())
			SupportChat.getInstance().saveResource("messages.yml", false);
		try {
			config.load(file);
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}
}
