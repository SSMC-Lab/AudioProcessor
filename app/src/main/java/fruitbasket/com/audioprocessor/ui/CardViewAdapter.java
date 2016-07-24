package fruitbasket.com.audioprocessor.ui;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.TextView;
import android.view.View;
import android.view.ViewGroup;
import android.view.LayoutInflater;

import java.util.List;

import fruitbasket.com.audioprocessor.R;


public class CardViewAdapter
        extends RecyclerView.Adapter<CardViewAdapter.ViewHolder>{
    private List<String> list=null;

    public CardViewAdapter(List<String> list) {
        this.list=list;
    }
    @Override
    public ViewHolder onCreateViewHolder( ViewGroup viewGroup, int i )
    {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.cardview, viewGroup, false);
        return new ViewHolder(v);
    }
    @Override
    public void onBindViewHolder( ViewHolder viewHolder, int i )
    {
        String content = list.get(i);
        Log.d("Liar",content);
        viewHolder.textView.setText(content);
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textView;
        public ViewHolder(View v) {
            super(v);
            textView = (TextView) v.findViewById(R.id.card_content);
        }
    }
}
