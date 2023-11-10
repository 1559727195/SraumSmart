package com.ipcamera.demo;

import android.os.Bundle;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class BaseActivity extends AppCompatActivity {
	  @Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}
	  
	  public void showToast(String content){
			Toast.makeText(this, content, Toast.LENGTH_SHORT).show();
		}
		public void showToast(int rid){
			Toast.makeText(this, getResources().getString(rid), Toast.LENGTH_LONG).show();
		}
	}
