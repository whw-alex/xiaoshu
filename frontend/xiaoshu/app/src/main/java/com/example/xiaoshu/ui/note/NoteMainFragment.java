
package com.example.xiaoshu.ui.note;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import android.content.Context;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.*;
import androidx.recyclerview.widget.*;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;


import com.example.xiaoshu.R;

import java.util.*;

enum FileType {
    FOLDER,
    NOTE,
    PLACEHOLDER,
    BUTTON;
}

public class NoteMainFragment extends Fragment{

    RecyclerView mRecyclerView;
    FileRecycleAdapter mRecyclerAdapter;
    ArrayList<File> file_list = new ArrayList<File>();
    ArrayList<File> original = new ArrayList<File>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_main, container, false);
        
        // 生成模拟数据
        // TODO: 从后端获取数据
        for(int i = 0; i < 10; i++) {
            Log.d("add", String.valueOf(i));
            if (i == 0)
                file_list.add(new File("", "", "content_todo", FileType.PLACEHOLDER));
            else if (i < 3)
                file_list.add(new File("", "Folder", "content_todo", FileType.FOLDER));
            else if (i < 9)
                file_list.add(new File("", "Note", "content_todo", FileType.NOTE));
            else
                file_list.add(new File("", "Add", "content_todo", FileType.BUTTON));
        }
        original.addAll(file_list.subList(1, file_list.size()-1));

        // 设置RecyclerView
        mRecyclerView=(RecyclerView)view.findViewById(R.id.recyclerview);
        mRecyclerAdapter = new FileRecycleAdapter(getActivity(), file_list);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        // 设置返回按钮
        View back = view.findViewById(R.id.back_arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: 从后端获取数据
                mRecyclerAdapter.replaceFileList(original);
            }
        });
        return view;
    }

    // TODO:添加多种初始化满足不同文件类型的需求
    static class File {
        public String url;
        public String title;
        public String content;
        public FileType type;

        File(String url_, String title_, String content_, FileType type_) {
            this.url = url_;
            this.title = title_;
            this.content = content_;
            this.type = type_;
        }
    }

    public class FileRecycleAdapter extends RecyclerView.Adapter<FileRecycleAdapter.FileViewHodler> {

        Context context;
        List<File> file_list;
        public FileRecycleAdapter(Context context, List<File> file_list) {
            this.context = context;
            this.file_list = file_list;
        }

        @NonNull
        @Override
        public FileViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            // 根据View种类使用不同layout进行初始化
            View itemView;
            if(viewType == FileType.PLACEHOLDER.ordinal()) {
                itemView = LayoutInflater.from(context).inflate(R.layout.item_placeholder, parent, false);
            }
            else if (viewType == FileType.NOTE.ordinal() || viewType == FileType.FOLDER.ordinal()) {
                itemView = LayoutInflater.from(context).inflate(R.layout.item_file_card, parent, false);
            }
            else {
                itemView = LayoutInflater.from(context).inflate(R.layout.item_file_card, parent, false);
            }

            return new FileViewHodler(itemView, viewType);
        }

        @Override
        public void onBindViewHolder(@NonNull FileViewHodler holder, int position) {
            // 获取当前文件以及文件种类
            File data = file_list.get(position);
            holder.mType = data.type;
            // 根据View种类设置不同的数据
            switch (data.type) {
                case NOTE:
                    holder.mFileTitle.setText(data.title);//获取实体类中的name字段并设置
                    holder.mFileDate.setText(data.content);//获取实体类中的price字段并设置
                    break;
                case FOLDER:
                    Drawable img = ContextCompat.getDrawable(context, R.drawable.folder);
                    holder.mFileImg.setImageDrawable(img);
                    holder.mFileTitle.setText(data.title);//获取实体类中的name字段并设置
                    holder.mFileDate.setText(data.content);//获取实体类中的price字段并设置
                    break;
                case PLACEHOLDER:
                case BUTTON:
                    break;
            }

        }

        @Override
        public int getItemViewType(int position) {
            return file_list.get(position).type.ordinal();
        }

        @Override
        public int getItemCount() {
            return file_list.size();
        }

        public void replaceFileList(ArrayList<File> new_list) {
            int index = file_list.size() - 2;

            while (index > 0) {
                file_list.remove(index);
                notifyItemRemoved(index);
                index--;
            }

            for (File f : new_list) {
                index++;
                file_list.add(index, f);
                notifyItemInserted(index);
            }
        }

        // 自定义Viewhodler
        public class FileViewHodler extends RecyclerView.ViewHolder {
            private ImageView mFileImg;
            private TextView mFileTitle;
            private TextView mFileDate;
            private FileType mType;

            public FileViewHodler(View itemView, int viewType) {
                super(itemView);

                // 根据View种类获取layout节点
                if (viewType == FileType.NOTE.ordinal() || viewType == FileType.FOLDER.ordinal()) {
                    mFileImg = (ImageView) itemView.findViewById(R.id.image);
                    mFileTitle = (TextView) itemView.findViewById(R.id.title);
                    mFileDate = (TextView) itemView.findViewById(R.id.date);
                }

                // 根据View种类设置响应函数
                if (viewType == FileType.FOLDER.ordinal()) {
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Toast.makeText(context, "点击了xxx", Toast.LENGTH_SHORT).show();
                            // TODO: 从后端获取数据
                            ArrayList<File> new_list = new ArrayList<File>();
                            new_list.add(new File("", "subFolder", "content", FileType.FOLDER));
                            new_list.add(new File("", "subNote", "content", FileType.NOTE));

                            replaceFileList(new_list);
                        }
                    });
                }


            }
        }

    }
}
