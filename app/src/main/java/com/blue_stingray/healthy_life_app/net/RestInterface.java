package com.blue_stingray.healthy_life_app.net;

import com.blue_stingray.healthy_life_app.App;
import com.blue_stingray.healthy_life_app.model.Alert;
import com.blue_stingray.healthy_life_app.model.AppGoal;
import com.blue_stingray.healthy_life_app.model.AppUsage;
import com.blue_stingray.healthy_life_app.model.Application;
import com.blue_stingray.healthy_life_app.model.Device;
import com.blue_stingray.healthy_life_app.model.Goal;
import com.blue_stingray.healthy_life_app.model.Icon;
import com.blue_stingray.healthy_life_app.model.Lifeline;
import com.blue_stingray.healthy_life_app.model.MentorRequest;
import com.blue_stingray.healthy_life_app.model.Session;
import com.blue_stingray.healthy_life_app.model.SessionDevice;
import com.blue_stingray.healthy_life_app.model.Stat;
import com.blue_stingray.healthy_life_app.model.UsageReport;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.form.AppForm;
import com.blue_stingray.healthy_life_app.net.form.GoalForm;
import com.blue_stingray.healthy_life_app.net.form.IconForm;
import com.blue_stingray.healthy_life_app.net.form.LifelineForm;
import com.blue_stingray.healthy_life_app.net.form.LifelineUpdateForm;
import com.blue_stingray.healthy_life_app.net.form.ManyGoalForm;
import com.blue_stingray.healthy_life_app.net.form.RequestMentorForm;
import com.blue_stingray.healthy_life_app.net.form.SearchMentorForm;
import com.blue_stingray.healthy_life_app.net.form.SessionForm;
import com.blue_stingray.healthy_life_app.net.form.SocialSessionForm;
import com.blue_stingray.healthy_life_app.net.form.StatForm;
import com.blue_stingray.healthy_life_app.net.form.UpdateMentorRequestForm;
import com.blue_stingray.healthy_life_app.net.form.UserForm;

import retrofit.Callback;
import retrofit.http.*;

import java.util.Calendar;
import java.util.List;
import java.util.Objects;

/**
 * The RESTful interface
 */
public interface RestInterface {

    // Users
    @GET("/user")
    void getMyUsers(RetrofitDialogCallback<List<User>> cb);

    @GET("/user/me")
    void getMyUser(RetrofitDialogCallback<User> cb);

    @GET("/user/{id}")
    void getUser(@Path("id") int id, RetrofitDialogCallback<User> cb);

    @GET("/user/{id}/devices")
    void getUserDevices(@Path("id") int id, RetrofitDialogCallback<List<Device>> cb);

    @GET("/user/{id}/alerts")
    void getUserAlerts(@Path("id") int id, RetrofitDialogCallback<List<Alert>> cb);

    @GET("/user/{id}/apps/usage")
    void getUserAppsUsage(@Path("id") int id, RetrofitDialogCallback<List<AppGoal>> cb);

    @GET("/user/{id}/lockedApps")
    void getUserLockedApps(@Path("id") int id, RetrofitDialogCallback<List<Application>> cb);

    @GET("/user")
    void getUsersChangedSince(@Query("timestamp") int timestamp, RetrofitDialogCallback<List<User>> cb);

    @POST("/user")
    void createUser(@Body UserForm userForm, RetrofitDialogCallback<User> cb);

    @PUT("/user")
    void updateUser(@Body UserForm userForm, RetrofitDialogCallback<User> cb);

    @PUT("/user/{id}")
    void updateUser(@Path("id") int id, @Body UserForm userForm, RetrofitDialogCallback<User> cb);

    @DELETE("/user/{id}")
    void destroyUser(@Path("id") int id, RetrofitDialogCallback<Object> cb);

    @GET("/leaderboard")
    void getLeaderboard(RetrofitDialogCallback<List<User>> cb);

    // Sessions
    @POST("/session")
    void createSession(@Body SessionForm sessionForm, RetrofitDialogCallback<SessionDevice> cb);

    @DELETE("/session")
    void destroySession(RetrofitDialogCallback<Object> cb);

    // Apps
    @POST("/app")
    void createApp(@Body AppForm appForm, RetrofitDialogCallback<Application> cb);

    @GET("/app/{id}")
    void getApp(@Path("id") String id, Callback<Application> cb);

    @GET("/app/{id}")
    void getApp(@Path("id") String id, @Query("device_id") int deviceId, Callback<Application> cb);

    @GET("/app/{id}/usage")
    void getAppUsage(@Path("id") String id, RetrofitDialogCallback<AppUsage> cb);

    // Icons
    @POST("/icon")
    void createIcon(@Body IconForm iconForm, RetrofitDialogCallback<Icon> cb);

    // Goals
    @GET("/goal")
    void getMyGoals(RetrofitDialogCallback<List<Goal>> cb);

    @POST("/goal")
    void createGoal(@Body GoalForm goalForm, RetrofitDialogCallback<Goal> cb);

    @POST("/goal/many")
    void createGoalMany(@Body ManyGoalForm goalForms, Callback<List<Goal>> cb);

    // Stat
    @GET("/stat/lastUpdate")
    void getStatLastUpdateStamp(RetrofitDialogCallback<Stat> cb);

    @POST("/stat/bydate")
    void getStatsByDate(@Body StatForm statForm, RetrofitDialogCallback<Stat[]> cb);

    @POST("/stat")
    void createStats(@Body List<StatForm> statsArray, RetrofitDialogCallback<Stat> cb);

    @GET("/stat/report")
    void getMyReport(RetrofitDialogCallback<UsageReport> cb);

    // Lifeline
    @POST("/lifeline")
    void createLifeline(@Body LifelineForm lifelineForm, RetrofitDialogCallback<Lifeline> cb);

    @GET("/lifeline")
    void getLifeline(RetrofitDialogCallback<List<Lifeline>> cb);

    @PUT("/lifeline/{id}")
    void updateLifeline(@Path("id") int id, @Body LifelineUpdateForm lifelineUpdateForm, RetrofitDialogCallback<Lifeline> cb);

    @DELETE("/lifeline/{id}")
    void destroyLifeline(@Path("id") int id, RetrofitDialogCallback<Object> cb);

    // Alerts
    @GET("/alert")
    void getAlerts(RetrofitDialogCallback<List<Alert>> cb);

    // Devices
    @GET("/device/{id}/apps")
    void getDeviceApps(@Path("id") int id, RetrofitDialogCallback<List<Application>> cb);

    // Facebook Login
    @POST("/session/facebook")
    void facebookLogin(@Body SocialSessionForm socialSessionForm, RetrofitDialogCallback<SessionDevice> cb);

    // Google Login
    @POST("/session/google")
    void googleLogin(@Body SocialSessionForm socialSessionForm, RetrofitDialogCallback<SessionDevice> cb);

    // Mentor
    @POST("/mentor/remove")
    void removeMentor(RetrofitDialogCallback<Object> cb);

    @POST("/mentor/search")
    void searchMentor(@Body SearchMentorForm searchMentorForm, RetrofitDialogCallback<List<User>> cb);

    @POST("/mentor/request")
    void requestMentor(@Body RequestMentorForm requestMentorForm, RetrofitDialogCallback<Object> cb);

    @GET("/mentor/request")
    void getMentorRequest(RetrofitDialogCallback<List<MentorRequest>> cb);

    @POST("/mentor/request/update/{id}")
    void updateMentorRequest(@Path("id") int id, @Body UpdateMentorRequestForm updateMentorRequestForm, RetrofitDialogCallback<Object> cb);



}
