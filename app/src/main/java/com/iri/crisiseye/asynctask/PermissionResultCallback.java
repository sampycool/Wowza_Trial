package com.iri.crisiseye.asynctask;

import java.util.ArrayList;

/**
 * Created by tsarkar on 03/01/18.
 */

public interface PermissionResultCallback {


    void PermissionGranted(int request_code);
    void PartialPermissionGranted(int request_code, ArrayList<String> granted_permissions);
    void PermissionDenied(int request_code);
    void NeverAskAgain(int request_code);
}