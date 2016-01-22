package com.pygopar.api;

import com.pygopar.constants.OMCConst;
import com.pygopar.helpers.Command;
import com.pygopar.helpers.Token;
import com.pygopar.helpers.User;

import java.util.List;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.Header;
import retrofit.http.POST;
import retrofit.http.Path;

/**
 * Created by daniel on 1/21/16.
 */
public interface OMCAPI {
    String AUTH_HEADER = "Authorization";

    @FormUrlEncoded
    @POST(OMCConst.API_TOKEN_AUTH)
    Call<Token> getToken(@Field("username") String username, @Field("password") String password);

    @GET(OMCConst.API_GET_COMMANDS)
    Call<List<Command>> getCommands(@Header(AUTH_HEADER) String header);

    @GET(OMCConst.API_GET_COMMAND)
    Call<Command> getCommand(@Header(AUTH_HEADER) String header, @Path("pk") String pk);

    @FormUrlEncoded
    @POST(OMCConst.API_POST_NEW_USER)
    Call<User> postNewUser(@Field("username") String username, @Field("password") String password);
}
