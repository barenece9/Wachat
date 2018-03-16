package com.wachat.util;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.util.Base64;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class RunnableGetImageGallery implements Runnable {

	Handler mHandler;
	Uri uri;
	private Activity mActivity;
	private int thumbNailSize;
	
	@SuppressWarnings("unused")
	private int circelSize, blurWidth, blurHeight;

	public RunnableGetImageGallery(int circelSize, int blurWidth,int  blurHeight, Handler mHandler, Uri uri, Activity mActivity, int thumbNailSize) {
		this.circelSize = circelSize;
		this.blurHeight = blurHeight;
		this.blurWidth = blurWidth;
		this.mHandler = mHandler;
		this.uri = uri;
		this.mActivity = mActivity;
		this.thumbNailSize = thumbNailSize;
		
	}

	@Override
	public void run() {

		InputStream input = null;
		try {
			input = mActivity.getContentResolver().openInputStream(uri);
			BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
			onlyBoundsOptions.inJustDecodeBounds = true;
			onlyBoundsOptions.inDither = true;// optional
			onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
			BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
			if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1)) {
			} else {
				int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight
						: onlyBoundsOptions.outWidth;

				double ratio = (originalSize > thumbNailSize) ? (originalSize / thumbNailSize) : 1.0;
				BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
				bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
				bitmapOptions.inDither = true;// optional
				bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
				input = mActivity.getContentResolver().openInputStream(uri);
				Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
				Bitmap blurBitmap = null;
				blurBitmap = blurImage(bitmap);
				blurBitmap = Bitmap.createScaledBitmap(blurBitmap, blurWidth, blurHeight, true);
				Bundle mBundle = new Bundle();
				mBundle.putParcelable(Constants.IMAGE, bitmap);
				mBundle.putString(Constants.ENCODE, encodeTobase64(bitmap));
				mBundle.putParcelable(Constants.IMAGE_BLUR, blurBitmap);
				Message message = new Message();
				message.what = 0;
				message.setData(mBundle);
				mHandler.sendMessage(message);
			}
		} catch (Exception e) {

		} finally {
			try {
				input.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public Bitmap getThumbnail(Uri uri) throws IOException {
		InputStream input = mActivity.getContentResolver().openInputStream(uri);

		BitmapFactory.Options onlyBoundsOptions = new BitmapFactory.Options();
		onlyBoundsOptions.inJustDecodeBounds = true;
		onlyBoundsOptions.inDither = true;// optional
		onlyBoundsOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
		BitmapFactory.decodeStream(input, null, onlyBoundsOptions);
		input.close();
		if ((onlyBoundsOptions.outWidth == -1) || (onlyBoundsOptions.outHeight == -1))
			return null;

		int originalSize = (onlyBoundsOptions.outHeight > onlyBoundsOptions.outWidth) ? onlyBoundsOptions.outHeight
				: onlyBoundsOptions.outWidth;

		double ratio = (originalSize > thumbNailSize) ? (originalSize / thumbNailSize) : 1.0;
		BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
		bitmapOptions.inSampleSize = getPowerOfTwoForSampleRatio(ratio);
		bitmapOptions.inDither = true;// optional
		bitmapOptions.inPreferredConfig = Bitmap.Config.ARGB_8888;// optional
		input = mActivity.getContentResolver().openInputStream(uri);
		Bitmap bitmap = BitmapFactory.decodeStream(input, null, bitmapOptions);
		input.close();
		return bitmap;
	}

	public String encodeTobase64(Bitmap image) {
		Bitmap immagex = image;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
		byte[] b = baos.toByteArray();
		String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
		return imageEncoded;
	}

	public Bitmap decodeBase64(String input) {
		byte[] decodedByte = Base64.decode(input, 0);
		return BitmapFactory.decodeByteArray(decodedByte, 0, decodedByte.length);
	}

	private int getPowerOfTwoForSampleRatio(double ratio) {
		int k = Integer.highestOneBit((int) Math.floor(ratio));
		if (k == 0)
			return 1;
		else
			return k;
	}

	@TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
	@SuppressLint("NewApi")
	Bitmap blurImage(Bitmap input) {
		if (input == null) {
			return input;
		}
		try {
			RenderScript rsScript = RenderScript.create(mActivity.getApplicationContext());
			Allocation alloc = Allocation.createFromBitmap(rsScript, input);

			ScriptIntrinsicBlur blur = ScriptIntrinsicBlur.create(rsScript, Element.U8_4(rsScript));
			blur.setRadius(24);
			blur.setInput(alloc);

			Bitmap result = Bitmap.createBitmap(input.getWidth(), input.getHeight(), Bitmap.Config.ARGB_8888);
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
