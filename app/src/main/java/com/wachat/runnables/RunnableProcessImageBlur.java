package com.wachat.runnables;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;

import com.wachat.chatUtils.CompressImageUtils;
import com.wachat.data.DataTextChat;
import com.wachat.util.Constants;
import com.wachat.util.MediaUtils;

import java.io.File;

public class RunnableProcessImageBlur implements Runnable {

    private DataTextChat mDataVideoChat ;
    private Handler uiHandler;
    private Context mContext;
    private String myChatId, friendChatId, myPhoneNum, friendPhoneNum;
    private String userId = "";
    private String chatType = "", groupId = "";
    private String groupJId  = "";
    private String userName = "";
    public RunnableProcessImageBlur(Context mContext, DataTextChat mDataVideoChat , Handler uiHandler) {
        super();
        this.mDataVideoChat = mDataVideoChat;
        this.uiHandler = uiHandler;
        this.mContext = mContext;
    }

    @Override
    public void run() {

        String filePath = "";
        //try with original image - not compressing it
        filePath = mDataVideoChat.getFilePath();


        if (mDataVideoChat.getIsMasked().equalsIgnoreCase("1")) {
            //save masked image
            Bitmap blurredBitmap = null;
            try {
                blurredBitmap = maskImageAndSaveMaskedImageBitmap(filePath);

                Bitmap fixRotationBitmap = MediaUtils.fixImageRotation(blurredBitmap, filePath);
                if (fixRotationBitmap != null) {
                    String[] filePathSplit = filePath.split("/");
                    String blurredFileName = "";
                    if (filePathSplit != null && filePathSplit.length > 0) {
                        blurredFileName = filePathSplit[filePathSplit.length - 1];
                        File blurredBitmapFile = MediaUtils.saveImageStringToPublicFile(fixRotationBitmap, ".blurred_" + blurredFileName, mContext);
                        if (blurredBitmapFile != null)
                            mDataVideoChat.setBlurredImagePath(blurredBitmapFile.getPath());
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                e.printStackTrace();
            }

        }
        if (uiHandler != null) {
            Message message = uiHandler.obtainMessage();
            message.what = 1;
            Bundle mBundle = new Bundle();
            mBundle.putSerializable(Constants.B_RESULT, mDataVideoChat);
            message.setData(mBundle);
            uiHandler.sendMessage(message);
        }


        mContext = null;
    }

    private Bitmap maskImageAndSaveMaskedImageBitmap(String actualPath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        Bitmap data = BitmapFactory.decodeFile(actualPath, options);
        return transformBlur(data);
    }

    private Bitmap transformBlur(Bitmap bitmap) {
        bitmap = cropBitmapWidthToMultipleOfFour(bitmap);
        Bitmap argbBitmap = convertBitmap(bitmap, Bitmap.Config.ARGB_8888);

        Bitmap blurredBitmap = Bitmap.createBitmap(argbBitmap.getWidth(), argbBitmap.getHeight(),
                Bitmap.Config.ARGB_8888);
        // Initialize RenderScript and the script to be used
        RenderScript renderScript = RenderScript.create(mContext);
        ScriptIntrinsicBlur script = ScriptIntrinsicBlur
                .create(renderScript, Element.U8_4(renderScript));
        // Allocate memory for Renderscript to work with
        Allocation input = Allocation.createFromBitmap(renderScript, argbBitmap);
        Allocation output = Allocation.createFromBitmap(renderScript, blurredBitmap);

        script.setInput(input);
        script.setRadius(CompressImageUtils.BLUR_RADIUS);
        script.forEach(output);
        output.copyTo(blurredBitmap);

        renderScript.destroy();
        argbBitmap.recycle();
        return blurredBitmap;
    }

    private static Bitmap cropBitmapWidthToMultipleOfFour(Bitmap bitmap) {
        int bitmapWidth = bitmap.getWidth();
        Bitmap croppedBitmap = null;
        if (bitmapWidth % 4 != 0) {
            // This is the place to actually crop the bitmap,
            // but I don't have the necessary method in this demo project

//             bitmap = BitmapUtils.resize(bitmap, bitmapWidth - (bitmapWidth % 4), bitmap.getHeight(),
//                     ImageView.ScaleType.CENTER);
            try {
                croppedBitmap = Bitmap.createScaledBitmap(bitmap, bitmapWidth - (bitmapWidth % 4), bitmap.getHeight() - (bitmap.getHeight() % 4), true);
            } catch (Exception e) {
                return bitmap;
            } catch (OutOfMemoryError e) {
                return bitmap;
            }
        }
        return croppedBitmap != null ? croppedBitmap : bitmap;
    }

    private static Bitmap convertBitmap(Bitmap bitmap, Bitmap.Config config) {
        if (bitmap.getConfig() == config) {
            return bitmap;
        } else {
            Bitmap argbBitmap;
            argbBitmap = bitmap.copy(config, false);
            bitmap.recycle();
            if (argbBitmap == null) {
                throw new UnsupportedOperationException(
                        "Couldn't convert bitmap from config " + bitmap.getConfig() + " to "
                                + config);
            }
            return argbBitmap;
        }
    }
}
