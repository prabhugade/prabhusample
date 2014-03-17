package com.rest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;

public class RenameColumnInTable {

	public static void main(String[] args) 
	{
		try
		{
			RenameColumnInTable crename=new RenameColumnInTable();
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Enter Database name");
			String dbname=reader.readLine();
			System.out.println("Enter Username");
			String uname=reader.readLine();
			System.out.println("Enter Password");
			String pwd=reader.readLine();
			System.out.println("Enter Tablename");	
			String tname=reader.readLine();
			HashMap<String,String> map=new HashMap<String, String>();
			map.put("dataname2", "dataname2");
			map.put("dataname3", "dataname1");
			crename.renameColumn(dbname, uname, pwd, map,tname);
			
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
	public void renameColumn(String dbname,String uname,String pwd,HashMap<String,String> map,String tname)
	{
		Connection con=null;
		Statement st=null;
		try
		{
		con=getConnection(dbname,uname,pwd);
		st=con.createStatement();
		String condition="";
		Iterator<String> it=map.keySet().iterator();
		while(it.hasNext())
		{
			String oldname=it.next();
			String newname=map.get(oldname);
			condition+="CHANGE "+oldname+" "+newname+" VARCHAR( 50 ),";
		}
		condition=condition.substring(0, condition.length()-1);
		String sql="ALTER TABLE "+tname+" "+condition;
		//ALTER TABLE data CHANGE dataname dataname2 VARCHAR( 50 ),CHANGE dataname1 dataname3 VARCHAR( 50 )
				System.out.println(sql);
				int i=st.executeUpdate(sql);
				System.out.println(i);
				System.out.println("successfully changed");
				
		}catch(Exception e)
		{
			System.out.println(e.toString());
		}
	}
}
