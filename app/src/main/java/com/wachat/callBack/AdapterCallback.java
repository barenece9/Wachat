package com.wachat.callBack;

/**
 * Created by Priti Chatterjee on 28-08-2015.
 */
public interface AdapterCallback {

    /**
     * Used to get performed the click operation on the Recycler Or List Item View
     * @param {Object Will be Cast as needed in the Caller }
     */
    void OnClickPerformed(Object mObject);

    /**
     * Used to get performed Pagination
     * @param {Object Will be Cast as needed in the Caller }
     */
    void onPagination();
}
