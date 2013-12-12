package kr.co.mk.ecmd.sax;

public class Ecmd_menu_xml_menu_info_element {
	private static final long serialVersionUID = -4943568370424077038L;
	private String item_detail;
	private String item_startdate;
	private String item_enddate;
	private int item_count;
	
	public String get_item_detail(){return item_detail;}
	public void set_item_detail(String a){item_detail=a;}
	
	public String get_item_startdate(){return item_startdate;}
	public void set_item_startdate(String a){item_startdate=a;}	

	public String get_item_enddate(){return item_enddate;}
	public void set_item_enddate(String a){item_enddate=a;}	

	public int get_item_count(){return item_count;}
	public void set_item_count(int a){item_count=a;}	
}
