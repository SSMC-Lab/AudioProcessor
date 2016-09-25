package fruitbasket.com.audioprocessor.ui;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.widget.SeekBar;
import android.widget.TextView;
import android.view.View;
import android.view.LayoutInflater;

import fruitbasket.com.audioprocessor.R;

/**
 * Created by LiuGuoJie on 2016/7/18.
 */

public class MySeekBarPreference extends DialogPreference implements
        SeekBar.OnSeekBarChangeListener {

    private static final String PREFERENCE_NS = "http://schemas.android.com/apk/res/fruitbasket.com.audioprocessor";
    private static final String ANDROID_NS = "http://schemas.android.com/apk/res/android";

    private static final String ATTR_DEFAULT_VALUE = "defaultValue";
    private static final String ATTR_MIN_VALUE = "minValue";
    private static final String ATTR_MAX_VALUE = "maxValue";

    private int mMinValue;
    private int mMaxValue;
    private int mDefaultValue;
    private int mCurrentValue;

    private SeekBar mSeekBar;
    private TextView mValueText;
    private SharedPreferences sharedPreferences;

    public MySeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        // TODO Auto-generated constructor stub
        mMinValue = attrs
                .getAttributeIntValue(PREFERENCE_NS, ATTR_MIN_VALUE, 0);
        mMaxValue = attrs.getAttributeIntValue(PREFERENCE_NS, ATTR_MAX_VALUE,
                100);
        mDefaultValue = attrs.getAttributeIntValue(ANDROID_NS,
                ATTR_DEFAULT_VALUE, 50);

    }
    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);

        // Inflate layout
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.seek, null);

        // Get current value from settings
        sharedPreferences=getPreferenceManager().getSharedPreferences();
        mCurrentValue = sharedPreferences.getInt(MySeekBarPreference.this.getKey(),getPersistedInt(mDefaultValue));

        // Put minimum and maximum
        ((TextView) view.findViewById(R.id.dialog_MIN)).setText(Integer
                .toString(mMinValue)+"kHZ");
        ((TextView) view.findViewById(R.id.dialog_MAX)).setText(Integer
                .toString(mMaxValue)+"kHZ");

        // Setup SeekBar
        mSeekBar = (SeekBar) view.findViewById(R.id.dialog_seek);
        mSeekBar.setMax(mMaxValue - mMinValue);
        mSeekBar.setProgress(mCurrentValue - mMinValue);
        mSeekBar.setOnSeekBarChangeListener(this);

        // Put current value
        mValueText = (TextView) view.findViewById(R.id.dialog_NOW);
        mValueText.setText(Integer.toString(mCurrentValue)+"kHZ");

        builder.setView(view).setPositiveButton(this.getContext().getString(R.string.Yes),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,
                                int which) {
                sharedPreferences.edit().putInt(MySeekBarPreference.this.getKey(),mCurrentValue).commit();
            }
        }).setNegativeButton(this.getContext().getString(R.string.No),new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog,
                                int which) {

            }
        });
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress,
                                  boolean fromUser) {
        // TODO Auto-generated method stub
        mCurrentValue = progress + mMinValue;
        mValueText.setText(Integer.toString(mCurrentValue)+"kHZ");
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        // TODO Auto-generated method stub

    }
}

