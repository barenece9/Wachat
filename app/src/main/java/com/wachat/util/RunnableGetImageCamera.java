package com.wachat.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;

import com.wachat.application.AppVhortex;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

public class RunnableGetImageCamera implements Runnable {

    private Uri outputUri;
    private Handler uiHandler;
    private Context mContext;

    private int circelSize, blurWidth, blurHeight;

    public RunnableGetImageCamera(int circelSize, int blurWidth, int blurHeight, Context mContext, Uri outputUri, Handler uiHandler) {
        super();
        this.circelSize = circelSize;
        this.blurHeight = blurHeight;
        this.blurWidth = blurWidth;
        this.outputUri = outputUri;
        this.uiHandler = uiHandler;
        this.mContext = mContext;
    }

    @Override
    public void run() {

        if (outputUri != null) {
            ContentResolver cr = mContext.getContentResolver();
            Bitmap bitmap = null;
            Bitmap blurBitmap = null;
            try {
                bitmap = android.provider.MediaStore.Images.Media.getBitmap(cr, Uri.fromFile(new File(MediaUtils.getPath(AppVhortex.getInstance().getApplicationContext(), outputUri))));
                bitmap = Bitmap.createScaledBitmap(bitmap, circelSize, circelSize, true);
                bitmap = MediaUtils.fixImageRotation(bitmap, outputUri);
                blurBitmap = CommonMethods.fastblur(bitmap, mContext);
                blurBitmap = Bitmap.createScaledBitmap(blurBitmap, blurWidth, blurHeight, true);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (bitmap != null && uiHandler != null) {
                Message message = uiHandler.obtainMessage();
                message.what = 1;
                Bundle mBundle = new Bundle();
                mBundle.putParcelable(Constants.IMAGE, bitmap);
                mBundle.putParcelable(Constants.IMAGE_BLUR, blurBitmap);
                mBundle.putString(Constants.ENCODE, "");
                message.setData(mBundle);
                uiHandler.sendMessage(message);
            }
        }
    }

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    @SuppressLint("NewApi")
    Bitmap blurImage(Bitmap input) {
        if (input == null) {
            return input;
        }
        try {
            RenderScript rsScript = RenderScript
                    .create(mContext.getApplicationContext());
            Allocation alloc = Allocation.createFromBitmap(rsScript, input);

            ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rsScript,
                    Element.U8_4(rsScript));
            blur.setRadius(24);
            blur.setInput(alloc);

            Bitmap result = Bitmap.createBitmap(input.getWidth(),
                    input.getHeight(), Bitmap.Config.ARGB_8888);
            Allocation outAlloc = Allocation.createFromBitmap(rsScript, result);

            blur.forEach(outAlloc);
            outAlloc.copyTo(result);

            rsScript.destroy();
            return result;
        } catch (Exception e) {
            // We are not sure which type of exceptions may come from this block
            // So for now we return the original bitmap as it is
            return input;
        } catch (OutOfMemoryError e) {
            return input;
        }

    }


}
