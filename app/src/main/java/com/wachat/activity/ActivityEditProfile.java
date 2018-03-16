package com.wachat.activity;

import android.content.ContentValues;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.wachat.R;
import com.wachat.application.AppVhortex;
import com.wachat.application.BaseActivity;
import com.wachat.async.AsyncEditProfile;
import com.wachat.customViews.ProgressBarCircularIndeterminate;
import com.wachat.data.DataCountry;
import com.wachat.data.DataEditProfile;
import com.wachat.dialog.DialogGenderChoice;
import com.wachat.interfaces.InterfaceResponseCallback;
import com.wachat.storage.ConstantDB;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.MediaUtils;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

/**
 * Created by Priti Chatterjee on 26-08-2015.
 */
public class ActivityEditProfile extends BaseActivity implements View.OnClickListener, InterfaceResponseCallback {

    public Toolbar appBar;
//    private ImageView my_acc_profile_img;
    private TextView tv_gender, tv_language, /*tv_country,*/
            tv_cancel, tv_save, tv_phone_number;
    private DialogGenderChoice mDialogGenderChoice;
    private EditText et_name;
    private ImageView iv_blur_bg;
    private boolean mExternalStorageAvailable = false;
    private boolean mExternalStorageWriteable = false;
    private AsyncEditProfile mAsyncEditProfile;
    private String imgUrl = "", Name = "", gender = "", language = "", country = "", langCode = "";
    private ProgressBarCircularIndeterminate progressBarCircularIndetermininate;
    private DataCountry mDataCountry = new DataCountry();
    public static final String TEMP_PHOTO_FILE_NAME = "profilephoto.png";
    private File mFileTemp;
    private Uri imagePickUri;
    private View edit_profile_iv_placeholder;
    private View progressBar_profile_image_load;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        initComponent();


