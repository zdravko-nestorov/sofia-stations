package bg.znestorov.sofbus24.main;

import android.app.Activity;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import bg.znestorov.sofbus24.utils.Constants;

public class Help extends Activity implements OnClickListener {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_help);

		String helpInfo = "";

		// Getting the information transfered from "VirtualBoardsMapGPS" or
		// "StationTabView" activity
		try {
			helpInfo = (String) getIntent().getSerializableExtra(
					Constants.HELP_ACTIVITY);
		} catch (Exception e) {
			helpInfo = null;
		}

		// Set up the text to the HELP box
		TextView textView = (TextView) findViewById(R.id.map_help_content);
		textView.setText(Html.fromHtml(String.format(helpInfo)));

		// Set up click listeners for all the buttons
		View aboutOKButton = findViewById(R.id.map_help_ok_button);
		aboutOKButton.setOnClickListener(this);
	}

	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.map_help_ok_button:
			finish();
			break;
		}
	}
}