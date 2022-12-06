package com.example.littlegardener

import com.google.firebase.storage.FirebaseStorage

class StorageHelper {
    companion object {
        fun getStorage(): FirebaseStorage {
            return FirebaseStorage.getInstance()
        }
    }
}