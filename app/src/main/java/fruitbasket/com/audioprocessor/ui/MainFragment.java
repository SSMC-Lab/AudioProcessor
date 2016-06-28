package fruitbasket.com.audioprocessor.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import fruitbasket.com.audioprocessor.R;

/**
 * Created by Study on 21/06/2016.
 */
public class MainFragment extends Fragment {

    public MainFragment(){}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.main_fragment,container,false);
        return rootView;
    }
}
