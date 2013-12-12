package kr.co.mk.ecmd.sax;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.util.Log;

/*
 * 로컬에 저장되어 있는 ecmd 식단표 데이터를 조회/수정/삭제할때 사용하는 클래스
 * */
public class Ecmd_common_class{
	private String file_dir="/data/data/kr.co.mk.ecmd/files/";
	
	//해당 날짜에 음식 가지고 오기
	public ArrayList<Ecmd_menu_xml_menu_element> getMealData(Calendar dt){
		String dt_ymd=(new SimpleDateFormat("yyyyMMdd").format(dt.getTime())).toString();
		//Log.e("pkch",dt_ymd);
		//##############가지고 있는 메뉴 XML 데이터의 시작날짜를 비교
		String xml_str="";
		XMLReader reader = null;
		File xmlFile;
		ArrayList<Ecmd_menu_xml_menu_element> result_lists=new ArrayList<Ecmd_menu_xml_menu_element>();
		try{
			reader= SAXParserFactory.newInstance().newSAXParser().getXMLReader();
			xmlFile= new File(file_dir+"/ecmd_menu.xml");
			if(xmlFile.exists()==true){
				FileInputStream fis= new FileInputStream(xmlFile);
				BufferedReader br= new BufferedReader(new InputStreamReader(fis));
				String tmp=null;
				while((tmp=br.readLine())!=null){
					xml_str+=tmp;
				}
				fis.close();
				//메뉴 XML 파싱
				Ecmd_menu_xml_menu_parser parser= new Ecmd_menu_xml_menu_parser();
				reader.setContentHandler(parser);
				reader.parse(new InputSource(new ByteArrayInputStream(xml_str.getBytes("utf-8"))));
				ArrayList<Ecmd_menu_xml_menu_element> lists= parser.getMenu();
				for(int i=0;i<lists.size();i++){
					if(lists.get(i).get_dayfood_date().compareTo(dt_ymd)==0){
						result_lists.add(lists.get(i));
					}
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}
		return result_lists;
	}

	
	//해당 시간에 해당하는 식단형태(아침, 점심, 저녁) 가지고 오기
	public String getMealType(Calendar dt){
		String result_str="";
		int h= dt.get(Calendar.HOUR_OF_DAY);
		if(h<8) result_str="B"; //아침
		else if(h<13) result_str="L"; // 점심
		else result_str="D"; //저녁
		return result_str;
	}
	
	//해당식단에 해당하는 식단 가지고 오기
	public String getMealByType(Calendar dt,String meal_type, ArrayList<Ecmd_menu_xml_menu_element> lists){
		String txt="";
		if(meal_type.compareTo("B")==0){
			txt=(new SimpleDateFormat("yyyy/MM/dd").format(dt.getTime())).toString()+" 아침식단\n";
			for(int i=0;i<lists.size();i++){
				if(lists.get(i).get_dayfood_type()==10){
					txt+=lists.get(i).get_food_name()+"\n";
					break;
				}
			}
			
		}else if(meal_type.compareTo("L")==0){
			txt=(new SimpleDateFormat("yyyy/MM/dd").format(dt.getTime())).toString()+" 점심식단\n";
			for(int i=0,j=1 ;i<lists.size();i++){
				if(lists.get(i).get_dayfood_type()==20 || lists.get(i).get_dayfood_type()==30){
					txt+="제 "+j+"식단"+lists.get(i).get_food_name()+"\n\n";
					j++;
				}
			}
			
		}else if(meal_type.compareTo("D")==0){
			txt=(new SimpleDateFormat("yyyy/MM/dd").format(dt.getTime())).toString()+" 저녁식단\n";
			for(int i=0;i<lists.size();i++){
				if(lists.get(i).get_dayfood_type()==40){
					txt+=lists.get(i).get_food_name()+"\n";
					break;
				}
			}
		}
		return txt;
	}
}
