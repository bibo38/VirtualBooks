package me.bibo38.VirtualBooks;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class CommandListener
{
	protected static VirtualBooks main;
	
	private String msg = "";
	private HashMap<String, Book> books;
	private File bookDir;
	
	public CommandListener()
	{
		books = new HashMap<String, Book>();
		books.clear();
		
		// Befüllen mit vorhandenen Büchern
		bookDir = new File(main.getDataFolder(), "books");
		reload();
	}
	
	private boolean reload()
	{
		books.clear();
		String dateien[] = bookDir.list();
		
		for(String aktbook : dateien)
		{
			try
			{
				Book tmp = new Book(aktbook.substring(0, aktbook.length() - 4), main); // das .txt abziehen
				books.put(aktbook.substring(0, aktbook.length() - 4), tmp);
			} catch (IOException e)
			{
				msg = "Error reading book " + aktbook;
				e.printStackTrace();
				return false;
			}
		}
		
		return true;
	}
	
	private boolean list(CommandSender cs)
	{
		Iterator<String> it = books.keySet().iterator();
		while(it.hasNext())
		{
			cs.sendMessage(it.next());
		}
		
		return true;
	}
	
	private boolean delete(String name)
	{
		if(!books.containsKey(name))
		{
			msg = "Book doesn't exist!"; // Existiert nicht
			return false;
		}
		
		books.remove(name);
		File tmp = new File(bookDir, name + ".txt");
		if(!tmp.delete())
		{
			msg = "Error deleting file!";
			return false;
		} else
		{
			msg = "Successful deleted!";
			return true;
		}
	}
	
	private boolean isWritingBook(Player player)
	{
		// Schreibt player schon ein Buch?
		
		Iterator<String> it = books.keySet().iterator();
		while(it.hasNext())
		{
			Player akt = books.get(it.next()).getWriter();
			if(akt != null && akt.equals(player))
			{
				return true; // Spieler gefunden :-)
			}
		}
		
		return false;
	}
	
	private boolean create(String name)
	{
		if(books.containsKey(name))
		{
			msg = "Book already exists!";
			return false;
		}
		
		// Buch erstellen
		try
		{
			Book tmp = new Book(name, main);
			books.put(name, tmp);
			
			msg = "Book successful created!";
		} catch (IOException e) {
			msg = "Error creating book! Cannot create File!";
			return false;
		}
		
		return true;
	}
	
	private boolean read(CommandSender cs, String name)
	{
		if(!books.containsKey(name))
		{
			msg = "Book doesn't exist!"; // Existiert nicht
			return false;
		}
		
		try
		{
			int x = 1;
			for(String i : books.get(name).read().split("\n"))
			{
				cs.sendMessage(x + ": " + i); // Für die einzelnen Zeilen
				x++;
			}
		} catch (IOException e)
		{
			msg = "Error reading the book";
			return false;
		}
		
		return true;
	}
	
	private boolean write(Player player, String name)
	{
		if(!books.containsKey(name))
		{
			msg = "Book doesn't exist!";
			return false;
		}
		
		if(player == null)
		{
			msg = "You must be a player to write a book!";
			return false;
		}
		
		if(this.isWritingBook(player))
		{
			// Schon am Buch screiben, dann nichts wie weg!
			msg = "You cannot write two books simultaneously";
			return false;
		}
		
		try
		{
			if(books.get(name).setWriter(player))
			{
				msg = "You can now write!";
			} else
			{
				// Fehler in der Ausführung
				msg = "Annother User is writing the book already!";
				return false;
			}
		} catch (IOException e)
		{
			msg = "Error writing the book!";
			return false;
		} catch (NullPointerException e)
		{
			e.printStackTrace();
		}
		
		return true;
	}
	
	private boolean writeend(Player player)
	{
		if(player == null)
		{
			msg = "You must be a player to stop writing a book!";
			return false;
		}
		
		// Buch aussuchen und nehmen
		Iterator<String> it = books.keySet().iterator();
		while(it.hasNext())
		{
			Book akt = books.get(it.next());
			if(akt.getWriter() != null && akt.getWriter().equals(player))
			{
				// Gefunden
				try
				{
					akt.removeWriter();
				} catch (IOException e)
				{
					msg = "Error stop writing the book!";
					return false;
				}
				
				msg = "You can now chat with others!";
				return true; // Ende
			}
		}
		
		msg = "You aren't writing a book at this time!"; // Keinen gefunden
		return false;
	}
	
	private boolean remove(Book book, int line)
	{
		try
		{
			// Ok, Zeile gesetzt
			// Lese alles ein und lösche diese Zeile
			String erg = "";
			int x = 1;
			for(String i : book.read().split("\n"))
			{
				if(x != line) // Falls es nicht die Zeile ist, setze sie hin
				{
					erg += i + "\n";
				}
				x++;
			}
			
			book.setText(erg);
			book.write();
			
			msg = "Removed line successfully!";
		} catch(IOException e)
		{
			msg = "Error reading/writing book";
			return false;
		}
		
		return true;
	}
	
	public boolean onCommand(CommandSender cs, Command cmd, String[] args)
	{
		Player player;
		
		if(cs instanceof Player)
		{
			player = (Player) cs;
		} else
		{
			player = null;
		}
		
		boolean success = false; // War es erfolgreich?
			
		if(cmd.getName().equalsIgnoreCase("vbooks") && args.length >= 1)
		{
			if(args.length >= 2 && books.containsKey(args[1]))
			{
				if(args[0].equals("read") && VirtualBooks.perm.hasPerm((Player) cs, "read"))
				{
					success = this.read(cs, args[1]);
				}
				
				if(args[0].equals("delete") && VirtualBooks.perm.hasPerm((Player) cs, "delete"))
				{
					success = this.delete(args[1]);
				}
				
				if(args[0].equals("write") && VirtualBooks.perm.hasPerm((Player) cs, "write"))
				{
					success = this.write(player, args[1]);
				}
				
				if(args[0].equals("remove") && VirtualBooks.perm.hasPerm((Player) cs, "remove") && args.length == 3)
				{
					success = this.remove(books.get(args[1]), Integer.parseInt(args[2]));
				}
			} else
			{
				if(args.length == 2 && args[0].equals("create") && VirtualBooks.perm.hasPerm((Player) cs, "create"))
				{
					success = this.create(args[1]);
				} else
				{
					// msg = "Book doesn't exists!"; // Sicherheitslücke bei z.b. /vbooks read testbuch
				}
			}
			
			if(args[0].equals("writeend") && VirtualBooks.perm.hasPerm((Player) cs, "write"))
			{
				success = this.writeend(player);
			}
			
			if(args[0].equals("reload") && VirtualBooks.perm.hasPerm((Player) cs, "reload"))
			{
				success = this.reload();
			}
			
			if(args[0].equals("list") && VirtualBooks.perm.hasPerm((Player) cs, "list"))
			{
				success = this.list(cs);
			}
		}
		
		if(success)
		{
			cs.sendMessage(ChatColor.GREEN + msg);
		} else
		{
			cs.sendMessage(ChatColor.RED + msg);
		}
		msg = "";
		
		return true;
	}
}
