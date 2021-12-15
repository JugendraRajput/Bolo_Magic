package bolomagic.in.SendNotificationPack;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface APIService {
    @Headers({
            "Content-Type:application/json",
            "Authorization:key=AAAAMKs00oU:APA91bGTmeSs9mimcAiOpBZpSuFmP76kKDgrhg5pgs9_qKrMhsw8V-_QhgLMOPsM9QOR0Pm3z6WN-Ji3Zw98UMa9ArMenXv3XcGIFveAJ7as5sX5kqK2fIlZDEX4MARfe9BWb5EO_Jku" // Your server key
    })
    @POST("fcm/send")
    Call<MyResponse> sendNotification(@Body NotificationSender body);
}