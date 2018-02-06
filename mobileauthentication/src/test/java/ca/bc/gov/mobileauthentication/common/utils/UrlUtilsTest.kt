package ca.bc.gov.mobileauthentication.common.utils

import org.junit.Test

import org.junit.Assert.*

/**
 * Created by Aidan Laing on 2018-02-05.
 *
 */
class UrlUtilsTest {

    @Test
    fun cleanBaseUrlNoSlash() {
        val baseUrl = "http://foobar.com"
        val expected = "http://foobar.com/"
        val actual = UrlUtils.cleanBaseUrl(baseUrl)

        assertEquals(expected, actual)
    }

    @Test
    fun cleanBaseUrlSlash() {
        val baseUrl = "http://foobar.com/"
        val expected = "http://foobar.com/"
        val actual = UrlUtils.cleanBaseUrl(baseUrl)

        assertEquals(expected, actual)
    }

    @Test
    fun extractCodeSingleQueryParam() {
        val codeUrl = "http://foobar.com?code=123"
        val expected = "123"
        val actual = UrlUtils.extractCode(codeUrl)

        assertEquals(expected, actual)
    }

    @Test
    fun extractCodeSingleMultipleParam() {
        val codeUrl = "http://foobar.com?code=123&moo=abc"
        val expected = "123"
        val actual = UrlUtils.extractCode(codeUrl)

        assertEquals(expected, actual)
    }

    @Test
    fun extractCodeNoCode() {
        val codeUrl = "http://foobar.com"
        val expected = ""
        val actual = UrlUtils.extractCode(codeUrl)

        assertEquals(expected, actual)
    }

}