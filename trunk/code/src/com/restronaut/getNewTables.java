package com.restronaut;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

public class getNewTables 
{
	//working
	String dbname=null;
	public static void main(String[] args) 
	{
		try
		{
			getNewTables comp=new getNewTables();
			BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Enter Database name");
			String dbname=reader.readLine();
			System.out.println("Enter Username");
			String uname=reader.readLine();
			System.out.println("Enter Password");
			String pwd=reader.readLine();
			HashMap<String,String> map1=comp.getTables(dbname,uname,pwd);
			System.out.println("Enter Another Database name");
			dbname=reader.readLine();
			HashMap<String,String> map2=comp.getTables(dbname,uname,pwd);
			HashMap<String,String> newTables=comp.compareToMaps(map1, map2,dbname,uname,pwd);
			
			System.out.println("\n\n\n"+newTables.size());
			System.out.println("\n\n\n\n\n"+newTables);
			
			System.out.println("\n\n\n\n\n"+newTables.keySet());
			
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
	public HashMap<String,String> getTables(String dbname,String uname,String pwd)
	{
		HashMap<String,String> map=new HashMap<String,String>();
		Connection con=null;													
		Statement st=null;
		
		try
		{
			con=getConnection(dbname,uname,pwd);
			st=con.createStatement();
			DatabaseMetaData dbm = con.getMetaData();
			String[] types = {"TABLE"};
			ResultSet rs = dbm.getTables(null,null,"%",types);
			while (rs.next())
			{
				String table = rs.getString("TABLE_NAME");
				ResultSet rest=st.executeQuery("SELECT * FROM "+table);
				ResultSetMetaData rsmd = rest.getMetaData();
				int numColumns = rsmd.getColumnCount();
				String columnnames=numColumns+",";
				for (int i=1; i<numColumns+1; i++) 
				{
					String columnName = rsmd.getColumnName(i);
					String coltype=rsmd.getColumnTypeName(i);
					boolean autoinc=rsmd.isAutoIncrement(i);
					String s="";
					if(autoinc)
						s=" NOT NULL AUTO_INCREMENT";
					String colsize=rsmd.getColumnDisplaySize(i)+"";
					columnnames+="`"+columnName+"` "+coltype+"("+colsize+")"+s+",";
				}
				map.put(table, columnnames);
			}
		}catch(Exception e)
		{
			
			System.out.println(e.getMessage());
		}finally
		{
			try
			{
			con.close();
			st.close();
			}catch(Exception e)
			{
				System.out.println(e.getMessage());
			}
		}
		return map;
	}
	public HashMap<String,String> compareToMaps(HashMap<String,String> map1,HashMap<String,String> map2,String dbname,String uname,String pwd)
	{
		HashMap<String,String> tables=new HashMap<String,String>();
		HashMap<String,String> table=new HashMap<String,String>();
		try
		{
			System.out.println(map1);
			System.out.println(map2);
			Set<String> set=map1.keySet();
			Iterator<String> it=set.iterator();
			while(it.hasNext())
			{
				String tablename=it.next();
				if(map2.containsKey(tablename))
				{
					if(!map1.get(tablename).equals(map2.get(tablename)))
					{
						System.out.println("columns different\n"+tablename+" "+map1.get(tablename)+"    "+map2.get(tablename));
						String[] tab1data=map1.get(tablename).split(",");
						String[] tab2data=map2.get(tablename).split(",");
						if(tab1data[0].equalsIgnoreCase(tab2data[0]))
						{
							HashMap<String,String> columns=new HashMap<String,String>();
							for(int i=1;i<tab1data.length;i++)
								columns.put(tab2data[i].split(" ")[0], tab1data[i]);
							new RenameColumnInTable().renameColumn(dbname, uname, pwd, columns, tablename);
							//System.out.println(tablename+"  "+columns);
						}
					}
				}else
				{
					if(map2.containsKey(tablename+"s"))
						table.put(tablename+"s", tablename);
					tables.put(tablename, map1.get(tablename));
					System.out.println("new table\n"+tablename+" "+map1.get(tablename));
				}
			}
			new TableRename().renameTable(dbname, uname, pwd, table);
			System.out.println("s changes\n\n"+table.keySet());
		}catch(Exception e)
		{
			System.out.println(e.getMessage());
		}
		return tables;
	}
}
