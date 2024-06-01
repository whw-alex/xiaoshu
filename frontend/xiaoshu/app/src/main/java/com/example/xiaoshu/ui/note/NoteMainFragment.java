
package com.example.xiaoshu.ui.note;

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import static android.content.Context.MODE_PRIVATE;
import android.content.ContextWrapper;
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


import com.example.xiaoshu.API;
import com.example.xiaoshu.R;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import com.example.xiaoshu.NoteDetailActivity;
import com.example.xiaoshu.Request.FileListRequest;
import com.example.xiaoshu.Request.LoginRequest;
import com.example.xiaoshu.Request.AddFileRequest;
import com.example.xiaoshu.Response.AddFileResponse;
import com.example.xiaoshu.Response.FilelistResponse;
import com.example.xiaoshu.Response.LoginResponse;
import com.example.xiaoshu.StartActivity;
import com.getbase.floatingactionbutton.*;


import android.content.Intent;

import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NoteMainFragment extends Fragment{

    View mPage;
    RecyclerView mRecyclerView;
    FileRecycleAdapter mRecyclerAdapter;
    ArrayList<File> file_list = new ArrayList<File>();
    ArrayList<File> original = new ArrayList<File>();
    AlertDialog inputDialogForNote, inputDialogForFolder;
    EditText editTextForNote, editTextForFolder;

    Integer usrid;
    String curPath = "root";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_note_main, container, false);
        mPage = view;

        // 设置RecyclerView
        file_list.add(new File(File.FileType.PLACEHOLDER, "", "", ""));
        file_list.add(new File(File.FileType.BUTTON, "", "", ""));
        mRecyclerView=(RecyclerView)view.findViewById(R.id.recyclerview);
        mRecyclerAdapter = new FileRecycleAdapter(getActivity(), file_list);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.setLayoutManager(new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL));

        // 获取文件列表
        SharedPreferences sharedPreferences = view.getContext().getSharedPreferences("login_status", MODE_PRIVATE);
        usrid = sharedPreferences.getInt("id", 0);
        mRecyclerAdapter.gotoPath(curPath);

        // 设置返回按钮
        View back = view.findViewById(R.id.back_arrow);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int back =  curPath.lastIndexOf('/');
                if (back != -1) {
                    curPath = curPath.substring(0, back);
                }
                mRecyclerAdapter.gotoPath(curPath);
                ((TextView)mPage.findViewById(R.id.cur_path)).setText(">  " + curPath);
            }
        });

        // 设置添加按钮
        final FloatingActionButton add_note = (FloatingActionButton) view.findViewById(R.id.add_note);
        add_note.setIcon(R.drawable.ic_note);
        add_note.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputDialogForNote.show();
            }
        });

        final FloatingActionButton add_folder = (FloatingActionButton) view.findViewById(R.id.add_folder);
        add_folder.setIcon(R.drawable.ic_user);
        add_folder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inputDialogForFolder.show();
            }
        });

        // 设置对话弹框
        View dialog_note = inflater.inflate(R.layout.dialog_input, null);
        editTextForNote = dialog_note.findViewById(R.id.title);  // 在这里假设你的布局文件中包含一个 TextView 控件，并且有指定的 id

        inputDialogForNote = new AlertDialog.Builder(view.getContext())
            .setTitle("创建新的笔记")
            .setView(dialog_note)
            .setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: 同后端联系，完成/拒绝添加文件

                        File f = new File(File.FileType.NOTE, editTextForNote.getText().toString(), "暂时没有内容~", "");
                        mRecyclerAdapter.createFile(f);
                        editTextForNote.setText("");
                    }
                })
            .setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editTextForNote.setText("");
                    }
                })
            .create();

        // 设置对话弹框
        View dialog_folder = inflater.inflate(R.layout.dialog_input, null);
        editTextForFolder = dialog_folder.findViewById(R.id.title);  // 在这里假设你的布局文件中包含一个 TextView 控件，并且有指定的 id

        inputDialogForFolder = new AlertDialog.Builder(view.getContext())
            .setTitle("创建新的文件夹")
            .setView(dialog_folder)
            .setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO: 同后端联系，完成/拒绝添加文件
                        File f = new File(File.FileType.FOLDER, editTextForFolder.getText().toString(), "", "");
                        mRecyclerAdapter.createFile(f);
                        editTextForFolder.setText("");
                    }
                })
            .setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        editTextForFolder.setText("");
                    }
                })
            .create();

        return view;
    }

    public static class File {

        public enum FileType {
            FOLDER,
            NOTE,
            PLACEHOLDER,
            BUTTON;
        }

        public String title;
        public String content;
        public String date;
        public FileType type;

        public File(FileType type_, String title_, String content_, String date_) {
            type = type_;

            switch (type) {
                case FOLDER: case NOTE:
                    title = title_;
                    content = content_;
                    if (date_.compareTo("") == 0) {
                        Date curDate = new Date();
                        SimpleDateFormat ft = new SimpleDateFormat("MM月dd日");
                        date = ft.format(curDate);
                        if(date.charAt(0) == '0')
                            date = date.substring(1);
                    }
                    else
                        date = date_;
                    break;
                case PLACEHOLDER: case BUTTON:
                    break;
            }

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
            if(viewType == File.FileType.PLACEHOLDER.ordinal()) {
                itemView = LayoutInflater.from(context).inflate(R.layout.item_placeholder, parent, false);
            }
            else if (viewType == File.FileType.NOTE.ordinal() || viewType == File.FileType.FOLDER.ordinal()) {
                itemView = LayoutInflater.from(context).inflate(R.layout.item_file_card, parent, false);
            }
            else {
                itemView = LayoutInflater.from(context).inflate(R.layout.item_add_file, parent, false);
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
                    holder.mFileTitle.setText(data.title);
                    holder.mFileContent.setText(data.content);
                    holder.mFileDate.setText(data.date);
                    holder.setTitle(data.title);
                    break;
                case FOLDER:
                    Drawable img = ContextCompat.getDrawable(context, R.drawable.folder);
                    holder.mFileImg.setImageDrawable(img);
                    holder.mFileTitle.setText(data.title);
                    holder.mFileContent.setText("");
                    holder.mFileDate.setText(data.date);
                    holder.setTitle(data.title);
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

        public void gotoPath(String path) {
            API api = API.Creator.createApiService();
            FileListRequest request = new FileListRequest(usrid, path);
            Call<FilelistResponse> call = api.get_file_list(request);
            call.enqueue(new Callback<FilelistResponse>() {
                @Override
                public void onResponse(Call<FilelistResponse> call, Response<FilelistResponse> response) {
                    // print response body
                    FilelistResponse filelistResponse = response.body();
                    mRecyclerAdapter.replaceFileList(filelistResponse.get_file_list());
                }

                @Override
                public void onFailure(Call<FilelistResponse> call, Throwable t) {
                    System.out.println("Bug exists~");
                }
            });
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

        public void createFile(File f) {
            String label = null;
            int index = 1;
            switch (f.type) {
                case NOTE:
                    label = "note";
                    while (file_list.get(index).type == File.FileType.FOLDER ||
                           (file_list.get(index).type == File.FileType.NOTE &&
                            file_list.get(index).title.compareTo(f.title) < 0))
                        index++;
                    break;
                case FOLDER:
                    label = "folder";
                    while (file_list.get(index).type == File.FileType.FOLDER &&
                            file_list.get(index).title.compareTo(f.title) < 0)
                        index++;
                    break;
                case BUTTON: case PLACEHOLDER:
                    throw new IllegalArgumentException();
            }

            // 联系后端，尝试添加文件
            final int finalIndex = index;
            API api = API.Creator.createApiService();
            Log.d("path", curPath);
            AddFileRequest request = new AddFileRequest(usrid, f.title, curPath, label);
            Call<AddFileResponse> call = api.create_file(request);
            call.enqueue(new Callback<AddFileResponse>() {
                @Override
                public void onResponse(Call<AddFileResponse> call, Response<AddFileResponse> response) {
                    // print response body
                    if(response.isSuccessful()){
                        file_list.add(finalIndex, f);
                        notifyItemInserted(finalIndex);
                        Toast.makeText(mPage.getContext(), response.body().getMsg(),
                                Toast.LENGTH_SHORT).show();
                    }
                    else {
                        // TODO:获取后端失败原因
                        try {
                            JSONObject r = new JSONObject(response.errorBody().string());
                            Toast.makeText(mPage.getContext(), r.getString("msg"),
                                    Toast.LENGTH_SHORT).show();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                    }

                }

                @Override
                public void onFailure(Call<AddFileResponse> call, Throwable t) {
                    System.out.println("Bug exists~");
                }
            });
        }

        // 自定义Viewhodler
        public class FileViewHodler extends RecyclerView.ViewHolder {
            private ImageView mFileImg;
            private TextView mFileTitle;
            private TextView mFileContent;
            private TextView mFileDate;
            private File.FileType mType;
            private String mTitle;

            public FileViewHodler(View itemView, int viewType) {
                super(itemView);

                // 根据View种类获取layout节点
                if (viewType == File.FileType.NOTE.ordinal() || viewType == File.FileType.FOLDER.ordinal()) {
                    mFileImg = (ImageView) itemView.findViewById(R.id.image);
                    mFileTitle = (TextView) itemView.findViewById(R.id.title);
                    mFileContent = (TextView) itemView.findViewById(R.id.content);
                    mFileDate = (TextView) itemView.findViewById(R.id.date);
                }

                // 根据View种类设置响应函数
                if (viewType == File.FileType.FOLDER.ordinal()) {
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
//                            Toast.makeText(context, "点击了xxx", Toast.LENGTH_SHORT).show();
                            // TODO: 从后端获取数据
                            curPath = curPath + '/' + mTitle;
                            mRecyclerAdapter.gotoPath(curPath);
                            ((TextView)mPage.findViewById(R.id.cur_path)).setText(">  " + curPath);
                        }
                    });
                }
                else if (viewType == File.FileType.NOTE.ordinal()) {
                    itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // todo: 跳转到NoteDetailActivity
                            Intent intent = new Intent(context, NoteDetailActivity.class);
                            intent.putExtra("path", curPath+'/' + mTitle);
                            startActivity(intent);
                        }
                    });
                }
                else if (viewType == File.FileType.BUTTON.ordinal()) {
                    itemView.findViewById(R.id.image).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            inputDialogForNote.show();
                        }
                    });
                }

            }

            public void setTitle(String title) {
                mTitle = title;
            }

        }

    }
}
