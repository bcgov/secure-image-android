package ca.bc.gov.mobileauthentication.data

import ca.bc.gov.mobileauthentication.data.models.Token
import io.reactivex.Observable
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.Path

/**
 * Created by Aidan Laing on 2018-01-18.
 *
 */
interface AuthApi {

    @POST("/auth/realms/{realm_name}/protocol/openid-connect/token")
    @FormUrlEncoded
    fun getToken(
            @Path("realm_name") realmName: String,
            @Field("grant_type") grantType: String,
            @Field("redirect_uri") redirectUri: String,
            @Field("client_id") client_id: String,
            @Field("code") code: String
    ): Observable<Token>

    @POST("/auth/realms/{realm_name}/protocol/openid-connect/token")
    @FormUrlEncoded
    fun refreshToken(
            @Path("realm_name") realmName: String,
            @Field("redirect_uri") redirectUri: String,
            @Field("client_id") client_id: String,
            @Field("refresh_token") refreshToken: String,
            @Field("grant_type") grantType: String = "refresh_token"
    ): Observable<Token>

}