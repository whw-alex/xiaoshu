package com.example.xiaoshu;

import android.net.Uri;
import android.util.Log;
import android.view.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.*;
import android.widget.*;
import com.squareup.picasso.Picasso;



public class NoteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<NoteItem> noteList;

    public NoteAdapter(List<NoteItem> noteList) {
        this.noteList = noteList;
    }
    @Override
    public int getItemViewType(int position) {
        // 根据数据类型返回视图类型
        // 例如，可以使用 NoteItem 类中的类型字段来判断
        NoteItem item = noteList.get(position);
        if (item.getType() == NoteItem.TYPE_TEXT) {
            return NoteItem.TYPE_TEXT;
        } else if (item.getType() == NoteItem.TYPE_AUDIO) {
            return NoteItem.TYPE_AUDIO;
        } else if (item.getType() == NoteItem.TYPE_IMAGE) {
            return NoteItem.TYPE_IMAGE;
        } else if (item.getType() == NoteItem.TYPE_TEXT_PLACEHOLDER) {
            return NoteItem.TYPE_TEXT_PLACEHOLDER;
        }
        return super.getItemViewType(position);
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view;
        RecyclerView.ViewHolder viewHolder;

        // 根据视图类型加载不同的布局文件和视图持有者
        if (viewType == NoteItem.TYPE_TEXT) {
            view = inflater.inflate(R.layout.item_text, parent, false);
            viewHolder = new TextViewHolder(view);
        } else if (viewType == NoteItem.TYPE_AUDIO) {
            view = inflater.inflate(R.layout.item_audio, parent, false);
            viewHolder = new AudioViewHolder(view);
        } else if (viewType == NoteItem.TYPE_IMAGE) {
            view = inflater.inflate(R.layout.item_image, parent, false);
            viewHolder = new ImageViewHolder(view);
        } else if (viewType == NoteItem.TYPE_TEXT_PLACEHOLDER) {
            view = inflater.inflate(R.layout.item_text_placeholder, parent, false);
            viewHolder = new TextViewHolder(view);
        }
        else {
            throw new IllegalArgumentException("Invalid view type");
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        NoteItem item = noteList.get(position);

        // 根据视图类型，更新相应的视图控件
        if (holder instanceof TextViewHolder) {
            TextViewHolder textViewHolder = (TextViewHolder) holder;
            textViewHolder.editText.setText(item.getContent());
        } else if (holder instanceof AudioViewHolder) {
            AudioViewHolder audioViewHolder = (AudioViewHolder) holder;
            // 设置音频播放相关的逻辑
        } else if (holder instanceof ImageViewHolder) {
            ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
            // 设置图片相关的逻辑
//            Picasso.get().load(item.getContent()).into(imageViewHolder.imageView);
            if (item.getContent().equals("")){
                imageViewHolder.imageView.setImageResource(R.drawable.avatar_1);
            }
            else {
//                用uri加载图片
                imageViewHolder.imageView.setImageURI(null);
                Log.d("NoteAdapter", "onBindViewHolder: " + item.getContent());
                Uri uri = Uri.parse(item.getContent());
                Log.d("NoteAdapter", "onBindViewHolder: " + uri.toString());
                imageViewHolder.imageView.setImageURI(uri);
            }
        }
    }

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    // 定义文本类型的视图持有者
    private static class TextViewHolder extends RecyclerView.ViewHolder {
        EditText editText;

        TextViewHolder(@NonNull View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.editText);
        }
    }

    // 定义音频类型的视图持有者
    private static class AudioViewHolder extends RecyclerView.ViewHolder {
        // 定义音频播放相关的视图控件
        // 例如，MediaPlayer、播放按钮等等

        AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            // 初始化音频播放相关的视图控件
        }
    }

    // 定义图片类型的视图持有者
    private static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
