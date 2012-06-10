package me.bibo38.VirtualBooks;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;

import org.bukkit.plugin.java.JavaPlugin;

public class Book
{
	private static File bookDir = null;
	private static JavaPlugin main;
	
	private File bookFile;
	private String text;
	private String name;
	
	private RandomAccessFile file;
	
	public Book(String ename, JavaPlugin emain) throws IOException
	{
		main = emain;
		name = ename;
		
		if(bookDir == null)
		{
			bookDir = new File(main.getDataFolder(), "books");
			if(!bookDir.exists())
			{
				if(!bookDir.mkdirs()) // Ordner erstellen
				{
					throw new IOException("Failed to create necessary directorys!");
				}
			}
		}
		
		// Buch erstellen oder benutzen
		bookFile = new File(bookDir, name + ".txt");
		if(!bookFile.exists())
		{
			// Buch erstellen
			text = "";
			bookFile.createNewFile();
			file = new RandomAccessFile(bookFile, "rw");
			this.write(); // Datei schreiben
		} else
		{
			// Buch einlesen
			text = this.read(); // Einlesen
		}
	}
	
	protected void write() throws IOException
	{	
		if(file != null)
		{
			file.close();
		}
		file = new RandomAccessFile(bookFile, "rw");
		file.seek(0); // An den Anfang der datei gehen
		file.setLength(text.length());
		
		for(int i : text.toCharArray())
		{
			file.write(i); // Das Byte schreiben
		}
		file.close();
	}
	
	protected String read() throws IOException
	{
		String erg = "";
		
		if(file != null)
		{
			file.close();
		}
		file = new RandomAccessFile(bookFile, "rw");
		file.seek(0);
		
		while(file.getFilePointer() < file.length())
		{
			erg += file.readLine() + "\n"; // Einlesen Zeile für Zeile
		}
		
		text = erg;
		file.close();
		return erg;
	}
	
	public String getText()
	{
		return text;
	}
	
	protected void setText(String etext)
	{
		text = etext;
	}
}
