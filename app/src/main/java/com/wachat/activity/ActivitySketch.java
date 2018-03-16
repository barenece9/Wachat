package com.wachat.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SlidingDrawer;

import com.wachat.R;
import com.wachat.application.BaseActivity;
import com.wachat.customViews.DrawingView;
import com.wachat.data.DataShareImage;
import com.wachat.util.CommonMethods;
import com.wachat.util.Constants;
import com.wachat.util.MediaUtils;
import com.wachat.util.ToastType;
import com.wachat.util.ToastUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Priti Chatterjee on 20-08-2015.
 */
public class ActivitySketch extends BaseActivity implements SlidingDrawer.OnDrawerOpenListener, SlidingDrawer.OnDrawerCloseListener {

    public Toolbar appBar;
//    int arrPen[] = {8, 12, 16, 20, 25};
//    int arrBrush[] = {12, 16, 20, 24, 28};

    int arrPen[] = {4, 8, 12, 16, 20};
    int arrBrush[] = {8, 12, 16, 20, 24};
    private ImageView pen, brush, eraser, size, clear, send,
            red_pen, yellow_pen, blue_dark_pen, orange_pen, purple_pen, blue_light_pen, black_pen,
            red_brush, yellow_brush, blue_brush, orange_brush, purple_brush, blue_light_brush, black_brush,
            size_one, size_two, size_three, size_four, size_five,
            handle;
    private LinearLayout lnr_option_pen, lnr_option_brush, lnr_option_size, side_panel;
    private SlidingDrawer drawer;
    private DrawingView drawing;
    //brush size
    private float mediumBrush;
    private int sizeValuePosition = 0;
    private int draw_type = 1;
    private int TYPE_PEN = 1, TYPE_BRUSH = 2;
    private File sketchSavedFile;
    private String mSketchFileName = "";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sketch);

