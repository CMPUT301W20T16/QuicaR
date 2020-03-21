package com.example.quicar;

import java.util.ArrayList;

public interface OnGetPayDataListener {
    void onSuccess(ArrayList<PayRecord> records);

    void onFailure(String e);
}
