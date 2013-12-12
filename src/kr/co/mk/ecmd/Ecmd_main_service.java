package kr.co.mk.ecmd;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import kr.co.mk.ecmd.sax.Ecmd_menu_xml_food_element;
import kr.co.mk.ecmd.sax.Ecmd_menu_xml_food_parser;
import kr.co.mk.ecmd.sax.Ecmd_menu_xml_menu_element;
import kr.co.mk.ecmd.sax.Ecmd_menu_xml_menu_info_element;
import kr.co.mk.ecmd.sax.Ecmd_menu_xml_menu_parser;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;

/*
 * 
 * */
public class Ecmd_main_service extends Service implements Runnable {
	private final String _xml_url="http://dev.mk.co.kr/ecmd/xml/getXml.php";
	private String _menu_file="ecmd_menu.xml";
	private String _food_file="ecmd_food.xml";
	private String _option_file="option.conf";
	private int mStartId; // 시작 ID
	private Handler mHandler; // 서비스에 대한 스레드에 연결된 Handler. 타이머 이용한 반복 처리시 사용.
	private boolean mRunning; // 서비스 동작여부 flag
	private static final int TIMER_PERIOD = 1000*60; // 타이머 설정(1분) by pkch
	//private static final int TIMER_PERIOD = 1000; // 타이머 설정(1분) by pkch
	private ArrayList<Ecmd_menu_xml_menu_element> menu_arr; //식단 정보
	private Ecmd_menu_xml_menu_info_element menu_info;	// 식단 정보
	private ArrayList<Ecmd_menu_xml_food_element> food_arr;	//선호음식 정보
	
	//이하는 사용자가 입력한 값
	private String morning_flag=""; //조식알림
	private String lunch_flag=""; //중식알림
	private String dinner_flag=""; //석식알림
	private int morning_hour=7; //조식 알림시
	private int morning_min=50; //조식 알림분
	private int lunch_hour=11; //중식 알림시
	private int lunch_min=0; //중식 알림분
	private int dinner_hour=17; //석식 알림시
	private int dinner_min=20; //석식 알림분
	private ArrayList<String> chk_val1=new ArrayList<String>();  //선호메뉴선택배열

	// 서비스를 생성할 때 호출
	public void onCreate() {
		super.onCreate();
		mHandler = new Handler();
		mRunning = false;
		init();
	}
	
	//시작
	private void init(){
		String dirPath= getFilesDir().getAbsolutePath(); //XML 파일을 저장할 절대 경로
		String result="";
		
		/*
		 * 데이터 가지고 오기
		 * -식단표 XML 데이터와 선호메뉴로 지정된 음식 XML 데이터를 받아옴
		 * -식단 정보가 없을 경우 알림을 울리지 않는다.
		 */
		XMLReader reader = null;
		result=getStringFromFile(dirPath+"/"+_menu_file);
		if(result.equals("")==false){
			//식단, 식단정보 가지고 오기
			try {
				reader= SAXParserFactory.newInstance().newSAXParser().getXMLReader();
				Ecmd_menu_xml_menu_parser parser= new Ecmd_menu_xml_menu_parser();
				reader.setContentHandler(parser);
				reader.parse(new InputSource(new ByteArrayInputStream(result.getBytes("utf-8"))));
				menu_arr= parser.getMenu();
				menu_info=parser.getInfo();
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			}
			

			result=getStringFromFile(dirPath+"/"+_food_file);
			if(result.equals("")==false){
				//선호음식정보 가지고 오기
				try {
					Ecmd_menu_xml_food_parser parser= new Ecmd_menu_xml_food_parser();
					reader.setContentHandler(parser);
					reader.parse(new InputSource(new ByteArrayInputStream(result.getBytes("utf-8"))));
					food_arr= parser.getList();
				} catch (UnsupportedEncodingException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				} catch (SAXException e) {
					e.printStackTrace();
				}
			}

			String options_str=getStringFromFile(dirPath+"/"+_option_file);
			//###### OPTION파일에 저장된 내용 파싱
			if(options_str.length()>0){
				String tmp[]= options_str.split("\\|");
				morning_flag=tmp[0];
				if(tmp.length>3 && tmp[1].equals("")==false){
					String tmps[]=tmp[1].split("\\:");
					if(tmps.length==2){
						morning_hour=Integer.parseInt(tmps[0]);
						morning_min=Integer.parseInt(tmps[1]);
					}
				}
				lunch_flag=tmp[2];
				if(tmp.length>4 && tmp[3].equals("")==false){
					String tmps[]=tmp[3].split("\\:");
					if(tmps.length==2){
						lunch_hour=Integer.parseInt(tmps[0]);
						lunch_min=Integer.parseInt(tmps[1]);
					}
				}
				dinner_flag=tmp[4];
				if(tmp.length>5 && tmp[5].equals("")==false){
					String tmps[]=tmp[5].split("\\:");
					if(tmps.length==2){
						dinner_hour=Integer.parseInt(tmps[0]);
						dinner_min=Integer.parseInt(tmps[1]);
					}
				}
				if(tmp.length>6){
					String tmps[]=tmp[6].split(",");
					for(int i=0;i<tmps.length;i++){
						if(tmps[i]!=null && tmps[i]!="" && !tmps[i].equals("null")) {
							chk_val1.add(tmps[i]);
						}
					}
				}
			}
		}
	}
	
