package com.rest;

public class sample {

	public static void main(String[] args) 
	{
		String tabids=",registration,role,";
		System.out.println(tabids.substring(1, tabids.length()-1));
		String[] sp=tabids.substring(1, tabids.length()-1).split(",");
		System.out.println(sp.length);
	}

}
