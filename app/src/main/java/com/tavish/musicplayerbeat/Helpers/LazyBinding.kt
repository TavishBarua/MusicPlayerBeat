package com.tavish.musicplayerbeat.Helpers

import android.app.Activity
import android.view.View
import androidx.annotation.IdRes

@JvmName("LazyBinding")
fun <T> unsafelazy(initializer:()->T)= lazy(LazyThreadSafetyMode.NONE, initializer)
fun<T: View> Activity.binder(@IdRes res:Int):Lazy<T>{
    @Suppress("UNCHECKED_CAST")
    return unsafelazy { findViewById(res) as T }
}

/*fun<T: View> Activity.viewbinder(@IdRes view:View,@IdRes res:Int ):Lazy<T>{
    @Suppress("UNCHECKED_CAST")
    return unsafelazy { view.findViewById(res) as T }
}*/
