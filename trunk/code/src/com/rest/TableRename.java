package com.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;

public class TableRename 
{
	public static void main(String[] args)
	{
		try
		{
			TableRename trename=new TableRename();
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Enter Database name");
			String dbname=reader.readLine();
			System.out.println("Enter Username");
			String uname=reader.readLine();
			System.out.println("Enter Password");
			String pwd=reader.readLine();
			HashMap<String,String> map=new HashMap<String, String>();
			map.put("test", "data2");
			map.put("test1", "data3");
			trename.renameTable(dbname, uname, pwd, map);
			
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
	}
	public Connection getConnection(String database,String uname,String pwd)
	{
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
	public void renameTable(String dbname,String uname,String pwd,HashMap<String,String> map)
	{
		Connection con=null;
		Statement st=null;
		try
		{
		con=getConnection(dbname,uname,pwd);
		st=con.createStatement();
		Iterator<String> it=map.keySet().iterator();
		while(it.hasNext())
		{
			String oldname=it.next();
			String newname=map.get(oldname);
			String sql="RENAME TABLE "+dbname+"."+oldname+" TO "+dbname+"."+newname;
			System.out.println(sql);
			int i=st.executeUpdate(sql);
			System.out.println(i);
			if(i>0)
				System.out.println(oldname+" successfully changed to "+newname);
			else
				System.out.println("failed to rename table");
			
		}
		}catch(Exception e)
		{
			System.out.println(e.toString());
		}
	}

}
