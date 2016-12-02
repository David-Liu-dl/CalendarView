package org.unimelb.itime.vendor.helper;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import org.unimelb.itime.vendor.R;
import org.unimelb.itime.vendor.listener.ITimeContactInterface;

/**
 * Created by yuhaoliu on 17/08/16.
 */
public class LoadImgHelper {
    private static LoadImgHelper loadImgHelper;
    private final String TAG = "MyAPP";

    public static LoadImgHelper getInstance() {
        if(loadImgHelper == null)
        {
            loadImgHelper = new LoadImgHelper();
        }
        return loadImgHelper;
    }

    public void bindContactWithImageView(Context mContext, ITimeContactInterface contact, ImageView img_v){
        if (contact.getPhoto() != null){
            Log.i(TAG, "url: " + contact.getPhoto());
            Picasso.with(mContext).load(contact.getPhoto()).placeholder(R.drawable.invitee_selected_loading).into(img_v);
        }else {
            Picasso.with(mContext).load(R.drawable.invitee_selected_default_picture).into(img_v);
        }
    }

    public void bindUrlWithImageView(Context mContext, String url, ImageView img_v){
        if (url != null && !url.equals("")){
            Picasso.with(mContext).load(url).placeholder(R.drawable.invitee_selected_loading).into(img_v);
        }else {
            Picasso.with(mContext).load(R.drawable.invitee_selected_default_picture).into(img_v);
        }
    }
}
