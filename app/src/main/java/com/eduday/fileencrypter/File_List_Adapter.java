package com.eduday.fileencrypter;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eduday.fileencrypter.Utils.App_Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Random;



public class File_List_Adapter extends BaseAdapter {

    private ViewHolder viewHolder;
    private Context mContext;
    private ArrayList<File_List_Models> file_list_items=new ArrayList<File_List_Models>();
    private ArrayList<File_List_Models> file_list_search=new ArrayList<File_List_Models>();
    private File_List_Models file_list_data;

    private CommuInterface adapterCallback;


    public interface CommuInterface{
        void onMethodCallback(int pos);
        void onViewFile(int pos);
    }

    private int xml_id = R.layout.item_file_list;
    public File_List_Adapter(Context mContext, ArrayList<File_List_Models> folder_list_items, int resourceid) {
        this.mContext = mContext;
        this.file_list_items = folder_list_items;
        this.file_list_search.addAll(file_list_items);
        this.xml_id = resourceid;

        try {
            adapterCallback = ((CommuInterface) mContext);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getCount() {
        return file_list_items.size();
    }

    @Override
    public Object getItem(int i) {
        return file_list_items.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }


    @Override
    public View getView(int position, View view, ViewGroup viewGroup) {
        View v = view;
        if(view==null){
            LayoutInflater vi=(LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v=vi.inflate(xml_id,null);

            viewHolder = new ViewHolder(v);
            v.setTag(viewHolder);
        }else{
            viewHolder = (ViewHolder)view.getTag();
        }

        file_list_data= file_list_items.get(position);


        String newFilename = file_list_data.getFile_title();
        switch (file_list_data.getFile_type()){
            case "application/pdf" :
                newFilename = file_list_data.getFile_title().split(".pdf")[0];
                break;
            case "video/mp4" :
                newFilename = file_list_data.getFile_title().split(".mp4")[0];
                break;
            default:
                if(file_list_data.getFile_title().toLowerCase().contains("pdf")){
                    newFilename = file_list_data.getFile_title().split(".pdf")[0];
                }else if(file_list_data.getFile_title().toLowerCase().contains("mp4")){
                    newFilename = file_list_data.getFile_title().split(".mp4")[0];
                }
                break;
        }
        viewHolder.file_name.setText(newFilename);
        String filesize= App_Constant.convertToStringRepresentation(Long.valueOf(file_list_data.getFile_size()));
        viewHolder.file_size.setText("File Size : "+filesize);

        if(file_list_data.doneOrNor){
            viewHolder.sync_done.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_check_circle_24));
        }else{
            viewHolder.sync_done.setImageDrawable(mContext.getDrawable(R.drawable.ic_baseline_close_24));
        }

//        if(file_list_data.isFolder()){
//
//            // viewHolder.folder_file_count.setVisibility(View.VISIBLE);
//            viewHolder.file_size.setText("Contains : "+file_list_data.getFolderFilelength()+" files");
//            if(Integer.parseInt(file_list_data.getFolderFilelength()) > 0){
//                viewHolder.file_type.setImageResource(R.drawable.folder_fill);
//            }else{
//                viewHolder.file_type.setImageResource(R.drawable.folder_empty);
//            }
//        }else{
            //viewHolder.folder_file_count.setVisibility(View.GONE);
            switch (file_list_data.getFile_type()){
                case "application/pdf" :
                    viewHolder.file_type.setImageResource(R.drawable.pdf_file_type);
                    break;
//                case "application/vnd.ms-powerpoint" :
//                    viewHolder.file_type.setImageResource(R.drawable.ppt_file_type);
//                    break;
//                case "video/mp4" :
//                    viewHolder.file_type.setImageResource(R.drawable.video_file_type);
//                    break;
//                case "audio/mp3" :
//                    viewHolder.file_type.setImageResource(R.drawable.mp3_file_type);
//                    break;
//                default:
//                    if(file_list_data.getFile_title().toLowerCase().contains("pdf")){
//                        viewHolder.file_type.setImageResource(R.drawable.pdf_file_type);
//                    }else if(file_list_data.getFile_title().toLowerCase().contains("mp4")){
//                        viewHolder.file_type.setImageResource(R.drawable.video_file_type);
//                    }else if(file_list_data.getFile_title().toLowerCase().contains("mp3")){
//                        viewHolder.file_type.setImageResource(R.drawable.mp3_file_type);
//                    }else if(file_list_data.getFile_title().toLowerCase().contains("pps") || file_list_data.getFile_title().toLowerCase().contains("ppt")){
//                        viewHolder.file_type.setImageResource(R.drawable.ppt_file_type);
//                    }
//                    break;
            }
            //file_type


            viewHolder.sync_done.setContentDescription(String.valueOf(position));
            viewHolder.sync_done.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = Integer.parseInt(v.getContentDescription().toString());
                    if(file_list_items.get(pos).isDoneOrNor()){
                        adapterCallback.onViewFile(pos);
                    }else{
                        adapterCallback.onMethodCallback(pos);
                    }

                }
            });

            viewHolder.file_name.setContentDescription(String.valueOf(position));
            viewHolder.file_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int pos = Integer.parseInt(v.getContentDescription().toString());
                    if(file_list_items.get(pos).isDoneOrNor()){
                        adapterCallback.onViewFile(pos);
                    }else{
                        adapterCallback.onMethodCallback(pos);
                    }
                }
            });





        return v;
    }

    // Filter Class
    public void filter(String charText) {
        charText = charText.toLowerCase(Locale.getDefault());
        file_list_items.clear();
        if (charText.length() == 0) {
            file_list_items.addAll(file_list_search);
        }
        else
        {
            for (File_List_Models wp : file_list_search)
            {
                if (wp.getFile_title().toLowerCase(Locale.getDefault()).contains(charText))
                {
                    file_list_items.add(wp);
                }
            }
        }
        notifyDataSetChanged();
    }

    class ViewHolder{

        TextView file_name;
        TextView file_size;
        ImageView file_type,sync_done;

        public ViewHolder(View view) {

            file_name = view.findViewById(R.id.tv_file_name);
            file_size = view.findViewById(R.id.tv_file_size);
            file_type = view.findViewById(R.id.tv_file_type);
            sync_done = view.findViewById(R.id.sync_done);
        }
    }
}
