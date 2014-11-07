package com.blue_stingray.healthy_life_app.net;

import com.blue_stingray.healthy_life_app.model.Session;
import com.blue_stingray.healthy_life_app.model.SessionDevice;
import com.blue_stingray.healthy_life_app.model.User;
import com.blue_stingray.healthy_life_app.net.form.SessionForm;
import com.blue_stingray.healthy_life_app.net.form.UserForm;
import retrofit.Callback;
import retrofit.http.*;

import java.util.List;

/**
 * The RESTful interface
 */
public interface RestInterface {

    //Users
    @GET("/user")
    void getUser(Callback<User> cb);

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


    //Sessions
    @POST("/session")
    void createSession(@Body SessionForm sessionForm, Callback<SessionDevice> cb);

    @DELETE("/session")
    void destroySession(Callback cb);
}
