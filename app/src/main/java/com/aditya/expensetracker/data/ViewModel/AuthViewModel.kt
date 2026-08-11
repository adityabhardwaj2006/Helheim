package com.aditya.expensetracker.data.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.model.LoginResponse
import com.aditya.expensetracker.data.model.SignUpResponse
import com.aditya.expensetracker.domain.repo.AuthRepo
import com.aditya.expensetracker.data.TokenManager
import com.aditya.expensetracker.data.model.UserDTO
import com.aditya.expensetracker.data.model.profileResponse
import dagger.hilt.android.lifecycle.HiltViewModel
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
    private val tokenManager: TokenManager
) : ViewModel(){

    private val _userState = MutableStateFlow<ResultState<profileResponse>?>(ResultState.Idle)
    val userState = _userState.asStateFlow()
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

    fun logout(){
        viewModelScope.launch {
            tokenManager.clearToken()
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
            _userState.value = ResultState.Loading
            try{
                val response = authRepo.getProfile()
                _userState.value = ResultState.Success(response)
            }catch (err : Exception){
                _userState.value = ResultState.Error(err.message?: "Cannot get profile, please try again")
            }
        }

    }

    }


