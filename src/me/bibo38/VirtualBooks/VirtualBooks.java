package me.bibo38.VirtualBooks;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.logging.Logger;

import me.bibo38.Bibo38Lib.Permissions;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;


public class VirtualBooks extends JavaPlugin
{
	private Logger log;
	private PluginDescriptionFile pdFile;
	private ChatListener myChatListener;
	
	private File bookDir;
	private HashMap<String, Book> books;
	
	private Permissions perm;
	
	public void onEnable()
	{
		log = this.getLogger();
		pdFile = this.getDescription();
		bookDir = new File(this.getDataFolder(), "books");
		
		perm = new Permissions("vbooks");
		
		if(!bookDir.exists())
		{
			bookDir.mkdir();
		}
		
		books = new HashMap<String, Book>();
		books.clear();
		
		myChatListener = new ChatListener(this);
		
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
	
	public void onDisable()
	{
		log.info("VirtualBooks Version " + pdFile.getVersion() + " wurde deaktiviert!");
	}
	
	public boolean onCommand(CommandSender cs, Command cmd, String commandLabel, String[] args)
	{
		if(!(cs instanceof Player))
		{
			// Kein Spieler
			return true;
		}
		
		String msg = ""; // Rückmeldung
		boolean success = false; // War es erfolgreich?
		
		if(cmd.getName().equalsIgnoreCase("vbooks"))
		{
			if(args.length >= 2)
			{
				if(args[0].equals("create") && perm.hasPerm((Player) cs, "create"))
				{
					if(books.containsKey(args[1]))
					{
						msg = "Book already exists!";
					} else
					{	
						// Buch erstellen
						try
						{
							Book tmp = new Book(args[1], this);
							books.put(args[1], tmp);
							
							msg = "Book successful created!";
							success = true;
						} catch (IOException e) {
							msg = "Error creating book! Cannot create File!";
						}
					}
				}
				
				if(args[0].equals("read") && perm.hasPerm((Player) cs, "read"))
				{
					if(books.containsKey(args[1]))
					{
						try
						{
							int x = 1;
							for(String i : books.get(args[1]).read().split("\n"))
							{
								cs.sendMessage(x + ": " + i); // Für die einzelnen Zeilen
								x++;
							}
							// cs.sendMessage(books.get(args[1]).read());
						} catch (IOException e)
						{
							msg = "Error reading the book";
						}
					} else
					{
						msg = "Book doesn't exist!";
					}
				}
				
				if(args[0].equals("write") && perm.hasPerm((Player) cs, "write"))
				{
					if(books.containsKey(args[1]))
					{
						if(myChatListener.getSchreiber() == null)
						{
							try
							{
								myChatListener.setSchreiber((Player) cs,
										books.get(args[1]));
								success = true;
								msg = "You can now write!";
							} catch (IOException e)
							{
								msg = "Error reading book!";
							}
						} else
						{
							msg = "Annother Player is already writing a book";
						}
					} else
					{
						msg = "Book doesn't exist!";
					}
				}
				
				if(args[0].equals("writeend") && perm.hasPerm((Player) cs, "write"))
				{
					if(books.containsKey(args[1]))
					{
						if(myChatListener.getSchreiber().equals((Player) cs))
						{
							try
							{
								myChatListener.removeSchreiber();
								success = true;
								msg = "You can now chat with othetrs!";
							} catch (IOException e)
							{
								msg = "Error writing book!";
							}
						} else
						{
							msg = "You aren't writing a book";
						}
					} else
					{
						msg = "Book doesn't exist!";
					}
				}
				
				if(args[0].equals("remove") && perm.hasPerm((Player) cs, "remove"))
				{
					if(args.length >= 3)
					{
						try
						{
							// Ok, Zeile gesetzt
							// Lese alles ein und lösche diese Zeile
							String erg = "";
							int x = 1;
							int hin = Integer.parseInt(args[2]);
							for(String i : books.get(args[1]).read().split("\n"))
							{
								if(x != hin) // Falls es nicht die Zeile ist, setze sie hin
								{
									erg += i + "\n";
								}
								x++;
							}
							
							books.get(args[1]).setText(erg);
							books.get(args[1]).write();
							
							success = true;
							msg = "Removed line successfully!";
						} catch(IOException e)
						{
							msg = "Error reading/writing book";
						}
					} else
					{
						msg = "You must specify a line number!";
					}
				}
			} else
			{
				msg = "You must set a book name!";
			}
		}
		
		if(success)
		{
			cs.sendMessage(ChatColor.GREEN + msg);
		} else
		{
			cs.sendMessage(ChatColor.RED + msg);
		}
		
		return true;
	}
}
