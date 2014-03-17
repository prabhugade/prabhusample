package com.agrasweets;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.StringTokenizer;

public class KitchenAvailabulity 
{
	static DecimalFormat df = new DecimalFormat("#.###");
	public HashMap<String,String> getKitchenAvailabulity(Connection con,String kitchenid,String fromdate,String todate,BufferedWriter bufwriter)
	{
		boolean errcheck=false;
		String errorlog="start";
		String errorlogfinal="";
		HashMap<String,String> resultmap=new HashMap<String,String>();
		//Connection con=null;
		Statement st11=null,st12=null;
		try
		{
			//con=DatabaseConnections.getConnection();
			if(con!=null)
			{
				st11=con.createStatement();
				st12=con.createStatement();
				String sql="";
				
				//this is for his stock
				if(!kitchenid.equalsIgnoreCase("all"))
					sql="SELECT ItemId,sum(quantity) as quantity FROM `Inventory` WHERE kitchenid='"+kitchenid+"' AND DateTime BETWEEN '"+fromdate+"' AND '"+todate+"' group by ItemId";
				else
					sql="SELECT ItemId,sum(quantity) as quantity FROM `Inventory` WHERE kitchenid like 'kitchen%' AND DateTime BETWEEN '"+fromdate+"' AND '"+todate+"' group by ItemId";
				errorlog=sql;
				bufwriter.write(sql);
				bufwriter.newLine();
				ResultSet reset=st11.executeQuery(sql);
				while(reset.next())
				{
					String itid=reset.getString("ItemId");
					String qty=reset.getString("quantity");
					if(reset.wasNull())
						qty="0";

					if(resultmap.containsKey(itid))
					{
						double quantity=Double.parseDouble(resultmap.get(itid))+Double.parseDouble(qty);    				//taken quantity
						resultmap.put(itid, df.format(quantity));
					}else
						resultmap.put(itid, qty);
				}
				//this is for distibution stock
				if(!kitchenid.equalsIgnoreCase("all"))
					sql="SELECT ItemId,sum(quantity) as quantity FROM `Distribution` WHERE TakenFrom='"+kitchenid+"' AND DateTime BETWEEN '"+fromdate+"' AND '"+todate+"' group by ItemId";
				else
					sql="SELECT ItemId,sum(quantity) as quantity FROM `Distribution` WHERE TakenFrom like'kitchen%' AND DateTime BETWEEN '"+fromdate+"' AND '"+todate+"' group by ItemId";
				bufwriter.write(sql);
				bufwriter.newLine();
				errorlog=sql;
				reset=st11.executeQuery(sql);
				while(reset.next())
				{
					String itid=reset.getString("ItemId");
					String qty=reset.getString("quantity");
					if(reset.wasNull())
						qty="0";
					if(resultmap.containsKey(itid))
					{
						double quantity=Double.parseDouble(resultmap.get(itid))-Double.parseDouble(qty);    				                                            //taken quantity
						resultmap.put(itid, df.format(quantity));
					}else
					{
						double quantity=Double.parseDouble("0")-Double.parseDouble(qty); 
						resultmap.put(itid, df.format(quantity));
					}
				}
				//this is for return stock
				if(!kitchenid.equalsIgnoreCase("all"))
					sql="SELECT ItemId,ReturnQuantity FROM `Returns` WHERE ReturnPlace='"+kitchenid+"' AND DateTime BETWEEN '"+fromdate+"' AND '"+todate+"'";
				else
					sql="SELECT ItemId,ReturnQuantity FROM `Returns` WHERE ReturnPlace like 'kitchen%' AND DateTime BETWEEN '"+fromdate+"' AND '"+todate+"'";
				bufwriter.write(sql);
				bufwriter.newLine();
				errorlog=sql;
				ResultSet reset1=st12.executeQuery(sql);
				System.out.println("sasfa");
				while(reset1.next())
				{
					System.out.println("if");
					String itid=reset1.getString("ItemId");
					System.out.println(itid);
					
					StringTokenizer tokenizer=new StringTokenizer(itid,"_$_");
					String[] itemid=new String[tokenizer.countTokens()];
					int z=0;
					while(tokenizer.hasMoreTokens())
					{
						itemid[z]=tokenizer.nextToken();
						z++;
					}
					tokenizer=new StringTokenizer(reset1.getString("ReturnQuantity"),"_$_");
					String[] retqtys=new String[tokenizer.countTokens()];
					z=0;
					while(tokenizer.hasMoreTokens())
					{
						retqtys[z]=tokenizer.nextToken();
						z++;
					}
					for(int i=0;i<itemid.length;i++)
					{
						bufwriter.write(itemid[i]+"====="+retqtys[i]);
						bufwriter.newLine();
						if(resultmap.containsKey(itemid[i]))
						{
							double quantity=Double.parseDouble(resultmap.get(itemid[i]))+Double.parseDouble(retqtys[i]);
							resultmap.put(itemid[i], df.format(quantity));
						}else
							resultmap.put(itemid[i], retqtys[i]);
					}
				}
				System.out.println("asdfa");
			}else
			{
				errcheck=true;
				errorlog="failed to connect the database";
			}
		}
		catch(Exception e)
		{
			errorlogfinal=errorlog+" getKitchenAvailabulity "+e.toString();
		}finally
		{
			try
			{
				/*if(st11!=null)
					st11.close();
				if(con!=null)
					con.close();*/
				if(!errorlogfinal.equals("")||errcheck)
				{
					if(errorlogfinal.equals(""))
						errorlogfinal=errorlog+" getKitchenAvailabulity ";
					System.out.println(errorlogfinal);
				}
			}catch(Exception e)
			{
				System.out.println("getKitchenAvailabulity"+e.toString());
			}
		}
		return resultmap;
	}

}
