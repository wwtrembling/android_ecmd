package kr.co.mk.ecmd;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import kr.co.mk.ecmd.sax.Ecmd_menu_xml_food_element;
import kr.co.mk.ecmd.sax.Ecmd_menu_xml_food_parser;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

/*
 * ecmd 식단표 > 옵션 Activity
 * */
public class Ecmd_main_option extends Activity {
	private String _xml_file="ecmd_food.xml";
	private String _option_file="option.conf";
	ArrayList<Ecmd_menu_xml_food_element> lists;
	
	private String morning_flag=""; //조식알림
	private String lunch_flag=""; //중식알림
	private String dinner_flag=""; //석식알림
	private int morning_hour=7; //조식 알림시
	private int morning_min=50; //조식 알림분
	private int lunch_hour=11; //중식 알림시
	private int lunch_min=0; //중식 알림분
	private int dinner_hour=17; //석식 알림시
	private int dinner_min=20; //석식 알림분
	private String favorite_foods=null;
	private boolean favorite_food_clicked[];	//클릭여부 판단함
	static final int TIMER_DIALOG_ID1=1;
	static final int TIMER_DIALOG_ID2=2;
	static final int TIMER_DIALOG_ID3=3;
	private int current_timer_dialog_id=0;
	static final int FAVORITE_RESULT_OK=1111;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_option);

		loadStatus(); //사용자 데이터 가지고오기
		initSetting(); //세팅 초기화
		bindEvent();
	}
	

	//사용자 저장 데이터 불러오기
	private void loadStatus(){
		String dirPath= getFilesDir().getAbsolutePath();

		//######선호음식 xml 데이터 가지고 오기
		XMLReader reader = null;
		File xml_file;
		try {
			reader= SAXParserFactory.newInstance().newSAXParser().getXMLReader();
			xml_file= new File(dirPath+"/"+_xml_file);
			if(xml_file.exists()==true){
				String xml_str="";
				FileInputStream fis= new FileInputStream(xml_file);
				BufferedReader br= new BufferedReader(new InputStreamReader(fis));
				String tmp=null;
				while((tmp=br.readLine())!=null){
					xml_str+=tmp;
				}
				fis.close();
				
				//선호음식 XML 파싱
				Ecmd_menu_xml_food_parser parser= new Ecmd_menu_xml_food_parser();
				reader.setContentHandler(parser);
				reader.parse(new InputSource(new ByteArrayInputStream(xml_str.getBytes("utf-8"))));
				lists= parser.getList();
				favorite_food_clicked= new boolean[lists.size()];
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
		
		//###### OPTION파일에 저장된 내용 가지고 오기
		File option_file= new File(dirPath+"/"+_option_file);
		String options_str=null;
		if(option_file.exists()==true){
			FileInputStream fis;
			try {
				fis = new FileInputStream(option_file);
				BufferedReader br= new BufferedReader(new InputStreamReader(fis));
				String tmp=null;
				StringBuffer sb=new StringBuffer();
				while((tmp=br.readLine())!=null){
					sb.append(tmp);
				}
				fis.close();
				options_str=sb.toString();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			//Log.e("pkch","파일 내용 : "+options_str);
			
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
					favorite_foods=tmp[6];
				}
			}
		}
	}

	
	//선호메뉴 불러오기
	private void initSetting(){
		ToggleButton tbn=null;
		tbn=(ToggleButton)findViewById(R.id.ToggleButton01); //조식알림세팅
		if(morning_flag.equals("true")==true){
			tbn.setChecked(true);
			setChangeHourMinString(R.id.ToggleButton01_txt, morning_hour, morning_min);
		}else{
			tbn.setChecked(false);
			setChangeHourMinString(R.id.ToggleButton01_txt, 0, 0);
		}
		tbn=(ToggleButton)findViewById(R.id.ToggleButton02); //중식알림세팅
		if(lunch_flag.equals("true")==true){
			tbn.setChecked(true);
			setChangeHourMinString(R.id.ToggleButton02_txt, lunch_hour, lunch_min);
		}else{
			tbn.setChecked(false);
			setChangeHourMinString(R.id.ToggleButton02_txt, 0, 0);
		}
		tbn=(ToggleButton)findViewById(R.id.ToggleButton03); //석식알림세팅
		if(dinner_flag.equals("true")==true){
			tbn.setChecked(true);
			setChangeHourMinString(R.id.ToggleButton03_txt, dinner_hour, dinner_min);
		}else{
			tbn.setChecked(false);
			setChangeHourMinString(R.id.ToggleButton03_txt, 0, 0);
		}
		
		/*
		//선호메뉴 세팅
		ListView lv= (ListView)findViewById(R.id.listView1);
		OptionFoodAdapter adapter= new OptionFoodAdapter(R.layout.main_option_item_row, lists);
		lv.setAdapter(adapter);
		*/
	}

	//시간별 데이터 세팅
	private void setChangeHourMinString(int tid, int h, int m){
		String result="";
		if(h==0 && m==0){
			result="";
		}else{
			if(h>12){
				h=h-12;
				result="오후 "+h+"시 ";
			}
			else{
				result="오전 "+h+"시 ";
			}
			
			if(m>0){
				result+=m+" 분";
			}
		}
		TextView tv=null;
		tv=(TextView)findViewById(tid);
		tv.setText(result);
	}
	

	//사용자 데이터 저장
	private void saveStatus(){
		ToggleButton tbn=null;
		
		//조식알림
		tbn= (ToggleButton)findViewById(R.id.ToggleButton01);
		boolean morning_flag= tbn.isChecked();
		String morning_alarm=morning_hour+":"+morning_min;
		
		//중식알림
		tbn= (ToggleButton)findViewById(R.id.ToggleButton02);
		boolean lunch_flag= tbn.isChecked();
		String lunch_alarm=lunch_hour+":"+lunch_min;
		
		//석식알림
		tbn= (ToggleButton)findViewById(R.id.ToggleButton03);
		boolean dinner_flag= tbn.isChecked();
		String dinner_alarm=dinner_hour+":"+dinner_min;

		//선호메뉴정보 : favorite_foods를 그대로 저장
		
		
		//파일저장
		String fstr=morning_flag+"|"+morning_alarm+"|"+lunch_flag+"|"+lunch_alarm+"|"+dinner_flag+"|"+dinner_alarm+"|"+favorite_foods;
		//Log.e("pkch","저장 수행!! : "+fstr);
		//Log.e("pkch","============================");
		
		String dirPath= getFilesDir().getAbsolutePath();
		try {
			BufferedWriter br = new BufferedWriter(new FileWriter(dirPath+"/"+_option_file));
			br.write(fstr);
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//서비스 갱신
		Intent serviceIntent = new Intent(this, Ecmd_main_service.class);
		stopService(serviceIntent);
		startService(serviceIntent);
		
		finish();
	}
	
	
	//이벤트 마우스
	private void bindEvent(){
		findViewById(R.id.ToggleButton01).setOnClickListener(clickListener);
		findViewById(R.id.ToggleButton01_txt).setOnClickListener(clickListener);
		findViewById(R.id.ToggleButton02).setOnClickListener(clickListener);
		findViewById(R.id.ToggleButton02_txt).setOnClickListener(clickListener);
		findViewById(R.id.ToggleButton03).setOnClickListener(clickListener);
		findViewById(R.id.ToggleButton03_txt).setOnClickListener(clickListener);
		findViewById(R.id.option_ok_btn).setOnClickListener(clickListener);
		findViewById(R.id.option_cancle_btn).setOnClickListener(clickListener);
		findViewById(R.id.option_favorite_btn).setOnClickListener(clickListener);
	}
	
	View.OnClickListener clickListener= new View.OnClickListener() {
		public void onClick(View v) {
			ToggleButton tbn=null;
			Intent intent=null;
			switch(v.getId()){
				case R.id.option_ok_btn:
					saveStatus();
					break;
				case R.id.option_cancle_btn:
					finish();
					break;
				case R.id.ToggleButton01:	//조식 알림 버튼
					tbn=(ToggleButton)findViewById(R.id.ToggleButton01);
					if(tbn.isChecked()==true) setChangeHourMinString(R.id.ToggleButton01_txt, morning_hour, morning_min);
					else setChangeHourMinString(R.id.ToggleButton01_txt,0,0);
					break;
				case R.id.ToggleButton01_txt:	//조식 알림시간 textview
					tbn=(ToggleButton)findViewById(R.id.ToggleButton01);
					if(tbn.isChecked()==true){
						current_timer_dialog_id=TIMER_DIALOG_ID1;
						showDialog(TIMER_DIALOG_ID1);
					}
					break;
				case R.id.ToggleButton02:	//중식 알림 버튼
					tbn=(ToggleButton)findViewById(R.id.ToggleButton02);
					if(tbn.isChecked()==true) setChangeHourMinString(R.id.ToggleButton02_txt, lunch_hour, lunch_min);
					else setChangeHourMinString(R.id.ToggleButton02_txt,0,0);
					break;
				case R.id.ToggleButton02_txt:	//중식 알림시간 textview
					tbn=(ToggleButton)findViewById(R.id.ToggleButton02);
					if(tbn.isChecked()==true){
						current_timer_dialog_id=TIMER_DIALOG_ID2;
						showDialog(TIMER_DIALOG_ID2);
					}
					break;
				case R.id.ToggleButton03:	//석식 알림 버튼
					tbn=(ToggleButton)findViewById(R.id.ToggleButton03);
					if(tbn.isChecked()==true) setChangeHourMinString(R.id.ToggleButton03_txt, dinner_hour, dinner_min);
					else setChangeHourMinString(R.id.ToggleButton03_txt,0,0);
					break;
				case R.id.ToggleButton03_txt: //석식 알림시간 textview
					tbn=(ToggleButton)findViewById(R.id.ToggleButton03);
					if(tbn.isChecked()==true){
						current_timer_dialog_id=TIMER_DIALOG_ID3;
						showDialog(TIMER_DIALOG_ID3);
					}
					break;
				case R.id.option_favorite_btn:	//선호메뉴 버튼
					//데이터 담기
					intent= new Intent(Ecmd_main_option.this, Ecmd_main_option_favorite.class);
					intent.putExtra("food_list", lists);
					intent.putExtra("favorite_food_clicked", favorite_food_clicked);
					intent.putExtra("favorite_foods", favorite_foods);
					startActivityForResult(intent, FAVORITE_RESULT_OK);
					break;
			}
		}
	};
	
	//intent 결과값을 받을 경우 여기로 들어옴
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode){
		case FAVORITE_RESULT_OK:
			if(resultCode==RESULT_OK){
				favorite_foods=data.getStringExtra("favorite_foods");//선택한 음식번호를 그대로 저장
			}
		}
	};
	
	//타이머 다이어로그 생성 콜백함수
	protected Dialog onCreateDialog(int id){
		ToggleButton tbn=null;
		switch(id){
			case TIMER_DIALOG_ID1:
				tbn=(ToggleButton)findViewById(R.id.ToggleButton01);
				if(tbn.isChecked()==true) return new TimePickerDialog(this, mTimeSetListener, morning_hour, morning_min, false);
			case TIMER_DIALOG_ID2:
				tbn=(ToggleButton)findViewById(R.id.ToggleButton02);
				if(tbn.isChecked()==true) return new TimePickerDialog(this, mTimeSetListener, lunch_hour, lunch_min, false);
			case TIMER_DIALOG_ID3:
				tbn=(ToggleButton)findViewById(R.id.ToggleButton03);
				if(tbn.isChecked()==true) return new TimePickerDialog(this, mTimeSetListener, dinner_hour, dinner_min, false);
		}
		return null;
	}
	
	//타이머 클래스 선언
	private TimePickerDialog.OnTimeSetListener mTimeSetListener= new TimePickerDialog.OnTimeSetListener() {
		public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
			switch(current_timer_dialog_id){
				case TIMER_DIALOG_ID1:
					morning_hour=hourOfDay;
					morning_min=minute;
					setChangeHourMinString(R.id.ToggleButton01_txt, hourOfDay, minute);
					break;
				case TIMER_DIALOG_ID2:
					lunch_hour=hourOfDay;
					lunch_min=minute;
					setChangeHourMinString(R.id.ToggleButton02_txt, hourOfDay, minute);
					break;
				case TIMER_DIALOG_ID3:
					dinner_hour=hourOfDay;
					dinner_min=minute;
					setChangeHourMinString(R.id.ToggleButton03_txt, hourOfDay, minute);
					break;
			}
		}
	};
}
