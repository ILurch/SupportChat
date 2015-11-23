package de.lurch.sc.plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import de.lurch.sc.command.CommandSupport;
import de.lurch.sc.handler.PlayerListener;

/* Permissions:
 *  support.user
 *  support.admin
 */
public class SupportChat extends JavaPlugin {

	private static SupportChat instance;
	private static Messenger messenger;

	private List<Player> waitingPlayers = new ArrayList<Player>();
	private Map<Player, Player> inSupportChat = new HashMap<Player, Player>();

	@Override
	public void onEnable() {
		instance = this;
		messenger = new Messenger();
		new PlayerListener();
		getCommand("support").setExecutor(new CommandSupport());
	}

	public static SupportChat getInstance() {
		return instance;
	}

	public List<Player> getWaitingPlayers() {
		return waitingPlayers;
	}

	public static Messenger getMessenger() {
		return messenger;
	}

	public Map<Player, Player> getInSupportChat() {
		return inSupportChat;
	}

	public boolean isInSupportChat(Player player) {
		for (Player p : inSupportChat.keySet()) {
			if (p.equals(player))
				return true;
		}
		for (Player p : inSupportChat.values()) {
			if (p.equals(player))
				return true;
		}
		return false;
	}

	public void reload() {
		Bukkit.getPluginManager().disablePlugin(this);
		Bukkit.getPluginManager().enablePlugin(this);
	}

	public Player getSupportChatPartner(Player player) {
		if (inSupportChat.containsKey(player)) {
			return inSupportChat.get(player);
		} else {
			for (Player p : inSupportChat.keySet()) {
				if (inSupportChat.get(p).equals(player))
					return p;
			}
		}
		return null;
	}

	public void closeSupportChat(Player player) {
		if (isInSupportChat(player)) {
			if (inSupportChat.containsKey(player)) {
				getMessenger().sendMessage(inSupportChat.get(player), "SupportChatDeactivated");
				getMessenger().sendMessage(player, "SupportChatDeactivated", inSupportChat.get(player));
				inSupportChat.remove(player);
			} else {
				for (Player p : inSupportChat.keySet()) {
					if (inSupportChat.get(p).equals(player)) {
						inSupportChat.remove(p);
						getMessenger().sendMessage(p, "SupportChatDeactivated");
						getMessenger().sendMessage(player, "SupportChatDeactivated", p);
					}
				}
			}
		}
	}

	public void openSupportChat(Player admin, Player user) {
		if (isInSupportChat(admin)) {
			getMessenger().sendMessage(admin, "IsInSupportChatAdmin");
		} else if (isInSupportChat(user)) {
			getMessenger().sendMessage(admin, "IsInSupportChatUser");
		} else {
			waitingPlayers.remove(user);
			SupportChat.getInstance().getInSupportChat().put(admin, user);

			getMessenger().sendMessage(admin, "SupportChatActivated", user);
			getMessenger().sendMessage(user, "SupportChatActivated", admin);
		}
	}
}