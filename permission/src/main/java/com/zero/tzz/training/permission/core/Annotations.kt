package com.zero.tzz.training.permission.core

/**
 *
 * @author Zero_Tzz
 * @date 2019-10-18 14:32
 * @description PermissionSucceed
 */

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class PermissionSucceed(val permission: String)

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.RUNTIME)
annotation class PermissionFailed(val permission: String)
