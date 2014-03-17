package com.agrasweets;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;

public class KioskAvilabulity {
	public HashMap<String,String> getKioskAvilabulity(Connection con,String kioskid,String fromdate,String todate)
	{
		boolean errcheck=false;
		String errorlog="start";
		String errorlogfinal="";
		HashMap<String,String> resultmap=new HashMap<String,String>();
		//Connection con=null;
		Statement st123=null;
		try
		{
			//con=DatabaseConnections.getConnection();
			if(con!=null)
			{
				st123=con.createStatement();
				String sql="";
				
				//this is for his stock
				sql="SELECT ItemId, Quantity FROM `Orders` WHERE KioskId='"+kioskid+"' AND DateTime BETWEEN '"+fromdate+"' AND '"+todate+"' AND OrderStatus='Done' AND CustmerId not like 'driver%'";
				errorlog=sql;
				ResultSet reset=st123.executeQuery(sql);
				while(reset.next())
				{
					String itid[]=reset.getString("ItemId").split("\\_\\$\\_");
					String delqty[]=reset.getString("Quantity").split("\\_\\$\\_");
					for(int p=0;p<itid.length;p++)
					{
						if(resultmap.containsKey(itid[p]))
						{
							double quantity=Double.parseDouble(resultmap.get(itid[p]))-Double.parseDouble(delqty[p]);    				//taken quantity
							resultmap.put(itid[p], KitchenAvailabulity.df.format(quantity));
						}else
						{
							double quantity=Double.parseDouble("0")-Double.parseDouble(delqty[p]);    				//taken quantity
							resultmap.put(itid[p], KitchenAvailabulity.df.format(quantity));
						}
					}
				}
				//taken from kitchen
				sql="SELECT id.ItemName,id.ItemId,d.quantity FROM Distribution as d join ItemDetails as id on d.ItemId=id.ItemId WHERE d.DriverId='"+kioskid+"' AND d.DateTime BETWEEN '"+fromdate+"' AND '"+todate+"'";
				errorlog=sql;
				reset=st123.executeQuery(sql);
				while(reset.next())
				{
					String itid=reset.getString("ItemId");
					if(resultmap.containsKey(itid))
					{
						double quantity=Double.parseDouble(resultmap.get(itid))+reset.getDouble("quantity");
						resultmap.put(itid, KitchenAvailabulity.df.format(quantity));
					}else
					{
						resultmap.put(itid, reset.getString("quantity"));
					}
				}
				//given to drivers
				sql="SELECT id.ItemName,d.quantity,id.ItemId  FROM Distribution as d join ItemDetails as id on d.ItemId=id.ItemId WHERE d.TakenFrom='"+kioskid+"' AND d.DateTime BETWEEN '"+fromdate+"' AND '"+todate+"'";
				errorlog=sql;
				ResultSet rs=st123.executeQuery(sql);
				while(rs.next())
				{
					String itid=rs.getString("ItemId");
					if(resultmap.containsKey(itid))
					{
						double quantity=Double.parseDouble(resultmap.get(itid))-Double.parseDouble(rs.getString("quantity"));
						resultmap.put(itid, KitchenAvailabulity.df.format(quantity));
					}else
					{
						double quantity=Double.parseDouble("0")-Double.parseDouble(rs.getString("quantity"));
						resultmap.put(itid, KitchenAvailabulity.df.format(quantity));
					}
				}
				//for return to kiosk
				sql="SELECT ItemId,ReturnQuantity FROM `Returns` WHERE ReturnPlace='"+kioskid+"' AND DateTime BETWEEN '"+fromdate+"' AND '"+todate+"'";
				errorlog=sql;
				rs=st123.executeQuery(sql);
				while(rs.next())
				{
					String itid[]=rs.getString("ItemId").split("\\_\\$\\_");
					String rquantity[]=rs.getString("ReturnQuantity").split("\\_\\$\\_");
					for(int j=0;j<itid.length;j++)
					{
						if(resultmap.containsKey(itid[j]))
						{
							double quantity=Double.parseDouble(resultmap.get(itid[j]))+Double.parseDouble(rquantity[j]);
							resultmap.put(itid[j], KitchenAvailabulity.df.format(quantity));
						}else
						{
							double quantity=Double.parseDouble("0")+Double.parseDouble(rquantity[j]);
							resultmap.put(itid[j], KitchenAvailabulity.df.format(quantity));
						}
					}
				}
				
				//return to kitchen
				sql="SELECT ItemId,ReturnQuantity FROM `Returns` WHERE DriverId='"+kioskid+"' AND DateTime BETWEEN '"+fromdate+"' AND '"+todate+"'";
				errorlog=sql;
				rs=st123.executeQuery(sql);
				while(rs.next())
				{
					String[] itemid=rs.getString("ItemId").split("\\_\\$\\_");
					String[] quan=rs.getString("ReturnQuantity").split("\\_\\$\\_");
					for(int q=0;q<itemid.length;q++)
					{
						if(resultmap.containsKey(itemid[q]))
						{
							double quantity=Double.parseDouble(resultmap.get(itemid[q]))-Double.parseDouble(quan[q]);
							resultmap.put(itemid[q], KitchenAvailabulity.df.format(quantity));
						}else
						{
							double quantity=Double.parseDouble("0")-Double.parseDouble(quan[q]);
							resultmap.put(itemid[q], KitchenAvailabulity.df.format(quantity));
						}
					}
				}
				
			}else
			{
				errcheck=true;
				errorlog="failed to connect the database";
			}
		}
		catch(Exception e)
		{
			errorlogfinal=errorlog+" getKioskAvilabulity "+e.toString();
		}finally
		{
			try
			{
				/*if(st123!=null)
					st123.close();
				if(con!=null)
					con.close();*/
				if(!errorlogfinal.equals("")||errcheck)
				{
					if(errorlogfinal.equals(""))
						errorlogfinal=errorlog+" getKioskAvilabulity ";
					System.out.println(errorlogfinal);
				}
			}catch(Exception e)
			{
				System.out.println("getKioskAvilabulity"+e.toString());
			}
		}
		return resultmap;
	}

}
