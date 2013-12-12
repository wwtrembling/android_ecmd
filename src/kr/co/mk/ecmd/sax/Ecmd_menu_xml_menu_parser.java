package kr.co.mk.ecmd.sax;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class Ecmd_menu_xml_menu_parser extends DefaultHandler{
	private String tmps;
	private int tmpi;
	private float tmpf;
	private StringBuilder sb;
	
	private ArrayList<Ecmd_menu_xml_menu_element> lists;
	private Ecmd_menu_xml_menu_element el;
	private Ecmd_menu_xml_menu_info_element info;
	
	//문서 읽기가 시작했을 경우
	public void startDocument() throws SAXException{
		super.startDocument();
	}
	
	//문서 읽기가 끝났을 경우
	public void endDocument() throws SAXException{
		super.endDocument();
	}
	
	//Tag를 만났을 경우
	public void startElement(String uri, String localName, String qName, Attributes attr) throws SAXException{
		super.startElement(uri, localName, qName, attr);
		if("items".equals(qName)){
			sb= new StringBuilder();
			lists= new ArrayList<Ecmd_menu_xml_menu_element>();
			info= new Ecmd_menu_xml_menu_info_element();
		}else if("item_detail".equals(qName)){
			
		}else if("item_startdate".equals(qName)){
			
		}else if("item_enddate".equals(qName)){
			
		}else if("item_count".equals(qName)){
			
		}else if("item_list".equals(qName)){
			
		}else if("item".equals(qName)){
			el= new Ecmd_menu_xml_menu_element();
		}
		
	}
	
	//Tag 문이 끝났을 경우
	public void endElement(String uri, String localName, String qName) throws SAXException{
		super.endElement(uri, localName, qName);
		if("items".equals(qName)){
			sb= new StringBuilder();
		}else if("item_detail".equals(qName)){
			tmps=sb.toString();
			info.set_item_detail(tmps);
		}else if("item_startdate".equals(qName)){
			tmps=sb.toString();
			info.set_item_startdate(tmps);
			
		}else if("item_enddate".equals(qName)){
			tmps=sb.toString();
			info.set_item_enddate(tmps);
			
		}else if("item_count".equals(qName)){
			tmpi=Integer.parseInt(sb.toString());
			info.set_item_count(tmpi);
			
		}else if("item_list".equals(qName)){
			
		}
		else if("dayfood_md5".equals(qName)){
			tmps=sb.toString();
			el.set_dayfood_md5(tmps);
		}
		else if("dayfood_date".equals(qName)){
			tmps=sb.toString();
			el.set_dayfood_date(tmps);
		}
		else if("dayfood_type".equals(qName)){
			tmpi=Integer.parseInt(sb.toString());
			el.set_dayfood_type(tmpi);
		}
		else if("dayfood_type_name".equals(qName)){
			tmps=sb.toString();
			el.set_dayfood_type_name(tmps);
		}
		else if("food_names".equals(qName)){
			tmps=sb.toString();
			String tmp2[]= tmps.split(",");
			for(int i=0;i<tmp2.length;i++){
				if(tmp2[i].equals(null)==false){
					el.set_food_name(tmp2[i]);
				}
			}
		}
		else if("food_nos".equals(qName)){
			tmps=sb.toString();
			String tmp2[]= tmps.split(",");
			for(int i=0;i<tmp2.length;i++){
				tmpi=Integer.parseInt(tmp2[i]);
				if(tmpi>0){
					el.set_food_no(tmpi);
				}
			}
		}
		else if("food_types".equals(qName)){
			tmps=sb.toString();
			String tmp2[]= tmps.split(",");
			for(int i=0;i<tmp2.length;i++){
				tmpi=Integer.parseInt(tmp2[i]);
				if(tmpi>0){
					el.set_food_types(tmpi);
				}
			}
		}
		else if("food_duplrates".equals(qName)){
			tmps=sb.toString();
			String tmp2[]= tmps.split(",");
			for(int i=0;i<tmp2.length;i++){
				tmpf=Float.parseFloat(tmp2[i]);
				if(tmp2[i].equals(null)==false){
					el.set_food_duplrates(tmpf);
				}
			}
		}
		else if("item".equals(qName)){
			lists.add(el);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		sb.setLength(0);
		sb.append(ch,start,length);
	}
	
	public Ecmd_menu_xml_menu_info_element getInfo(){
		return this.info;
	}
	
	public ArrayList<Ecmd_menu_xml_menu_element> getMenu(){
		return this.lists;
	}
	
	
}