        initViews();
        setValues();
//        checkChangesForET();
    }

    private void checkChangesForET() {
        et_name.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                //your action
                if (!TextUtils.isEmpty(s.toString().trim()))
                    Name = String.valueOf(s);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        //setValues();
    }


    private void initComponent() {
        appBar = (Toolbar) findViewById(R.id.appBar);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
        appBar.setLogo(R.drawable.d_app_logo);
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        initSuperViews();
        tv_act_name.setText(getResources().getString(R.string.editProfile));

    }

    private void initViews() {
        progressBarCircularIndetermininate = (ProgressBarCircularIndeterminate) findViewById(R.id.progressBarCircularIndetermininate);
        progressBar_profile_image_load = findViewById(R.id.progressBar_profile_image_load);
        et_name = (EditText) findViewById(R.id.et_name);
        edit_profile_iv_placeholder = findViewById(R.id.edit_profile_iv_placeholder);
//        my_acc_profile_img.setOnClickListener(this);

        findViewById(R.id.edit_option_iv_img).setOnClickListener(this);
        tv_gender = (TextView) findViewById(R.id.tv_gender);
        tv_gender.setOnClickListener(this);

//        tv_country = (TextView) findViewById(R.id.tv_country);
//        tv_country.setOnClickListener(this);

        tv_phone_number = (TextView) findViewById(R.id.tv_phone_number);
        tv_language = (TextView) findViewById(R.id.tv_language);
        tv_language.setOnClickListener(this);

        tv_cancel = (TextView) findViewById(R.id.tv_cancel);
        tv_save = (TextView) findViewById(R.id.tv_save);
        tv_cancel.setOnClickListener(this);
        tv_save.setOnClickListener(this);

        iv_blur_bg = (ImageView) findViewById(R.id.iv_blur_bg);
        iv_blur_bg.setOnClickListener(this);

        tv_cancel.setVisibility(mPrefs.isFirstTimeProfileEditDone() ? View.VISIBLE : View.GONE);

        if (mPrefs.isFirstTimeProfileEditDone()) {
            setNavigationBack(R.drawable.ic_chat_share_header_back_icon);
        }

    }

    @Override
    public void onClick(View v) {
        Intent mIntent;
        switch (v.getId()) {
            case R.id.edit_option_iv_img:
                imageIntentChooser(iv_blur_bg, Math.max(iv_blur_bg.getWidth(), iv_blur_bg.getHeight()), iv_blur_bg.getWidth(), iv_blur_bg.getHeight());
                break;
            case R.id.iv_blur_bg:
                boolean url = false;
                String viewImagePath = "";
                if (TextUtils.isEmpty(imgUrl)) {
                    viewImagePath = mDataProfile.getProfileImage();
                    url = true;
                } else {
                    viewImagePath = imgUrl;
                }

                if (!TextUtils.isEmpty(viewImagePath)) {
                    mIntent = new Intent(this, ActivityPinchToZoom.class);
                    Bundle mBundle = new Bundle();
                    mBundle.putBoolean("url_image",url);
                    mBundle.putString(Constants.B_RESULT, viewImagePath);
                    mIntent.putExtras(mBundle);
                    startActivity(mIntent);
                }
                break;

            case R.id.tv_language:
                mIntent = new Intent(this, ActivitySelectLanguage.class);
                startActivityForResult(mIntent, Constants.EditToLang);
                break;


            case R.id.tv_country:
//                Intent mIntent1 = new Intent(this, ActivitySelectCountry.class);
//                startActivityForResult(mIntent1, Constants.ChooseCountry);
                break;

            case R.id.tv_gender:
                mDialogGenderChoice = new DialogGenderChoice();
                mDialogGenderChoice.showAll(false);
                mDialogGenderChoice.show(mFragmentManager, DialogGenderChoice.class.getSimpleName());
                break;

            case R.id.tv_male:
                tv_gender.setText(getResources().getString(R.string.male));
                gender = getResources().getString(R.string.male);
                mDialogGenderChoice.dismiss();
                break;

            case R.id.tv_female:
                tv_gender.setText(getResources().getString(R.string.female));
                gender = getResources().getString(R.string.female);
                mDialogGenderChoice.dismiss();
                break;

            case R.id.tv_cancel:
                finish();
                break;

            case R.id.tv_save:
                //call webservice
                //  if (et_name.getText().toString().length() > 0)
                CommonMethods.hideSoftKeyboard(this);
                if (CommonMethods.isOnline(this)) {
                    editProfile();
                } else {
                    ToastUtils.showAlertToast(this, getResources().getString(R.string.no_internet), ToastType.FAILURE_ALERT);
                }
                break;
            default:
                super.onClick(v);
                break;

        }
    }

    //fetch value from db
    private void setValues() {
        mDataProfile = mTableUserInfo.getUser();
        if (mDataProfile.getLanguage().equals("") || mDataProfile.getLanguage().equals("0")) {
            tv_language.setText(getResources().getString(R.string.english));
        } else
            tv_language.setText(mDataProfile.getLanguage());

        if (mDataProfile.getPhoneNo().equals("")) {
            tv_phone_number.setText("");
        } else
            tv_phone_number.setText("+" + mDataProfile.getCountryCode() + mDataProfile.getPhoneNo());

        et_name.setText("");
        if (mDataProfile.getUsername() != null) {

            et_name.setText(CommonMethods.getUTFDecodedString(mDataProfile.getUsername()));
            et_name.setSelection(et_name.getText().toString().length());
        }
        if (TextUtils.isEmpty(mDataProfile.getGender()) || mDataProfile.getGender().equals("0")) {
            tv_gender.setText(getResources().getString(R.string.select_gender));
        } else {
            gender = mDataProfile.getGender();
            tv_gender.setText(mDataProfile.getGender());
        }

        String imageUrl = "";

        if (mDataProfile.getProfileImage() != null && !mDataProfile.getProfileImage().equals("")) {
            imageUrl = mDataProfile.getProfileImage();
            progressBar_profile_image_load.setVisibility(View.VISIBLE);
            edit_profile_iv_placeholder.setVisibility(View.VISIBLE);
            mImageLoader.displayImage(imageUrl, iv_blur_bg, new ImageLoadingListener() {
                @Override
                public void onLoadingStarted(String imageUri, View view) {

                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    progressBar_profile_image_load.setVisibility(View.GONE);
                    edit_profile_iv_placeholder.setVisibility(View.VISIBLE);
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    progressBar_profile_image_load.setVisibility(View.GONE);
                    edit_profile_iv_placeholder.setVisibility(View.GONE);
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    progressBar_profile_image_load.setVisibility(View.GONE);
                    edit_profile_iv_placeholder.setVisibility(View.VISIBLE);
                }
            });
        } else {
            progressBar_profile_image_load.setVisibility(View.GONE);
            edit_profile_iv_placeholder.setVisibility(View.VISIBLE);
        }



       /* mImageLoader.displayImage(imageUrl, iv_blur_bg, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {

            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                imagePath = imageUri;

                iv_blur_bg.setImageBitmap(CommonMethods.fastblur(loadedImage, mActivity));
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {

            }
        });*/

     /*   final String finalImageUrl = imageUrl;
        Target t = new Target() {

            @Override
            public void onPrepareLoad(Drawable arg0) {
            }

            @Override
            public void onBitmapFailed(Drawable arg0) {

            }

            @Override
            public void onBitmapLoaded(Bitmap arg0, Picasso.LoadedFrom arg1) {

                Bitmap blurredBitmap = CommonMethods.correctBlur(arg0, mActivity);
                iv_blur_bg.setImageBitmap(blurredBitmap);
            }
        };*/

//        Picasso.with(this).load(imageUrl)
//                .transform(new BlurTransformation(this, CompressImageUtils.BLUR_RADIUS)).into(iv_blur_bg);
    }


    private void editProfile() {
        Name = et_name.getText().toString().trim();
        Name = TextUtils.isEmpty(Name) ? "" : Name;

        if (TextUtils.isEmpty(Name)) {
            ToastUtils.showAlertToast(this,
                    getString(R.string.profile_alert_profile_name_blank), ToastType.FAILURE_ALERT);
            return;
        }

        if(!TextUtils.isEmpty(gender)&&gender.equalsIgnoreCase("Select Gender")){
            gender = "";
        }

        //web service with all modified parameter.
        if (CommonMethods.isOnline(this)) {
            mAsyncEditProfile = new AsyncEditProfile(mDataProfile.getUserId(),
                    CommonMethods.getUTFEncodedString(Name), imgUrl,
                    language, langCode.toUpperCase(), gender, country, this);
            //if nothing is changed asynctask not called
//            if (!TextUtils.isEmpty(Name) || !TextUtils.isEmpty(imgUrl) ||
//                    !TextUtils.isEmpty(language) || !TextUtils.isEmpty(gender) || !TextUtils.isEmpty(country))
            //if username not blank
            //Edit 2016-02-03, Remove name empty validation as per QA feedback
//            if (!TextUtils.isEmpty(et_name.getText().toString().trim())) {
            progressBarCircularIndetermininate.setVisibility(View.VISIBLE);
            if (CommonMethods.checkBuildVersionAsyncTask()) {
                mAsyncEditProfile.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
            } else {
                mAsyncEditProfile.execute();
            }
        } else {
            ToastUtils.showAlertToast(this, getString(R.string.no_internet), ToastType.FAILURE_ALERT);
        }
//            } else {
//                ToastUtils.showAlertToast(mActivity, getResources().getString(R.string.profile_username_blank), ToastType.FAILURE_ALERT);
//            }
    }

    @Override
    protected void onLangChange() {

    }

    @Override
    protected void onNetworkChange(boolean isActive) {

    }

    @Override
    protected void onSearchPerformed(String result) {
        Intent mIntent = new Intent(this, ActivityFindPeople.class);
        Bundle mBundle = new Bundle();
        mBundle.putString(Constants.B_RESULT, result);
        mIntent.putExtras(mBundle);
        startActivity(mIntent);
    }

    @Override
    protected void CommonMenuClcik() {

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        Bundle mBundle;

        switch (requestCode) {
            case Constants.EditToLang:
                if (data != null) {
                    mBundle = data.getExtras();
                    if (mBundle != null) {
                        String selection = mBundle.getString(Constants.B_RESULT);
                        langCode = mBundle.getString(Constants.B_CODE);
                        // Toast.makeText(mActivity, selection, Toast.LENGTH_SHORT).show();
                        tv_language.setText(selection);
                        language = selection;
                    }
                }
                break;
//            case Constants.ImagePickerChat:
//                if (data != null) {
//                    mBundle = data.getExtras();
//                    if (mBundle != null) {
//                        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//                        String url = mBundle.getString(Constants.B_RESULT);
//
//                    }
//                }
//                break;

//            case Constants.ChooseCountry:
//                if (data != null) {
//                    try {
//                        DataCountry mDataCountry = (DataCountry) (data.getExtras()).getSerializable(Constants.B_RESULT);
//                        this.mDataCountry = mDataCountry;
//                        tv_country.setText(mDataCountry.getCountryName());
//                        mPrefs.setCountryName(mDataCountry.getCountryName());
//                        country = mDataCountry.getCountryName();
//                    } catch (ClassCastException e) {
//                        e.printStackTrace();
//                    }
//                }
//                break;


            default:
                super.onActivityResult(requestCode, resultCode, data);
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
                iv_blur_bg.setImageBitmap(mBitmap);
                edit_profile_iv_placeholder.setVisibility(View.GONE);
                imagePath = MediaUtils.getPath(AppVhortex.getInstance().getApplicationContext(), outputFileUri);
                imgUrl = imagePath;
//                imgUrl = savebitmap(imagePath, mBitmap).getPath();

            }else{
                imagePath = "";
                imgUrl = "";
            }

            Log.d("EditProfile","Fetched image: "+ imgUrl);
//            ImageLoader.getInstance().displayImage(outputFileUri.toString(),iv_blur_bg);

        } catch (Exception e) {
            ToastUtils.showAlertToast(this, "Failed to process the image. Please try again", ToastType.FAILURE_ALERT);
            return;
        }

    }

    private Uri savebitmap(String filename, Bitmap mBitmap) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;

        File file = new File(filename);
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, filename);
        }
        if (Environment.MEDIA_MOUNTED.equals(Environment
                .getExternalStorageState())) {
            mFileTemp = new File(Environment.getExternalStorageDirectory(),
                    TEMP_PHOTO_FILE_NAME);
        } else {
            mFileTemp = new File(getFilesDir(), TEMP_PHOTO_FILE_NAME);
        }
        try {
            // make a new bitmap from your file
//            Bitmap bitmap = BitmapFactory.decodeFile(file.getName());

            outStream = new FileOutputStream(mFileTemp);
            mBitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Uri.fromFile(mFileTemp);

    }

    private Bitmap rotateImage(Bitmap source, int angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(),
                source.getHeight(), matrix, true);
    }

    @Override
    public void onResponseObject(Object mObject) {
        progressBarCircularIndetermininate.setVisibility(View.GONE);
        if (mObject instanceof DataEditProfile) {
            ToastUtils.showAlertToast(this, getString(R.string.profile_update_success), ToastType.SUCESS_ALERT);


            //store value in prefs
            //   mPrefs.setLanguage(tv_language.getText().toString());

            DataEditProfile mDataEditProfile = ((DataEditProfile) mObject);
            ContentValues mContentValues = new ContentValues();

//            if (!TextUtils.isEmpty(mDataEditProfile.getName()))
            mContentValues.put(ConstantDB.UserName, mDataEditProfile.getName());
//            if (!TextUtils.isEmpty(mDataEditProfile.getImageurl()))
            mContentValues.put(ConstantDB.ProfileImage, mDataEditProfile.getImageurl());
            if (!TextUtils.isEmpty(mDataEditProfile.getLanguage()))
                mContentValues.put(ConstantDB.Language, mDataEditProfile.getLanguage());
            if (!TextUtils.isEmpty(langCode))
                mContentValues.put(ConstantDB.LanguageIdentifire, langCode.toUpperCase());

            if (!TextUtils.isEmpty(mDataEditProfile.getGender()))
                mContentValues.put(ConstantDB.Gender, mDataEditProfile.getGender());
            if (!TextUtils.isEmpty(mDataEditProfile.getCountryName()))
                mContentValues.put(ConstantDB.CountryName, mDataEditProfile.getCountryName());
            if (mTableUserInfo.editProfile(mContentValues, mDataProfile.getUserId()))
                mDataProfile = mTableUserInfo.getUser();

            mPrefs.setFirstTimeProfileEditDone();
            finish();
        }
    }

    @Override
    public void onResponseList(ArrayList<?> mList) {

    }

    @Override
    public void onResponseFaliure(String responseText) {

        ToastUtils.showAlertToast(this, responseText, ToastType.FAILURE_ALERT);
        progressBarCircularIndetermininate.setVisibility(View.GONE);
    }

    @Override
    public void onBackPressed() {
        if (mPrefs.isFirstTimeProfileEditDone()) {
            super.onBackPressed();
        }
    }


