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

        viewHolder.textView1.setText(content.substring(0,content.indexOf("\n\n\n")));
        viewHolder.textView2.setText(content.substring(content.indexOf("\n\n\n") + 3));
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder{
        public TextView textView1;
        public TextView textView2;
        public ViewHolder(View v) {
            super(v);
            textView1 = (TextView) v.findViewById(R.id.card_content1);
            textView2 = (TextView) v.findViewById(R.id.card_content2);
        }
    }
}
