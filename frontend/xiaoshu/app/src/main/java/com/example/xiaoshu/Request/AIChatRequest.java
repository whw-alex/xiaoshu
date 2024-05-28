package com.example.xiaoshu.Request;

import android.util.Log;

import com.example.xiaoshu.NoteItem;

import java.util.List;

public class AIChatRequest {
    String prompt;

    public AIChatRequest(String question, List<NoteItem> noteItemList) {
        prompt = "以下是笔记的内容：\n";
        for(NoteItem item : noteItemList) {
            if(item.getType() == NoteItem.TYPE_TEXT && !item.getContent().trim().isEmpty()) {
                prompt += (item.getContent() + '\n');
            }
        }

        if (prompt.equals("以下是笔记的内容：\n")) {
            prompt = "请回答问题：" + question;
        }
        else {
            prompt += "基于以上笔记内容，回答问题：" + question;
        }

//        if (prompt.equals("以下是笔记的内容：\n")) {
//            prompt = "请你以一个名叫小术的AI的口吻，回答问题：" + question;
//        }
//        else {
//            prompt += "基于以上笔记内容，请你以一个名叫小术的AI的口吻，回答问题：" + question;
//        }
    }
}
