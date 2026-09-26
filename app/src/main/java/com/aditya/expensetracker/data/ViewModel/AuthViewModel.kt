package com.aditya.expensetracker.data.ViewModel

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.model.LoginResponse
import com.aditya.expensetracker.data.model.SignUpResponse
import com.aditya.expensetracker.domain.repo.AuthRepo
import com.aditya.expensetracker.data.TokenManager
import com.aditya.expensetracker.data.model.profileResponse
import com.aditya.expensetracker.data.remote.AuthManager
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo : AuthRepo,
    private val tokenManager: TokenManager,
    private val authManager: AuthManager,
    @ApplicationContext private val context: Context
) : ViewModel(){

    private val TAG = "AuthDebug"

    private val _userState = MutableStateFlow<ResultState<profileResponse>?>(ResultState.Idle)
    val userState = _userState.asStateFlow()

    private val _updatePasswordState = MutableStateFlow<ResultState<SignUpResponse>>(ResultState.Idle)
    val updatePasswordState = _updatePasswordState.asStateFlow()

    private val _updateProfileState = MutableStateFlow<ResultState<Unit>>(ResultState.Idle)
    val updateProfileState = _updateProfileState.asStateFlow()

    val isLoggedIn: StateFlow<Boolean?> = tokenManager.getToken()
        .map { it != null }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    val userId: StateFlow<String?> = tokenManager.getUserId()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    private val _loginState = MutableStateFlow<ResultState<LoginResponse>>(ResultState.Idle)
    val loginState = _loginState.asStateFlow()

    private val _signUpState = MutableStateFlow<ResultState<SignUpResponse>>(ResultState.Idle)
    val signUpState = _signUpState.asStateFlow()

    fun login(
        username : String,
        password : String
    ){

        viewModelScope.launch {
            _loginState.value = ResultState.Loading
            try{
                val response = authRepo.login(username,password)
                _loginState.value = ResultState.Success(response)

            }catch (error : Exception){
                _loginState.value = ResultState.Error(error.message ?: "Cannot login, please try again")
            }
            }
        }

    fun getGoogleSignInIntent(): Intent {
        return authManager.getSignInIntent()
    }

    fun onGoogleSignInResult(data: Intent?) {
        viewModelScope.launch {
            _loginState.value = ResultState.Loading
            val result = authManager.handleSignInResult(data)
            result.onSuccess { userData ->
                try {
                    val response = authRepo.googleLogin(
                        idToken = userData.googleIdToken,
                        email = userData.email,
                        photoUrl = userData.profilePictureUrl?.toString()
                    )
                    _loginState.value = ResultState.Success(response)
                } catch (e: Exception) {
                    _loginState.value = ResultState.Error(e.message ?: "Backend login failed")
                }
            }.onFailure { e ->
                _loginState.value = ResultState.Error(e.message ?: "Google Sign-in failed")
            }
        }
    }

    fun logout(){
        viewModelScope.launch {
            authRepo.logout()
            authManager.signOut()
        }
    }

    fun signup(
        firstName : String,
        lastName : String,
        username : String,
        password : String
    ){
        viewModelScope.launch{
            _signUpState.value = ResultState.Loading
            try{
                val response =  authRepo.signup(
                    firstName,
                    lastName,
                    username,
                    password
                )
                _signUpState.value = ResultState.Success(response)
            }catch(err : Exception){

                _signUpState.value = ResultState.Error(err.message ?: "Cannot signup, please try again")

            }

        }
    }
    fun getProfile(){
        viewModelScope.launch {
            val currentState = _userState.value
            val hasData = (currentState as? ResultState.Success<profileResponse>)?.data?.data != null
            
            if (!hasData) {
                _userState.value = ResultState.Loading
            }

            try{
                val response = authRepo.getProfile()
                _userState.value = ResultState.Success(response)
            }catch (err : Exception){
                if (!hasData) {
                    val errorMessage = if (err is java.net.UnknownHostException || err is java.net.ConnectException) {
                        "Offline: Profile data not available"
                    } else {
                        err.message ?: "Cannot get profile, please try again"
                    }
                    _userState.value = ResultState.Error(errorMessage)
                }
            }
        }

    }

    fun updateProfile(
        firstName: String,
        lastName: String,
        username: String
    ) {
        viewModelScope.launch {
            _updateProfileState.value = ResultState.Loading
            try {
                authRepo.updateProfile(firstName, lastName, username)
                _updateProfileState.value = ResultState.Success(Unit)
                // Refresh profile data in state
                getProfile()
            } catch (err: Exception) {
                _updateProfileState.value = ResultState.Error(err.message ?: "Cannot update profile, please try again")
            }
        }
    }

    fun updatePassword(
        newPassword : String
    ){
        viewModelScope.launch {
            _updatePasswordState.value = ResultState.Loading
            try{
                val response = authRepo.updatePassword(newPassword)
                _updatePasswordState.value = ResultState.Success(response)
            }catch(err: Exception){
                _updatePasswordState.value = ResultState.Error(err.message?:"Something went wrong")
            }
        }

    }

}
