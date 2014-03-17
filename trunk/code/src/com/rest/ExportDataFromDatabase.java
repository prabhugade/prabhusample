package com.rest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class ExportDataFromDatabase {

	String DBName=null;	
	Connection con=null;
	Statement st=null;
	Statement st1=null;
	Statement st2=null;
	public static void main(String[] args) 
	{
		ExportDataFromDatabase defaultreq=new ExportDataFromDatabase();
		defaultreq.exporttofile("hydhouse");
	}
	private void exporttofile(String dbname) 
	{
		//not working
		Connection Conn=null;
		try
		{
				
				connect(dbname);
				Statement stmt = con.createStatement();

			    // Export the data
			    String filename = "/home/prabhu/project/test.sql";
			    String tablename = "Orders";
			    stmt.executeQuery("SELECT * INTO OUTFILE \"" + filename + "\" FROM " + tablename);
				System.out.println("successfully exported");
		}
		catch(Exception e)
		{
			try
			{
			//con.rollback();
			}catch(Exception e1)
			{
				System.out.println(e1.getMessage());
			}
			System.out.println(e);
		}
	}
	public String connect(String dbname)
	{
		//String res=null;
		try
		{
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			String url="jdbc:mysql://localhost:3306/"+dbname;
			con=DriverManager.getConnection(url,"root","Silica123");
			st=con.createStatement();
			st1=con.createStatement();
			st2=con.createStatement();
			//res="connection obtained";
		}
		catch(Exception e)
		{
			System.err.println(e);
		}
		return "connection obtained";
	}
}
