package com.ais.sand.radiofm91;

import android.app.Activity;
import android.os.Bundle;
import com.example.radiofm91.R;

public class MainActivity extends Activity {
	private String tag = getClass().getSimpleName();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		new RequestTask()
				.execute("http://api.traffy.in.th/apis/getKey.php?appid=abcb6710");

	}

}
