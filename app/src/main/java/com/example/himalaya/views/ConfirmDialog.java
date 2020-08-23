package com.example.himalaya.views;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.himalaya.R;

public class ConfirmDialog extends Dialog {
    private TextView cancel_btn;
    private TextView enter_btn;
    public ConfirmDialog(@NonNull Context context) {
        this(context,0);
    }

    public ConfirmDialog(@NonNull Context context, int themeResId) {
        this(context, false,null);
    }

    protected ConfirmDialog(@NonNull Context context, boolean cancelable, @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.confirm_dialog);
        cancel_btn=findViewById(R.id.cancel_btn);
        enter_btn=findViewById(R.id.enter_btn);
        initEvent();
    }

    private void initEvent(){
        cancel_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(onDialogClickListener!=null){
                    onDialogClickListener.onCancelClick();
                }
            }
        });

        enter_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onDialogClickListener!=null) {
                    onDialogClickListener.onEnterClick();
                }
            }
        });
    }

    private OnDialogClickListener onDialogClickListener;

    public void setOnDialogClickListener(OnDialogClickListener onDialogClickListener){
        this.onDialogClickListener=onDialogClickListener;
    }
    public interface OnDialogClickListener{
        void onCancelClick();

        void onEnterClick();
    }
}
