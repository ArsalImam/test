package com.bykea.pk.partner.dal.source.remote

import android.util.Log

import com.bykea.pk.partner.dal.source.remote.data.PersonalInfoData
import com.bykea.pk.partner.dal.source.remote.response.BaseResponse
import com.bykea.pk.partner.dal.source.remote.response.GetDriverProfile
import com.bykea.pk.partner.dal.source.remote.response.GetWithdrawalPaymentMethods
import com.bykea.pk.partner.dal.source.remote.response.WithdrawPostResponse
import com.bykea.pk.partner.dal.source.withdraw.WithdrawRepository

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class WithdrawRemoteDataSource {

    /**
     * API to get all payment method list from server
     *
     * @param userId of driver
     * @param tokenId of driver
     * @param callback to get results in case of failure or success
     */
    fun getAllPaymentMethods(userId: String, tokenId: String, callback: WithdrawRepository.LoadWithdrawalCallback<*>) {
        Backend.telos.getWithdrawalPaymentMethods(/*"http://www.mocky.io/v2/5d417cb83100007bc2539436", */tokenId, userId)
                .enqueue(object : Callback<GetWithdrawalPaymentMethods> {
                    override fun onResponse(call: Call<GetWithdrawalPaymentMethods>, response: Response<GetWithdrawalPaymentMethods>) {
                        Log.v(WithdrawRemoteDataSource::class.java.simpleName, response.toString())
                        if (response.isSuccessful) {
                            val methods = response.body()
                            Log.v(WithdrawRemoteDataSource::class.java.simpleName, methods!!.data!!.toString())
                            callback.onDataLoaded(methods.data)
                        } else {
                            callback.onDataNotAvailable("No Payment Methods Found")
                        }
                    }

                    override fun onFailure(call: Call<GetWithdrawalPaymentMethods>, t: Throwable) {
                        t.printStackTrace()
                        callback.onDataNotAvailable(t.localizedMessage)
                    }
                })
    }

    /**
     * API to get complete driver profile from server
     *
     * @param userId of driver
     * @param tokenId of driver
     * @param callback to get results in case of failure or success
     */
    fun getDriverProfile(userId: String, tokenId: String, callback: WithdrawRepository.LoadWithdrawalCallback<PersonalInfoData>) {
        Backend.telos.getDriverProfile(userId, tokenId, "d")
                .enqueue(object : Callback<GetDriverProfile> {
                    override fun onResponse(call: Call<GetDriverProfile>, response: Response<GetDriverProfile>) {
                        Log.v(WithdrawRemoteDataSource::class.java.simpleName, response.toString())
                        if (response.isSuccessful) {
                            val methods = response.body()
                            callback.onDataLoaded(methods!!.data)
                        } else {
                            callback.onDataNotAvailable("No Payment Methods Found")
                        }
                    }

                    override fun onFailure(call: Call<GetDriverProfile>, t: Throwable) {
                        t.printStackTrace()
                        callback.onDataNotAvailable(t.localizedMessage)
                    }
                })
    }

    /**
     * Perform withdrawal operation by executing API
     *
     * @param amount to withdraw
     * @param userId of driver
     * @param tokenId of driver
     * @param paymentMethod from which payment needs to delivered
     * @param callback to get results in case of failure or success
     */
    fun performWithdraw(amount: Int, userId: String, tokenId: String, paymentMethod: Int,
                        callback: WithdrawRepository.LoadWithdrawalCallback<Boolean>) {
        Backend.telos.getPerformWithdraw(/*"http://www.mocky.io/v2/5d42ee9a3200005700764438", */
                tokenId, userId, paymentMethod, amount)
                .enqueue(object : Callback<WithdrawPostResponse> {
                    override fun onResponse(call: Call<WithdrawPostResponse>, response: Response<WithdrawPostResponse>) {
                        Log.v(WithdrawRemoteDataSource::class.java.simpleName, response.toString())
                        if (response.isSuccessful) {
                            val baseResponse = response.body()
                            callback.onDataLoaded(baseResponse!!.isSuccess())
                        } else {
                            callback.onDataNotAvailable("No Payment Methods Found")
                        }
                    }

                    override fun onFailure(call: Call<WithdrawPostResponse>, t: Throwable) {
                        t.printStackTrace()
                        callback.onDataNotAvailable(t.localizedMessage)
                    }
                })
    }
}