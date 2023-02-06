package vungnv.com.foodyum.utils;

import static android.app.Activity.RESULT_OK;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import vungnv.com.foodyum.Constant;


public class ImagePicker implements Constant {
    public static void pickImage(Context context, OnImagePickedListener listener) {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        ((Activity) context).startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
        mListener = listener;
    }

    public interface OnImagePickedListener {
        void onImagePicked(Uri uri);
    }

    private static OnImagePickedListener mListener;

    public static void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_PICTURE && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri uri = data.getData();
            mListener.onImagePicked(uri);
        }
    }

}
