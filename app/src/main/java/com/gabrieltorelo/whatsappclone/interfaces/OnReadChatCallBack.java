package com.gabrieltorelo.whatsappclone.interfaces;

import com.gabrieltorelo.whatsappclone.model.chat.Chat;

import java.util.List;

public interface OnReadChatCallBack {
    void onReadSuccess(List<Chat> list);
    void onReadFailed();
}
