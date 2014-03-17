package com.rest;

import java.io.BufferedReader;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class DefaultRequerements 
{
	String DBName=null;	
	Connection con=null;
	Statement st=null;
	Statement st1=null;
	Statement st2=null;
	public static void main(String[] args) 
	{
		//to read the database from localsystem
		DefaultRequerements defaultreq=new DefaultRequerements();
		defaultreq.defaultReqInsert("hydh");
	}
	private void defaultReqInsert(String dbname) 
	{
		Connection Conn=null;
		try
		{
				Conn = DriverManager.getConnection("jdbc:mysql://localhost/?user=root&password=Silica123"); 
				st=Conn.createStatement();
				int Result=st.executeUpdate("CREATE DATABASE "+dbname);
				if(Result>0)
				{
				connect(dbname);
				con.setAutoCommit(false);
				ScriptRunner runner = new ScriptRunner(con, false, true);
				runner.runScript(new BufferedReader(new FileReader("/home/prabhu/Downloads/restaurant_main.sql")));
				con.commit();
				}
			System.out.println("successfully created");
		}
		catch(Exception e)
		{
			try
			{
			con.rollback();
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
