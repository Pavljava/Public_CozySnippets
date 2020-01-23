package com.scrappers.notepadpp;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomListAdapter extends ArrayAdapter<String> implements Filterable {

    private Activity context;
    private ArrayList<String> maintitle;
    private ArrayList<String> subtitle;
    public static  TextView Subtxtview;


    /// wooohooo warning critical fatal warning >>>this is the constructor should be the same name as the class name
    public CustomListAdapter(Activity context, ArrayList<String> maintitle,ArrayList<String> subtitle) {
        super(context, R.layout.custom_list_xml, maintitle);
        // TODO Auto-generated constructor stub

        this.context = context;
        this.maintitle = maintitle;
        this.subtitle = subtitle;


    }

//    @NotNull
//    public Filter getFilter(){
//
//        final Filter filter= new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//            FilterResults fr=new FilterResults();
//
//            if(constraint == null|| constraint.length()==0){
//
//                fr.values=maintitle;
//                fr.count=maintitle.length;
//            }
//            else {
//
//                lsAdapter.clear();
//                ArrayList<String> list=new ArrayList<>();
//                for(int num=0; num<=maintitle.length-1; ++num){
//                    if(maintitle[num].startsWith(constraint.toString())){
//                        list.add(maintitle[num]);
//                    }
//                }
//                fr.values=list;
//                fr.count=list.size();
//
//
//            }
//
//
//
//
//
//                return fr;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//                try {
//                    if ( results.count == 0 ){
//                        notifyDataSetInvalidated();
//                    } else {
//                        maintitle = (String[]) results.values;
//                        notifyDataSetChanged();
//                    }
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            }
//        };
//        return filter;
//    }

    @Override
    public View getView(final int position, View view, ViewGroup parent) {
        //define the layout inflater & the view to inflate a custom layout from a xml layout file
        LayoutInflater inflater = context.getLayoutInflater();
        final View rowView = inflater.inflate(R.layout.custom_list_xml, null, true);

        //define your components

        TextView Maintxtview=rowView.findViewById(R.id.mainTitleTxt);
         Subtxtview=rowView.findViewById(R.id.subTitleTxt);



        Maintxtview.setText(maintitle.get(position));

        Subtxtview.setText(subtitle.get(position));

        //List View Animation
        Animation animation = AnimationUtils.loadAnimation(context,R.anim.fade_in);
        rowView.startAnimation(animation);


        //finish your function by returning the custom view layout
        return rowView;

    }









}

