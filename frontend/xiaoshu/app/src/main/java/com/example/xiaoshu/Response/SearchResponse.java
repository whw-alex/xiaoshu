package com.example.xiaoshu.Response;

import com.example.xiaoshu.ui.fragments.NoteMainFragment;

import java.util.ArrayList;

public class SearchResponse {
    ArrayList<String> titles;
    ArrayList<String> contents;
    ArrayList<String> dates;
    ArrayList<String> paths;

    ArrayList<NoteMainFragment.File> file_list = new ArrayList<NoteMainFragment.File>();


    public ArrayList<NoteMainFragment.File> get_file_list() {
        if (file_list.size() < titles.size()){
            int n = titles.size();
            for(int i = 0; i < n; i++){
                file_list.add(new NoteMainFragment.File(NoteMainFragment.File.FileType.NOTE,
                        titles.get(i), contents.get(i), dates.get(i)));
            }
        }
        return file_list;
    }

    public ArrayList<String> get_paths() {
        return paths;
    }
}
