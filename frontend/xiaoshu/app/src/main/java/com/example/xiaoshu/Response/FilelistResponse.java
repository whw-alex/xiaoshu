package com.example.xiaoshu.Response;

import com.example.xiaoshu.ui.fragments.NoteMainFragment.File;

import java.util.ArrayList;

public class FilelistResponse {
    ArrayList<String> labels;
    ArrayList<String> titles;
    ArrayList<String> contents;
    ArrayList<String> dates;
    ArrayList<File> file_list = new ArrayList<File>();

//    FilelistResponse(ArrayList<Integer> ints) {
//        System.out.println("here");
//        for(Integer i : ints) {
//            Log.d("array", Integer.toString(i));
//        }
//    }

    public ArrayList<File> get_file_list() {
        if (file_list.size() < labels.size()){
            int n = labels.size();
            for(int i = 0; i < n; i++){
                File.FileType type = File.FileType.FOLDER;
                if (labels.get(i).compareTo("note") == 0)
                    type = File.FileType.NOTE;
                file_list.add(new File(type, titles.get(i), contents.get(i), dates.get(i)));
            }
        }

        return file_list;
    }
}
