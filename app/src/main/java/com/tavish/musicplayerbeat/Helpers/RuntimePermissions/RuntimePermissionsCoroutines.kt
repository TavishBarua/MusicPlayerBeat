package com.tavish.musicplayerbeat.Helpers.RuntimePermissions


import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/*
    suspend fun androidx.fragment.app.FragmentActivity.askPermission(vararg permissions: String): PermissionResult = suspendCoroutine { continuation ->
        var resumed = false
        RuntimePermission.askPermission(this)
            .request(permissions.toList())
            .onResponse { result ->
                if (!resumed) {
                    resumed = true
                    when {
                        result.isAccepted -> continuation.resume(result)
                        else -> continuation.resumeWithException(PermissionException(result))
                    }
                }
            }
            .ask()
    }


suspend fun androidx.fragment.app.Fragment.askPermission(vararg permissions: String): PermissionResult = suspendCoroutine { continuation ->
        var resumed = false
        when (activity) {
            null -> continuation.resumeWithException(NoActivityException())
            else -> RuntimePermission.askPermission(this)
                .request(permissions.toList())
                .onResponse { result ->
                    if (!resumed) {
                        resumed = true
                        when {
                            result.isAccepted -> continuation.resume(result)
                            else -> continuation.resumeWithException(PermissionException(result))
                        }
                    }
                }
                .ask()
        }
    }

*/



