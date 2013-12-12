package kr.co.mk.ecmd;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;

import kr.co.mk.ecmd.sax.Ecmd_common_class;
import kr.co.mk.ecmd.sax.Ecmd_menu_xml_menu_element;
import kr.co.mk.ecmd.sax.Ecmd_menu_xml_menu_info_element;
import kr.co.mk.ecmd.sax.Ecmd_menu_xml_menu_parser;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.webkit.JsPromptResult;
import android.webkit.JsResult;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

/*
 * 시간표 메인 activity
 * 
 * */
public class Ecmd_main extends Activity {
	private WebView wv;
	private final String _url="http://dev.mk.co.kr/ecmd/";
	private final String _xml_url="http://dev.mk.co.kr/ecmd/xml/getXml.php";
	private final Handler handler = new Handler();
	ComponentName mService; // 시작 서비스의 이름

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_webview);

		// 인터넷이 연결이 되어 있는지 확인
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo ni = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
		boolean isWifiConn = ni.isConnected();
		ni = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
		boolean isMobileConn = ni.isConnected();
		// 인터넷이 연결되어 있지 않을 경우
		if (isWifiConn == false && isMobileConn == false) {
			String txt="";
			
			Toast.makeText(this, "인터넷에 연결되어 있지 않습니다.", Toast.LENGTH_SHORT).show();
			LinearLayout ll= (LinearLayout)findViewById(R.id.main_ll);
			ll.removeView(findViewById(R.id.main_webview));

			//오늘날짜에 저장된 식단을 가지고 옴
			Calendar dt= Calendar.getInstance();
			Ecmd_common_class ecc= new Ecmd_common_class();
			ArrayList<Ecmd_menu_xml_menu_element> lists=ecc.getMealData(dt);
			if(lists!=null){
				String meal_type=ecc.getMealType(dt);		//아침/점심/저녁 구분
				txt= ecc.getMealByType(dt, meal_type, lists);	//날씨, 형태를 기준으로 해당 데이터 받아오기
			}
			//텍스트 뷰 동적 생성
			TextView tv =new TextView(this);
			tv.setLayoutParams(new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			tv.setTextColor(Color.parseColor("#000000"));
			tv.setTextSize(20);
			tv.setText(txt);
			ll.addView(tv);
			//FrameLayout fname= (FrameLayout)findViewById(R.id.main_webview);
			//fname.removeView(view)
			//finish();
		} else {
			initWebview(); //웹뷰 초기화
			//선호메뉴 파일이 없을 경우 강제로 받아오도록 수정함
			String dirPath= getFilesDir().getAbsolutePath();
			File xmlFile= new File(dirPath+"/ecmd_food.xml");
			boolean ecmd_food_data_exists=xmlFile.exists();
			chkDateForEcmdMain(ecmd_food_data_exists);// 월요일 일경우에는 데이터를 가지고 옴
			startService(); //서비스 시작
		}	
	}
	
	private void initWebview() {
		wv = (WebView) findViewById(R.id.main_webview);
		wv.setWebChromeClient(new WebChromeClient() {
			// 자바스크립트 alert
			public boolean onJsAlert(WebView view, String url, String msg,
					final android.webkit.JsResult result) {
				new AlertDialog.Builder(Ecmd_main.this)
						.setTitle("Notice")
						.setMessage(msg)
						.setPositiveButton("확인",
								new AlertDialog.OnClickListener() {
									public void onClick(DialogInterface dialog,
											int which) {
										result.confirm();
									}

								}).setCancelable(false).create().show();
				return true;
			}

			// 자바스크립트 prompt
			public boolean onJsPrompt(WebView view, String url, String message,
					String defaultValue, final JsPromptResult result) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(
						Ecmd_main.this).setTitle(message);
				final EditText et = new EditText(Ecmd_main.this);
				et.setSingleLine();
				et.setText(defaultValue);
				builder.setView(et);
				builder.setPositiveButton("확인", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						result.confirm(et.getText().toString());
					}
				}).setNeutralButton("취소", new OnClickListener() {
					public void onClick(DialogInterface dialog, int which) {
						result.cancel();
					}
				}).setCancelable(false).create().show();
				return true;
			}

			// 자바스크립트 confirm
			public boolean onJsConfirm(WebView view, String url,
					String message, final JsResult result) {
				final AlertDialog.Builder builder = new AlertDialog.Builder(
						view.getContext());
				builder.setTitle("Notice").setMessage(message)
						.setPositiveButton("확인", new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								result.confirm();
							}
						}).setNeutralButton("취소", new OnClickListener() {
							public void onClick(DialogInterface dialog,
									int which) {
								result.cancel();
							}
						});
				builder.setOnCancelListener(new OnCancelListener() {
					public void onCancel(DialogInterface dialog) {
						result.cancel();
					}
				}).setCancelable(false).create().show();
				return true;
			}
		});
		wv.getSettings().setJavaScriptEnabled(true);
		wv.addJavascriptInterface(new WebViewBridge(), "exitOfEcmd");// JS calls Adnroid function
		wv.loadUrl(_url);
		wv.setWebViewClient(new WebViewClient());
	}
	
	//최신 식단과 선호음식리스틀를 받기 위해 날짜를 체크
	private void chkDateForEcmdMain(boolean ecmd_food_data_exists){
		//현재날짜에서 월요일을 확인
		Date cur_date= new Date();
		int w= cur_date.getDay();
		long cur_time = System.currentTimeMillis();
		long monday_time= cur_time-(w-1)*3600*24*1000;
		Date mon_date= new Date(monday_time);
		
		String mon_getmonth=((mon_date.getMonth()+1)>10?Integer.toString(mon_date.getMonth()+1):"0"+Integer.toString((mon_date.getMonth()+1)));
		String mon_getdate=((mon_date.getDate())>10?Integer.toString(mon_date.getDate()):"0"+Integer.toString((mon_date.getDate())));
		String mon_date_str= (mon_date.getYear()+1900)+mon_getmonth+mon_getdate;
		
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
		
		//##############날짜가 일치하지 않거나 파일이 존재하지 않을 경우 데이터를 가지고 옴, 혹은 선호데이타가 없을 경우에는 무조건 가지고 옴
		if(flag==false || ecmd_food_data_exists==false){
			Emcd_main_getxml_Async task= new Emcd_main_getxml_Async(this,_xml_url);
			task.execute();
		}
	}

	//서비스 시작
	private void startService(){
		mService= startService(new Intent(this, Ecmd_main_service.class));
	}
	
	// 뒤로 가기
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK) && wv!=null && wv.canGoBack()) {
			wv.goBack();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	// --------------------------------------------------------------------옵션메뉴
	private final int MENU_HOME = 1;
	private final int MENU_SETTING = 2;

	public boolean onCreateOptionsMenu(Menu menu) {
		super.onCreateOptionsMenu(menu);
		menu.add(0, MENU_HOME, Menu.NONE, "HOME");
		menu.add(0, MENU_SETTING, Menu.NONE, "Setting");
		return true;
	}
	
	@Override
	public void finish() {
		super.finish();
	}

	// 옵션메뉴를 선택했을 경우
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch (item.getItemId()) {
			case MENU_HOME: { // 홈으로 갔을 경우
				if(wv!=null){
					wv.loadUrl(_url);
					wv.setWebViewClient(new WebViewClient());
				}
				break;
			}
			case MENU_SETTING: { // 세팅
				startActivityForResult(new Intent(this, Ecmd_main_option.class), 1);
				break;
			}
		}
		return true;
	}

	//Main Intent 복귀시
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		/*
		if(requestCode==1001 && resultCode==RESULT_OK){
			Toast.makeText(this, "로그인이 완료되었습니다.", Toast.LENGTH_SHORT).show();
		}
		*/
	}
	

	// 자바스크립트에서 ANDROID 호출하도록함
	private class WebViewBridge {
		@SuppressWarnings("unused")
		public void goMain() {
			handler.post(new Runnable() {
				public void run() {
					finish();
				}
			});
		}
	}
	
	//Landscape & Portrait 처리
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		super.onConfigurationChanged(newConfig);
		//Log.e("pkch","called!");
	}
}