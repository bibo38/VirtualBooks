package me.bibo38.VirtualBooks;

import java.io.File;
import java.util.HashMap;
import java.util.logging.Logger;

import me.bibo38.Bibo38Lib.Permissions;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;


public class VirtualBooks extends JavaPlugin
{
	private Logger log;
	private PluginDescriptionFile pdFile;
	private CommandListener myCommandListener;
	
	private File bookDir;
	private HashMap<String, Book> books;
	
	public static Permissions perm;
	
	@Override
	public void onEnable()
	{
		log = this.getLogger();
		pdFile = this.getDescription();
		bookDir = new File(this.getDataFolder(), "books");
		
		CommandListener.main = this;
		
		
		perm = new Permissions("vbooks");
		
		if(!bookDir.exists())
		{
			bookDir.mkdir();
		}
		
		books = new HashMap<String, Book>();
		books.clear();
		
		myCommandListener = new CommandListener();
		
		// Befüllen mit Büchern
		/* String dateien[] = bookDir.list();
		
		for(String aktbook : dateien)
		{
			try
			{
				Book tmp = new Book(aktbook.substring(0, aktbook.length() - 4), this); // das .txt abziehen
				books.put(aktbook.substring(0, aktbook.length() - 4), tmp);
			} catch (IOException e)
			{
				log.info(ChatColor.RED + "Fehler beim Einlesen des Buches " + aktbook);
				e.printStackTrace();
			}
		} */
		
		log.info("VirtualBooks Version " + pdFile.getVersion() + " wurde aktiviert!");
	}
	
	@Override
	public void onDisable()
	{
		log.info("VirtualBooks Version " + pdFile.getVersion() + " wurde deaktiviert!");
	}
	
	@Override
	public boolean onCommand(CommandSender cs, Command cmd, String commandLabel, String[] args)
	{
		return myCommandListener.onCommand(cs, cmd, args);
	}
}
