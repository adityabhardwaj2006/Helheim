package com.aditya.expensetracker.data.repoImpl

import com.aditya.expensetracker.common.ResultState
import com.aditya.expensetracker.data.Api.TransactionApi
import com.aditya.expensetracker.data.model.TransactionRequest
import com.aditya.expensetracker.data.model.TransactionResponse
import com.aditya.expensetracker.data.model.getTransactionResponse
import com.aditya.expensetracker.data.model.Transaction
import com.aditya.expensetracker.data.model.TransactionItem
import com.aditya.expensetracker.data.TokenManager
import com.aditya.expensetracker.domain.repo.Repo
import com.aditya.expensetracker.domain.repo.TransactionRepo
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepoImpl @Inject constructor(
    private val api : TransactionApi,
    private val localRepo: Repo,
    private val tokenManager: TokenManager
) : TransactionRepo {

    private val _transactions = MutableStateFlow<ResultState<getTransactionResponse>>(ResultState.Idle)
    override val transactions: StateFlow<ResultState<getTransactionResponse>> = _transactions.asStateFlow()

    override suspend fun saveTransaction(
        amount: Double,
        date: Long?,
        type: String,
        paidTo: String?,
        category: String?,
        savedFor: String?,
        receivedFrom: String?,
        paymentMethod: String,
        token: String
    ): TransactionResponse {
        return api.saveTransaction(
            TransactionRequest(
                amount = amount,
                date = date,
                type = type,
                paidTo = paidTo,
                category = category,
                savedFor = savedFor,
                receivedFrom = receivedFrom,
                paymentMethod = paymentMethod
            ),
            token = token
        )
    }

    override suspend fun getTransactions(token: String): getTransactionResponse {
        val currentState = _transactions.value
        val shouldShowLoading = currentState !is ResultState.Success<*> || 
                (currentState.data as getTransactionResponse).transactions.isEmpty()
        
        if (shouldShowLoading) {
            _transactions.value = ResultState.Loading
        }

        return try {
            val userId = tokenManager.getUserId().first() ?: ""
            val localList = localRepo.getAllTransaction(userId).first()

            if (localList.isNotEmpty()) {
                val newList = localList.map {
                    TransactionItem(
                        _id = it.backendId,
                        localId = it.id,
                        amount = it.amount,
                        date = it.date,
                        type = it.type,
                        paidTo = it.paidTo,
                        category = it.category,
                        savedFor = it.savedFor,
                        receivedFrom = it.receivedFrom,
                        paymentMethod = it.paymentMethod
                    )
                }.distinctBy { it._id ?: it.localId }
                val response = getTransactionResponse(success = true, transactions = newList)
                _transactions.value = ResultState.Success(response)
                response
            } else {
                if (token.replace("Bearer ", "").trim().isEmpty()) {
                    val response = getTransactionResponse(success = false, transactions = emptyList())
                    _transactions.value = ResultState.Error("Authentication token is missing")
                    return response
                }
                val networkResponse = api.getTransactions(token)
                val distinctTransactions = networkResponse.transactions.distinctBy { it._id ?: it.localId }
                
                distinctTransactions.forEach {
                    localRepo.insertTransaction(
                        Transaction(
                            backendId = it._id,
                            amount = it.amount,
                            date = it.date,
                            type = it.type,
                            paidTo = it.paidTo,
                            category = it.category,
                            savedFor = it.savedFor,
                            receivedFrom = it.receivedFrom,
                            paymentMethod = it.paymentMethod,
                            userId = userId
                        )
                    )
                }
                val response = networkResponse.copy(transactions = distinctTransactions)
                _transactions.value = ResultState.Success(response)
                response
            }
        } catch (e: Exception) {
            if (e is retrofit2.HttpException && e.code() == 401) {
                tokenManager.clearToken()
            }
            val errorMessage = e.message ?: "Failed to fetch transactions"
            if (shouldShowLoading) {
                _transactions.value = ResultState.Error(errorMessage)
            }
            // Return a failed response instead of throwing to prevent crashes in background scopes
            getTransactionResponse(success = false, transactions = emptyList())
        }
    }

    override suspend fun deleteTransaction(token: String, id: String): TransactionResponse {
        return api.deleteTransaction(token, id)
    }

    override fun updateLocalState(response: getTransactionResponse) {
        _transactions.value = ResultState.Success(response)
    }


}
