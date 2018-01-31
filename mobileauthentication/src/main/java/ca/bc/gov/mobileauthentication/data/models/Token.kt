package ca.bc.gov.mobileauthentication.data.models

import com.google.gson.annotations.SerializedName

/**
 * Created by Aidan Laing on 2018-01-18.
 *
 */
data class Token(
        @SerializedName("access_token") val accessToken: String?,
        @SerializedName("expires_in") val expiresIn: Long?,
        @SerializedName("refresh_expires_in") val refreshExpiresIn: Long?,
        @SerializedName("refresh_token") val refreshToken: String?,
        @SerializedName("token_type") val bearer: String?,
        @SerializedName("id_token") val idToken: String?,
        @SerializedName("not-before-policy") val notBeforePolicy: Long?,
        @SerializedName("session_state") val sessionState: String?,
        val expiresAt: Long = System.currentTimeMillis() + (expiresIn ?: 0 * 1000),
        val refreshExpiresAt: Long = System.currentTimeMillis() + (refreshExpiresIn ?: 0 * 1000)
) {

    fun isExpired(): Boolean = expiresAt > System.currentTimeMillis()
    fun isRefreshExpired(): Boolean = refreshExpiresAt > System.currentTimeMillis()

}