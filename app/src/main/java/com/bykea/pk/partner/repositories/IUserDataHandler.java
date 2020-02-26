package com.bykea.pk.partner.repositories;

import com.bykea.pk.partner.dal.source.remote.response.BookingListingResponse;
import com.bykea.pk.partner.models.data.DirectionDropOffData;
import com.bykea.pk.partner.models.data.RankingResponse;
import com.bykea.pk.partner.models.data.SignUpAddNumberResponse;
import com.bykea.pk.partner.models.data.SignUpCompleteResponse;
import com.bykea.pk.partner.models.data.SignUpOptionalDataResponse;
import com.bykea.pk.partner.models.data.SignUpSettingsResponse;
import com.bykea.pk.partner.models.data.SignupUplodaImgResponse;
import com.bykea.pk.partner.models.response.AcceptCallResponse;
import com.bykea.pk.partner.models.response.AcceptLoadboardBookingResponse;
import com.bykea.pk.partner.models.response.AddSavedPlaceResponse;
import com.bykea.pk.partner.models.response.ArrivedResponse;
import com.bykea.pk.partner.models.response.BankAccountListResponse;
import com.bykea.pk.partner.models.response.BankDetailsResponse;
import com.bykea.pk.partner.models.response.BeginRideResponse;
import com.bykea.pk.partner.models.response.BiometricApiResponse;
import com.bykea.pk.partner.models.response.CancelRideResponse;
import com.bykea.pk.partner.models.response.ChangePinResponse;
import com.bykea.pk.partner.models.response.CheckDriverStatusResponse;
import com.bykea.pk.partner.models.response.CommonResponse;
import com.bykea.pk.partner.models.response.ContactNumbersResponse;
import com.bykea.pk.partner.models.response.ConversationChatResponse;
import com.bykea.pk.partner.models.response.ConversationResponse;
import com.bykea.pk.partner.models.response.DownloadAudioFileResponse;
import com.bykea.pk.partner.models.response.DriverDestResponse;
import com.bykea.pk.partner.models.response.DriverPerformanceResponse;
import com.bykea.pk.partner.models.response.DriverStatsResponse;
import com.bykea.pk.partner.models.response.DriverVerifiedBookingResponse;
import com.bykea.pk.partner.models.response.EndRideResponse;
import com.bykea.pk.partner.models.response.FeedbackResponse;
import com.bykea.pk.partner.models.response.ForgotPasswordResponse;
import com.bykea.pk.partner.models.response.FreeDriverResponse;
import com.bykea.pk.partner.models.response.GeocoderApi;
import com.bykea.pk.partner.models.response.GetCitiesResponse;
import com.bykea.pk.partner.models.response.GetConversationIdResponse;
import com.bykea.pk.partner.models.response.GetProfileResponse;
import com.bykea.pk.partner.models.response.GetSavedPlacesResponse;
import com.bykea.pk.partner.models.response.GetZonesResponse;
import com.bykea.pk.partner.models.response.HeatMapUpdatedResponse;
import com.bykea.pk.partner.models.response.LoadBoardResponse;
import com.bykea.pk.partner.models.response.LocationResponse;
import com.bykea.pk.partner.models.response.LoginResponse;
import com.bykea.pk.partner.models.response.LogoutResponse;
import com.bykea.pk.partner.models.response.MultiDeliveryAcceptCallResponse;
import com.bykea.pk.partner.models.response.MultiDeliveryCallDriverAcknowledgeResponse;
import com.bykea.pk.partner.models.response.MultiDeliveryCancelBatchResponse;
import com.bykea.pk.partner.models.response.MultiDeliveryCompleteRideResponse;
import com.bykea.pk.partner.models.response.MultiDeliveryDriverArrivedResponse;
import com.bykea.pk.partner.models.response.MultiDeliveryDriverStartedResponse;
import com.bykea.pk.partner.models.response.MultiDeliveryFeedbackResponse;
import com.bykea.pk.partner.models.response.PilotStatusResponse;
import com.bykea.pk.partner.models.response.ProblemPostResponse;
import com.bykea.pk.partner.models.response.RegisterResponse;
import com.bykea.pk.partner.models.response.RejectCallResponse;
import com.bykea.pk.partner.models.response.SendMessageResponse;
import com.bykea.pk.partner.models.response.ServiceTypeResponse;
import com.bykea.pk.partner.models.response.ShahkarResponse;
import com.bykea.pk.partner.models.response.TopUpPassWalletResponse;
import com.bykea.pk.partner.models.response.TripHistoryResponse;
import com.bykea.pk.partner.models.response.TripMissedHistoryResponse;
import com.bykea.pk.partner.models.response.UpdateAppVersionResponse;
import com.bykea.pk.partner.models.response.UpdateConversationStatusResponse;
import com.bykea.pk.partner.models.response.UpdateDropOffResponse;
import com.bykea.pk.partner.models.response.UpdateProfileResponse;
import com.bykea.pk.partner.models.response.UpdateRegIDResponse;
import com.bykea.pk.partner.models.response.UploadAudioFile;
import com.bykea.pk.partner.models.response.UploadDocumentFile;
import com.bykea.pk.partner.models.response.UploadImageFile;
import com.bykea.pk.partner.models.response.VerifyCodeResponse;
import com.bykea.pk.partner.models.response.VerifyNumberResponse;
import com.bykea.pk.partner.models.response.WalletHistoryResponse;
import com.bykea.pk.partner.models.response.ZoneAreaResponse;

