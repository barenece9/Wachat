package com.wachat.application;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ImageView;

import com.wachat.R;

/**
 * Created by Priti Chatterjee on 26-08-2015.
 */
public abstract class BaseFragment extends Fragment {

    protected BaseActivity mBaseActivity;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if(activity instanceof BaseActivity){
            mBaseActivity = (BaseActivity)activity;
        }
    }

//    @Override
//    public void onCreateOptionsMenu (Menu menu, MenuInflater inflater){
//        inflater.inflate(R.menu.menu_main, menu);
//        MenuItem item = menu.findItem(R.id.menu_search);
//        SearchView sv = new SearchView(mBaseActivity.getSupportActionBar().getThemedContext());
//        int searchImgId = getResources().getIdentifier("android:id/search_button", null, null);
//        sv.setIconified(true);
//        sv.setIconifiedByDefault(true);
////        sv.set
////        sv.setOnQueryTextListener(this);
//        MenuItemCompat.setShowAsAction(item, MenuItemCompat.SHOW_AS_ACTION_COLLAPSE_ACTION_VIEW | MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
//        MenuItemCompat.setActionView(item, sv);
//
//        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
//            @Override
//            public boolean onQueryTextSubmit(String query) {
//                onSearchPerformed(query);
//                return false;
//            }
//
//            @Override
//            public boolean onQueryTextChange(String newText) {
//                System.out.println("tap");
//                return false;
//            }
//        });
//
////        sv.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
////            @Override
////            public boolean onQueryTextSubmit(String query) {
////                System.out.println("search query submit");
////                return false;
////            }
////
////            @Override
////            public boolean onQueryTextChange(String newText) {
////                System.out.println("tap");
////                return false;
////            }
////        });
//    }

    public abstract void onSearchPerformed(String search);

    public void notifyFragment(){

    }
}
