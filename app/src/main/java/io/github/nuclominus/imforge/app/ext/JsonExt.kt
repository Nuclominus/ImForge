package io.github.nuclominus.imforge.app.ext

import com.google.gson.Gson

fun Any.toJson(): String {
    return Gson().toJson(this)
}

inline fun <reified T> String.toObject(): T {
    return Gson().fromJson(this, T::class.java)
}