package com.wachat.chatUtils;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.ContactsContract;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.widget.Toast;

import com.wachat.util.LogUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;


public class SelectFile {
    private static final String LOG_TAG = LogUtils
            .makeLogTag(SelectFile.class);
    private static int SELECT_PICTURE = 0;
    private static int SELECT_CAMERA = 1;
    private static int SELECT_VIDEO = 3;
    public static final int SELECT_RECORDED_SOUND = 4;
    private static int SELECT_FILE_ATTACHEMENT = 5;
    private static int REQUEST_PICK_CONTACT = 6;
    public static int SELECT_MUSIC = 7;
    public static int SELECT_WALLPAPER = 8;
    public static String ATTACHMENT_TYPE = "";
    public static Uri VIDEO_URI = null;
    public static Uri AUDIO_URI = null;
    public static Uri CONTACT_URI = null;

    @SuppressWarnings("deprecation")
    public static String getRealPathFromURI(Uri contentUri,
                                            String attachment_type, Context context) {
        Cursor cursor = null;
        int column_index = 0;
        if (attachment_type != null && !attachment_type.equalsIgnoreCase("")) {
            if (attachment_type.equalsIgnoreCase("image")) {
                String[] proj = {MediaStore.Images.Media.DATA};
                cursor = ((Activity) context).managedQuery(contentUri, proj,
                        null, null, null);
                column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            } else if (attachment_type.equalsIgnoreCase("audio")) {
                String[] proj = {MediaStore.Audio.Media.DATA};
                cursor = ((Activity) context).managedQuery(contentUri, proj,
                        null, null, null);
                column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
            } else if (attachment_type.equalsIgnoreCase("video")) {
                String[] proj = {MediaStore.Video.Media.DATA};
                cursor = ((Activity) context).managedQuery(contentUri, proj,
                        null, null, null);
                column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            } else if (attachment_type.equalsIgnoreCase(" ")) {
                String[] proj = {MediaStore.Video.Media.DATA};
                cursor = ((Activity) context).managedQuery(contentUri, proj,
                        null, null, null);
                column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            }
            cursor.moveToFirst();

            String str = null;
            try {

                str = cursor.getString(column_index);
            } catch (Exception e1) {
                e1.printStackTrace();
            }
            try {
                // cursor.close();
                // cursor=null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return str;
        }
        return null;
    }

    public static void getFileAttachement(Context context) throws Exception {
        openFile(context);
    }

    public static void openFile(Context mContext) {

        Intent sIntent = new Intent("com.sec.android.app.myfiles.PICK_DATA");

        sIntent.addCategory(Intent.CATEGORY_DEFAULT);
        Intent chooserIntent;
        if (mContext.getPackageManager().resolveActivity(sIntent, 0) != null) {
            // it is device with samsung file manager
            chooserIntent = Intent.createChooser(sIntent, "Open folder");
            chooserIntent.putExtra(Intent.EXTRA_STREAM, "");

            try {
                ((Activity) mContext).startActivityForResult(chooserIntent,
                        SELECT_FILE_ATTACHEMENT);
            } catch (ActivityNotFoundException ex) {
                Toast.makeText(mContext.getApplicationContext(),
                        "No suitable application found for this action.",
                        Toast.LENGTH_SHORT).show();
                LogUtils.i(LOG_TAG,
                        "No activity can handle picking a file. Showing alternatives.");
            }
        } else {
            Intent fileintent = new Intent(Intent.ACTION_GET_CONTENT);
            fileintent.setType("file/*");
            try {
                ((Activity) mContext).startActivityForResult(fileintent,
                        SELECT_FILE_ATTACHEMENT);
            } catch (ActivityNotFoundException e) {
                Toast.makeText(mContext.getApplicationContext(),
                        "No suitable application found for this action.",
                        Toast.LENGTH_SHORT).show();
                LogUtils.i(LOG_TAG,
                        "No activity can handle picking a file. Showing alternatives.");
            }
        }
    }

	/*public static void getImage(final Context context) {

		StaticDataSupport.URIIntent = null;
		final String[] items = new String[] { context.getResources().getString(
				R.string.text_profile_addimagefrom_camera),
				context.getResources().getString(
						R.string.text_profile_addimagefrom_library) };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(context,
				AlertDialog.THEME_HOLO_LIGHT);

		builder.setTitle(context.getResources().getString(
				R.string.app_name_alert));
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) { // pick from
																	// camera
				if (item == 0) {
					try {
						String name = java.util.UUID.randomUUID().toString();
						ContentValues values = new ContentValues();
						values.put(MediaStore.Images.Media.TITLE, name);
						Uri mCapturedImageURI = context
								.getContentResolver()
								.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
										values);
						Intent intentPicture = new Intent(
								MediaStore.ACTION_IMAGE_CAPTURE);
						File photo = new File(Environment
								.getExternalStorageDirectory(), "Pic.jpg");
						intentPicture.putExtra(MediaStore.EXTRA_OUTPUT,
								mCapturedImageURI);
						@SuppressWarnings("unused")
						Uri imageUri = Uri.fromFile(photo);
						// intentPicture.setData(mCapturedImageURI);
						// intentPicture.putExtras(intentPicture);
						StaticDataSupport.PicIntent = intentPicture;
						StaticDataSupport.URIIntent = mCapturedImageURI;
						Activity a = (Activity) context;
						a.startActivityForResult(intentPicture, SELECT_CAMERA);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else { // pick from file
					try {
						Intent intent = new Intent();
						intent.setType("image*//*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
						intent.addCategory(Intent.CATEGORY_OPENABLE);
						Activity a = (Activity) context;
						a.startActivityForResult(intent, SELECT_PICTURE);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		final AlertDialog dialog = builder.create();
		dialog.show();

	}*/

	/*public static void textCopy(final Context context, final String text) {

		StaticDataSupport.URIIntent = null;
		final String[] items = new String[] { context.getResources().getString(
				R.string.copy) };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(context,
				AlertDialog.THEME_HOLO_LIGHT);

		builder.setTitle(context.getResources().getString(
				R.string.app_name_alert));
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) { // pick from
																	// camera
				if (item == 0) {
					try {
						ClipboardManager clipboard = (ClipboardManager) context
								.getSystemService(context.CLIPBOARD_SERVICE);
						android.content.ClipData clip = android.content.ClipData
								.newPlainText("Copied Text", text);
						clipboard.setPrimaryClip(clip);

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		});

		final AlertDialog dialog = builder.create();
		dialog.show();

	}*/

	/*public static void getWallpaper(final Context context) {

		StaticDataSupport.URIIntent = null;
		final String[] items = new String[] { context.getResources().getString(
				R.string.wallpaper),
				context.getResources().getString(
						R.string.text_profile_addimagefrom_library) };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(context,
				AlertDialog.THEME_HOLO_LIGHT);

		builder.setTitle(context.getResources().getString(
				R.string.app_name_alert));
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) { // pick from
																	// camera
				if (item == 0) {
					try {
						Intent intent = new Intent(context,
								WallpaperActivity.class);
						context.startActivity(intent);

					} catch (Exception e) {
						e.printStackTrace();
					}
				} else { // pick from file
					try {
						Intent intent = new Intent();
						intent.setType("image*//*");
                        intent.setAction(Intent.ACTION_GET_CONTENT);
						intent.addCategory(Intent.CATEGORY_OPENABLE);
						Activity a = (Activity) context;
						a.startActivityForResult(intent, SELECT_WALLPAPER);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}
		});

		final AlertDialog dialog = builder.create();
		dialog.show();

	}*/

	/*public static void getVideo(final Context context) {

		StaticDataSupport.URIIntent = null;
		final String[] items = new String[] { context.getResources().getString(
				R.string.text_profile_addimagefrom_library) };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(context,
				AlertDialog.THEME_HOLO_LIGHT);

		builder.setTitle(context.getResources().getString(
				R.string.app_name_alert));
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) { // pick from
																	// camera
				*//*
				 * if (item == 0) { Intent takeVideoIntent = new Intent(
				 * MediaStore.ACTION_VIDEO_CAPTURE); ((Activity)
				 * context).startActivityForResult( takeVideoIntent,
				 * SELECT_VIDEO); }
				 *//*if (item == 0) { // pick from file
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_PICK);
					intent.setData(MediaStore.Video.Media.EXTERNAL_CONTENT_URI);
					Activity a = (Activity) context;
					a.startActivityForResult(intent, SELECT_VIDEO);
				}
			}
		});

		final AlertDialog dialog = builder.create();
		dialog.show();

	}*/

    public static void getSound(final Context context) {
        //
        // StaticDataSupport.URIIntent = null;
        // final String[] items = new String[] {"Record" };
        // ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
        // android.R.layout.select_dialog_item, items);
        // AlertDialog.Builder builder = new AlertDialog.Builder(context);
        //
        // builder.setTitle("HHC");
        // builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
        // public void onClick(DialogInterface dialog, int item) { // pick from
        // // camera
        // // if (item == 0) {
        // // Intent intent = new Intent();
        // // intent.setAction(android.content.Intent.ACTION_PICK);
        // // intent.setData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
        // // Activity a = (Activity) context;
        // // a.startActivityForResult(intent, SELECT_RECORDED_SOUND);
        // // }
        // if(item==0) { // pick from file
        Intent intent = new Intent(MediaStore.Audio.Media.RECORD_SOUND_ACTION);
        ((Activity) context).startActivityForResult(intent,
                SELECT_RECORDED_SOUND);
        // }
        // }
        // });
        //
        // final AlertDialog dialog = builder.create();
        // dialog.show();

    }

/*	public static void getMusic(final Context context) {

		StaticDataSupport.URIIntent = null;
		final String[] items = new String[] { context.getResources().getString(
				R.string.text_profile_addimagefrom_library) };
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(context,
				android.R.layout.select_dialog_item, items);
		AlertDialog.Builder builder = new AlertDialog.Builder(context,
				AlertDialog.THEME_HOLO_LIGHT);

		builder.setTitle(context.getResources().getString(
				R.string.app_name_alert));
		builder.setAdapter(adapter, new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int item) { // pick from
																	// camera
				if (item == 0) {
					Intent intent = new Intent();
					intent.setAction(Intent.ACTION_PICK);
					intent.setData(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
					Activity a = (Activity) context;
					a.startActivityForResult(intent, SELECT_MUSIC);
				}
			}
		});

		final AlertDialog dialog = builder.create();
		dialog.show();

	}*/

    public static void getContact(Context context) {
        Intent pickContactIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.Contacts.CONTENT_URI);
        // pickContactIntent
        // .setType(ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE);
        Activity a = (Activity) context;
        a.startActivityForResult(pickContactIntent, REQUEST_PICK_CONTACT);
    }

