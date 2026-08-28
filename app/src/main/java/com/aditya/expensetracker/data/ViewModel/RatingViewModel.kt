package com.aditya.expensetracker.data.ViewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.model.RatingRequest
import com.aditya.expensetracker.data.model.SignUpResponse
import com.aditya.expensetracker.domain.repo.RatingRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RatingViewModel @Inject constructor(
    private val ratingRepo: RatingRepo
): ViewModel() {
    private val _ratingState = MutableStateFlow<ResultState<SignUpResponse>>(ResultState.Idle)
    val ratingState = _ratingState.asStateFlow()

    fun giveRating(
        rating : Int,
        feedback : String
    ){
        _ratingState.value = ResultState.Loading
        viewModelScope.launch {
            try {
                val response = ratingRepo.giveRating(rating,feedback)
                _ratingState.value = ResultState.Success(response)
            }catch (err: Exception){
                _ratingState.value = ResultState.Error(err.message?:"Something went wrong")
            }

        }


    }

}