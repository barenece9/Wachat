package com.wachat.activity;

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.util.Base64;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.application.AppVhortex;
import com.wachat.application.BaseActivity;
import com.wachat.async.AsyncContactUs;
import com.wachat.data.DataProfile;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.storage.TableUserInfo;
import com.wachat.util.CommonMethods;
import com.wachat.util.MediaUtils;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Priti Chatterjee on 20-08-2015.
 */
public class ActivityContactUs extends BaseActivity implements View.OnClickListener, InterfaceResponseCallback {

    public Toolbar appBar;
    private AsyncContactUs mAsyncContactUs;
    private TextView tv_save, tv_cancel;
    private TableUserInfo mTableUserInfo;
    private DataProfile mDataProfile;
    private EditText txt_subet, et_msg;
    private View progressBarCircularIndetermininate;
    private ImageView img_attachement;
    private static Uri outputFileUri;
    private static Bitmap ImageBitmap;
    private String filePath = "", userid, isUpdated = "0";
    private String imgBase64Photo = "";
    private Uri selectedImageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_us);
        initComponent();
        initViews();
        mTableUserInfo = new TableUserInfo(this);
        getProfileData();

    }

    private void getProfileData() {
        mDataProfile = mTableUserInfo.getUser();
    }

    private void initViews() {
        //Commit test
        txt_subet = (EditText) findViewById(R.id.txt_subtext);
        et_msg = (EditText) findViewById(R.id.txt_message);
        img_attachement = (ImageView) findViewById(R.id.img_attachement);
        img_attachement.setOnClickListener(this);
        progressBarCircularIndetermininate = findViewById(R.id.progressBarCircularIndetermininate);
    }

    private void initComponent() {
        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
        appBar.setLogo(R.drawable.d_app_logo);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setNavigationBack(R.drawable.ic_chat_share_header_back_icon);
        initSuperViews();
        tv_act_name.setText(getResources().getString(R.string.contact_us));
        tv_save = (TextView) findViewById(R.id.tv_save);
        tv_cancel = (TextView) findViewById(R.id.tv_cancel);

        tv_save.setOnClickListener(this);
        tv_cancel.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        super.onClick(v);

        switch (v.getId()) {
            case R.id.tv_save:
                if (CommonMethods.isOnline(this)) {
                    if (TextUtils.isEmpty(txt_subet.getText().toString().trim())) {
                        ToastUtils.showAlertToast(this, "Please enter subject to send", ToastType.FAILURE_ALERT);
                        return;
                    }
                    if (TextUtils.isEmpty(et_msg.getText().toString().trim())) {
                        ToastUtils.showAlertToast(this, "Please enter message to send", ToastType.FAILURE_ALERT);
                        return;
                    }
                    progressBarCircularIndetermininate.setVisibility(View.VISIBLE);
                    mAsyncContactUs = new AsyncContactUs(mDataProfile.getCountryCode() + mDataProfile.getPhoneNo(),
                            txt_subet.getText().toString().trim(), et_msg.getText().toString().trim(), outputpath, this);
                    if (mAsyncContactUs != null && mAsyncContactUs.getStatus() != AsyncTask.Status.RUNNING)
                        if (CommonMethods.checkBuildVersionAsyncTask()) {
                            mAsyncContactUs.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                        } else {
                            mAsyncContactUs.execute();
                        }


                } else {
                    ToastUtils.showAlertToast(this, getResources().getString(R.string.no_internet), ToastType.FAILURE_ALERT);
                }
                break;

            case R.id.tv_cancel:
                onBackPressed();
                break;
            case R.id.img_attachement:
//                openImageIntent();
                imageIntentChooser(img_attachement, Math.max(img_attachement.getWidth(), img_attachement.getHeight()), img_attachement.getWidth(), img_attachement.getHeight());
                break;
        }
    }


    @Override
    protected void setImage(String encoded, Bitmap mBitmap, Bitmap mBlur, Uri outputFileUri) {
        super.setImage(encoded, mBitmap, mBlur, outputFileUri);

        //stop rotation in s4
        try {
//            ExifInterface ei = new ExifInterface(outputFileUri.getPath());
//            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
//                    ExifInterface.ORIENTATION_NORMAL);
//            switch (orientation) {
//                case ExifInterface.ORIENTATION_ROTATE_90:
//                    mBitmap = rotateImage(mBitmap, 90);
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_180:
//                    mBitmap = rotateImage(mBitmap, 180);
//                    break;
//                case ExifInterface.ORIENTATION_ROTATE_270:
//                    mBitmap = rotateImage(mBitmap, 270);
//                    break;
//            }
//            mBitmap = MediaUtils.fixImageRotation(mBitmap, outputFileUri);
            if (mBitmap != null) {
                img_attachement.setImageBitmap(mBlur);
                img_attachement.setImageBitmap(mBitmap);
                outputpath = MediaUtils.getPath(AppVhortex.getInstance().getApplicationContext(), outputFileUri);
//                imgUrl = imagePath;

            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }

    @Override
    protected void onLangChange() {

    }

    @Override
    protected void onNetworkChange(boolean isActive) {

    }

    @Override
    protected void onSearchPerformed(String result) {

    }

    @Override
    protected void CommonMenuClcik() {

    }

    @Override
    public void onResponseObject(Object mObject) {

        progressBarCircularIndetermininate.setVisibility(View.GONE);
        ToastUtils.showAlertToast(this, "Enquiry sent successfully", ToastType.SUCESS_ALERT);
        txt_subet.setText("");
        et_msg.setText("");
        img_attachement.setImageResource(R.drawable.ic_share_image_screen_plus_photos);
    }

    @Override
    public void onResponseList(ArrayList<?> mList) {

    }

    @Override
    public void onResponseFaliure(String responseText) {
        try {
            progressBarCircularIndetermininate.setVisibility(View.GONE);
            ToastUtils.showAlertToast(this, "Something went wrong. Please try again.", ToastType.SUCESS_ALERT);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    String outputpath = "";

    private void openImageIntent() {

        // Determine Uri of camera image to save.
        final File root = new File(Environment.getExternalStorageDirectory()
                + File.separator + "vortext" + File.separator);
        root.mkdirs();
        String fname = "vortext.png";
        final File sdImageMainDirectory = new File(root, fname);
        outputpath = sdImageMainDirectory.getAbsolutePath();
        outputFileUri = Uri.fromFile(sdImageMainDirectory);

        // Camera.
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(
                MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getPackageManager();
        final List<ResolveInfo> listCam = packageManager.queryIntentActivities(
                captureIntent, 0);
        for (ResolveInfo res : listCam) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName,
                    res.activityInfo.name));
            intent.setPackage(packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, outputFileUri);
            intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1);
            cameraIntents.add(intent);
        }

        final Intent galleryIntent = new Intent();
        // Intent galleryIntent = new Intent(
        // Intent.ACTION_PICK,
        // android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galleryIntent.setType("image/*");
        galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);

        final Intent chooserIntent = Intent.createChooser(galleryIntent,
                getString(R.string.chooserIntent));

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
                cameraIntents.toArray(new Parcelable[]{}));

        startActivityForResult(chooserIntent, 9);
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult();
//        if (resultCode == RESULT_OK) {
//            String realPath = "";
//            if (requestCode == 9) {
//                final boolean isCamera;
//                if (data == null) {
//                    isCamera = true;
//                } else {
//                    final String action = data.getAction();
//                    if (action == null) {
//                        isCamera = false;
//                    } else {
//                        isCamera = action
//                                .equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
//                    }
//                }
//                String selectedImagePath = null;
//                if (isCamera) {
//                    selectedImageUri = outputFileUri;
//                    // outputFileUri = null;
//                } else {
//                    selectedImageUri = data == null ? null : data.getData();
//                    selectedImagePath = getPath(selectedImageUri);
//                    if (selectedImagePath == null) {
//                        if (Build.VERSION.SDK_INT < 11)
//                            realPath = RealPathUtil
//                                    .getRealPathFromURI_BelowAPI11(
//                                            ActivityContactUs.this, data.getData());
//
//                            // SDK >= 11 && SDK < 19
//                        else if (Build.VERSION.SDK_INT < 19)
//                            realPath = RealPathUtil
//                                    .getRealPathFromURI_API11to18(
//                                            ActivityContactUs.this, data.getData());
//
//                            // SDK > 19 (Android 4.4)
//                        else
//                            realPath = RealPathUtil.getRealPathFromURI_API19(
//                                    ActivityContactUs.this, data.getData());
//
//                        selectedImagePath = realPath;
//                    }
//                }
//
//                String filemanagerstring = selectedImageUri.getPath();
//
//                if (selectedImagePath != null) {
//                    filePath = selectedImagePath;
//                    img_attachement.setImageURI(Uri.parse(filePath));
////                    tv_one_attach.setVisibility(View.VISIBLE);
//                } else if (filemanagerstring != null) {
//                    filePath = filemanagerstring;
////                    tv_one_attach.setVisibility(View.VISIBLE);
//                    img_attachement.setImageURI(null);
//                     img_attachement.setImageURI(Uri.parse(filePath));
//                } else {
////                    Toast.makeText(getActivity(),
////                            getString(R.string.Unknown_path), Toast.LENGTH_LONG)
////                            .show();
////                    Log.e(getString(R.string.Bitmap),
////                            getString(R.string.Unknown_path));
////                    tv_one_attach.setVisibility(View.VISIBLE);
//
//                }
//
//                decodeFile(filePath);
//
//            }
//        }
//    }


    public String getPath(Uri uri) {
        String path = "";
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = null;
        try {
            cursor = managedQuery(uri, projection, null, null,
                    null);
            if (cursor != null) {
                // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
                // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
                int column_index = cursor
                        .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                cursor.moveToFirst();
                path = cursor.getString(column_index);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }

        return path;

    }

    public void decodeFile(String filePath) {
        // Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, o);

        // The new size we want to scale to
        final int REQUIRED_SIZE = 1024;

        // Find the correct scale value. It should be the power of 2.
        int width_tmp = o.outWidth, height_tmp = o.outHeight;
        int scale = 1;
        while (true) {
            if (width_tmp < REQUIRED_SIZE && height_tmp < REQUIRED_SIZE)
                break;
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        // Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        ImageBitmap = BitmapFactory.decodeFile(filePath, o2);

        if (ImageBitmap != null) {
            setImageResource(filePath);
        }
    }

    private void setImageResource(String filePath) {
        try {
            ExifInterface ei = new ExifInterface(filePath);
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_NORMAL);
            Bitmap mBitmap = null;
            switch (orientation) {
                case ExifInterface.ORIENTATION_ROTATE_90:
                    mBitmap = rotateImage(ImageBitmap, 90);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_180:
                    mBitmap = rotateImage(ImageBitmap, 180);
                    break;
                case ExifInterface.ORIENTATION_ROTATE_270:
                    mBitmap = rotateImage(ImageBitmap, 270);
                    break;

            }

            if (mBitmap != null) {
                saveimage(outputpath, mBitmap);
                // encodedImageBitmap(mBitmap);
            } else {
                saveimage(outputpath, ImageBitmap);
                // encodedImageBitmap(ImageBitmap);
            }
            isUpdated = "1";
            // tv_upload_photopath.setText(filePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void saveimage(String path, Bitmap finalBitmap) {
        File file = new File(path);
        if (file.exists())
            file.delete();
        try {
            FileOutputStream out = new FileOutputStream(file);
            finalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out);
            out.flush();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private Bitmap rotateImage(Bitmap source, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, true);
    }

    private void encodedImageBitmap(Bitmap photo) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        photo.compress(Bitmap.CompressFormat.JPEG, 90, stream);
        byte[] image = stream.toByteArray();
        imgBase64Photo = new String(Base64.encode(image, Base64.DEFAULT));
    }
}
