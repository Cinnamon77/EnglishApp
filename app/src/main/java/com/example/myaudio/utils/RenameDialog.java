package com.example.myaudio.utils;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowMetrics;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.NonNull;

import com.example.myaudio.R;
import com.example.myaudio.databinding.DialogRenameBinding;

public class RenameDialog extends Dialog implements View.OnClickListener{
    private DialogRenameBinding binding;
//    创建点击确定执行的接口函数
    public interface OnEnsureListener{
        public void onEnsure(String msg);//填进去重命名名称
    }
    private OnEnsureListener onEnsureListener;

    public void setOnEnsureListener(OnEnsureListener onEnsureListener) {
        this.onEnsureListener = onEnsureListener;
    }

    public RenameDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogRenameBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.dialogRenameBtnCancel.setOnClickListener(this);
        binding.dialogRenameBtnEnsure.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int viewId = v.getId();

        if (viewId == R.id.dialog_rename_btn_cancel) {
            cancel();
            // 处理取消按钮点击事件
        } else if (viewId == R.id.dialog_rename_btn_ensure) {
            if (onEnsureListener!=null) {
                String msg = binding.dialogRenameEt.getText().toString().trim();
                onEnsureListener.onEnsure(msg);


            }
            cancel();

            // 处理确定按钮点击事件
        }


    }

    /*设置EditText显示原来的标题名称
    *
    **/
    public void setTipText(String oldText){
        binding.dialogRenameEt.setText(oldText);
    }
    /*
    * 设置对话框宽度和屏幕宽度一致
    * */
    public void setDialogWidth(){
//        获取当前屏幕窗口对象
        Window window = getWindow();
//        获取窗口的信息参数
        WindowManager.LayoutParams wlp = window.getAttributes();
//        获取屏幕的宽度
        Display display = window.getWindowManager().getDefaultDisplay();
        wlp.width = display.getWidth();
        wlp.gravity = Gravity.BOTTOM;
//        设置窗口背景透明
        window.setBackgroundDrawableResource(android.R.color.transparent);
//        设置窗口参数
        window.setAttributes(wlp);
//        自动弹出软键盘
        handler.sendEmptyMessageDelayed(1,100);
    }
    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message message) {
            InputMethodManager manager=
                    (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            manager.toggleSoftInput(0,InputMethodManager.HIDE_NOT_ALWAYS);
            return false;
        }
    });
}
