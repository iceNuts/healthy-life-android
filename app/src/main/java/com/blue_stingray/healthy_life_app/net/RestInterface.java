package com.blue_stingray.healthy_life_app.net;

import com.blue_stingray.healthy_life_app.model.Alert;
import com.blue_stingray.healthy_life_app.model.AppUsage;
import com.blue_stingray.healthy_life_app.model.Application;
import com.blue_stingray.healthy_life_app.model.Goal;
import com.blue_stingray.healthy_life_app.model.Icon;
import com.blue_stingray.healthy_life_app.model.Lifeline;
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
import com.blue_stingray.healthy_life_app.net.form.SessionForm;
import com.blue_stingray.healthy_life_app.net.form.StatForm;
import com.blue_stingray.healthy_life_app.net.form.UserForm;
import com.squareup.okhttp.Call;

import retrofit.Callback;
import retrofit.http.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

/**
 * The RESTful interface
 */
public interface RestInterface {

    // Users
    @GET("/user")
    void getMyUsers(Callback<List<User>> cb);

    @GET("/user/me")
    void getMyUser(Callback<User> cb);

    @GET("/user/{id}")
    void getUser(@Path("id") int id, Callback<User> cb);

    @GET("/user")
    void getUsersChangedSince(@Query("timestamp") int timestamp, Callback<List<User>> cb);

    @POST("/user")
    void createUser(@Body UserForm userForm, Callback<User> cb);

    @PUT("/user")
    void updateUser(@Body UserForm userForm, Callback<User> cb);

    @PUT("/user/{id}")
    void updateUser(@Path("id") int id, @Body UserForm userForm, Callback<User> cb);

    @DELETE("/user")
    void destroyUser(Callback cb);

    @DELETE("/user/{id}")
    void destroyUser(@Path("id") int id, Callback cb);

    @GET("/leaderboard")
    void getLeaderboard(Callback<List<User>> cb);

    // Sessions
    @POST("/session")
    void createSession(@Body SessionForm sessionForm, Callback<SessionDevice> cb);

    @DELETE("/session")
    void destroySession(Callback cb);

    // Apps
    @POST("/app")
    void createApp(@Body AppForm appForm, Callback<Application> cb);

    @GET("/app/{id}")
    void getApp(@Path("id") String id, Callback<Application> cb);

    @GET("/app/{id}/usage")
    void getAppUsage(@Path("id") String id, Callback<AppUsage> cb);

    // Icons
    @POST("/icon")
    void createIcon(@Body IconForm iconForm, Callback<Icon> cb);

    // Goals
    @GET("/goal")
    void getGoals(Callback<Goal[]> cb);

    @POST("/goal")
    void createGoal(@Body GoalForm goalForm, Callback<Goal> cb);

    // Stat
    @GET("/stat/lastUpdate")
    void getStatLastUpdateStamp(Callback<Stat> cb);

    @GET("/stat/bydate")
    void getStatsByDate(@Query("application_id") String application_id, @Query("start") String start, @Query("stop") String stop, Callback<Stat[]> cb);

    @POST("/stat")
    void createStats(@Body List<StatForm> statsArray, Callback<Stat> cb);

    @GET("/stat/report")
    void getMyReport(Callback<UsageReport> cb);

    // Lifeline
    @POST("/lifeline")
    void createLifeline(@Body LifelineForm lifelineForm, Callback<Lifeline> cb);

    @GET("/lifeline")
    void getLifeline(Callback<List<Lifeline>> cb);

    @PUT("/lifeline/{id}")
    void updateLifeline(@Path("id") int id, @Body LifelineUpdateForm lifelineUpdateForm, Callback<Lifeline> cb);

    // Alerts
    @GET("/alert")
    void getAlerts(Callback<List<Alert>> cb);

}
