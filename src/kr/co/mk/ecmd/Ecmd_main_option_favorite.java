package kr.co.mk.ecmd;

import java.io.Serializable;
import java.util.ArrayList;

import kr.co.mk.ecmd.sax.Ecmd_menu_xml_food_element;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/*
 * ecmd 식단표 > 옵션 > 선호메뉴선택 Activity
 * */
public class Ecmd_main_option_favorite extends Activity{
	private ArrayList<Ecmd_menu_xml_food_element> food_list=null;	//전체 선호메뉴
	private ArrayList<Integer> favorites_list=null; 	//사용자가 선택한 선호메뉴
	private boolean favorite_food_clicked[]=null;	//사용자가 클릭한 선호메뉴
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE); //제목 없애기
		setContentView(R.layout.main_option_favorite_view);
		init();
		bindEvent();
	}
	
	@SuppressLint("NewApi")
	private void init(){
		Intent intent= this.getIntent();
		Serializable tmp=null;
		tmp= intent.getSerializableExtra("food_list");
		food_list= (ArrayList<Ecmd_menu_xml_food_element>)tmp;
		tmp= intent.getSerializableExtra("favorite_food_clicked");
		favorite_food_clicked= (boolean[])tmp;
		
		String favorite_foods=intent.getStringExtra("favorite_foods");
		favorites_list= new ArrayList<Integer>();
		if(favorite_foods!=null && !favorite_foods.isEmpty()){
			String tmps[]=favorite_foods.split(",");
			int tmp_int=0;
			for(int i=0;i<tmps.length;i++){
				tmp_int=Integer.parseInt(tmps[i]);
				favorites_list.add(tmp_int);
			}
		}
		if(food_list==null){
			Toast.makeText(this, "선호 메뉴 목록이 존재하지 않습니다. 자동으로 업데이트 되기까지 잠시 기다려 주시기 바랍니다.", Toast.LENGTH_SHORT).show();
		}else if(!food_list.isEmpty()){
			//선호메뉴 세팅
			ListView lv= (ListView)findViewById(R.id.option_favorite_lv1);
			OptionFoodAdapter adapter= new OptionFoodAdapter(R.layout.main_option_item_row, food_list);
			lv.setAdapter(adapter);
		}
	}
	
	private void bindEvent(){
		findViewById(R.id.option_favorite_ok_btn).setOnClickListener(clicklistener);
		findViewById(R.id.option_favorite_no_btn).setOnClickListener(clicklistener);
	}
	
	View.OnClickListener clicklistener= new OnClickListener() {
		public void onClick(View v) {
			Intent intent= new Intent();
			switch(v.getId()){
				case R.id.option_favorite_ok_btn:
					//값을 저장하고 return 시켜준다

					StringBuilder sb=new StringBuilder();
					Ecmd_menu_xml_food_element el=null;
					CheckBox chkbox=null;
					int tmp_food_no;
					for(int i=0;i<favorites_list.size();i++){
						tmp_food_no=favorites_list.get(i);
						sb.append(favorites_list.get(i)+",");
					}
					String favorite_foods = sb.toString();
					if(favorite_foods.length()>0){favorite_foods=favorite_foods.substring(0,favorite_foods.length()-1);}
					intent.putExtra("favorite_foods", favorite_foods);
					setResult(RESULT_OK, intent);
					finish();
					break;
				case R.id.option_favorite_no_btn:
					finish();
					break;
			}
		}
	};
	

	//선호메뉴 list adapter 확인
	private class OptionFoodAdapter extends BaseAdapter{
		private Context context;
		private LayoutInflater inflater;
		private int layout_id;
		ArrayList<Ecmd_menu_xml_food_element> items;
		
		public OptionFoodAdapter(int layout_id, ArrayList<Ecmd_menu_xml_food_element> items){
			this.context= Ecmd_main_option_favorite.this;
			this.layout_id=layout_id;
			this.inflater= (LayoutInflater)this.context.getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
			this.items=items;
			
			
		}
		
		public int getCount() {
			return this.items.size();
		}

		public Object getItem(int position) {
			return this.items.get(position);
		}

		public long getItemId(int position) {
			return this.items.get(position).get_food_no();
		}

		public View getView(final int position, View v, ViewGroup parent) {
			if(v==null){
				v=inflater.inflate(layout_id, parent, false);
			}
			Ecmd_menu_xml_food_element item= items.get(position);
			TextView tv=(TextView)v.findViewById(R.id.options_item_row_tv1);
			tv.setText(item.get_food_name());
			CheckBox chkbox=(CheckBox)v.findViewById(R.id.options_item_row_checkbox1);
			chkbox.setTag(item.get_food_no());
			
			//설정값 찾음
			for(int i=0;i<favorites_list.size();i++){
				if(favorites_list.get(i) == item.get_food_no()){
					favorite_food_clicked[position]=true;
					break;
				}
			}
			chkbox.setChecked(favorite_food_clicked[position]);
			chkbox.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					CheckBox tmp_chkbox=(CheckBox)v.findViewById(R.id.options_item_row_checkbox1);
					int food_no=(Integer)(tmp_chkbox.getTag());
					if(favorite_food_clicked[position]==true){	//체크를 해제하는 경우
						favorite_food_clicked[position]=false;
						//해당 음식을 제거한다.
						for(int i=0;i<favorites_list.size();i++){
							if(favorites_list.get(i).equals(food_no+"")==true){
								favorites_list.remove(i);
							}
						}
					}
					else {//체크할 경우
						favorite_food_clicked[position]=true;
						//해당 음식을 추가한다.
						favorites_list.add(food_no);
					}
				}
			});
			return v;
		}
	}
}
