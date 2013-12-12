package kr.co.mk.ecmd;

import java.util.Date;
import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

public class Ecmd_main_service_view extends Activity{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main_service_view);
		Intent intent= getIntent();
		Date cur_date= new Date();
		String time_zone=intent.getStringExtra("time_zone");
		String mtype_str="";
		if(time_zone.equals("M"))mtype_str="아침";
		else if(time_zone.equals("L"))mtype_str="점심";
		else if(time_zone.equals("D"))mtype_str="저녁";
		String msg1=intent.getStringExtra("msg1");
		String msg2=intent.getStringExtra("msg2");
		String msg="\r\n"+(cur_date.getMonth()+1)+"월 "+(cur_date.getDate())+"일 "+mtype_str+" 식단입니다.\r\n\r\n";

		if(msg2.length()>0){
			msg2=msg2.substring(0,msg2.length()-1);
			msg+="*오늘은 선호하시는 음식("+msg2+")이 있네요~*\r\n\r\n";
		}
		
		
		if(msg1.length()>0){
			String new_msg1="";
			String a[]= msg1.split("\\|");
			for(int i=0;i<a.length;i++){
				new_msg1="";
				if(a.length>1){
					new_msg1+="\r\n제 "+(i+1)+" 식단 입니다.\r\n";
				}
				String b[]= a[i].split("\\,");
				for(int j=0;j<b.length;j++){
					new_msg1+=b[j]+"\r\n";
				}
				if(new_msg1.length()>0){
					msg+=new_msg1;
				}
			}
		}
		TextView tv= (TextView)findViewById(R.id.text1);
		tv.setText(msg);
		bindEvent();
		
	}
	
	private void bindEvent(){
		findViewById(R.id.Button1).setOnClickListener(clickListener);
		findViewById(R.id.Button2).setOnClickListener(clickListener);
	}
	
	
	
	View.OnClickListener clickListener= new View.OnClickListener() {
		public void onClick(View v) {
			switch(v.getId()){
				case R.id.Button1:
					finish();
					break;
				case R.id.Button2:
					//실행 여부 확인
					boolean running_chk_flag=false;
					ActivityManager actMng= (ActivityManager)Ecmd_main_service_view.this.getSystemService(Context.ACTIVITY_SERVICE);
					List<RunningAppProcessInfo> list = actMng.getRunningAppProcesses();
					for(RunningAppProcessInfo rap : list){
						//실행한 경우에는 따로 app 을 실행하지 않음
						if(rap.processName.equals("kr.co.mk.ecmd")){
							running_chk_flag=true;
							break;
						}
					}
					if(running_chk_flag==false){
						Intent intent = new Intent(Ecmd_main_service_view.this, Ecmd_main.class);
						startActivity(intent);
					}
					finish();
					break;
			}
		}
	};
}
