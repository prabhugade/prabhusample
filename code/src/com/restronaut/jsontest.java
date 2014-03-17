package com.restronaut;

import java.util.ArrayList;
import java.util.HashMap;

import org.json.JSONObject;

public class jsontest 
{
	public static void main(String[] args)
	{
		try
		{
			/*ArrayList<JSONObject> list=new ArrayList<JSONObject>();
			JSONObject obj=new JSONObject();
			obj.put("tot", "10");
			obj.put("one", "1");
			obj.put("name", "a");
			list.add(obj);
			System.out.println(list);
			JSONObject jobj=list.get(0);
			jobj.put("tot", "10");
			System.out.println(list);
			list.add(jobj);
			obj=new JSONObject();
			obj.put("tot", "10");
			obj.put("one", "1");
			obj.put("name", "a");
			list.add(obj);
			System.out.println(list);
			if(list.get(0)==list.get(2))
				System.out.println("equal");
			else
				System.out.println("not equal");*/
			
			HashMap<String,JSONObject> list=new HashMap<String,JSONObject>();
			JSONObject obj=new JSONObject();
			obj.put("tot", "10");
			obj.put("one", "1");
			obj.put("name", "a");
			list.put("a",obj);
			System.out.println(list);
			JSONObject jobj=list.get("a");
			jobj.put("tot", "20");
			System.out.println(list);
			//list.put("a",jobj);
			obj=new JSONObject();
			obj.put("tot", "10");
			obj.put("one", "1");
			obj.put("name", "a");
			list.put("b",obj);
			System.out.println(list);
			if(list.get(0)==list.get(2))
				System.out.println("equal");
			else
				System.out.println("not equal");
		}catch(Exception e)
		{
			System.out.println(e.toString());
		}
	}

}
