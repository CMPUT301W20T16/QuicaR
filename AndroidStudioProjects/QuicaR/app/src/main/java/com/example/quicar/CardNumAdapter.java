package com.example.quicar;

import android.content.Context;
import android.widget.ArrayAdapter;
import java.util.List;

public class CardNumAdapter<T> extends ArrayAdapter{
    public CardNumAdapter(Context context, int resource, List<T>  objects) {
        super(context, resource, objects);
    }

    @Override
    public int getCount() {
        // don't display last item. It is used as hint.
        int count = super.getCount();
        return count > 0 ? count - 1 : count;
    }
}
