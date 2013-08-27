package com.ais.sand.radiofm91;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.widget.TabHost;

import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.example.radiofm91.R;

public class MainActivity extends SherlockFragmentActivity {
	private String tag = getClass().getSimpleName();
	private ViewPager mViewPager;
	private TabHost mTabHost;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new RequestTask()
				.execute("http://api.traffy.in.th/apis/getKey.php?appid=abcb6710");
	}

}
