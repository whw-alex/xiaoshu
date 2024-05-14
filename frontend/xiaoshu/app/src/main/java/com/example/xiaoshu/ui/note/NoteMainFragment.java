package com.example.xiaoshu.ui.note;

import androidx.fragment.app.Fragment;


import android.content.Context;
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

public class NoteMainFragment extends Fragment{

    RecyclerView mRecyclerView;
    FileRecycleAdapter mRecyclerAdapter;
    ArrayList<File> file_list = new ArrayList<File>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_main, container, false);
        
        // 生成模拟数据
        // TODO: 从后端获取数据
        for(int i = 0; i < 10; i++) {
            Log.d("add", String.valueOf(i));
            File file = new File("", "title", "content");
            file_list.add(file);
        }

        //获取RecyclerView
        mRecyclerView=(RecyclerView)view.findViewById(R.id.recyclerview);
        //创建adapter
        mRecyclerAdapter = new FileRecycleAdapter(getActivity(), file_list);
        //给RecyclerView设置adapter
        mRecyclerView.setAdapter(mRecyclerAdapter);
        //设置layoutManager,可以设置显示效果，是线性布局、grid布局，还是瀑布流布局
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity(), LinearLayoutManager.VERTICAL, false));
        //设置item的分割线
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(),DividerItemDecoration.VERTICAL));
        //RecyclerView中没有item的监听事件，需要自己在适配器中写一个监听事件的接口。参数根据自定义
//        mRecyclerAdapter.setOnItemClickListener(new FileRecycleAdapter.OnItemClickListener() {
//            @Override
//            public void OnItemClick(View view, GoodsEntity data) {
//                //此处进行监听事件的业务处理
//                Toast.makeText(getActivity(),"我是item",Toast.LENGTH_SHORT).show();
//            }
//        });
        return view;
    }


    static class File {
        public String url;
        public String title; // 标题
        public String content; //内容

        File(String url_, String title_, String content_) {
            this.url = url_;
            this.title = title_;
            this.content = content_;
        }
    }

    public class FileRecycleAdapter extends RecyclerView.Adapter<FileRecycleAdapter.FileViewHodler> {

        Context context;
        List<File> file_list;
        //创建构造函数
        public FileRecycleAdapter(Context context, List<File> file_list) {
            //将传递过来的数据，赋值给本地变量
            this.context = context;//上下文
            this.file_list = file_list;//实体类数据ArrayList
        }

        @NonNull
        @Override
        public FileViewHodler onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            //创建自定义布局
            View itemView = View.inflate(context, R.layout.item_file_card, null);
            return new FileViewHodler(itemView);
        }

        @Override
        public void onBindViewHolder(@NonNull FileViewHodler holder, int position) {
            //根据点击位置绑定数据
            File data = file_list.get(position);
//            holder.mFileImg.setImageURI();
            holder.mFileTitle.setText(data.title);//获取实体类中的name字段并设置
            holder.mFileDate.setText(data.content);//获取实体类中的price字段并设置
        }

        @Override
        public int getItemCount() {
            return file_list.size();
        }

        //自定义viewhodler
        class FileViewHodler extends RecyclerView.ViewHolder {
            private ImageView mFileImg;
            private TextView mFileTitle;
            private TextView mFileDate;

            public FileViewHodler(View itemView) {
                super(itemView);
                mFileImg = (ImageView) itemView.findViewById(R.id.image);
                mFileTitle = (TextView) itemView.findViewById(R.id.title);
                mFileDate = (TextView) itemView.findViewById(R.id.date);
                //点击事件放在adapter中使用，也可以写个接口在activity中调用
                //方法一：在adapter中设置点击事件
//                itemView.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View v) {
//                        //可以选择直接在本位置直接写业务处理
//                        //Toast.makeText(context,"点击了xxx",Toast.LENGTH_SHORT).show();
//                        //此处回传点击监听事件
//                        if (onItemClickListener != null) {
//                            onItemClickListener.OnItemClick(v, goodsEntityList.get(getLayoutPosition()));
//                        }
//                    }
//                });

            }
        }
    }
}
