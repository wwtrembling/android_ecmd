package kr.co.mk.ecmd.sax;

import java.util.ArrayList;

import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import android.util.Log;

public class Ecmd_menu_xml_food_parser extends DefaultHandler{
	private String tmps;
	private int tmpi;
	private StringBuilder sb;
	
	private Ecmd_menu_xml_food_element el;
	private Ecmd_menu_xml_food_info_element info_el;
	private ArrayList<Ecmd_menu_xml_food_element> lists;
	
	
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
			lists= new ArrayList<Ecmd_menu_xml_food_element>();
			info_el= new Ecmd_menu_xml_food_info_element();
		}else if("st_date".equals(qName)){
		}else if("item_count".equals(qName)){
		}else if("item_list".equals(qName)){			
		}else if("item".equals(qName)){
			el= new Ecmd_menu_xml_food_element();
		}
	}
	
	//Tag 문이 끝났을 경우
	public void endElement(String uri, String localName, String qName) throws SAXException{
		super.endElement(uri, localName, qName);
		if("items".equals(qName)){
		}else if("item_detail".equals(qName)){
			tmps=sb.toString();
			info_el.set_item_detail(tmps);
		}else if("item_startdate".equals(qName)){
			tmps=sb.toString();
			info_el.set_item_startdate(tmps);
		}else if("item_count".equals(qName)){
			tmpi=Integer.parseInt(sb.toString());
			info_el.set_item_count(tmpi);
		}else if("item_list".equals(qName)){
			
		}else if("item".equals(qName)){
			this.lists.add(el);
		}else if("food_no".equals(qName)){
			tmpi=Integer.parseInt(sb.toString());
			el.set_food_no(tmpi);
		}else if("food_name".equals(qName)){
			tmps=sb.toString();
			el.set_food_name(tmps);
		}
	}
	
	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		super.characters(ch, start, length);
		sb.setLength(0);
		sb.append(ch,start,length);
	}
	
	public ArrayList<Ecmd_menu_xml_food_element> getList(){
		return this.lists;
	}
	
	
	public Ecmd_menu_xml_food_info_element getInfo(){
		return this.info_el;
	}
}
