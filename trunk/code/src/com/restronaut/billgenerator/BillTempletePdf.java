package com.restronaut.billgenerator;

import java.awt.Color;
import java.io.FileOutputStream;
import java.text.DecimalFormat;

import org.json.JSONObject;

import com.lowagie.text.Chunk;
import com.lowagie.text.Document;
import com.lowagie.text.Element;
import com.lowagie.text.Font;
import com.lowagie.text.HeaderFooter;
import com.lowagie.text.Image;
import com.lowagie.text.PageSize;
import com.lowagie.text.Paragraph;
import com.lowagie.text.Rectangle;
import com.lowagie.text.pdf.PdfPCell;
import com.lowagie.text.pdf.PdfPTable;
import com.lowagie.text.pdf.PdfWriter;

public class BillTempletePdf {
	DecimalFormat amtformat = new DecimalFormat("#.##");
	DecimalFormat qtyformat = new DecimalFormat("#.###");
	Double totamt=0.0;
	public static void main(String[] args) throws Exception {
		JSONObject jsondata=new JSONObject();
		jsondata.put("imageurl", "http://www.bakersinn.com/wp-content/uploads/2013/10/logo.png");
		jsondata.put("invoicename", "Silica");
		jsondata.put("rest", "Silicaresto");
		jsondata.put("restaddress", "Address");
		jsondata.put("billingaddress", "Billing Address");
		jsondata.put("shipingaddress", "Shiping Address");
		jsondata.put("restaddressdata", "Silicaresto\nhyderabad");
		jsondata.put("billingaddressdata", "Silicaresto\nhyderabad\n\n\n\n");
		jsondata.put("shipingaddressdata", "Silicaresto\nhyderabad\n\n\n\n");
		jsondata.put("footer", "\n\n\n\n\n\n\n\n\n\n\nTerms & Conditions\n1 All disputes arising out of this transaction are subject to jurisdiction only.\n2 Our responsibility ceases once the goods are delivered to the Super Stockist/his agent or upon delivery to the transporter.\n	No claims will be entertained by us for shortages or damages in transit.\n3 All orders will be despatched either in whole or in part subject to availability of stocks.\n4 All payments should be made either by Crossed Account Payee Cheque/Demand Draft drawn in favour of Bakers Inn Global Private Limited- payable at For Bakers Inn Global Private Limited");
		jsondata.put("custsign", "Customer Signatory");
		jsondata.put("authsign", "Authorised Signatory");
		JSONObject itemheaders=new JSONObject();
		itemheaders.put("sno", "Sno");
		itemheaders.put("itname", "Item Name");
		itemheaders.put("MC", "MASTER CARTOONS");
		itemheaders.put("PacForMC", "No of packets for MC");
		itemheaders.put("totpac", "Total Packets");
		itemheaders.put("UOM", "UOM");
		itemheaders.put("RateForPac", "Rate For Packet");
		itemheaders.put("amt", "Amount");
		jsondata.put("itemheaders", itemheaders);
		JSONObject itemsdata=new JSONObject();
		itemsdata.put("itname", "item1_$_item2");
		itemsdata.put("qty", "1_$_2");
		itemsdata.put("PacForMC", "10_$_20");
		itemsdata.put("measurement", "packet_$_KG");
		itemsdata.put("RateForPac", "10_$_12");
		itemsdata.put("amt", "100_$_240");
		jsondata.put("itemdata", itemsdata);
		new BillTempletePdf().pdfForm(jsondata);
	}
	public void pdfForm(JSONObject datajson) throws Exception
	{
		String address=datajson.getString("restaddressdata");
		String billingaddress=datajson.getString("billingaddressdata");
		String shipingaddress=datajson.getString("shipingaddressdata");
		JSONObject itemheaderdata=datajson.getJSONObject("itemheaders");
		JSONObject itemsdata=datajson.getJSONObject("itemdata");
		/**
		 * pdf set to A4 size
		 */
		Document document=new Document(PageSize.A4);
		PdfWriter.getInstance(document,new FileOutputStream("/home/prabhu/billtest2.pdf"));
		// headers and footers must be added before the document is opened
		Paragraph footertext=new Paragraph("Registered office: Shop No:23,6-3-1110.Amrutha Mall,Somajiguda, Hyderabad-500082.AP,India.\nCorporate Office:176 & 176/2,Linkwell Building,Tadbund X Road, Secundrabad.-500009,AP,India.",new Font(Font.TIMES_ROMAN, 10, Font.NORMAL, Color.BLACK));
		HeaderFooter footer = new HeaderFooter(footertext, false);
		footer.setBorder(Rectangle.NO_BORDER);
		footer.setAlignment(Element.ALIGN_LEFT);
		document.setFooter(footer);
		HeaderFooter header = new HeaderFooter(new Paragraph(getChunk(datajson.getString("imageurl"))), false);
		header.setBorder(Rectangle.NO_BORDER);
		header.setAlignment(Element.ALIGN_LEFT);
		document.setHeader(header);
		/*Paragraph para=new Paragraph("INVOICE",getCellFont(Color.BLACK));
		para.setAlignment(Element.ALIGN_RIGHT);
		HeaderFooter header1 = new HeaderFooter(para, false);
		header1.setAlignment(Element.ALIGN_CENTER);
		document.setHeader(header1);*/
		document.open();
		/*
		 * This is the image and bill name adding
		 */
		//float[] columnWidths = new float[] {40f, 60f, 50f};
		PdfPTable table1=new PdfPTable(3);
		table1.setWidthPercentage(98);
		if(datajson.has("imageurl"))
			table1.addCell(imageCells(datajson.getString("imageurl")));
		else
			table1.addCell(billnameCells(""));
		table1.addCell(billnameCells(""));
		if(datajson.has("invoicename"))
			table1.addCell(billnameCells(datajson.getString("invoicename")));
		else
			table1.addCell(billnameCells(""));
		table1.setHorizontalAlignment(Element.ALIGN_LEFT);
		document.add(table1);

		/*
		 * Restaurant and Customer Details
		 */
		table1=new PdfPTable(2);
		table1.setWidthPercentage(98);
		if(datajson.has("rest"))
			table1.addCell(detailsCells(datajson.getString("rest")));
		else
			table1.addCell(detailsCells(" "));
		if(datajson.has("invoiceno"))
			table1.addCell(detailsCells(datajson.getString("invoiceno")));
		else
			table1.addCell(detailsCells(" "));
		if(datajson.has("restaddress"))
			table1.addCell(detailsCells(datajson.getString("restaddress")));
		else
			table1.addCell(detailsCells(" "));

		if(datajson.has("invoicedate"))
			table1.addCell(detailsCells(datajson.getString("invoicedate")));
		else
			table1.addCell(detailsCells(" "));
		if(datajson.has("restaddress"))
			table1.addCell(nameKeyCells(address));
		else
			table1.addCell(detailsCells(" "));
		table1.addCell(nameKeyCells(""));
		/*
		 *  For address
		 */
		if(datajson.has("billingaddress")&&datajson.has("shipingaddress"))
		{
			PdfPTable table2=new PdfPTable(1);
			table2.setWidthPercentage(40);
			table2.addCell(detailsCells(datajson.getString("billingaddress")));
			table2.addCell(nameKeyCells(billingaddress));
			table1.addCell(table2);

			table2=new PdfPTable(1);
			table2.setWidthPercentage(40);
			table2.addCell(detailsCells(datajson.getString("shipingaddress")));
			table2.addCell(nameKeyCells(shipingaddress));
			table1.addCell(table2);
		}else if(datajson.has("shipingaddress"))
		{
			PdfPTable table2=new PdfPTable(1);
			table2.setWidthPercentage(40);
			table2.addCell(detailsCells(datajson.getString("shipingaddress")));
			table2.addCell(nameKeyCells(shipingaddress));
			table1.addCell(table2);
			table1.addCell(detailsCells(""));
		}else if(datajson.has("billingaddress"))
		{
			PdfPTable table2=new PdfPTable(1);
			table2.setWidthPercentage(40);
			table2.addCell(detailsCells(datajson.getString("billingaddress")));
			table2.addCell(nameKeyCells(billingaddress));
			table1.addCell(table2);
			table1.addCell(detailsCells(""));
		}
		document.add(table1);
		document.add(new Paragraph(" "));
		/*
		 * Adding items data
		 */
		table1=writeItemsData(itemheaderdata,itemsdata);
		document.add(table1);
		/*
		 * Tax Data adding
		 */
		table1=taxData("0_$_0_$_0_$_0_$_90.7_$_0_$_0","10");
		document.add(table1);
		/*
		 * footer
		 */
		document.add(new Paragraph(" "));
		if(datajson.has("footer"))
		{
			Paragraph phonpara=new Paragraph(datajson.getString("footer"));
			phonpara.setAlignment(Element.ALIGN_LEFT);
			document.add(phonpara);
			document.add(new Paragraph(" "));
		}
		if(datajson.has("footer"))
		{
			Paragraph phonpara=new Paragraph(datajson.getString("footer"),new Font(Font.TIMES_ROMAN, 7, Font.NORMAL, Color.BLACK));
			phonpara.setAlignment(Element.ALIGN_LEFT);
			document.add(phonpara);
			document.add(new Paragraph(" "));
		}
		/*
		 * Signature 
		 */
		PdfPTable table2=new PdfPTable(2);
		table2.setWidthPercentage(98);
		if(datajson.has("custsign")&&datajson.has("authsign"))
		{
			table2.addCell(nameKeyCells(datajson.getString("custsign")));
			table2.addCell(nameKeyrightCells(datajson.getString("authsign")));
		}
		else if(datajson.has("custsign"))
		{
			table2.addCell(nameKeyCells(""));
			table2.addCell(nameKeyrightCells(datajson.getString("custsign")));
		}else if(datajson.has("authsign"))
		{
			table2.addCell(nameKeyCells(""));
			table2.addCell(nameKeyrightCells(datajson.getString("authsign")));
		}
		table2.addCell(nameKeyCells("\n\n\n"));
		table2.addCell(nameKeyCells("\n\n\n"));
		document.add(table2);
		document.close();
		System.out.println("successfully created");
	}
	public PdfPTable writeItemsData(JSONObject headerjson,JSONObject itemdata) throws Exception 
	{
		PdfPTable table=new PdfPTable(headerjson.length());
		table.setWidthPercentage(100);
		if(headerjson.has("sno"))
			table.addCell(ItemsHeader(headerjson.getString("sno")));
		if(headerjson.has("itname"))
			table.addCell(ItemsHeader(headerjson.getString("itname")));
		if(headerjson.has("MC"))
			table.addCell(ItemsHeader(headerjson.getString("MC")));
		if(headerjson.has("PacForMC"))
			table.addCell(ItemsHeader(headerjson.getString("PacForMC")));
		if(headerjson.has("totpac"))
			table.addCell(ItemsHeader(headerjson.getString("totpac")));
		if(headerjson.has("UOM"))
			table.addCell(ItemsHeader(headerjson.getString("UOM")));
		if(headerjson.has("RateForPac"))
			table.addCell(ItemsHeader(headerjson.getString("RateForPac")));
		if(headerjson.has("amt"))
			table.addCell(ItemsHeader(headerjson.getString("amt")));

		String[] item=itemdata.getString("itname").split("\\_\\$\\_");
		String[] qtys=itemdata.getString("qty").split("\\_\\$\\_");
		String[] amount=itemdata.getString("amt").split("\\_\\$\\_");
		String[] measurement=itemdata.getString("measurement").split("\\_\\$\\_");
		String[] PacForMC=itemdata.getString("PacForMC").split("\\_\\$\\_");
		String[] RateForPac=itemdata.getString("RateForPac").split("\\_\\$\\_");
		//String[] taxs=taxdeatils.replace("_$_",",").split(",");
		double qty=0;
		for(int p=0;p<item.length;p++)
		{
			if(headerjson.has("sno"))
				table.addCell(ItemsCell(p+""));
			if(headerjson.has("itname"))
				table.addCell(ItemsCell(item[p]));
			if(headerjson.has("MC"))
				table.addCell(ItemsCell(qtys[p]));
			if(headerjson.has("PacForMC"))
				table.addCell(ItemsCell(PacForMC[p]));
			if(headerjson.has("totpac"))
				table.addCell(ItemsCell(qtyformat.format(Double.parseDouble(qtys[p])*Double.parseDouble(PacForMC[p]))));
			if(headerjson.has("UOM"))
				table.addCell(ItemsCell(measurement[p]));
			if(headerjson.has("RateForPac"))
				table.addCell(ItemsCell(RateForPac[p]));
			if(headerjson.has("amt"))
				table.addCell(ItemsCell(amount[p]));
			qty+=Double.parseDouble(qtys[p]);
			totamt+=Double.parseDouble(amount[p]);
		}
		if(headerjson.has("sno"))
			table.addCell(ItemsCell(" "));
		if(headerjson.has("itname"))
			table.addCell(detailsCells("Total"));
		if(headerjson.has("MC"))
			table.addCell(detailsCells(qtyformat.format(qty)));
		if(headerjson.has("PacForMC"))
			table.addCell(ItemsCell(""));
		if(headerjson.has("totpac"))
			table.addCell(ItemsCell(""));
		if(headerjson.has("UOM"))
			table.addCell(ItemsCell(""));
		if(headerjson.has("RateForPac"))
			table.addCell(ItemsCell(""));
		if(headerjson.has("amt"))
			table.addCell(detailsCells(amtformat.format(totamt)));
		return table;
	}
	public PdfPTable taxData(String taxes,String discount) throws Exception 
	{
		PdfPTable finaltable=new PdfPTable(2);
		finaltable.setWidthPercentage(98);
		PdfPTable table=new PdfPTable(3);
		String[] taxs=taxes.split("\\_\\$\\_");
		String[] charges={"Vat","ServiceTax","ServiceCharge","PackageingCharge","DelivaryCharge","Discount","Total Amount"};
		boolean check=false;
		for(int i=0;i<charges.length-2;i++)
		{
			if(Double.parseDouble(taxs[i])>0)
			{
				check=true;
				totamt+=Double.parseDouble(taxs[i]);
				table.addCell(nameKeyCells(charges[i]));
				table.addCell(nameKeyCells(""));
				table.addCell(nameKeyCells(taxs[i]));
			}
		}
		if(Double.parseDouble(discount)>0)
		{
			check=true;
			totamt-=Double.parseDouble(discount);
			table.addCell(nameKeyCells(charges[5]));
			table.addCell(nameKeyCells(""));
			table.addCell(nameKeyCells(discount));
		}
		if(check)
		{
			table.addCell(detailsCells(charges[6]));
			table.addCell(nameKeyCells(""));
			table.addCell(detailsCells(totamt+""));
		}
		finaltable.addCell(detailsCells(""));
		table.setHorizontalAlignment(Element.ALIGN_RIGHT);
		table.setWidthPercentage(100);
		PdfPCell cell=new PdfPCell(table);
		cell.setBorder(0);
		finaltable.addCell(cell);
		return finaltable;
	}
	public PdfPCell nameKeyrightCells(String name)throws Exception
	{
		Paragraph para=new Paragraph(name,getCellFont(Color.BLACK));
		para.setAlignment(Element.ALIGN_CENTER);
		PdfPCell cell=new PdfPCell(para);
		cell.setPadding(5.0f);
		cell.setHorizontalAlignment (Element.ALIGN_CENTER);
		cell.setBackgroundColor(Color.WHITE);
		cell.setBorder(0);
		return cell;
	}
	public PdfPCell nameKeyCells(String name)throws Exception
	{
		Paragraph para=new Paragraph(name,getCellFont(Color.BLACK));
		para.setAlignment(Element.ALIGN_LEFT);
		PdfPCell cell=new PdfPCell(para);
		cell.setPadding(5.0f);
		cell.setHorizontalAlignment (Element.ALIGN_LEFT);
		cell.setBackgroundColor(Color.WHITE);
		cell.setBorder(0);
		return cell;
	}
	public PdfPCell billnameCells(String name)throws Exception
	{
		Paragraph para=new Paragraph(name,getBillnameFont(Color.BLACK));
		para.setAlignment(Element.ALIGN_CENTER);
		PdfPCell cell=new PdfPCell(para);
		cell.setPadding(5.0f);
		cell.setHorizontalAlignment (Element.ALIGN_CENTER);
		cell.setBackgroundColor(Color.WHITE);
		cell.setBorder(0);
		return cell;
	}
	public PdfPCell detailsCells(String name)throws Exception
	{
		Paragraph para=new Paragraph(name,getDetailsFont(Color.BLACK));
		para.setAlignment(Element.ALIGN_LEFT);
		PdfPCell cell=new PdfPCell(para);
		cell.setPadding(5.0f);
		cell.setHorizontalAlignment (Element.ALIGN_LEFT);
		cell.setBackgroundColor(Color.WHITE);
		cell.setBorder(0);
		return cell;
	}
	public Font getCellFont(Color color)
	{
		return new Font(Font.TIMES_ROMAN, 10, Font.NORMAL, color);
	}
	public Font getBillnameFont(Color color)
	{
		return new Font(Font.TIMES_ROMAN, 20, Font.BOLD, Color.BLACK);
	}
	public Font getDetailsFont(Color color)
	{
		return new Font(Font.TIMES_ROMAN, 10, Font.BOLD, Color.BLACK);
	}
	public PdfPCell imageCells(String url) throws Exception
	{
		Image image = Image.getInstance(url);
		image.scaleAbsolute(150f, 75f);
		PdfPCell cell=new PdfPCell(image);
		cell.setPadding(5.0f);
		cell.setHorizontalAlignment (Element.ALIGN_RIGHT);
		cell.setBackgroundColor(Color.WHITE);
		cell.setBorder(0);
		return cell;
	}
	public PdfPCell ItemsHeader(String name)throws Exception
	{
		Paragraph para=new Paragraph(name,getDetailsFont(Color.WHITE));
		para.setAlignment(Element.ALIGN_LEFT);
		PdfPCell cell=new PdfPCell(para);
		cell.setPadding(5.0f);
		cell.setHorizontalAlignment (Element.ALIGN_LEFT);
		cell.setBackgroundColor(Color.GRAY);
		cell.setBorder(0);
		return cell;
	}
	public PdfPCell ItemsCell(String name)throws Exception
	{
		Paragraph para=new Paragraph(name,getCellFont(Color.BLACK));
		para.setAlignment(Element.ALIGN_LEFT);
		PdfPCell cell=new PdfPCell(para);
		cell.setPadding(5.0f);
		cell.setHorizontalAlignment (Element.ALIGN_LEFT);
		cell.setBackgroundColor (Color.WHITE);
		cell.setBorder(0);
		return cell;
	}
	public PdfPCell ItemsSizeHeader(String name)throws Exception
	{
		Paragraph para=new Paragraph(name,getDetailsFont(Color.WHITE));
		para.setAlignment(Element.ALIGN_LEFT);
		PdfPCell cell=new PdfPCell(para);
		cell.setPadding(5.0f);
		cell.setHorizontalAlignment (Element.ALIGN_LEFT);
		cell.setBackgroundColor(Color.GRAY);
		cell.setBorder(0);
		return cell;
	}
	public PdfPCell ItemsSizeCell(String name)throws Exception
	{
		Paragraph para=new Paragraph(name,getCellFont(Color.BLACK));
		para.setAlignment(Element.ALIGN_LEFT);
		PdfPCell cell=new PdfPCell(para);
		cell.setPadding(5.0f);
		cell.setHorizontalAlignment (Element.ALIGN_LEFT);
		cell.setBackgroundColor (Color.WHITE);
		cell.setBorder(0);
		return cell;
	}
	private Chunk getChunk(String url)  
    {  
        Chunk Chunk = null;  
        try  
        {  
            Image Imagex = Image.getInstance(url);  
            Imagex.scaleAbsolute(60f, 40f);
            Imagex.setAlignment(Image.LEFT);  
            Chunk = new Chunk(Imagex,0,0);  
        }catch(Exception e)
        	{
        		System.err.println(e.getMessage());
        	}  
        finally{
        	return Chunk;
        	}  
    }  
}
