package com.bakersinn;

import java.sql.Connection;
import java.sql.DriverManager;

public class FindTaxesDifferents {
	
	public static void main(String[] args) 
	{
		
	}
	public static Connection getConnection(String database)
	{
		String uname="root";
		String pwd="silica";
		Connection con=null;
		try
		{
			if(con==null)
			{
				Class.forName("com.mysql.jdbc.Driver").newInstance();
				con=DriverManager.getConnection("jdbc:mysql://localhost:3306/"+database,uname,pwd);
			}
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		return con;
	}
}
