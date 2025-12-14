package androidlead.weatherappui.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class AuthRepository {
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser?
        get() = firebaseAuth.currentUser

    suspend fun login(email: String, pass: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.signInWithEmailAndPassword(email, pass).await()
            if (result.user != null) {
                Result.Success(result.user!!)
            } else {
                Result.Error(Exception("User not found"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    suspend fun signup(name: String, email: String, pass: String): Result<FirebaseUser> {
        return try {
            val result = firebaseAuth.createUserWithEmailAndPassword(email, pass).await()
            if (result.user != null) {
                // Ideally, update profile with name here
                // val profileUpdates = userProfileChangeRequest { displayName = name }
                // result.user!!.updateProfile(profileUpdates).await()
                Result.Success(result.user!!)
            } else {
                Result.Error(Exception("User creation failed"))
            }
        } catch (e: Exception) {
            Result.Error(e)
        }
    }

    fun logout() {
        firebaseAuth.signOut()
    }
}

sealed class Result<out T> {
    data class Success<out T>(val data: T) : Result<T>()
    data class Error(val exception: Exception) : Result<Nothing>()
}
