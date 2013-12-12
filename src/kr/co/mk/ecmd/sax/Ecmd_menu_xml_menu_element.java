package kr.co.mk.ecmd.sax;

import java.io.Serializable;
import java.util.ArrayList;

import org.xml.sax.helpers.DefaultHandler;

public class Ecmd_menu_xml_menu_element implements Serializable{
	private static final long serialVersionUID = -4943568370424077037L;
	private String dayfood_md5;
	private String dayfood_date;
	private int dayfood_type;
	private String dayfood_type_name;
	private ArrayList<String> food_name= new ArrayList<String>();
	private ArrayList<Integer> food_no=new ArrayList<Integer>();
	private ArrayList<Integer> food_type= new ArrayList<Integer>();
	private ArrayList<Float> food_duplrate= new ArrayList<Float>();
	
	public void set_dayfood_md5(String a){ dayfood_md5=a;}
	public String get_dayfood_md5(){return dayfood_md5;}
	
	public void set_dayfood_date(String a){dayfood_date=a;}
	public String get_dayfood_date(){return dayfood_date;}
	
	public void set_dayfood_type(int a){dayfood_type=a;}
	public int get_dayfood_type(){return dayfood_type;}
	
	public void set_dayfood_type_name(String a){dayfood_type_name=a;}
	public String get_dayfood_type_name(){return dayfood_type_name;}
	
	public void set_food_name(String a){food_name.add(a);}
	public ArrayList<String> get_food_name(){return food_name;}
	
	public void set_food_no(int a){food_no.add(a);}
	public ArrayList<Integer> get_food_no(){return food_no;}
	
	public void set_food_types(int a){food_type.add(a);}
	public ArrayList<Integer> get_food_types(){return food_type;}

	public void set_food_duplrates(float a){food_duplrate.add(a);}
	public ArrayList<Float> get_food_duplrates(){return food_duplrate;}

}