//        getWindow().setBackgroundDrawable(new ColorDrawable(Color.WHITE));
//        View view = this.getWindow().getDecorView();
//        view.setBackgroundColor(Color.WHITE);
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss")
                .format(new Date());
        mSketchFileName = "Sketch_" + timeStamp + ".jpg";
        initComponent();
        initViews();
        drawing.setColor("#ff0000");
        drawing.setBrushSize(getPenSize(sizeValuePosition));

    }

    private void initViews() {
        drawer = (SlidingDrawer) findViewById(R.id.drawer);

        handle = (ImageView) findViewById(R.id.handle);
        drawer.setOnDrawerOpenListener(this);
        drawer.setOnDrawerCloseListener(this);
        drawing = (DrawingView) findViewById(R.id.drawing);
        pen = (ImageView) findViewById(R.id.pen);
        brush = (ImageView) findViewById(R.id.brush);
        eraser = (ImageView) findViewById(R.id.eraser);
        size = (ImageView) findViewById(R.id.size);
        clear = (ImageView) findViewById(R.id.clear);
        send = (ImageView) findViewById(R.id.send);

        //option views
        lnr_option_pen = (LinearLayout) findViewById(R.id.lnr_option_pen);
        lnr_option_brush = (LinearLayout) findViewById(R.id.lnr_option_brush);
        lnr_option_size = (LinearLayout) findViewById(R.id.lnr_option_size);

        //pen
        red_pen = (ImageView) findViewById(R.id.red_pen);
        yellow_pen = (ImageView) findViewById(R.id.yellow_pen);
        blue_dark_pen = (ImageView) findViewById(R.id.blue_dark_pen);
        orange_pen = (ImageView) findViewById(R.id.orange_pen);
        purple_pen = (ImageView) findViewById(R.id.purple_pen);
        blue_light_pen = (ImageView) findViewById(R.id.blue_light_pen);
        black_pen = (ImageView) findViewById(R.id.black_pen);

        //brush
        red_brush = (ImageView) findViewById(R.id.red_brush);
        yellow_brush = (ImageView) findViewById(R.id.yellow_brush);
        blue_brush = (ImageView) findViewById(R.id.blue_brush);
        orange_brush = (ImageView) findViewById(R.id.orange_brush);
        purple_brush = (ImageView) findViewById(R.id.purple_brush);
        blue_light_brush = (ImageView) findViewById(R.id.blue_light_brush);
        black_brush = (ImageView) findViewById(R.id.black_brush);

        //size
        size_one = (ImageView) findViewById(R.id.size_one);
        size_three = (ImageView) findViewById(R.id.size_three);
        size_two = (ImageView) findViewById(R.id.size_two);
        size_four = (ImageView) findViewById(R.id.size_four);
        size_five = (ImageView) findViewById(R.id.size_five);

        side_panel = (LinearLayout) findViewById(R.id.side_panel);

        clickListeners();
    }

    private void clickListeners() {
        pen.setOnClickListener(this);
        brush.setOnClickListener(this);
        eraser.setOnClickListener(this);
        size.setOnClickListener(this);
        clear.setOnClickListener(this);
        send.setOnClickListener(this);

        //views
        red_pen.setOnClickListener(this);
        yellow_pen.setOnClickListener(this);
        blue_dark_pen.setOnClickListener(this);
        orange_pen.setOnClickListener(this);
        purple_pen.setOnClickListener(this);
        blue_light_pen.setOnClickListener(this);
        black_pen.setOnClickListener(this);

        red_brush.setOnClickListener(this);
        yellow_brush.setOnClickListener(this);
        blue_brush.setOnClickListener(this);
        orange_brush.setOnClickListener(this);
        purple_brush.setOnClickListener(this);
        blue_light_brush.setOnClickListener(this);
        black_brush.setOnClickListener(this);


        size_one.setOnClickListener(this);
        size_two.setOnClickListener(this);
        size_three.setOnClickListener(this);
        size_four.setOnClickListener(this);
        size_five.setOnClickListener(this);
    }

    private void initComponent() {
        appBar = (Toolbar) findViewById(R.id.appBar);
        //  appBar.setLogo(R.drawable.d_app_logo);
        appBar.setBackgroundColor(getResources().getColor(R.color.app_Brown));
        setSupportActionBar(appBar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        setNavigationBack(R.drawable.ic_header_back_icon);
        initSuperViews();
        tv_act_name.setText(getResources().getString(R.string.sketch));
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

boolean alreadyNotifiedMediaScanner = false;
    @Override
    protected void CommonMenuClcik() {
        if (drawing.isPaint()) {
            drawing.setDrawingCacheEnabled(true);
            //attempt to save
//            String imgSaved = MediaStore.Images.Media.insertImage(
//                    getContentResolver(), drawing.getDrawingCache(),
//                    UUID.randomUUID().toString() + ".png", "drawing");

            sketchSavedFile = MediaUtils.saveImageStringToPublicFile(drawing.getDrawingCache(), mSketchFileName,this);

            if (sketchSavedFile != null && sketchSavedFile.exists()) {

//                        ToastUtils.showAlertToast(mActivity, "Drawing successfully sent", ToastType.SUCESS_ALERT);
//                        ToastUtils.showAlertToast(mActivity, imgSaved, ToastType.SUCESS_ALERT);
                if(!alreadyNotifiedMediaScanner) {
                    try {
                        int index = sketchSavedFile.getPath().lastIndexOf('.') + 1;
                        String ext = sketchSavedFile.getPath().substring(index).toLowerCase();
                        MediaUtils.refreshGalleryAppToShowTheFile(getApplicationContext(), sketchSavedFile.getPath(), ext);
                        alreadyNotifiedMediaScanner = true;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            //feedback
            if (sketchSavedFile != null && sketchSavedFile.exists()) {
                ToastUtils.showAlertToast(this, "Drawing successfully saved", ToastType.SUCESS_ALERT);
            } else {
                ToastUtils.showAlertToast(this, "Oops! Image could not be saved.", ToastType.FAILURE_ALERT);
            }
            drawing.destroyDrawingCache();
        } else {
            ToastUtils.showAlertToast(this, "No image to save", ToastType.FAILURE_ALERT);
        }
    }

    private void hideViews(View mView) {
        if (mView != null)
            if (mView.isShown())
                mView.setVisibility(View.GONE);


    }

    private void toggleViews(View mView) {
        if (mView != null)
            if (!mView.isShown())
                mView.setVisibility(View.VISIBLE);
            else
                mView.setVisibility(View.GONE);
    }

//    private String getImagePatFromURI(String imgSaved) {
//        String selectedImagePath = "";
//
//        Uri uri = Uri.parse(imgSaved);
//
//        if (uri != null) {
//             selectedImagePath = MediaUtils.getRealPathFromURI(
//                    uri, MediaUtils.ATTACHMENT_TYPE_IMAGE, this);
//            if (TextUtils.isEmpty(selectedImagePath)) {
//                selectedImagePath = MediaUtils.getPath(
//                        this, uri);
//            }
//        }
//        return selectedImagePath;
//    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.pen:
                draw_type = TYPE_PEN;
                hideViews(lnr_option_brush);
                hideViews(lnr_option_size);

//                toggleViews(lnr_option_pen);
                if (!lnr_option_pen.isShown())
                    openPenChoice();
                else
                    hideViews(lnr_option_pen);

                break;
            case R.id.brush:
                draw_type = TYPE_BRUSH;
                hideViews(lnr_option_pen);
                hideViews(lnr_option_size);

                // toggleViews(lnr_option_brush);
                if (!lnr_option_brush.isShown())
                    openBrushChoice();
                else
                    hideViews(lnr_option_brush);

                break;
            case R.id.eraser:
                hideViews(lnr_option_pen);
                hideViews(lnr_option_brush);
                hideViews(lnr_option_size);

                drawing.setErase(true);
                setDrawSizeWithDrawType(draw_type);
                break;
            case R.id.size:
                hideViews(lnr_option_pen);
                hideViews(lnr_option_brush);

                // toggleViews(lnr_option_size);
                if (!lnr_option_size.isShown())
                    openSizeChoice();
                else
                    hideViews(lnr_option_size);

               // drawer.close();
                break;
            case R.id.clear:
                drawing.startNew();
                drawing.setIsPaint(false);
                drawer.close();
                break;
            case R.id.send:

                if (drawing.isPaint()) {
                    drawing.setDrawingCacheEnabled(true);
                    //attempt to save


                   sketchSavedFile = MediaUtils.saveImageStringToPublicFile(drawing.getDrawingCache(),mSketchFileName, this);
                    if (sketchSavedFile != null && sketchSavedFile.exists()) {

//                        ToastUtils.showAlertToast(mActivity, "Drawing successfully sent", ToastType.SUCESS_ALERT);
//                        ToastUtils.showAlertToast(mActivity, imgSaved, ToastType.SUCESS_ALERT);
                        if(!alreadyNotifiedMediaScanner) {
                            try {
                                int index = sketchSavedFile.getPath().lastIndexOf('.') + 1;
                                String ext = sketchSavedFile.getPath().substring(index).toLowerCase();
                                MediaUtils.refreshGalleryAppToShowTheFile(getApplicationContext(), sketchSavedFile.getPath(), ext);
                                alreadyNotifiedMediaScanner = true;
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                        sendSkecthTochat(sketchSavedFile.getPath());

                    } else {



                    }
                    drawing.destroyDrawingCache();
                } else {
                    ToastUtils.showAlertToast(this, "Oops! Image could not be saved. Please try again", ToastType.FAILURE_ALERT);
                }

                drawer.close();

                break;

/////////// pen color choose
            case R.id.red_pen:
                drawing.setErase(false);
                drawing.setColor("#ff0000");
                drawing.setBrushSize(getPenSize(sizeValuePosition));

                drawer.close();
                break;
            case R.id.yellow_pen:
                drawing.setErase(false);
                drawing.setColor("#fcff00");
                drawing.setBrushSize(getPenSize(sizeValuePosition));

                drawer.close();

                break;
            case R.id.blue_dark_pen:
                drawing.setErase(false);
                drawing.setColor("#1666d0");
                drawing.setBrushSize(getPenSize(sizeValuePosition));
                drawer.close();
                break;
            case R.id.orange_pen:
                drawing.setErase(false);
                drawing.setColor("#e27408");
                drawing.setBrushSize(getPenSize(sizeValuePosition));
                drawer.close();
                break;

            case R.id.purple_pen:
                drawing.setErase(false);
                drawing.setColor("#fb67f2");
                drawing.setBrushSize(getPenSize(sizeValuePosition));
                drawer.close();
                break;
            case R.id.blue_light_pen:
                drawing.setErase(false);
                drawing.setColor("#4ea1bb");
                drawer.close();
                break;
            case R.id.black_pen:
                drawing.setErase(false);
                drawing.setColor("#000000");
                drawer.close();
                break;


            /////////// brush color choose
            case R.id.red_brush:
                drawing.setErase(false);
                drawing.setColor("#ff0000");
                drawing.setBrushSize(getBrushSize(sizeValuePosition));
                drawer.close();
                break;
            case R.id.yellow_brush:
                drawing.setErase(false);
                drawing.setColor("#fcff00");
                drawing.setBrushSize(getBrushSize(sizeValuePosition));
                drawer.close();
                break;
            case R.id.blue_brush:
                drawing.setErase(false);
                drawing.setColor("#1666d0");
                drawing.setBrushSize(getBrushSize(sizeValuePosition));
                drawer.close();

                break;
            case R.id.orange_brush:
                drawing.setErase(false);
                drawing.setColor("#e27408");
                drawing.setBrushSize(getBrushSize(sizeValuePosition));
                drawer.close();

                break;

            case R.id.purple_brush:
                drawing.setErase(false);
                drawing.setColor("#fb67f2");
                drawing.setBrushSize(getBrushSize(sizeValuePosition));
                drawer.close();
                break;
            case R.id.blue_light_brush:
                drawing.setErase(false);
                drawing.setColor("#4ea1bb");
                drawing.setBrushSize(getBrushSize(sizeValuePosition));
                drawer.close();
                break;
            case R.id.black_brush:
                drawing.setErase(false);
                drawing.setColor("#000000");
                drawing.setBrushSize(getBrushSize(sizeValuePosition));
                drawer.close();
                break;

            /////////// size choose
            case R.id.size_one:
                sizeValuePosition = 0;
                setDrawSizeWithDrawType(draw_type);
                drawer.close();
                break;
            case R.id.size_two:
                sizeValuePosition = 1;
                setDrawSizeWithDrawType(draw_type);
                drawer.close();
                break;
            case R.id.size_three:
                sizeValuePosition = 2;
                setDrawSizeWithDrawType(draw_type);
                drawer.close();
                break;
            case R.id.size_four:
                sizeValuePosition = 3;
                setDrawSizeWithDrawType(draw_type);
                drawer.close();
                break;
            case R.id.size_five:
                sizeValuePosition = 4;
                setDrawSizeWithDrawType(draw_type);
                drawer.close();
                break;

            default:
                super.onClick(v);
                break;
        }
    }

    private void sendSkecthTochat(String imgSaved) {
//        String imgUri = getImagePatFromURI(imgSaved);

        DataShareImage mDataShareImage = new DataShareImage();
        mDataShareImage.setImgUrl(imgSaved);
        mDataShareImage.setSketchType(true);

        ArrayList<DataShareImage> mListString = new ArrayList<DataShareImage>();
        mListString.add(mDataShareImage);

        Intent mIntent = new Intent();
        Bundle mBundle = new Bundle();
        mBundle.putSerializable(Constants.B_RESULT, mListString);
        mIntent.putExtras(mBundle);
        setResult(Constants.SketchToSelection, mIntent);
        finish();

    }

    private void openPenChoice() {

//        float x_pos=side_panel.getWidth()+side_panel.getX();
//        float y_pos=pen.getY();
//        lnr_option_pen.setX(x_pos);
//        lnr_option_pen.setY(y_pos);
//        lnr_option_pen.setVisibility(View.VISIBLE);

        int arr[] = new int[2];
        pen.getLocationInWindow(arr);

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) lnr_option_pen.getLayoutParams();
        layoutParams.leftMargin = arr[0] + CommonMethods.getDensityPixel(this);
        layoutParams.topMargin = arr[1] / 9;
        lnr_option_pen.setLayoutParams(layoutParams);
        lnr_option_pen.setVisibility(View.VISIBLE);
    }

    private void openBrushChoice() {

        int arr[] = new int[2];
        brush.getLocationInWindow(arr);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) lnr_option_brush.getLayoutParams();
        layoutParams.leftMargin = arr[0] + CommonMethods.getDensityPixel(this);
        layoutParams.topMargin = arr[1] / 2;
        lnr_option_brush.setLayoutParams(layoutParams);
        lnr_option_brush.setVisibility(View.VISIBLE);
    }

    private void openSizeChoice() {
        int arr[] = new int[2];
        eraser.getLocationInWindow(arr);
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) lnr_option_size.getLayoutParams();
        layoutParams.leftMargin = arr[0] + CommonMethods.getDensityPixel(this);
        layoutParams.topMargin = arr[1];
        lnr_option_size.setLayoutParams(layoutParams);
        lnr_option_size.setVisibility(View.VISIBLE);
    }

    //define size array value for pen,brush,eraser

    @Override
    public void onDrawerClosed() {
        if (lnr_option_pen.getVisibility() == View.VISIBLE)
            lnr_option_pen.setVisibility(View.GONE);

        if (lnr_option_brush.getVisibility() == View.VISIBLE)
            lnr_option_brush.setVisibility(View.GONE);

        if (lnr_option_size.getVisibility() == View.VISIBLE)
            lnr_option_size.setVisibility(View.GONE);
    }

    @Override
    public void onDrawerOpened() {

    }

    public int getPenSize(int pos) {
        int a = arrPen[pos];
        return a;
    }

    public int getBrushSize(int pos) {
        int a = arrBrush[pos];
        return a;
    }


    public void setDrawSizeWithDrawType(int draw_type) {

        switch (draw_type) {

            case 1:
                //pen
                drawing.setBrushSize(getPenSize(sizeValuePosition));

                break;
            case 2:
                //brush
                drawing.setBrushSize(getBrushSize(sizeValuePosition));
                break;
            default:
                break;

        }
    }
}
