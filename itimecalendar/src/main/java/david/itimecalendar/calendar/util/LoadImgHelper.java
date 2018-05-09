package david.itimecalendar.calendar.util;

import android.content.Context;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;
import com.squareup.picasso.Transformation;

import david.itimecalendar.R;
import david.itimecalendar.calendar.listeners.ITimeContactInterface;

/**
 * Created by David Liu on 17/08/16.
 * ${COMPANY}
 * lyhmelbourne@gmail.com
 */
public class LoadImgHelper {
    private static LoadImgHelper loadImgHelper;

    public static LoadImgHelper getInstance() {
        if(loadImgHelper == null)
        {
            loadImgHelper = new LoadImgHelper();
        }
        return loadImgHelper;
    }

    public void bindContactWithImageView(Context mContext, ITimeContactInterface contact, ImageView img_v){
        if (contact.getPhoto() != null){
            Picasso.with(mContext).load(contact.getPhoto()).placeholder(R.drawable.invitee_selected_loading).into(img_v);
        }else {
            Picasso.with(mContext).load(R.drawable.invitee_selected_default_picture).into(img_v);
        }
    }

    public void bindUrlWithImageView(Context mContext, Transformation transformation,String url, ImageView img_v, int size){
        if (url != null && !url.equals("")){
            Picasso.with(mContext).load(url).resize(size,size).transform(transformation).placeholder(R.drawable.invitee_selected_loading).into(img_v);
        }else {
            Picasso.with(mContext).load(R.drawable.invitee_selected_default_picture).transform(transformation).resize(size,size).into(img_v);
        }
    }
}
