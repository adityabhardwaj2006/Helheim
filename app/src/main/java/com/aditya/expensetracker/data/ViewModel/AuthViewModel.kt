package com.aditya.expensetracker.data.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.model.LoginResponse
import com.aditya.expensetracker.data.model.SignUpResponse
import com.aditya.expensetracker.domain.repo.AuthRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val authRepo : AuthRepo
) : ViewModel(){

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
                _loginState.value = ResultState.Error(error.message ?: "Something went wrong")
            }
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

                _signUpState.value = ResultState.Error(err.message ?: "Something went wrong")

            }

        }
    }

    }


