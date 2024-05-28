package com.example.xiaoshu;

import android.content.Context;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.*;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.squareup.picasso.Picasso;
import android.graphics.BitmapFactory;


public class NoteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<NoteItem> noteList;
    private static final String BASE_URL = "http://10.0.2.2:8000/";


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
        holder.itemView.setTag(item);


        // 根据视图类型，更新相应的视图控件
        if (holder instanceof TextViewHolder) {
            TextViewHolder textViewHolder = (TextViewHolder) holder;
            textViewHolder.bindItem(item);
            textViewHolder.editText.setText(item.getContent());
        } else if (holder instanceof AudioViewHolder) {
            AudioViewHolder audioViewHolder = (AudioViewHolder) holder;
            audioViewHolder.bindItem(item);
            // 设置音频播放相关的逻辑
        } else if (holder instanceof ImageViewHolder) {
            ImageViewHolder imageViewHolder = (ImageViewHolder) holder;
            imageViewHolder.bindItem(item);
            // 设置图片相关的逻辑
//            Picasso.get().load(item.getContent()).into(imageViewHolder.imageView);
            if (item.getContent().equals("")){
                imageViewHolder.imageView.setImageResource(R.drawable.avatar_1);
            }
            else {
//                用uri加载图片
                imageViewHolder.imageView.setImageURI(null);
                Log.d("NoteAdapter", "onBindViewHolder: " + item.getContent());
                String url;
                if (item.getContent().contains("static")) {
                    Log.d("NoteAdapter","contains static");
                     url = item.getContent();
                     Log.d("NoteAdapter", "onBindViewHolder url: " + url);
//                     Picasso.get().load(url).into(imageViewHolder.imageView);
//                    显示网络图片
//                    Context context = imageViewHolder.imageView.getContext();
//                    Glide.with(context).load(url).into(imageViewHolder.imageView);
                    loadImageUrl(url, imageViewHolder.imageView);
                } else {

                     url = item.getContent();
                    Uri uri = Uri.parse(url);
                    Log.d("NoteAdapter", "onBindViewHolder: " + uri.toString());
                    imageViewHolder.imageView.setImageURI(uri);
                }

            }
        }
    }

public int loadImageUrl(String url, ImageView imageView) {
    // 使用 Picasso 加载图片
    Picasso.get().load(url).into(imageView);
    return 0;

}

    @Override
    public int getItemCount() {
        return noteList.size();
    }

    // 定义文本类型的视图持有者
    public static class TextViewHolder extends RecyclerView.ViewHolder {
        EditText editText;
        NoteItem item;

        void bindItem(NoteItem item) {
            this.item = item;
            editText.setText(item.getContent());
        }

        TextViewHolder(@NonNull View itemView) {
            super(itemView);
            editText = itemView.findViewById(R.id.editText);
            editText.addTextChangedListener(new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                    item.setContent(s.toString());
                }

                @Override
                public void afterTextChanged(Editable s) {
                    // 更新数据源中的文本内容
                    // 例如，可以使用 set 方法更新 NoteItem 对象中的文本内容
//                    NoteItem item = (NoteItem) editText.getTag();
                    item.setContent(s.toString());

                }
            });
        }
    }

    // 定义音频类型的视图持有者
    private static class AudioViewHolder extends RecyclerView.ViewHolder {
        // 定义音频播放相关的视图控件
        // 例如，MediaPlayer、播放按钮等等
        NoteItem item;
        MediaPlayer mediaPlayer;
        ImageView playButton;
        TextView audioText;

        void bindItem(NoteItem item) {

            this.item = item;
            Log.d("AudioViewHolder", "bindItem: " + item.getContent());
            try {
                mediaPlayer = new MediaPlayer();
                mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
                mediaPlayer.setDataSource(item.getContent());
                mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                    @Override
                    public void onPrepared(MediaPlayer mp) {
//                        mediaPlayer.start();
                        int duration = mediaPlayer.getDuration();
                        audioText.setText(String.format(Locale.getDefault(), "%02d:%02d",
                                duration / 1000 / 60, duration / 1000 % 60));

                    }
                });
                mediaPlayer.prepareAsync();
            } catch (IOException e) {
                e.printStackTrace();

            }

        }
        AudioViewHolder(@NonNull View itemView) {
            super(itemView);
            // 初始化音频播放相关的视图控件
            playButton = itemView.findViewById(R.id.play);
            audioText = itemView.findViewById(R.id.duration);
            playButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mediaPlayer.isPlaying()) {
                        mediaPlayer.pause();
                        Log.d("AudioViewHolder", "onClick: pause");
                        playButton.setImageResource(R.drawable.ic_play_circle);

                    }
                    else {
                        Log.d("AudioViewHolder", "current position: " + mediaPlayer.getCurrentPosition());
                        if (mediaPlayer.getCurrentPosition() > 0) {
                            mediaPlayer.start();
                            playButton.setImageResource(R.drawable.ic_pause_circle);
                        } else {
//                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
//                                @Override
//                                public void onPrepared(MediaPlayer mp) {
//                                    mediaPlayer.start();
//                                    playButton.setImageResource(R.drawable.ic_pause_circle);
//                                }
//                            });
//                            mediaPlayer.prepareAsync();
                            mediaPlayer.start();
                            playButton.setImageResource(R.drawable.ic_pause_circle);
                        }
                    }
                }
            });

        }
    }

    // 定义图片类型的视图持有者
    private static class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        NoteItem item;
        void bindItem(NoteItem item) {
            this.item = item;
        }

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
        }
    }
}
