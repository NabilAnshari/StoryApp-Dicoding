package com.nabil.submission1_appstory.Local

sealed class Outcome <out R> private constructor() {
    data class Success <out T>(val data: T) : Outcome<T>()
    data class Error (val error: String) : Outcome<Nothing>()
    object Loading : Outcome<Nothing>()
}