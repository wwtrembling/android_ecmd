package kr.co.mk.ecmd.lib;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;

import android.util.Log;
import android.widget.Toast;

public class SendHttp {
	private URL url;
	private String url_path;
	HttpURLConnection httpURLCon;

	public SendHttp(String url_path) {
		this.url_path = url_path;
	}

	public String getUrl() {
		return url_path;
	}

	public String doSend(HashMap<String, String> hmap) {
		String key = null;
		StringBuffer sb = new StringBuffer();
		Iterator<String> iter = hmap.keySet().iterator();
		while (iter.hasNext()) {
			key = (String) iter.next();
			sb.append(key).append("=").append(hmap.get(key)).append("&");
		}

		PrintWriter pw = null;
		BufferedReader bf = null;
		StringBuilder buff = null;
		String line = "";
		String result = "";
		try {
			url = new URL(url_path);
			httpURLCon = (HttpURLConnection) url.openConnection();
			httpURLCon.setDefaultUseCaches(false);
			httpURLCon.setDoInput(true);
			httpURLCon.setDoOutput(true);
			httpURLCon.setRequestMethod("POST");
			httpURLCon.setRequestProperty("Content-Type",	"application/x-www-form-urlencoded");
			pw = new PrintWriter(new OutputStreamWriter(httpURLCon.getOutputStream(), "UTF-8"));
			pw.write(sb.toString());
			pw.flush();
			
			bf = new BufferedReader(new InputStreamReader(httpURLCon.getInputStream(), "UTF-8"));
			buff = new StringBuilder();
			while ((line = bf.readLine()) != null) {
				result += line;
				buff.append(line);
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return result;
	}
}
