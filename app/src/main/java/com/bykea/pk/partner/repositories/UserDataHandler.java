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
import com.bykea.pk.partner.models.response.UploadAudioFile;
import com.bykea.pk.partner.models.response.UploadDocumentFile;
import com.bykea.pk.partner.models.response.VerifyCodeResponse;
import com.bykea.pk.partner.models.response.VerifyNumberResponse;
import com.bykea.pk.partner.models.response.WalletHistoryResponse;


public class UserDataHandler implements IUserDataHandler {

    @Override
    public void onDropOffUpdated(DriverDestResponse commonResponse) {

    }

    @Override
    public void onForgotPassword(ForgotPasswordResponse commonResponse) {

    }

    @Override
    public void onNumberVerification(VerifyNumberResponse commonResponse) {

    }

    @Override
    public void onCodeVerification(VerifyCodeResponse commonResponse) {

    }

    @Override
    public void onUploadFile(UploadDocumentFile uploadDocumentFile) {

    }

    @Override
    public void onUploadAudioFile(UploadAudioFile uploadAudioFile) {

    }

    @Override
    public void onUserRegister(RegisterResponse registerUser) {

    }

    @Override
    public void onUserLogin(LoginResponse loginResponse) {

    }

    @Override
    public void onPilotLogout(LogoutResponse logoutResponse) {

    }

    @Override
    public void onUserLogout(LogoutResponse logoutResponse) {

    }

    @Override
    public void onGetServiceTypes(ServiceTypeResponse serviceTypeResponse) {

    }

    @Override
    public void onGetTripHistory(TripHistoryResponse tripHistoryResponse) {

    }

    @Override
    public void onGetMissedTripHistory(TripMissedHistoryResponse tripHistoryResponse) {
    }


    @Override
    public void onReverseGeocode(GeocoderApi response) {

    }

    @Override
    public void onUpdateStatus(PilotStatusResponse pilotStatusResponse) {

    }

    @Override
    public void onGetSettingsResponse(boolean isUpdated) {

    }

    @Override
    public void getHeatMap(HeatMapResponse heatMapResponse) {

    }

    @Override
    public void onUpdateProfile(UpdateProfileResponse profileResponse) {

    }

    @Override
    public void getWalletData(WalletHistoryResponse walletHistoryResponse) {

    }

    @Override
    public void getAccountNumbers(AccountNumbersResponse walletHistoryResponse) {

    }

    @Override
    public void getContactNumbers(ContactNumbersResponse walletHistoryResponse) {

    }

    @Override
    public void onRunningTrips(CheckDriverStatusResponse response) {

    }

    @Override
    public void onFreeDriver(FreeDriverResponse freeDriverResponse) {

    }

    @Override
    public void onAcceptCall(AcceptCallResponse acceptCallResponse) {

    }

    @Override
    public void onRejectCall(RejectCallResponse rejectCallResponse) {

    }

    @Override
    public void onCancelRide(CancelRideResponse cancelRideResponse) {

    }

    @Override
    public void onEndRide(EndRideResponse endRideResponse) {

    }

    @Override
    public void onArrived(ArrivedResponse arrivedResponse) {

    }

    @Override
    public void onBeginRide(BeginRideResponse beginRideResponse) {

    }

    @Override
    public void onFeedback(FeedbackResponse feedbackResponse) {

    }

    @Override
    public void onGetConversations(ConversationResponse response) {

    }

    @Override
    public void onSendMessage(SendMessageResponse response) {

    }

    @Override
    public void onGetConversationChat(ConversationChatResponse response) {

    }

    @Override
    public void onGetConversationId(GetConversationIdResponse response) {

    }

    @Override
    public void onAck(String msg) {
    }

    @Override
    public void onUpdateConversationStatus(UpdateConversationStatusResponse response) {

    }

    @Override
    public void onGetProfileResponse(GetProfileResponse response) {

    }

    @Override
    public void onDriverStatsResponse(DriverStatsResponse response) {
    }

    @Override
    public void onUpdateDropOff(UpdateDropOffResponse response) {

    }

    @Override
    public void onCommonResponse(CommonResponse response) {

    }

    @Override
    public void onChangePinResponse(ChangePinResponse response) {

    }

    @Override
    public void onCitiesResponse(GetCitiesResponse response) {

    }

    @Override
    public void onProblemPosted(ProblemPostResponse response) {

    }

    @Override
    public void onError(int errorCode, String errorMessage) {

    }
}
