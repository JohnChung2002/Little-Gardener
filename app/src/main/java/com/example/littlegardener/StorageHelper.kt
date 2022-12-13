package com.example.littlegardener

import android.content.Context
import android.net.Uri
import com.google.firebase.storage.FirebaseStorage

class StorageHelper {
    companion object {
        private fun getStorage(): FirebaseStorage {
            return FirebaseStorage.getInstance()
        }

        fun uploadImage(context: Context, uri: Uri, listener: (String) -> Unit) {
            val storage = getStorage()
            if (uri.toString()
                    .contains(context.resources.getString(R.string.firebase_storage_url))
            ) {
                listener.invoke(uri.toString())
            } else {
                val ref = storage.reference.child("images/${uri.lastPathSegment}")
                ref.putFile(uri).addOnSuccessListener {
                    ref.downloadUrl.addOnSuccessListener {
                        listener.invoke(it.toString())
                    }
                }
            }
        }

        fun uploadImages(context: Context, uriList: MutableList<Uri>, listener: (List<String>) -> Unit) {
            val storage = getStorage()
            val images = mutableListOf<String>()
            for (uri in uriList) {
                if (uri.toString().contains(context.resources.getString(R.string.firebase_storage_url))) {
                    images.add(uri.toString())
                    if (images.size == uriList.size) {
                        listener.invoke(images)
                    }
                } else {
                    val ref = storage.reference.child("images/${uri.lastPathSegment}")
                    ref.putFile(uri).addOnSuccessListener {
                        ref.downloadUrl.addOnSuccessListener {
                            images.add(it.toString())
                            if (images.size == uriList.size) {
                                uriList.clear()
                                listener.invoke(images)
                            }
                        }
                    }
                }
            }
        }
    }
}