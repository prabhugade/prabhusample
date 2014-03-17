package com.rest;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

public class DecodeUrl {

	public static void main(String[] args) throws UnsupportedEncodingException 
	{
		String s="http://localhost:8080/Restaurant/weborder?id=6&authtoken=CCBC4E0ACF440BC3FFCC909E9E1B4ECE0C37B1B2&busid=pos&rest=justbiryani&customerinfo=%20{%22pincode%22:%22500032%22,%22landmark%22:%22Kondapur%20byChirec%22,%22alteraddress%22:%22-%22,%22email%22:%22-%22,%22address%22:%22GowthamiEnclave,%22,%22custname%22:%22aaa%22,%22custid%22:%220.0010680480779587942%22,%22mobile%22:%220000000%22,%22orderplace%22:%22alteraddress%22}%20&discount=%200%20&taxes=%20{%22vt%22:%220%22,%22st%22:%220%22,%20%22sc%22:%220%22,%22pc%22:%220%22,%22dlc%22:%220%22,%22drc%22:%220%22,%22stdlc%22:%220%22,%22adch%22:%220%22}%20&itemdata=%20[{%22itemname%22%20:%20%22D%20Spl%20Chi%20Biryani%22%20,%20%22qty%22%20:%221%22,%20%22amount%22%20:%20%22100%22%20,%20%22description%22%20:%20%22less%20spicy1%22}]";
		String url=URLDecoder.decode(s, "UTF-8");
		System.out.println(url);
	}

}
