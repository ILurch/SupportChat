package de.lurch.sc.handler;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerChatTabCompleteEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import de.lurch.sc.plugin.SupportChat;

public class PlayerListener implements Listener {

	public PlayerListener() {
		Bukkit.getPluginManager().registerEvents(this, SupportChat.getInstance());
	}

	@EventHandler
	public void onEnable() {

	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public void onPlayerQuit(PlayerQuitEvent event) {
		SupportChat.getInstance().closeSupportChat(event.getPlayer());

		if (SupportChat.getInstance().getWaitingPlayers().contains(event.getPlayer())) {
			SupportChat.getInstance().getWaitingPlayers().remove(event.getPlayer());
			for (Player admin : Bukkit.getOnlinePlayers()) {
				if (admin.hasPermission("supportchat.admin")) {
					SupportChat.getMessenger().sendMessage(admin, "WarnAdminUserLeave", event.getPlayer());
				}
			}
		}
	}

	@EventHandler
	public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
		if (SupportChat.getInstance().isInSupportChat(event.getPlayer())) {
			event.setCancelled(true);
			String format = SupportChat.getMessenger().getMessage("SupportChat.Format").replace("%message%", event.getMessage());
			if (SupportChat.getMessenger().useDisplayName()) {
				event.getPlayer().sendMessage(format.replace("%player%", event.getPlayer().getDisplayName()));
				SupportChat.getInstance().getSupportChatPartner(event.getPlayer()).sendMessage(format.replace("%player%", event.getPlayer().getDisplayName()));
			} else {
				event.getPlayer().sendMessage(format.replace("%player%", event.getPlayer().getName()));
				SupportChat.getInstance().getSupportChatPartner(event.getPlayer()).sendMessage(format.replace("%player%", event.getPlayer().getDisplayName()));
			}
		}
	}

	@EventHandler
	public void onTabCompleter(PlayerChatTabCompleteEvent event) {
		event.getTabCompletions().clear();
		event.getTabCompletions().add("Du hast hier nichts zu suchen.");
	}
}
