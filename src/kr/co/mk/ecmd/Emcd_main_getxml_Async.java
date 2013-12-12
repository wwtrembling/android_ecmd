package kr.co.mk.ecmd;

import java.io.File;
import java.io.FileOutputStream;
import java.util.HashMap;

import kr.co.mk.ecmd.lib.SendHttp;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class Emcd_main_getxml_Async extends AsyncTask<Void, String, Void>{
	private ProgressDialog dialog;
	private Context context;
	private String _url;
	private boolean _resultFlag=false;
	private int fromWhere=0;
	public Emcd_main_getxml_Async(Ecmd_main ecmd_main, String _url) {	//앱을 실행시켰을 때 실행되는 생성자
		this.context= ecmd_main;
		this._url=_url;
		this.fromWhere=1;
	}

	public Emcd_main_getxml_Async(Ecmd_main_service ecmd_main, String _url) {//서비스가 실행되었을때 실행되는 생성자
		this.context= ecmd_main;
		this._url=_url;
		this.fromWhere=2;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		if(this.fromWhere==1){
			dialog = ProgressDialog.show(context, "Data Loading..","최신 정보를 확인하고 있습니다.", true);
		}
	}

	@Override
	protected Void doInBackground(Void... params) {
		//##############XML 데이터 가지고 오기
		//메뉴 가지고오기
		HashMap<String, String> hm= new HashMap<String, String>();
		hm.put("cmd", "current_data");
		SendHttp shttp= new SendHttp(_url);
		String result_menu=shttp.doSend(hm);
		hm.clear();

		//식단 가지고 오기
		hm.put("cmd", "get_food");
		shttp= new SendHttp(_url);
		String result_food=shttp.doSend(hm);
		
		if(result_menu.equals("")){
			this._resultFlag=false;
		}
		else{
			//##############XML 파일 저장
			String dirPath= context.getFilesDir().getAbsolutePath();
			File file= new File(dirPath);
			File savefile;
			FileOutputStream fos;
			
			//폴더 생성
			if(!file.exists()){file.mkdirs();}
			
			//파일 저장
			try{				
				//일반 식단 메뉴
				savefile= new File(dirPath+"/ecmd_menu.xml");
				fos= new FileOutputStream(savefile);
				fos.write(result_menu.getBytes("utf-8"));
				fos.close();
				
				//선호 식단 가지고 오기
				savefile= new File(dirPath+"/ecmd_food.xml");
				fos= new FileOutputStream(savefile);
				fos.write(result_food.getBytes("utf-8"));
				fos.close();
			}catch (Exception e) {}
			
			this._resultFlag= true;
		}
		return null;
	}
	@Override
	protected void onPostExecute(Void result) {
		super.onPostExecute(result);
		if(this.fromWhere==1){
			dialog.dismiss();
			if(this._resultFlag==true){
				Toast.makeText(context, "데이터가 갱신되었습니다.",Toast.LENGTH_SHORT).show();
			}else{
				Toast.makeText(context, "이번 주 식단이 아직 도착하지 않았습니다.",Toast.LENGTH_SHORT).show();
			}
		}
	}
	

}
