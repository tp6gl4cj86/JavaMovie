package com.olis;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class Movie
{ 

	public int     id;
	public String  moviedate;
	public String  moviename;
	public String  moviepath;
	public String  trailerpath;
	public boolean father;
	public boolean mother;
	public boolean osister;
	public boolean jave;
	public boolean ysister;

	public Movie(int id, String moviedate, String moviename, String moviepath, String trailerpath, boolean father, boolean mother, boolean osister, boolean java, boolean ysister)
	{
		this.id = id;
		this.moviedate = moviedate;
		this.moviename = moviename;
		this.moviepath = moviepath;
		this.trailerpath = trailerpath;
		this.father = father;
		this.mother = mother;
		this.osister = osister;
		this.jave = java;
		this.ysister = ysister;
	}

	public String [] getContent()
	{
		return new String [] {moviedate, getTrailername(), moviename, getIsLooked(father), getIsLooked(mother), getIsLooked(osister), getIsLooked(jave), getIsLooked(ysister)};
	}
	
	private String getTrailername()
	{
		return trailerpath.equals("") ? "" : "¼½©ñ";
	}
	
	private String getIsLooked(boolean islook)
	{
		return islook ? "¢Ý" : "";
	}
	
	
	
	/*
	####  ##    ##   ####  ########   ########   ########   
	 ##   ###   ##    ##      ##      ##     ##  ##     ##  
	 ##   ####  ##    ##      ##      ##     ##  ##     ##  
	 ##   ## ## ##    ##      ##      ##     ##  ########   
	 ##   ##  ####    ##      ##      ##     ##  ##     ##  
	 ##   ##   ###    ##      ##      ##     ##  ##     ##  
	####  ##    ##   ####     ##      ########   ########   
	*/
	public static void createTable()
	{
		try
        {
			Class.forName("org.sqlite.JDBC");
			Connection connect = DriverManager.getConnection("jdbc:sqlite:JavaMovie.db");
			
			Statement statement = connect.createStatement();
			
			try
		    {
		    	String sql = 
		    	    	"CREATE TABLE MOVIE "
		    	    		+ "(ID INTEGER PRIMARY KEY AUTOINCREMENT, "
		    	    		+ "MOVIEDATE TEXT NOT NULL, "
		    	    		+ "MOVIENAME TEXT NOT NULL, "
		    	    		+ "MOVIEPATH TEXT NOT NULL, "
		    	    		+ "TRAILERPATH TEXT NOT NULL, "
		    	    		+ "FATHER BOOLEAN NOT NULL, "
		    	    		+ "MOTHER BOOLEAN NOT NULL, "
		    	    		+ "OSISTER BOOLEAN NOT NULL, "
		    	    		+ "JAVA BOOLEAN NOT NULL, "
		    	    		+ "YSISTER BOOLEAN NOT NULL, "
		    	    		+ "ENABLE BOOLEAN NOT NULL)";
		    	
		    	statement.executeUpdate(sql);
		    }
		    catch (Exception e)
		    {
		    	e.printStackTrace();
		    }
			
			try
	        {
		    	String sql = "CREATE TABLE AUTODIRECTORY (ID INTEGER PRIMARY KEY AUTOINCREMENT, AUTODIRECTORY TEXT NOT NULL)";
		    	
		    	statement.executeUpdate(sql);
	        }
	        catch (Exception e)
	        {
	        	e.printStackTrace();
	        }
			
			statement.close();
			connect.close();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
	}
	
	
	
	/*
	   ###     ##     ##  ########    #######   
	  ## ##    ##     ##     ##      ##     ##  
	 ##   ##   ##     ##     ##      ##     ##  
	##     ##  ##     ##     ##      ##     ##  
	#########  ##     ##     ##      ##     ##  
	##     ##  ##     ##     ##      ##     ##  
	##     ##   #######      ##       #######   
	*/
	public static String getAutoDirectoryPath()
	{
		String AutoDirectoryPath = "";
		try
        {
			Class.forName("org.sqlite.JDBC");
			Connection connect = DriverManager.getConnection("jdbc:sqlite:JavaMovie.db");
			
			Statement statement = connect.createStatement();
			
			ResultSet rs = statement.executeQuery("SELECT * FROM AUTODIRECTORY;");
			if (rs.next())
			{
				AutoDirectoryPath = rs.getString("autodirectory");
			}
			rs.close();
			
			statement.close();
			connect.close();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
		
		return AutoDirectoryPath;
	}
	
	public static void updateAutoDirectoryPath(String path)
	{
		try
        {
			Class.forName("org.sqlite.JDBC");
			Connection connect = DriverManager.getConnection("jdbc:sqlite:JavaMovie.db");
			
			Statement statement = connect.createStatement();
			
			String sql = "DELETE from AUTODIRECTORY;";
			statement.executeUpdate(sql);
			
			sql = "INSERT INTO AUTODIRECTORY (AUTODIRECTORY) VALUES ('" + path + "');";
			statement.executeUpdate(sql);
			
			statement.close();
			connect.close();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
	}
	
	public static void runAutoDirectory()
	{
		try
        {
			Class.forName("org.sqlite.JDBC");
			Connection connect = DriverManager.getConnection("jdbc:sqlite:JavaMovie.db");

			Statement statement = connect.createStatement();
			
			ResultSet rs = null;
			
			File movieDirectory = new File(new File("Movie").getAbsolutePath());
			if (movieDirectory.exists() && movieDirectory.isDirectory())
			{
				File[] AutoDirectoryFiles = movieDirectory.listFiles();
				for(File AutoDirectoryFile : AutoDirectoryFiles)
				{
					if (AutoDirectoryFile.isDirectory() && AutoDirectoryFile.getName().length() >= 8)
					{
						String filename = AutoDirectoryFile.getName().substring(8);
						rs = statement.executeQuery("SELECT * FROM MOVIE WHERE MOVIENAME = '" + filename + "'");
						if (rs.next())
						{
							Movie.enableMovie(statement, rs.getInt("id"));
						}
						else
						{
							Movie.chooseDirectory(AutoDirectoryFile, true);
						}
					}
				}
			}
			
			rs.close();
			statement.close();
			connect.close();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
		
		LoadingDialog mLoadingDialog = new LoadingDialog();
		mLoadingDialog.setVisible(true);
		
		try
        {
			Class.forName("org.sqlite.JDBC");
			Connection connect = DriverManager.getConnection("jdbc:sqlite:JavaMovie.db");

			Statement statement = connect.createStatement();
			String AutoDirectoryPath = null;
			
			ResultSet rs = statement.executeQuery("SELECT * FROM AUTODIRECTORY;");
			if (rs.next())
			{
				AutoDirectoryPath = rs.getString("autodirectory");
			}
			
			if (AutoDirectoryPath != null)
			{
				File AutoDirectory = new File(AutoDirectoryPath);
				File[] AutoDirectoryFiles = AutoDirectory.listFiles();
				for(File AutoDirectoryFile : AutoDirectoryFiles)
				{
					if (AutoDirectoryFile.isDirectory() && AutoDirectoryFile.getName().length() >= 8)
					{
						String filename = AutoDirectoryFile.getName().substring(8);
						rs = statement.executeQuery("SELECT * FROM MOVIE WHERE MOVIENAME = '" + filename + "'");
						if (rs.next())
						{
							Movie.enableMovie(statement, rs.getInt("id"));
						}
						else
						{
							Movie.chooseDirectory(AutoDirectoryFile, true);
						}
					}
				}
			}
			
			rs.close();
			statement.close();
			connect.close();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
		finally
		{
//			mLoadingDialog.setVisible(false);
//			mLoadingDialog.dispose();
		}
	}
	
	public static String [] chooseDirectory(File AutoDirectory, boolean isAutoAdd)
	{
		String moviedate = "";
		String moviename = "";
		String moviepath = "";
		String trailerpath = "";
		if(AutoDirectory.isDirectory() && AutoDirectory.getName().length() >= 8)
		{			
			moviedate = AutoDirectory.getName().substring(0, 8);
			moviename = AutoDirectory.getName().substring(8);
			
			File[] filelist = AutoDirectory.listFiles();
			for(File file : filelist)
			{
				String filename = file.getName();
				if(filename.indexOf(".") > 0 && !filename.substring(filename.indexOf(".")+1).equals("srt") && !filename.substring(filename.indexOf(".")+1).equals("ass"))
				{					
					if(filename.contains(moviename))
					{
						moviepath = file.getAbsolutePath();
						break;
					}
				}
			}
			
			for(File file : filelist)
			{
				String filename = file.getName();
				if(filename.contains("UB"))
				{
					trailerpath = file.getAbsolutePath();
					break;
				}
			}
			
			if (!moviepath.equals("") && isAutoAdd)
			{
				Movie.addMovie(moviedate, moviename, moviepath, trailerpath);
			}
		}
		
		return new String [] {moviedate, moviename, moviepath, trailerpath};
	}
	
	
	
	/*
	##     ##   #######   ##     ##  ####  ########   
	###   ###  ##     ##  ##     ##   ##   ##         
	#### ####  ##     ##  ##     ##   ##   ##         
	## ### ##  ##     ##  ##     ##   ##   ######     
	##     ##  ##     ##   ##   ##    ##   ##         
	##     ##  ##     ##    ## ##     ##   ##         
	##     ##   #######      ###     ####  ########   
	*/
	public static int addMovie(String moviedate, String moviename, String moviepath, String trailerpath)
	{
		int id = -1;
		
		try
        {
			Class.forName("org.sqlite.JDBC");
			Connection connect = DriverManager.getConnection("jdbc:sqlite:JavaMovie.db");
			
			Statement statement = connect.createStatement();
			
			String sql = "INSERT INTO MOVIE "
					+ "(MOVIEDATE,MOVIENAME,MOVIEPATH,TRAILERPATH,FATHER,MOTHER,OSISTER,JAVA,YSISTER,ENABLE) VALUES "
					+ "('" + moviedate + "', " + 
					   "'" + moviename + "', " +
					   "'" + moviepath + "', " +
					   "'" + trailerpath + "', " + 
					"'0', '0', '0', '0', '0', '1' );";
			
			id = statement.executeUpdate(sql);
			
			ResultSet rs = statement.getGeneratedKeys();
			if (rs.next())
			{
				id = rs.getInt(1);
			}
			
			statement.close();
			connect.close();
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
		
		return id;
	}
	
	public static void updateMovie(Movie movie)
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
			Connection connect = DriverManager.getConnection("jdbc:sqlite:JavaMovie.db");
			
			Statement statement = connect.createStatement();
			
			String sql = "UPDATE MOVIE SET " + 
					"MOVIEDATE = '" + movie.moviedate + "', " + 
					"MOVIENAME = '" + movie.moviename + "', " + 
					"MOVIEPATH = '" + movie.moviepath + "', " + 
					"TRAILERPATH = '" + movie.trailerpath + "' " + 
					"WHERE ID = " + movie.id;
			
			statement.executeUpdate(sql);
			
			statement.close();
			connect.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void updateMovieLooked(Movie movie)
	{
		try
		{
			Class.forName("org.sqlite.JDBC");
			Connection connect = DriverManager.getConnection("jdbc:sqlite:JavaMovie.db");
			
			Statement statement = connect.createStatement();
			
			String sql = "UPDATE MOVIE SET " + 
					"FATHER = "   + (movie.father 	? "'1'" : "'0'") + ", " + 
					"MOTHER = "   + (movie.mother 	? "'1'" : "'0'") + ", " + 
					"OSISTER = "  + (movie.osister 	? "'1'" : "'0'") + ", " + 
					"JAVA = " 	  + (movie.jave 	? "'1'" : "'0'") + ", " + 
					"YSISTER = "  + (movie.ysister  ? "'1'" : "'0'") + " " + 
					"WHERE ID = " + movie.id;
			
			statement.executeUpdate(sql);
			
			statement.close();
			connect.close();
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	public static void enableMovie(Statement statement, int id) throws SQLException
	{
		String sql = "UPDATE MOVIE SET ENABLE = '1' WHERE ID = " + id;
		statement.executeUpdate(sql);
	}
	
	public static void deleteMovie(int id)
	{
		try
        {
			try
			{
				Class.forName("org.sqlite.JDBC");
				Connection connect = DriverManager.getConnection("jdbc:sqlite:JavaMovie.db");
				
				Statement statement = connect.createStatement();
				
				String sql = "UPDATE MOVIE SET ENABLE = '0' WHERE ID = " + id;
				
				statement.executeUpdate(sql);
				
				statement.close();
				connect.close();
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
        }
        catch (Exception e)
        {
        	e.printStackTrace();
        }
	}
	
}
