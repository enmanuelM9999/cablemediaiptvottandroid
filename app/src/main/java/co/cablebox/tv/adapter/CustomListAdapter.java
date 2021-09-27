package co.cablebox.tv.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import co.cablebox.tv.R;
import co.cablebox.tv.socket.Notificaciones;

public class CustomListAdapter extends ArrayAdapter<Notificaciones> {

    private Context mContext;
    private int id;
    private List<Notificaciones> items ;

    public CustomListAdapter(Context context, int textViewResourceId , List<Notificaciones> list )
    {
        super(context, textViewResourceId, list);
        mContext = context;
        id = textViewResourceId;
        items = list ;
    }

    @Override
    public View getView(int position, View v, ViewGroup parent)
    {
        View mView = v ;
        if(mView == null){
            LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            mView = vi.inflate(id, null);
        }

        TextView text = (TextView) mView.findViewById(R.id.tv_notifi);

        if(items.get(position) != null ){
            text.setTextColor(Color.WHITE);
            text.setText(items.get(position).getMessage());
            text.setBackgroundColor(Color.RED);
            int color = Color.argb( 200, 255, 64, 64 );
            text.setBackgroundColor( color );

        }

        return mView;
    }

}