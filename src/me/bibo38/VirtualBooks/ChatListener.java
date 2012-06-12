package me.bibo38.VirtualBooks;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class ChatListener implements Listener
{
	private JavaPlugin main;
	private Player schreiber = null;
	private Book aktbook = null;
	
	public ChatListener(JavaPlugin emain)
	{
		main = emain;
		
		main.getServer().getPluginManager().registerEvents(this, main); // Gleich registrieren
	}
	
	protected void setSchreiber(Player player, Book book) throws IOException
	{
		schreiber = player;
		aktbook = book;
		aktbook.read();
	}
	
	protected void removeSchreiber() throws IOException
	{
		if(aktbook  != null)
		{
			aktbook.write();
			aktbook = null;
			schreiber = null;
		}
	}
	
	public Player getSchreiber()
	{
		return schreiber;
	}
	
	@EventHandler
	public void onPlayerChat(PlayerChatEvent evt)
	{
		if(schreiber != null && aktbook != null && schreiber.equals(evt.getPlayer()))
		{
			// Es ist der Schreiber, alle Nachrichten werden nun aus dem Chat genommen
			String aktText = aktbook.getText();
			aktText += evt.getMessage() + "\n";
			aktbook.setText(aktText);
			
			schreiber.sendMessage(ChatColor.GREEN + "Added Line: " + evt.getMessage());
			
			evt.setCancelled(true);
		}
	}
}