    public static Bitmap getVideoThumbnail(String path) throws Exception {
        Bitmap thumb = null;

        // if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD_MR1) {
        // MediaMetadataRetriever mmr = new MediaMetadataRetriever();
        // // try {
        // mmr.setDataSource(path);
        // thumb = AppLog.getResizedBitmap(mmr.getFrameAtTime());
        // } else {
        thumb = ThumbnailUtils.createVideoThumbnail(path,
                MediaStore.Video.Thumbnails.MINI_KIND);
        thumb = getResizedBitmap(thumb);

        // }

        return thumb;
    }

    public static Bitmap getResizedBitmap(Bitmap bitmap) {

        int originalSize = (bitmap.getHeight() > bitmap.getWidth()) ? bitmap
                .getHeight() : bitmap.getWidth();
        if (originalSize > 100) {
            int sampleSize = (int) Math.ceil((double) originalSize / 640);
            int scaleWidth = bitmap.getWidth() / sampleSize;
            int scaleHeight = bitmap.getHeight() / sampleSize;
            Bitmap resizedBitmap = null;
            try {
                // "RECREATE" THE NEW BITMAP
                resizedBitmap = Bitmap.createScaledBitmap(bitmap, scaleWidth,
                        scaleHeight, true);
            } catch (Exception e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                return bitmap;
            }
            return resizedBitmap;
        } else {
            return bitmap;
        }

    }