import java.util.ArrayList;

public interface IUserDataHandler {

    /***
     * Update Drop off location response received from API Server.
     * @param commonResponse Pojo class which hold latest driver destination response.
     */
    void onDropOffUpdated(DriverDestResponse commonResponse);

    void onForgotPassword(ForgotPasswordResponse commonResponse);

    void onNumberVerification(VerifyNumberResponse commonResponse);

    void onCodeVerification(VerifyCodeResponse commonResponse);

    void onUploadFile(UploadDocumentFile uploadDocumentFile);

    void onUploadAudioFile(UploadAudioFile uploadAudioFile);

    void onUploadImageFile(UploadImageFile uploadAudioFile);

    void onUserRegister(RegisterResponse registerUser);

    void onUserLogin(LoginResponse loginResponse);

    void onPilotLogout(LogoutResponse logoutResponse);

    void onUserLogout(LogoutResponse logoutResponse);

    void onGetServiceTypes(ServiceTypeResponse serviceTypeResponse);

    void onGetTripHistory(TripHistoryResponse tripHistoryResponse);

    void onGetMissedTripHistory(TripMissedHistoryResponse tripHistoryResponse);

    void onReverseGeocode(GeocoderApi response);

    void onUpdateStatus(PilotStatusResponse pilotStatusResponse);

    void onGetSettingsResponse(boolean isUpdated);

    void getHeatMap(ArrayList<HeatMapUpdatedResponse> heatMapResponse);

    void onUpdateProfile(UpdateProfileResponse profileResponse);

    void getWalletData(WalletHistoryResponse walletHistoryResponse);

    void getAccountNumbers(BankAccountListResponse walletHistoryResponse);

    void onBankDetailsResponse(BankDetailsResponse response);

    void getContactNumbers(ContactNumbersResponse walletHistoryResponse);

    void onRunningTrips(CheckDriverStatusResponse response);

    void onFreeDriver(FreeDriverResponse freeDriverResponse);

    /*TRIP CALLBACK METHODS*/
    void onAcceptCall(AcceptCallResponse acceptCallResponse);

    void onRejectCall(RejectCallResponse rejectCallResponse);


    void onCancelRide(CancelRideResponse cancelRideResponse);


    void onEndRide(EndRideResponse endRideResponse);


    void onArrived(ArrivedResponse arrivedResponse);


    void onBeginRide(BeginRideResponse beginRideResponse);

    void onFeedback(FeedbackResponse feedbackResponse);

    void onGetConversations(ConversationResponse response);

    void onSendMessage(SendMessageResponse response);

    void onGetConversationChat(ConversationChatResponse response);

    void onGetConversationId(GetConversationIdResponse response);

    void onAck(String msg);

    void onUpdateConversationStatus(UpdateConversationStatusResponse response);

    void onGetProfileResponse(GetProfileResponse response);

    void onDriverStatsResponse(DriverStatsResponse response);

    void onUpdateDropOff(UpdateDropOffResponse response);

    void onCommonResponse(CommonResponse response);

    void onChangePinResponse(ChangePinResponse response);

    void onCitiesResponse(GetCitiesResponse response);

    void onProblemPosted(ProblemPostResponse response);

    void onDownloadAudio(DownloadAudioFileResponse response);

    void onUpdateRegid(UpdateRegIDResponse response);

    void onAddSavedPlaceResponse(AddSavedPlaceResponse response);

    void onDeleteSavedPlaceResponse();

    void onGetSavedPlacesResponse(GetSavedPlacesResponse response);

    void onZonesResponse(GetZonesResponse response);

    void onZoneAreasResponse(ZoneAreaResponse response);

    void onShahkarResponse(ShahkarResponse response);

