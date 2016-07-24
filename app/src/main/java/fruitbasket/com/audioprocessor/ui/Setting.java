package fruitbasket.com.audioprocessor.ui;

import android.preference.PreferenceActivity;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import fruitbasket.com.audioprocessor.R;

/**
 * Created by LiuGuoJie on 2016/7/21.
 */

public class Setting extends PreferenceActivity {
    public static final String VOICE_TYPE="VOICE_TYPE";
    public static final String VOICE_CHOICE="VOICE_CHOICE";
    public static final String HZ="HZ";

    private Toolbar toolbar;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting);
        setContentView(R.layout.setting);

        toolbar = (Toolbar) findViewById(R.id.setting_toolbar);
        toolbar.setTitle("设置");
        toolbar.setTitleTextColor(Color.WHITE);
        Drawable d=getResources().getDrawable(R.drawable.abc_ic_ab_back_mtrl_am_alpha);
        toolbar.setNavigationIcon(d);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        int option = prefs.getInt("HZ", 0);
        // String[] optionText = getResources().getStringArray(R.array.voice_choice);
        Toast.makeText(Setting.this,"option = " + option + ",select : " + option,Toast.LENGTH_SHORT).show();
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }
}
