package com.wachat.dialog;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.text.Html;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebView;
import android.widget.TextView;

import com.wachat.R;
import com.wachat.util.Constants;

/**
 * Created by Sanjit on 11-03-2016.
 */
public class CopyRightDialog extends DialogFragment {

    private static final String COPYRIGHT_INFO = "copyright";
    private static final String TERMS_OF_USAGE = "terms";
    private static final String PRIVACY_POLICY = "privacy";
    private String type = "";


    private TextView tv_copy_right_txt;
    private WebView copyright_dialog_webview;


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getBundleData();
    }

    private void getBundleData() {
        Bundle mBundle = getArguments();
        if (mBundle != null && mBundle.containsKey(Constants.B_TYPE)) {
            type = mBundle.getString(Constants.B_TYPE);
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getDialog().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getDialog().setCanceledOnTouchOutside(true);
        View view = inflater.inflate(R.layout.dialog_copy_right, container, false);
//        tv_copy_right_txt = (TextView) view.findViewById(R.id.tv_copy_right_txt);
//        tv_copy_right_txt.setText(Html
//                .fromHtml(getResources().getString(R.string.vhortext_copyright_details)).toString(),TextView.BufferType.SPANNABLE);
        copyright_dialog_webview = (WebView)view.findViewById(R.id.copyright_dialog_webview);
        copyright_dialog_webview.loadData(String.valueOf(Html
                .fromHtml("<![CDATA[<body style=\"text-align:justify;color:#222222; \">"
                        + getResources().getString(R.string.vhortext_copyright_details)
                        + "</body>]]>")), "text/html", "utf-8");

        return view;
    }

    private String getType(){
        if(TextUtils.isEmpty(type)){
            return getResources().getString(R.string.vhortext_copyright_details);
        }
        
        if(type.equals(COPYRIGHT_INFO)){
            return getResources().getString(R.string.vhortext_copyright_details);
        }else if(type.equals(TERMS_OF_USAGE)){
            return getResources().getString(R.string.vhortext_copyright_details);
        }else if(type.equals(PRIVACY_POLICY)){
            return getResources().getString(R.string.vhortext_copyright_details);
        }
        return getResources().getString(R.string.vhortext_copyright_details);
    }
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


    }

}
