package com.wise.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

/**
 * @author 王庆文
 * 读取表情配置文件
 */
public class FileUtils {
	private List<String> list = null;
	InputStream in = null;
	BufferedReader br = null;
	public List<String> getEmojiFile(Context context) {
		try {
			if(list != null){
				list.clear();
			}
			list = new ArrayList<String>();
			in = context.getResources().getAssets().open("emoji");// �ļ�����Ϊrose.txt
			br = new BufferedReader(new InputStreamReader(in,"UTF-8"));
			String str = null;
			while ((str = br.readLine()) != null) {
				list.add(str);
			}
			in.close();
			br.close();
			in = null;
			br = null;
			return list;
		} catch (IOException e) {
			e.printStackTrace();
			return null;
		}
	}
}
