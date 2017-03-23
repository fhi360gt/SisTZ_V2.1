package com.example.sergio.sistz.data;

import android.content.Context;
import android.widget.ArrayAdapter;

import java.util.List;

/**
 * Created by Sergio on 2/22/2016.
 */
public class GEOAdapter extends ArrayAdapter<GEO>{
    private Context context;
    private int layout;
    private List<GEO> data;

    public GEOAdapter(Context context, int resource, List<GEO> objects) {
        super(context, resource, objects);
        this.context = context;
        this.layout = resource;
        this.data = objects;
    }

//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View v = convertView;
//        if (v==null) {
//            LayoutInflater lf = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//            v = lf.inflate(layout, null);
//        }
//        if (data.get(position) != null) {
//            TextView text = (TextView) v.findViewById(R.id.text1);
//            text.setText(data.get(position).get_name1());
//            //Spinner spinner4 = (Spinner) v.findViewById(R.id.spinner4);
//
//
//        }
//        return v;
//    }
}
