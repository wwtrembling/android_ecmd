package kr.co.mk.ecmd.sax;

import org.xml.sax.helpers.DefaultHandler;

public class Ecmd_menu_xml_food_info_element extends DefaultHandler {
	private static final long serialVersionUID = -4943568370424077040L;
	private String item_detail;
	private String item_startdate;
	private int item_count;

	public void set_item_detail(String a){this.item_detail=a;}
	public String get_item_detail(){return this.item_detail;}
	
	public void set_item_startdate(String a){this.item_startdate=a;}
	public String get_item_startdate(){return this.item_startdate;}
	
	public void set_item_count(int a){this.item_count=a;}
	public int get_item_count(){return item_count;}
	
}
