package kr.co.mk.ecmd.sax;

import java.io.Serializable;

public class Ecmd_menu_xml_food_element implements Serializable{
	private static final long serialVersionUID = -4943568370424077039L;
	private int food_no;
	private String food_name;

	public void set_food_no(int a){ food_no=a;}
	public int get_food_no(){return food_no;}
	
	public void set_food_name(String a){food_name=a;}
	public String get_food_name(){return food_name;}
}
