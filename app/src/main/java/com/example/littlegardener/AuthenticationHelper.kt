package com.example.littlegardener

import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.FirebaseAuth

class AuthenticationHelper {
    companion object {
        fun getAuth(): FirebaseAuth {
            return FirebaseAuth.getInstance()
        }

        fun signInEmailPassword(email: String, password: String, listener:(Boolean)->Unit) {
            val auth = getAuth()
            auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    listener.invoke(true)
                } else {
                    listener.invoke(false)
                }
            }
        }

        fun signInOAuth(credential: AuthCredential, listener:(Boolean)->Unit) {
            val auth = getAuth()
            auth.signInWithCredential(credential).addOnCompleteListener {
                if (it.isSuccessful) {
                    listener.invoke(true)
                } else {
                    listener.invoke(false)
                }
            }
        }

        fun signUpEmailPassword(email: String, name: String, password: String, listener:(Boolean)->Unit) {
            val auth = getAuth()
            auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                if (it.isSuccessful) {
                    FirestoreHelper.createUserEntry(name)
                    listener.invoke(true)
                } else {
                    listener.invoke(false)
                }
            }
        }

        fun forgetPassword(email: String, listener:(Boolean)->Unit) {
            val auth = getAuth()
            auth.sendPasswordResetEmail(email).addOnCompleteListener {
                if (it.isSuccessful) {
                    listener.invoke(true)
                } else {
                    listener.invoke(false)
                }
            }
        }

        fun signOut() {
            val auth = getAuth()
            auth.signOut()
        }
    }
}