    public static Bitmap getResizedBitmap_new(Bitmap bitmap) {

        int originalSize = (bitmap.getHeight() > bitmap.getWidth()) ? bitmap
                .getHeight() : bitmap.getWidth();
        if (originalSize > 100) {
            int sampleSize = (int) Math.ceil((double) originalSize / 320);
            int scaleWidth = bitmap.getWidth() / sampleSize;
            int scaleHeight = bitmap.getHeight() / sampleSize;
            Bitmap resizedBitmap = null;
            try {
                // "RECREATE" THE NEW BITMAP
                resizedBitmap = Bitmap.createScaledBitmap(bitmap, scaleWidth,
                        scaleHeight, true);
            } catch (Exception e) {
                e.printStackTrace();
            } catch (OutOfMemoryError e) {
                return bitmap;
            }
            return resizedBitmap;
        } else {
            return bitmap;
        }

    }


    /**
     * @param params
     * @param mJsonResponse
     */
    public static void mCreateAndSaveFile(String params, String mJsonResponse) {
        try {
            FileWriter file = new FileWriter("/mnt/sdcard" + "/" + params);
            file.write(mJsonResponse);
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param params
     * @param mJsonResponse
     */
    public static String mCreateAndSaveConversationEmailFile(Context mContext, String params,
                                                           String mJsonResponse) throws Exception {

        String path = getOutputTextFilePath(mContext, params);
        FileWriter file = new FileWriter(path);
        file.write(mJsonResponse);
        file.flush();
        file.close();

        return path;
    }

    public static String getOutputTextFilePath(Context mContext, String fileNameWithExtnsn)
            throws Exception {

        File mediaStorageDir = null;
        String dirPath;

        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        if (Environment.getExternalStorageState().equals(
                Environment.MEDIA_MOUNTED)) {
            dirPath = mContext.getExternalFilesDir(null).getPath();

        } else {
            // if SD card is not accessible, then use internal storage.
            dirPath = mContext.getFilesDir().getPath();

        }

        mediaStorageDir = new File(dirPath);

        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                throw new FileNotFoundException("failed to create directory");
            }
        }

        return mediaStorageDir.getPath() + File.separator
                + fileNameWithExtnsn;
    }

	/*
	 * public static Bitmap loadContactPhoto(ContentResolver cr, long id) { Uri
	 * uri = ContentUris.withAppendedId( ContactsContract.Contacts.CONTENT_URI,
	 * id); InputStream input = ContactsContract.Contacts
	 * .openContactPhotoInputStream(cr, uri); if (input == null) { return null;
	 * } return BitmapFactory.decodeStream(input); }
	 */


    @TargetApi(Build.VERSION_CODES.KITKAT)
    public static String getPath(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

}
