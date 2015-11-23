package de.lurch.sc.command;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import de.lurch.sc.plugin.SupportChat;

public class CommandSupport implements CommandExecutor {

		@Override
		public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
			if (sender.hasPermission("support.admin") && sender instanceof Player) {
				Player player = (Player) sender;
				SupportChat.getInstance().getWaitingPlayers().remove(player);
				if (args.length == 0) {
					sendAdminHelpPage(sender);
				} else {
					if (args[0].equalsIgnoreCase("help")) {
							sendAdminHelpPage(sender);
					} else if (args[0].equalsIgnoreCase("open")) {
							if (args.length == 1) {
								sendAdminHelpPage(sender);
							} else {
								if (args[1].equalsIgnoreCase("latest")) {
									if (SupportChat.getInstance().getWaitingPlayers().size() < 1) {
										SupportChat.getMessenger().sendMessage(player, "NoSupportNeeded");
									} else {
										Player target = SupportChat.getInstance().getWaitingPlayers().get(SupportChat.getInstance().getWaitingPlayers().size() - 1);
										SupportChat.getInstance().openSupportChat(player, target);
									}

								} else {
									Player target = Bukkit.getPlayer(args[1]);
									if (target != null) {
										if (target.equals(player)) {
												SupportChat.getMessenger().sendMessage(player, "YourSelfSupportChat");
										} else
												SupportChat.getInstance().openSupportChat(player, target);
									} else {
										SupportChat.getMessenger().sendMessage(player, "PlayerNotOnline", target);
									}
								}
							}

					} else if (args[0].equalsIgnoreCase("close")) {
							SupportChat.getInstance().closeSupportChat(player);
					} else if (args[0].equalsIgnoreCase("list")) {
							String prefix = SupportChat.getMessenger().getMessage("Prefix");
							int players = SupportChat.getInstance().getWaitingPlayers().size();
							player.sendMessage(prefix + SupportChat.getMessenger().getMessage("UsersInQueue").replace("%players%", "" + players));
							for (Player p : SupportChat.getInstance().getWaitingPlayers()) {
								player.sendMessage("§a" + p.getName());
							}

					} else {
							sendAdminHelpPage(sender);
					}
				}

			} else if (sender.hasPermission("support.user")) {
				if (sender instanceof Player) {
					Player player = (Player) sender;
					if (SupportChat.getInstance().getWaitingPlayers().contains(player)) {
							SupportChat.getInstance().getWaitingPlayers().remove(player);
							SupportChat.getMessenger().sendMessage(player, "CommandSupportUserLeave");
							for (Player admin : Bukkit.getOnlinePlayers()) {
								if (admin.hasPermission("support.admin")) {
									SupportChat.getMessenger().sendMessage(admin, "WarnAdminUserLeave", player);
								}
							}
					} else {
							if (!isAdminOnline())
								SupportChat.getMessenger().sendMessage(player, "NoAdminOnline");
							else {
								SupportChat.getInstance().getWaitingPlayers().add(player);
								SupportChat.getMessenger().sendMessage(player, "CommandSupportUserJoin");
								for (Player admin : Bukkit.getOnlinePlayers()) {
									if (admin.hasPermission("support.admin")) {
										SupportChat.getMessenger().sendMessage(admin, "WarnAdminUserJoin", player);
									}
								}
							}
					}
				} else {
					// Ignore
				}
			} else {
				if (sender instanceof Player) {
					SupportChat.getMessenger().sendMessage((Player) sender, "NoPerms");
				}
			}
			return true;
		}

		private static void sendAdminHelpPage(CommandSender sender) {
			sender.sendMessage("§4Admin Help Page");
			sender.sendMessage("§c/sc help §7Shows this Help Page");
			sender.sendMessage("§c/sc open latest §7Starts a SupportChat with the Player who queued as last");
			sender.sendMessage("§c/sc open player §7Starts a SupportChat with the given Player");
			sender.sendMessage("§c/sc close §7Closes your current SupportChat");
			sender.sendMessage("§c/sc list §7Lists all Players who are in the help queue");
		}

		private static boolean isAdminOnline() {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.hasPermission("support.admin"))
					return true;
			}
			return false;
		}

}
