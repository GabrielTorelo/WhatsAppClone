package com.gabrieltorelo.whatsappclone.interfaces;

import com.gabrieltorelo.whatsappclone.model.user.Connection;

import java.util.List;

public interface OnReadStatusCallBack {
    void onReadSuccess(List<Connection> list);
    void onReadFailed();
}
