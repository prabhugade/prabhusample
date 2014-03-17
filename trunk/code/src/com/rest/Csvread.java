package com.rest;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.HashMap;
import java.util.Iterator;

public class Csvread {

	public static void main(String[] args) {
		try
		{
			File file=new File("/home/prabhu/Desktop/Returns.csv");
			FileReader fr=new FileReader(file);
			BufferedReader br=new BufferedReader(fr);
			HashMap<String,	String> map=new HashMap<String, String>();
			String line;
			String sqlitid="'";
			while((line=br.readLine())!=null)
			{
				String[] cols=line.split(",");
				//System.out.println(cols.length);
				String[] itemids=cols[0].split("\\_\\$\\_");
				String[] qtys=cols[1].split("\\_\\$\\_");
				//System.out.println(itemids.length);
				for(int p=0;p<itemids.length;p++)
				{
					String itid=itemids[p];
					//System.out.println(itid+" "+qtys[p]);
					double qty=Double.parseDouble(qtys[p]);
					if(map.containsKey(itid))
					{
						qty+=Double.parseDouble(map.get(itid));
					}else
					{
						sqlitid+=itid+"','";
					}
					map.put(itid, qty+"");
				}
			}
			//System.out.println(map);
			Iterator<String> it=map.keySet().iterator();
			String inventoryreturnsql="INSERT INTO `InventoryReturn`(kitchenid, EmployeeId, ItemId,WasteQty, TransforQty, DateTime) VALUES ";
			String inventory="INSERT INTO `Inventory` (`InventoryId`, `ItemId`, `quantity`, `DateTime`,`EmployeeId`,`kitchenid`) VALUES ";
			while(it.hasNext())
			{
				String itid=it.next();
				String qty=map.get(itid);
				inventoryreturnsql+=" ( 'kitchen', '1', '"+itid+"', '0', '"+qty+"', '2014-01-24 08:22:40'),";
				inventory+="('"+Math.random()+"','"+itid+"','"+qty+"','2014-01-24 08:23:00','1','kitchen'),";
			}
			System.out.println("=================");
			System.out.println(inventoryreturnsql);
			System.out.println("===========");
			System.out.println(inventory);
			System.out.println("=============");
		}catch (Exception e) {
			System.out.println(e.toString());
		}
	}

}