//    protected void imageIntentChooser() {
//
//
//        final CharSequence[] items = {"Take Photo", "Choose from Library", "Cancel"};
//
//        AlertDialog.Builder builder = new AlertDialog.Builder(
//                mActivity);
//        builder.setTitle("Select Photo!");
//        builder.setItems(items, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int item) {
//                if (items[item].equals("Take Photo")) {
//                    startImageIntentCamra();
//                } else if (items[item].equals("Choose from Library")) {
//                    Crop.pickImage(ActivityEditProfile.this);
//                    //captureImageFromGalery();
//                } else if (items[item].equals("Cancel")) {
//                    dialog.dismiss();
//                }
//            }
//        });
//        builder.show();
//
//    }
//
//
//    protected void startImageIntentCamra() {
//        boolean externalStorageAvailable;
//        boolean externalStorageWriteable;
//        String state = Environment.getExternalStorageState();
//        if (Environment.MEDIA_MOUNTED.equals(state)) {
//            externalStorageAvailable = externalStorageWriteable = true;
//        } else if (Environment.MEDIA_MOUNTED_READ_ONLY.equals(state)) {
//            externalStorageAvailable = true;
//            externalStorageWriteable = false;
//        } else {
//            externalStorageAvailable = externalStorageWriteable = false;
//        }
//        String fileName = "Image" + System.currentTimeMillis() + ".jpg";
//
//        ContentValues values = new ContentValues();
//        values.put(MediaStore.Images.Media.TITLE, fileName);
//        values.put(MediaStore.Images.Media.DESCRIPTION, "Image capture by camera");
//        if (externalStorageAvailable || externalStorageWriteable) {
//            imagePickUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
//        }
//
//
//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        if (imagePickUri != null)
//            intent.putExtra(MediaStore.EXTRA_OUTPUT, imagePickUri);
//        intent.putExtra(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA, 0);
//        intent.putExtra(MediaStore.EXTRA_SCREEN_ORIENTATION, ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//
//        long freeinternal = CommonMethods.FreeInternalMemory();
//        long freeExternal = CommonMethods.FreeExternalMemory();
//        if (externalStorageAvailable || externalStorageWriteable) {
//            if (freeExternal > 10) {
//
//                startActivityForResult(intent, Constants.ACTION_IMAGE_CAPTURE);
//            } else {
//                Toast.makeText(this, "Not Enough Memory", Toast.LENGTH_SHORT).show();
//            }
//        } else {
//            if (freeinternal > 10) {
//                startActivityForResult(intent, Constants.ACTION_IMAGE_CAPTURE);
//            } else {
//                Toast.makeText(this, "Not Enough Memory", Toast.LENGTH_SHORT).show();
//            }
//        }
//
//    }
//
//
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
////        super.onActivityResult(requestCode, resultCode, data);
//
//
//        Bundle mBundle;
//
//        if (resultCode != RESULT_OK) {
//            if (requestCode == Crop.REQUEST_CROP) {
//                ToastUtils.showAlertToast(this,"Failed to process the image",ToastType.FAILURE_ALERT);
//            }
//            imagePickUri = null;
//            return;
//        }
//
//        if (requestCode ==Constants.EditToLang) {
//            if (data != null) {
//                mBundle = data.getExtras();
//                if (mBundle != null) {
//                    String selection = mBundle.getString(Constants.B_RESULT);
//                    langCode = mBundle.getString(Constants.B_CODE);
//                    // Toast.makeText(mActivity, selection, Toast.LENGTH_SHORT).show();
//                    tv_language.setText(selection);
//                    language = selection;
//                }
//            }
//            return;
//        }
////        String extension = "";
//        if (requestCode == Crop.REQUEST_PICK && resultCode == RESULT_OK) {
////            extension = MediaUtils.getExtensionFromFileName(data.getData().toString());
////            if()
//            imagePickUri = data.getData();
//            beginCrop(imagePickUri);
//        } else if (requestCode == Crop.REQUEST_CROP) {
//            handleCrop(resultCode, data);
//        } else if (requestCode == Constants.ACTION_IMAGE_CAPTURE) {
////            handleCrop(resultCode, data);
//            if (imagePickUri == null) {
//                imagePickUri = data.getData();
//            }
//            beginCrop(imagePickUri);
//        }
//
//    }
//
//
//    private void beginCrop(Uri source) {
//
//        String path = "";
//        path = MediaUtils.getPath(AppVhortex.getInstance().getApplicationContext(), source);
//
//        if (TextUtils.isEmpty(path)) {
//            ToastUtils.showAlertToast(this, "Failed to fetch the image. Please try again", ToastType.FAILURE_ALERT);
//            return;
//        }
//        File file = null;
//        try {
//            file = new File(path);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        if (file == null) {
//
//        }
//
//        Uri uri = Uri.fromFile(file);
//        ImageUtils.normalizeImageForUri(this, uri);
//        String filePath = file.getPath();
//
//        Intent intent = new Intent(this, CropImage.class);
//        intent.putExtra(CropImage.IMAGE_PATH, filePath);
//        intent.putExtra(CropImage.SCALE, true);
//
//        intent.putExtra(CropImage.ASPECT_X, 1);
//        intent.putExtra(CropImage.ASPECT_Y, 1);
//
//        startActivityForResult(intent, Crop.REQUEST_CROP);
//    }
//
//    private void handleCrop(int resultCode, Intent result) {
//        if (resultCode == RESULT_OK) {
//            if (imagePickUri != null) {
//
//                processImage(imagePickUri);
//            } else {
//                String mStringUri = Crop.getOutput(result).toString();
//                Uri mUri = Uri.parse(mStringUri);
//                if (mUri != null) {
//                    processImage(mUri);
//                }
//            }
//
//        } else if (resultCode == Crop.RESULT_ERROR) {
//            Toast.makeText(this, Crop.getError(result).getMessage(), Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    private void processImage(Uri mUri) {
//        this.imagePickUri = mUri;
//
//
//
//
//        ImageLoader.getInstance().displayImage(imagePickUri.toString(), iv_blur_bg, new ImageLoadingListener() {
//            @Override
//            public void onLoadingStarted(String imageUri, View view) {
//
//            }
//
//            @Override
//            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
//
//            }
//
//            @Override
//            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
//                my_acc_profile_img.setVisibility(View.GONE);
//                findViewById(R.id.my_acc_profile_wrapper).setVisibility(View.GONE);
//            }
//
//            @Override
//            public void onLoadingCancelled(String imageUri, View view) {
//
//            }
//        });
//
//        imgUrl = MediaUtils.getPath(AppVhortex.getInstance().getApplicationContext(), imagePickUri);
//
////        imgUrl = savebitmap(imagePath, mBitmap).getPath();
//    }

}
