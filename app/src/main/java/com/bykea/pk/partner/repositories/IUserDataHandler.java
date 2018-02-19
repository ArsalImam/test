package com.bykea.pk.partner.repositories;

import com.bykea.pk.partner.models.response.AcceptCallResponse;
import com.bykea.pk.partner.models.response.AccountNumbersResponse;
import com.bykea.pk.partner.models.response.ArrivedResponse;
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
import com.bykea.pk.partner.models.response.HeatMapResponse;
import com.bykea.pk.partner.models.response.HeatMapUpdatedResponse;
import com.bykea.pk.partner.models.response.LoginResponse;
import com.bykea.pk.partner.models.response.LogoutResponse;
import com.bykea.pk.partner.models.response.PilotStatusResponse;
import com.bykea.pk.partner.models.response.ProblemPostResponse;
import com.bykea.pk.partner.models.response.RegisterResponse;
import com.bykea.pk.partner.models.response.RejectCallResponse;
import com.bykea.pk.partner.models.response.SendMessageResponse;
import com.bykea.pk.partner.models.response.ServiceTypeResponse;
import com.bykea.pk.partner.models.response.SettingsResponse;
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

    void getAccountNumbers(AccountNumbersResponse walletHistoryResponse);

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

    void onError(int errorCode, String errorMessage);
}
