package com.example.xiaoshu;

import static android.content.Context.MODE_PRIVATE;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.*;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.*;
import android.widget.*;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestBuilder;
import com.bumptech.glide.annotation.GlideModule;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.ImageViewTarget;
import com.example.xiaoshu.Response.DeleteFileResponse;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import android.graphics.BitmapFactory;
import com.kongzue.dialogx.dialogs.CustomDialog;
import com.kongzue.dialogx.dialogs.MessageDialog;
import com.kongzue.dialogx.interfaces.OnBindView;

import com.example.xiaoshu.Request.DeleteItemRequest;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class NoteAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    private List<NoteItem> noteList;
    private static final String BASE_URL = "http://10.0.2.2:8000/";
    Context pageContext;
    int id;
    String path;


    public NoteAdapter(List<NoteItem> noteList, Context pageContext, int id, String path) {
        this.pageContext = pageContext;
        this.noteList = noteList;
        this.id = id;
        this.path = path;
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

        // 为最后一个item设置margin，以防止其紧贴底部
        if (position == getItemCount() - 1) {
            ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
            float density = pageContext.getResources().getDisplayMetrics().density;
            ((ViewGroup.MarginLayoutParams)params).bottomMargin = Math.round(200 * density);
        }

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
                     url = item.getContent();
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

    private class FitX extends ImageViewTarget<Bitmap> {
        private ImageView target;
        public FitX(ImageView target) {
            super(target);
            this.target = target;
        }
        @Override
        protected void setResource(Bitmap resource) {
            view.setImageBitmap(resource);
            //获取原图的宽高
            int width = resource.getWidth();
            int height = resource.getHeight();
            //获取imageView的宽
            int imageViewWidth = target.getWidth();
            //计算缩放比例
            float sy = (float) (imageViewWidth * 0.1) / (float) (width * 0.1);
            //计算图片等比例放大后的高
            int imageViewHeight = (int) (height * sy);
            ViewGroup.LayoutParams params = target.getLayoutParams();
            params.height = imageViewHeight;
            target.setLayoutParams(params);
        }
    }

    public int loadImageUrl(String url, ImageView imageView) {
        url = url.trim();
        Picasso.get()
                .load(url)
                .into(imageView);
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

    public void deleteFile(int type, String content)
    {
        int pos = 0;
        for(; pos < noteList.size(); pos++) {
            if(noteList.get(pos).getType() == type &&
                    noteList.get(pos).getContent().equals(content))
                break;
        }
        Log.d("NoteAdapter", "deleteFile: " + pos);
        API api = API.Creator.createApiService();
        final int final_pos = pos;
        Call<DeleteFileResponse> call = api.delete_item(new DeleteItemRequest(id, path, type == NoteItem.TYPE_AUDIO ? "audio" : "image", pos));
        call.enqueue(new Callback<DeleteFileResponse>() {
            @Override
            public void onResponse(Call<DeleteFileResponse> call, Response<DeleteFileResponse> response) {
                // print response body
                if (response.isSuccessful()) {
                    System.out.println(final_pos);
                    noteList.remove(final_pos);
                    notifyItemRemoved(final_pos);


                } else {
                    // TODO:获取后端失败原因
                    try {
                        JSONObject r = new JSONObject(response.errorBody().string());
                        String msg = r.getString("msg");
                        Log.d("NoteAdapter", "onResponse: " + msg);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                }
            }
            @Override
            public void onFailure(Call<DeleteFileResponse> call, Throwable t) {
                System.out.println("Bug exists~");
            }
        });

    }

    // 定义音频类型的视图持有者
    private class AudioViewHolder extends RecyclerView.ViewHolder {
        // 定义音频播放相关的视图控件
        // 例如，MediaPlayer、播放按钮等等
        NoteItem item;
        MediaPlayer mediaPlayer;
        ImageView playButton;
        TextView audioText;
        TextView title;

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
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        playButton.setImageResource(R.drawable.ic_play_circle);
                        mediaPlayer.seekTo(0);
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
            title = itemView.findViewById(R.id.title);
            title.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    CustomDialog.show(new OnBindView<CustomDialog>(R.layout.popup_delete) {
                                @Override
                                public void onBind(final CustomDialog dialog, View v) {
                                    View btnDelete = v.findViewById(R.id.delete);
                                    btnDelete.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            deleteFile(item.getType(), item.getContent());
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            })
                            .setAlignBaseViewGravity(title.findViewById(R.id.title), Gravity.TOP|Gravity.CENTER)
                            .setBaseViewMarginBottom(-60);


                    return true;
                }
            });
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
    private class ImageViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;
        NoteItem item;
        void bindItem(NoteItem item) {
            this.item = item;
        }

        ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.imageView);
            imageView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    CustomDialog.show(new OnBindView<CustomDialog>(R.layout.popup_delete) {
                                @Override
                                public void onBind(final CustomDialog dialog, View v) {
                                    View btnDelete = v.findViewById(R.id.delete);
                                    btnDelete.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            deleteFile(item.getType(), item.getContent());
                                            dialog.dismiss();
                                        }
                                    });
                                }
                            })
                            .setAlignBaseViewGravity(imageView.findViewById(R.id.imageView), Gravity.CENTER)
                            .setBaseViewMarginBottom(40);
                    return true;
                }
            });
        }
    }


}
