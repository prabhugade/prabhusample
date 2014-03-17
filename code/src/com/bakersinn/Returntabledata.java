package com.bakersinn;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class Returntabledata {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ArrayList<String> trans_add_dates=new ArrayList<String>();
		/*trans_add_dates.add("2014-02-07 12:14:18");
		trans_add_dates.add("2014-02-08 12:40:45");
		trans_add_dates.add("2014-02-09 13:28:19");
		trans_add_dates.add("2014-02-10 10:31:31");
		trans_add_dates.add("2014-02-11 07:43:27");
		trans_add_dates.add("2014-02-12 10:30:03");
		trans_add_dates.add("2014-02-13 10:26:52");
		trans_add_dates.add("2014-02-14 11:32:17");
		trans_add_dates.add("2014-02-15 00:00:12");*/
		trans_add_dates.add("2014-02-16 00:08:11");
		trans_add_dates.add("2014-02-17 09:51:54");
		trans_add_dates.add("2014-02-18 10:01:18");
		//new Returntabledata().ForKiosk(trans_add_dates);
		//new Returntabledata().ForKiosk1(trans_add_dates);
		//new Returntabledata().ForKiosk2(trans_add_dates);
		new Returntabledata().ForKiosk3(trans_add_dates);
	}

	public void ForKiosk(ArrayList<String> trans_add_dates)
	{
		HashMap<String, String> hashmap=new HashMap<String, String>();
		hashmap.put("0.05791509574282239","5");
		hashmap.put("0.06540477579089332","5");
		hashmap.put("0.09140903375072518","5");
		hashmap.put("0.11290764992373081","5");
		hashmap.put("0.11591541651469117","6");
		hashmap.put("0.1350382644930319","6");
		hashmap.put("0.23839670911764577","6");
		hashmap.put("0.26619132058014716","9");
		hashmap.put("0.3196904945423318","6");
		hashmap.put("0.3476237239927279","5");
		hashmap.put("0.3718426746348368","6");
		hashmap.put("0.48919749355917896","9");
		hashmap.put("0.49154297316074713","6");
		hashmap.put("0.510314543679221","10");
		hashmap.put("0.5693165275994093","6");
		hashmap.put("0.608368157320709","6");
		hashmap.put("0.6096356581372261","5");
		hashmap.put("0.6258152088332262","5");
		hashmap.put("0.6678903523281178","5");
		hashmap.put("0.6947931588018673","5");
		hashmap.put("0.710883850282523","5");
		hashmap.put("0.7193848445286468","6");
		hashmap.put("0.7315894280282762","5");
		hashmap.put("0.7866040280528674","5");
		hashmap.put("0.8314745724020847","5");
		hashmap.put("0.8696410123593344","5");
		hashmap.put("0.8989063627247212","10");
		hashmap.put("0.9297578140090456","5");
		hashmap.put("0.9431422260953649","30");
		hashmap.put("0.9825389774537449","10");
		String itid;
		String itemids="";
		String qtys="";
		Iterator<String> it=hashmap.keySet().iterator();
		while(it.hasNext())
		{
			itid=it.next();
			itemids+=itid+"_$_";
			qtys+=hashmap.get(itid)+"_$_";
		}
		itemids=itemids.substring(0,itemids.length()-3);
		qtys=qtys.substring(0,qtys.length()-1);
		String sql2=" INSERT INTO `Returns`(`ItemId`, `DriverId`, `ReturnQuantity`, `DeliveredQunatity`, `DateTime`, `Amount`, `Status`, `ReturnPlace`, `EmployeeId`) VALUES ";
		for(int p=0;p<trans_add_dates.size();p++)
		{
			sql2+="  ('"+itemids+"','kiosk0.8154693457301795','"+qtys+"','0','"+trans_add_dates.get(p)+"','0','completed','kitchen','kitchen'),";
		}
		System.out.println("=======================================================kiosk0.8154693457301795");
		System.out.println(sql2);
	}
	
	//For kiosk
			public void ForKiosk1(ArrayList<String> trans_add_dates)
			{
				HashMap<String, String> hashmap=new HashMap<String, String>();
				hashmap.put("0.05791509574282239","17");
				hashmap.put("0.5693165275994093","12");
				hashmap.put("0.710883850282523","13");
				hashmap.put("0.7193848445286468","18");
				hashmap.put("0.7315894280282762","33");
				String itid;
				String itemids="";
				String qtys="";
				Iterator<String> it=hashmap.keySet().iterator();
				while(it.hasNext())
				{
					itid=it.next();
					itemids+=itid+"_$_";
					qtys+=hashmap.get(itid)+"_$_";
				}
				itemids=itemids.substring(0,itemids.length()-1);
				qtys=qtys.substring(0,qtys.length()-1);
				String sql2=" INSERT INTO `Returns`(`ItemId`, `DriverId`, `ReturnQuantity`, `DeliveredQunatity`, `DateTime`, `Amount`, `Status`, `ReturnPlace`, `EmployeeId`) VALUES ";
				for(int p=0;p<trans_add_dates.size();p++)
				{
					sql2+="  ('"+itemids+"','kiosk0.9877869034514064','"+qtys+"','0','"+trans_add_dates.get(p)+"','0','completed','kitchen','kitchen'),";
				}
				System.out.println("=======================================================kiosk0.9877869034514064");
				System.out.println(sql2);
			}
			
			//For kiosk
			public void ForKiosk2(ArrayList<String> trans_add_dates)
			{
				HashMap<String, String> hashmap=new HashMap<String, String>();
				hashmap.put("0.9297578140090456","45");
				hashmap.put("0.5693165275994093","20");
				hashmap.put("0.710883850282523","19");
				hashmap.put("0.3718426746348368","21");
				hashmap.put("0.510314543679221","37");
				hashmap.put("0.06540477579089332","17");
				hashmap.put("0.9431422260953649","15");
				hashmap.put("0.48919749355917896","41");
				hashmap.put("0.23839670911764577","16");
				hashmap.put("0.9825389774537449","37");
				hashmap.put("0.3476237239927279","19");
				hashmap.put("0.05791509574282239","19");
				hashmap.put("0.7193848445286468","18");
				hashmap.put("0.49154297316074713","17");
				hashmap.put("0.8314745724020847","16");
				hashmap.put("0.6258152088332262","19");
				hashmap.put("0.11591541651469117","12");
				hashmap.put("0.6947931588018673","20");
				hashmap.put("0.09140903375072518","17");
				hashmap.put("0.8989063627247212","37");
				hashmap.put("0.608368157320709","19");
				hashmap.put("0.26619132058014716","41");
				hashmap.put("0.1350382644930319","11");
				hashmap.put("0.3196904945423318","19");
				String itid;
				String itemids="";
				String qtys="";
				Iterator<String> it=hashmap.keySet().iterator();
				while(it.hasNext())
				{
					itid=it.next();
					itemids+=itid+"_$_";
					qtys+=hashmap.get(itid)+"_$_";
				}
				itemids=itemids.substring(0,itemids.length()-1);
				qtys=qtys.substring(0,qtys.length()-1);
				String sql2=" INSERT INTO `Returns`(`ItemId`, `DriverId`, `ReturnQuantity`, `DeliveredQunatity`, `DateTime`, `Amount`, `Status`, `ReturnPlace`, `EmployeeId`) VALUES ";
				for(int p=0;p<trans_add_dates.size();p++)
				{
					sql2+="  ('"+itemids+"','kiosk0.3627435694951967','"+qtys+"','0','"+trans_add_dates.get(p)+"','0','completed','kitchen','kitchen'),";
				}
				System.out.println("=======================================================kiosk0.3627435694951967");
				System.out.println(sql2);
			}
			//For kiosk
			public void ForKiosk3(ArrayList<String> trans_add_dates)
			{
				HashMap<String, String> hashmap=new HashMap<String, String>();
				hashmap.put("0.11290764992373081","10");
				hashmap.put("0.48919749355917896","20");
				hashmap.put("0.510314543679221","20");
				hashmap.put("0.7866040280528674","10");
				hashmap.put("0.6096356581372261","10");
				hashmap.put("0.3476237239927279","20");
				hashmap.put("0.8989063627247212","20");
				hashmap.put("0.1350382644930319","20");
				hashmap.put("0.23839670911764577","20");
				hashmap.put("0.7315894280282762","10");
				hashmap.put("0.09140903375072518","20");
				hashmap.put("0.05791509574282239","20");
				hashmap.put("0.6258152088332262","20");
				hashmap.put("0.06540477579089332","20");
				hashmap.put("0.6947931588018673","20");
				hashmap.put("0.6678903523281178","6");
				hashmap.put("0.7498407974176314","30");
				hashmap.put("0.3718426746348368","20");
				hashmap.put("0.9297578140090456","20");
				hashmap.put("0.608368157320709","20");
				hashmap.put("0.9825389774537449","20");
				hashmap.put("0.8696410123593344","14");
				hashmap.put("0.5693165275994093","20");
				hashmap.put("0.8314745724020847","20");
				hashmap.put("0.49154297316074713","20");
				hashmap.put("0.26619132058014716","20");
				String itid;
				String itemids="";
				String qtys="";
				Iterator<String> it=hashmap.keySet().iterator();
				while(it.hasNext())
				{
					itid=it.next();
					itemids+=itid+"_$_";
					qtys+=hashmap.get(itid)+"_$_";
				}
				itemids=itemids.substring(0,itemids.length()-1);
				qtys=qtys.substring(0,qtys.length()-1);
				String sql2=" INSERT INTO `Returns`(`ItemId`, `DriverId`, `ReturnQuantity`, `DeliveredQunatity`, `DateTime`, `Amount`, `Status`, `ReturnPlace`, `EmployeeId`) VALUES ";
				for(int p=0;p<trans_add_dates.size();p++)
				{
					sql2+="  ('"+itemids+"','kiosk0.31858688961820414','"+qtys+"','0','"+trans_add_dates.get(p)+"','0','completed','kitchen','kitchen'),";
				}
				System.out.println("=======================================================kiosk0.31858688961820414");
				System.out.println(sql2);
			}
}