    void onBonusChartResponse(RankingResponse response);

    void onDriverPerformanceResponse(DriverPerformanceResponse response);

    void onLoadBoardResponse(LoadBoardResponse response);

    void onTopUpPassWallet(TopUpPassWalletResponse response);

    void onLocationUpdate(LocationResponse response);

    void onSignUpSettingsResponse(SignUpSettingsResponse response);

    void onSignUpAddNumberResponse(SignUpAddNumberResponse response);

    void onSignUpImageResponse(SignupUplodaImgResponse response);

    void onSignUpOptionalResponse(SignUpOptionalDataResponse response);

    void onSignupCompleteResponse(SignUpCompleteResponse response);

    void onBiometricApiResponse(BiometricApiResponse response);

    /**
     * callback for loadboard accept api call
     *
     * @param response loadboard specific booking acceptance response
     */
    void onAcceptLoadboardBookingResponse(AcceptLoadboardBookingResponse response);

    void onError(int errorCode, String errorMessage);

    //#region MultiDelivery Sockets Response onLoadBoardListFragmentInteractionListener

    /**
     * This method will be invoked when driver acknowledge response received.
     *
     * @param response The {@link MultiDeliveryCallDriverAcknowledgeResponse} object.
     */
    void onDriverAcknowledgeResponse(MultiDeliveryCallDriverAcknowledgeResponse response);

    /**
     * This method will be invoked when multi delivery accept call response received
     *
     * @param response The {@link MultiDeliveryAcceptCallResponse} object.
     * @see com.bykea.pk.partner.utils.ApiTags#MULTI_DELIVERY_SOCKET_ACCEPT_CALL
     */
    void onMultiDeliveryAcceptCall(MultiDeliveryAcceptCallResponse response);

    /**
     * This method will be invoked when multi delivery arrived response received
     *
     * @param response The {@link MultiDeliveryDriverArrivedResponse} object.
     * @see com.bykea.pk.partner.utils.ApiTags#MULTI_DELIVERY_SOCKET_DRIVER_ARRIVED
     */
    void onMultiDeliveryDriverArrived(MultiDeliveryDriverArrivedResponse response);

    /**
     * This method will be invoked when multi delivery started response received
     *
     * @param response The {@link MultiDeliveryDriverStartedResponse} object.
     * @see com.bykea.pk.partner.utils.ApiTags#MULTI_DELIVERY_SOCKET_DRIVER_STARTED
     */
    void onMultiDeliveryDriverStarted(MultiDeliveryDriverStartedResponse response);

    /**
     * This method will be invoked when multi delivery finished response received
     *
     * @param response The {@link MultiDeliveryCompleteRideResponse} object.
     * @param data     The {@linkplain DirectionDropOffData} object.
     * @see com.bykea.pk.partner.utils.ApiTags#MULTI_DELIVERY_SOCKET_TRIP_FINISHED
     */
    void onMultiDeliveryDriverRideFinish(MultiDeliveryCompleteRideResponse response,
                                         DirectionDropOffData data);

    /**
     * This method will be invoked when multi delivery feedback response received
     *
     * @param response The {@link MultiDeliveryFeedbackResponse} object.
     * @see com.bykea.pk.partner.utils.ApiTags#MULTI_DELIVERY_SOCKET_TRIP_FEEDBACK_DRIVER
     */
    void onMultiDeliveryDriverFeedback(MultiDeliveryFeedbackResponse response);

    /**
     * This method will be invoked when multi delivery batch request canceled response received
     *
     * @param response The {@link MultiDeliveryCancelBatchResponse} object.
     * @see com.bykea.pk.partner.utils.ApiTags#MULTI_DELIVERY_SOCKET_BATCH_CANCELED
     */
    void onMultiDeliveryDriverCancelBatch(MultiDeliveryCancelBatchResponse response);

    //end region

    void onUpdateAppVersionResponse(UpdateAppVersionResponse response);

    /**
     * this method will be invoked when booking listing received from kronos
     *
     * @param bookingListingResponse callback to send data to the controller on complete
     * @see com.bykea.pk.partner.models.data.SettingsData for kronos URLs
     */
    void onBookingListingResponse(BookingListingResponse bookingListingResponse);

    /**
     * this method can be invoked to get stats data from kronos
     *
     * @param driverVerifiedBookingResponse callback to send data to the controller on complete
     * @see com.bykea.pk.partner.models.data.SettingsData for kronos URLs
     */
    void onDriverVerifiedBookingResponse(DriverVerifiedBookingResponse driverVerifiedBookingResponse);
}
