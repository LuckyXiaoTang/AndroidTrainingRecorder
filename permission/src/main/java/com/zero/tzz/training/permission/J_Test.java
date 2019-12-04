package com.zero.tzz.training.permission;

import android.Manifest;

import com.zero.tzz.training.permission.core.PermissionFailed;
import com.zero.tzz.training.permission.core.PermissionHelper;
import com.zero.tzz.training.permission.core.PermissionSucceed;

/**
 * @author Zero_Tzz
 * @date 2019-10-29 18:06
 * @description J_Test
 */
public class J_Test {


    public void test() {
        PermissionHelper
                .permissions(Manifest.permission.CALL_PHONE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .request(this);
    }

    @PermissionSucceed(permission = Manifest.permission.CALL_PHONE)
    private void aaa1() {
        System.out.println("aaaaa1");
    }
    @PermissionFailed(permission = Manifest.permission.CALL_PHONE)
    private void aaa2() {
        System.out.println("aaaaa2");
    }

    @PermissionSucceed(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private void bbb1() {
        System.out.println("bbbbb1");
    }

    @PermissionFailed(permission = Manifest.permission.WRITE_EXTERNAL_STORAGE)
    private void bbb2() {
        System.out.println("bbbbb2");
    }
}
