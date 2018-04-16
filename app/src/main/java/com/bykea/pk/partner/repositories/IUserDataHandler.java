package com.bykea.pk.partner.repositories;

import com.bykea.pk.partner.models.data.SignUpAddNumberResponse;
import com.bykea.pk.partner.models.data.SignUpCompleteResponse;
import com.bykea.pk.partner.models.data.SignUpOptionalDataResponse;
import com.bykea.pk.partner.models.data.SignUpSettingsResponse;
import com.bykea.pk.partner.models.data.SignupUplodaImgResponse;
import com.bykea.pk.partner.models.response.AcceptCallResponse;
import com.bykea.pk.partner.models.response.BankAccountListResponse;
import com.bykea.pk.partner.models.response.AddSavedPlaceResponse;
import com.bykea.pk.partner.models.response.ArrivedResponse;
import com.bykea.pk.partner.models.response.BankDetailsResponse;
import com.bykea.pk.partner.models.response.BeginRideResponse;
import com.bykea.pk.partner.models.response.CancelRideResponse;
import com.bykea.pk.partner.models.response.ChangePinResponse;
import com.bykea.pk.partner.models.response.CheckDriverStatusResponse;
import com.bykea.pk.partner.models.response.CommonResponse;
import com.bykea.pk.partner.models.response.ContactNumbersResponse;
import com.bykea.pk.partner.models.response.ConversationChatResponse;
import com.bykea.pk.partner.models.response.ConversationResponse;
import com.bykea.pk.partner.models.response.DownloadAudioFileResponse;
import com.bykea.pk.partner.models.response.DriverDestResponse;
import com.bykea.pk.partner.models.response.DriverStatsResponse;
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
import com.bykea.pk.partner.models.response.LocationResponse;
import com.bykea.pk.partner.models.response.LoginResponse;
import com.bykea.pk.partner.models.response.LogoutResponse;
import com.bykea.pk.partner.models.response.PilotStatusResponse;
import com.bykea.pk.partner.models.response.ProblemPostResponse;
import com.bykea.pk.partner.models.response.RegisterResponse;
import com.bykea.pk.partner.models.response.RejectCallResponse;
import com.bykea.pk.partner.models.response.SendMessageResponse;
import com.bykea.pk.partner.models.response.ServiceTypeResponse;
import com.bykea.pk.partner.models.response.TopUpPassWalletResponse;
import com.bykea.pk.partner.models.response.TripHistoryResponse;
import com.bykea.pk.partner.models.response.TripMissedHistoryResponse;
import com.bykea.pk.partner.models.response.UpdateConversationStatusResponse;
import com.bykea.pk.partner.models.response.UpdateDropOffResponse;
import com.bykea.pk.partner.models.response.UpdateProfileResponse;
import com.bykea.pk.partner.models.response.UpdateRegIDResponse;
import com.bykea.pk.partner.models.response.UploadAudioFile;
import com.bykea.pk.partner.models.response.UploadDocumentFile;
import com.bykea.pk.partner.models.response.VerifyCodeResponse;
import com.bykea.pk.partner.models.response.VerifyNumberResponse;
import com.bykea.pk.partner.models.response.WalletHistoryResponse;
import com.bykea.pk.partner.models.response.ZoneAreaResponse;

import java.util.ArrayList;

public interface IUserDataHandler {

    void onDropOffUpdated(DriverDestResponse commonResponse);

    void onForgotPassword(ForgotPasswordResponse commonResponse);

    void onNumberVerification(VerifyNumberResponse commonResponse);

    void onCodeVerification(VerifyCodeResponse commonResponse);

    void onUploadFile(UploadDocumentFile uploadDocumentFile);

    void onUploadAudioFile(UploadAudioFile uploadAudioFile);

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

    void onTopUpPassWallet(TopUpPassWalletResponse response);
    void onLocationUpdate(LocationResponse response);
    void onSignUpSettingsResponse(SignUpSettingsResponse response);
    void onSignUpAddNumberResponse(SignUpAddNumberResponse response);
    void onSignUpImageResponse(SignupUplodaImgResponse response);
    void onSignUpOptionalResponse(SignUpOptionalDataResponse response);
    void onSignupCompleteResponse(SignUpCompleteResponse response);

    void onError(int errorCode, String errorMessage);
}
