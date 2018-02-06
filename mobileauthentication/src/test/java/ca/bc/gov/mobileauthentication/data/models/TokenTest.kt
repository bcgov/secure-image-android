package ca.bc.gov.mobileauthentication.data.models

import org.junit.Test

import org.junit.Assert.*

/**
 * Created by Aidan Laing on 2018-02-05.
 *
 */
class TokenTest {

    @Test
    fun tokenExpiryAfterCurrentTime() {
        val currentTime = 2000L
        val expiresAt = 3000L
        val token = Token(null, null, null, null,
                null, null, null, null, expiresAt)

        val expected = true
        val actual = token.isExpired(currentTime)

        assertEquals(expected, actual)
    }

    @Test
    fun tokenExpiryBeforeCurrentTime() {
        val currentTime = 2000L
        val expiresAt = 1000L
        val token = Token(null, null, null, null,
                null, null, null, null, expiresAt)

        val expected = false
        val actual = token.isExpired(currentTime)

        assertEquals(expected, actual)
    }

    @Test
    fun tokenExpiryEqualToCurrentTime() {
        val currentTime = 2000L
        val expiresAt = 2000L
        val token = Token(null, null, null, null,
                null, null, null, null, expiresAt)

        val expected = false
        val actual = token.isExpired(currentTime)

        assertEquals(expected, actual)
    }

    @Test
    fun refreshExpiryAfterCurrentTime() {
        val currentTime = 2000L
        val refreshExpiresAt = 3000L
        val token = Token(null, null, null, null,
                null, null, null, null, 0, refreshExpiresAt)

        val expected = true
        val actual = token.isRefreshExpired(currentTime)

        assertEquals(expected, actual)
    }

    @Test
    fun refreshExpiryBeforeCurrentTime() {
        val currentTime = 2000L
        val refreshExpiresAt = 1000L
        val token = Token(null, null, null, null,
                null, null, null, null, 0, refreshExpiresAt)

        val expected = false
        val actual = token.isRefreshExpired(currentTime)

        assertEquals(expected, actual)
    }

    @Test
    fun refreshExpiryEqualToCurrentTime() {
        val currentTime = 2000L
        val refreshExpiresAt = 2000L
        val token = Token(null, null, null, null,
                null, null, null, null, 0, refreshExpiresAt)

        val expected = false
        val actual = token.isRefreshExpired(currentTime)

        assertEquals(expected, actual)
    }

}