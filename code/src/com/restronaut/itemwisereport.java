package com.restronaut;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;

import org.json.JSONObject;

public class itemwisereport {

	public static void main(String[] args) throws Exception {
		try
		{
		String scan="";
        FileReader file = new FileReader("/home/prabhu/Desktop/datajson1");
        BufferedReader br = new BufferedReader(file);
        int i=0;
        if((scan = br.readLine()) != null)
                {
        	//System.out.println(i+"\n");
           // System.out.println(scan);
           // i++;
                }
        JSONObject jsondata=new JSONObject(scan);
       // System.out.println(jsondata.length());
        writeCsv(jsondata);
        br.close();
       
		}catch (Exception e) {
			System.out.println(e);
		}
	}
	public static void writeCsv(JSONObject jsondata)
	{
		try
		{
		System.out.println(jsondata.length());
		JSONObject items=jsondata.getJSONArray("items").getJSONObject(0);
		jsondata.remove("items");
		System.out.println(jsondata.length());
		System.out.println(items.toString());
		//For items loop
		Iterator<String> itemsit=items.keys();
		StringBuilder line = new StringBuilder();
		StringBuilder header = new StringBuilder();
		ArrayList<String> dates=new ArrayList<String>();
		header.append("Itemname,Totalsold,");
		/*Iterator<String> it=jsondata.keys();
		String date;
		while(it.hasNext())
		{
			date=it.next();
			header.append(date+",");
			dates.add(date);
		}*/
		String dat[]="2013-12-03,2013-12-04,2013-12-05,2013-12-07,2013-12-10,2013-12-12,2013-12-13,2013-12-14,2013-12-17,2013-12-21,2013-12-25,2014-01-06,2014-01-07,2014-01-08,2014-01-09,2014-01-10,2014-01-11,2014-01-12,2014-01-16".split("\\,");
		for(int p=0;p<dat.length;p++)
		{
			header.append(dat[p]+",");
			dates.add(dat[p]);
		}
		System.out.println(header);
		JSONObject tempjson;
		StringBuilder tempbuld;
		while(itemsit.hasNext())
		{
			String itname=itemsit.next();
			double qty=0;
			tempbuld=new StringBuilder();
			for(int p=0;p<dates.size();p++)
			{
				tempjson=jsondata.getJSONArray(dates.get(p)).getJSONObject(0);
				if(tempjson.has(itname))
				{
					qty+=Double.parseDouble(tempjson.getString(itname));
					tempbuld.append(tempjson.getString(itname)+",");
				}else
					tempbuld.append("0,");
			}
			line.append(itname+",");
			line.append(qty+",");
			line.append(tempbuld.substring(0, tempbuld.length()-1)+"\n");
		}
		 File newTextFile = new File("/home/prabhu/Desktop/datajsonreport1.csv");
         FileWriter fw = new FileWriter(newTextFile);
         fw.write(header.substring(0, header.length()-1)+"\n");
         fw.write(line.toString());
         fw.close();
		}catch (Exception e) {
			System.out.println(e);
		}
	}
}
