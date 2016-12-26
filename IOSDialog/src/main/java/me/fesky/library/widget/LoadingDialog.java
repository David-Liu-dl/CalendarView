package me.fesky.library.widget;

import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import me.fesky.library.R;

/**
 * Created by liuqiang on 2016/5/6.
 */
public class LoadingDialog {
    /**
     * 创建自定义的  圆形 progressDialog对象
     *
     * @param context
     * @param tips
     * @return
     */
    public static Dialog createCircleProgressDialog(Context context, String tips) {
        return createCircleProgressDialog(context, tips, false);
    }


    /**
     * @param context
     * @param tips
     * @param isCancelable 返回键是否可用
     * @return
     */
    public static Dialog createCircleProgressDialog(Context context, String tips, boolean isCancelable) {

        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.dialog_circle_progress, null);// 得到加载view
        LinearLayout layout = (LinearLayout) v.findViewById(R.id.dialog_wrapper_view);// 加载布局
        // main.xml中的ImageView
        ImageView spaceshipImage = (ImageView) v.findViewById(R.id.iv_progress_logo);
        TextView tipTextView = (TextView) v.findViewById(R.id.tv_progress_tip);// 提示文字
        // 加载动画
        Animation rotateAnimation = AnimationUtils.loadAnimation(
                context, R.anim.progress_rotate_animation);
        // 使用ImageView显示动画
        spaceshipImage.startAnimation(rotateAnimation);
        tipTextView.setText(tips);// 设置加载信息

        Dialog loadingDialog = new Dialog(context, R.style.CircleProgressDialog);// 创建自定义样式dialog

        loadingDialog.setCancelable(isCancelable);// “返回键” 是否可用
        // 设置布局
        loadingDialog.setContentView(
                layout,
                new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT)
        );
        return loadingDialog;
    }
}