	// 서비스의 종료시 호출
	public void onDestroy() {
		super.onDestroy();
		Log.e("pkch","Service Destroied!");
		// onDestroy가 호출되어 서비스가 종료되어도
		// postDelayed는 바로 정지되지 않고 다음 번 run 메소드를 호출.
		mRunning = false;
	}
	
	/* 서비스 시작할 때 호출. background에서의 처리가 시작됨.
	 * startId : 서비스 시작요구 id. stopSelf에서 종료할 때 사용.
	 * onStart는 여러번 호출될 수 있기 때문에 식별자로 사용.
	 */
	public void onStart(Intent intent, int startId) {
		Log.e("pkch", "Service start!");
		mStartId=startId;
		if (!mRunning) {
			// this : 서비스 처리의 본체인 run 메소드. Runnable 인터페이스를 구현 필요.
			// postDelayed : 일정시간마다 메소드 호출
			mHandler.postDelayed(this, TIMER_PERIOD);
			mRunning = true;
		}
	}
	
	//왼쪽부터 0으로 채우는 함수
	private String pad(int a){
		String result="";
		if(0<a && a<10){result="0"+a;}
		else {result=""+a;}
		return result;
	}
	
	//서비스 실행
	public void run() {
		if (!mRunning) {// 서비스 종료 요청이 들어온 경우 그냥 종료
			Log.e("pkch", "Already running");
			return;
		}else {
			Log.e("pkch", "Running");
			chkDate();//월요일인지 체크하여 데이터를 받아옴
			
			//데이터가 존재하는지 확인(없을 경우에는 pass~~)
			if(menu_arr==null || menu_arr.size()==0){return;}
			
			//0.option 에 저장된 시간들과 시간대를 가지고 옴
			//1.option 에 저장된 시간들과 현재시간을 비교함
			//2,틀릴경우에는 1분후에 다시 수행
			//3,맞을 경우에는 오늘 날짜에 해당하는 식단을 가지고 옴
			//4.식단이 option에 저장된 시간대와 일치하는지 확인후 출력

			//options에 저장된 시간들과 현재 시간을 비교함
			GregorianCalendar today= new GregorianCalendar();
			int dow=today.get(today.DAY_OF_WEEK);
			//월요일부터 금요일까지만 제한시킴
			if(2<=dow && dow<=6){
				//설정에 현재 시간이 어디에 저장되어 있는지 확인
				String time_zone="";		//현재시간이 아침(B)/점심(L)/저녁(D) 중 어느시간인지 확인
				int cur_hour= today.get(today.HOUR_OF_DAY);
				int cur_min= today.get(today.MINUTE);
				if(morning_hour==cur_hour && morning_min==cur_min){time_zone="M";}
				else if(lunch_hour==cur_hour && lunch_min==cur_min){time_zone="L";}
				else if(dinner_hour==cur_hour && dinner_min==cur_min){time_zone="D";}

				//설정에 데이터 출력여부 확인
				boolean pass_chk_flag=false;
				if(time_zone.equals("M") && morning_flag.equals("true")) pass_chk_flag=true;
				else if(time_zone.equals("L") && lunch_flag.equals("true")) pass_chk_flag=true;
				else if(time_zone.equals("D") && dinner_flag.equals("true")) pass_chk_flag=true;
				
				//time_zone="M";//###################### remove!!!!!!!!!
				if(time_zone.equals("")==false){
					//오늘날짜에 저장된 식단을 가지고 옴
					String today_str=today.get(today.YEAR)+""+pad(today.get(today.MONTH)+1)+""+pad(today.get(today.DAY_OF_MONTH));
					ArrayList<Ecmd_menu_xml_menu_element> menu_tmp_arr= new ArrayList<Ecmd_menu_xml_menu_element>(); // 임시 저장소
					for(int i=0;i<menu_arr.size();i++){
						if(menu_arr.get(i).get_dayfood_date().equals(today_str)){ //오늘날짜일 경우!
							if(time_zone.equals("M") && menu_arr.get(i).get_dayfood_type()==10){
								menu_tmp_arr.add(menu_arr.get(i));
							}else if(time_zone.equals("L") && (menu_arr.get(i).get_dayfood_type()==20 || menu_arr.get(i).get_dayfood_type()==30)){
								menu_tmp_arr.add(menu_arr.get(i));
							}else if(time_zone.equals("D") && menu_arr.get(i).get_dayfood_type()==40){
								menu_tmp_arr.add(menu_arr.get(i));
							}
						}
					}
					
					StringBuffer msg1=new StringBuffer();
					StringBuffer msg2=new StringBuffer();
					//식단 확인
					for(int i=0;i<menu_tmp_arr.size();i++){
						for(int j=0;j<menu_tmp_arr.get(i).get_food_name().size();j++){
							int food_no=menu_tmp_arr.get(i).get_food_no().get(j);
							msg1.append(menu_tmp_arr.get(i).get_food_name().get(j)+",");
							//선호식단 확인
							for(int k=0;k<chk_val1.size();k++){
								//Log.e("pkch",food_no+"");
								//Log.e("pkch",chk_val1.get(k)+"");
								if(Integer.parseInt(chk_val1.get(k))==food_no){
									//Log.e("pkch","("+menu_tmp_arr.get(i).get_food_name().get(j)+", "+food_no+")선호음식!!");
									msg2.append(menu_tmp_arr.get(i).get_food_name().get(j)+",");
									break;
								}
							}
							//Log.e("pkch","-----------");
						}
						msg1.append("|");
					}

					//최종 메시지를 출력함
					if(msg1.toString().equals("")==false){
						Intent popupIntent= new Intent(this, Ecmd_main_service_view.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						popupIntent.putExtra("time_zone", time_zone);
						popupIntent.putExtra("msg1", msg1.toString());
						popupIntent.putExtra("msg2", msg2.toString());
						this.startActivity(popupIntent);
					}
				}
			}
			
			// 다음 작업을 다시 요구1분마다 실행함
			mHandler.postDelayed(this, TIMER_PERIOD);
		}
	}

	//최신 식단과 선호음식리스틀를 받기 위해 날짜를 체크 >> 월요일일 경우에는 데이타를 받으러 감
	private void chkDate(){
		//현재날짜에서 월요일을 확인
		Date cur_date= new Date();
		int w= cur_date.getDay();
		long cur_time = System.currentTimeMillis();
		long monday_time= cur_time-(w-1)*3600*24*1000;
		Date mon_date= new Date(monday_time);
		
		String tmp_getmonth=((mon_date.getMonth()+1)>10?Integer.toString((mon_date.getMonth()+1)):"0"+Integer.toString((mon_date.getMonth()+1)));
		String tmp_getdate=((mon_date.getDate())>10?Integer.toString(mon_date.getDate()):"0"+Integer.toString((mon_date.getDate())));
		String mon_date_str=(mon_date.getYear()+1900)+""+tmp_getmonth+""+tmp_getdate;
		
		//##############가지고 있는 메뉴 XML 데이터의 시작날짜를 비교
		String xml_str="";
		boolean flag=false;
		int readcount=0;
		String dirPath= getFilesDir().getAbsolutePath();
		XMLReader reader = null;
		File xmlFile;
		try{
			reader= SAXParserFactory.newInstance().newSAXParser().getXMLReader();
			xmlFile= new File(dirPath+"/ecmd_menu.xml");
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
				Ecmd_menu_xml_menu_info_element info= parser.getInfo();
				if(mon_date_str.equals(info.get_item_startdate())){
					flag=true;
				}
			}
		}catch(IOException e){
			e.printStackTrace();
		} catch (SAXException e) {
			e.printStackTrace();
		} catch (ParserConfigurationException e) {
			e.printStackTrace();
		}

		//##############날짜가 일치하지 않거나 파일이 존재하지 않을 경우 데이터를 가지고 옴
		if(flag==false){
			Log.e("pkch","데이터가 없어서 받으러 감");
			Emcd_main_getxml_Async task= new Emcd_main_getxml_Async(this,_xml_url);
			task.execute();
		}else {
			init();
		}
	}
	
	// 원격 메소드 호출을 위해 사용
	// 메서드 호출을 제공하지 않으면 null을 반환
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}
	
	private String getStringFromFile(String fpath){
		String xml_str="";
		XMLReader reader = null;
		File xml_file= new File(fpath);
		if(xml_file.exists()==true){
			try {
				reader= SAXParserFactory.newInstance().newSAXParser().getXMLReader();
				if(xml_file.exists()==true){
					FileInputStream fis= new FileInputStream(xml_file);
					BufferedReader br= new BufferedReader(new InputStreamReader(fis));
					String tmp=null;
					while((tmp=br.readLine())!=null){
						xml_str+=tmp;
					}
					fis.close();
				}
			} catch (SAXException e) {
				e.printStackTrace();
			} catch (ParserConfigurationException e) {
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return xml_str;
	}
}
