package com.bykea.pk.partner.repositories;

import com.bykea.pk.partner.models.data.RankingResponse;
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
    public void onUploadImageFile(UploadImageFile uploadAudioFile) {

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
    public void getHeatMap(ArrayList<HeatMapUpdatedResponse> heatMapResponse) {

    }

    @Override
    public void onUpdateProfile(UpdateProfileResponse profileResponse) {

    }

    @Override
    public void getWalletData(WalletHistoryResponse walletHistoryResponse) {

    }

    @Override
    public void getAccountNumbers(BankAccountListResponse walletHistoryResponse) {

    }

    @Override
    public void onBankDetailsResponse(BankDetailsResponse response) {

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
    public void onDownloadAudio(DownloadAudioFileResponse response) {

    }

    @Override
    public void onUpdateRegid(UpdateRegIDResponse response) {

    }

    @Override
    public void onAddSavedPlaceResponse(AddSavedPlaceResponse response) {

    }

    @Override
    public void onDeleteSavedPlaceResponse() {

    }

    @Override
    public void onGetSavedPlacesResponse(GetSavedPlacesResponse response) {

    }

    @Override
    public void onZonesResponse(GetZonesResponse response) {

    }

    @Override
    public void onZoneAreasResponse(ZoneAreaResponse response) {

    }

    @Override
    public void onShahkarResponse(ShahkarResponse response) {

    }

    @Override
    public void onBonusChartResponse(RankingResponse response) {

    }

    @Override
    public void onDriverPerformanceResponse(DriverPerformanceResponse response) {

    }

    @Override
    public void onLoadBoardResponse(LoadBoardResponse response) {

    }

    @Override
    public void onTopUpPassWallet(TopUpPassWalletResponse response) {

    }

    @Override
    public void onLocationUpdate(LocationResponse response) {

    }

    @Override
    public void onSignUpSettingsResponse(SignUpSettingsResponse response) {

    }

    @Override
    public void onSignUpAddNumberResponse(SignUpAddNumberResponse response) {

    }

    @Override
    public void onSignUpImageResponse(SignupUplodaImgResponse response) {

    }

    @Override
    public void onSignUpOptionalResponse(SignUpOptionalDataResponse response) {

    }

    @Override
    public void onSignupCompleteResponse(SignUpCompleteResponse response) {

    }

    @Override
    public void onBiometricApiResponse(BiometricApiResponse response) {

    }

    @Override
    public void onError(int errorCode, String errorMessage) {

    }
}
