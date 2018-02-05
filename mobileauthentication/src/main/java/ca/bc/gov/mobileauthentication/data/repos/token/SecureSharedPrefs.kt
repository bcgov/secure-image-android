package ca.bc.gov.mobileauthentication.data.repos.token

import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.nio.charset.Charset
import java.security.KeyStore
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Created by Aidan Laing on 2018-01-23.
 *
 */
class SecureSharedPrefs(
        private val keyStore: KeyStore,
        private val sharedPrefs: SharedPreferences
) {

    init {
        keyStore.load(null)
    }

    /**
     * Gets String from shared prefs, decrypts it, and returns the result
     */
    fun getString(key: String): String {
        val aesSecretKey = getAESSecretKey(key, 256)
        return getDecryptedString(key, aesSecretKey)
    }

    /**
     * Saves an encrypted version of the passed String to shared prefs
     */
    fun saveString(key: String, data: String) {
        val aesSecretKey = getAESSecretKey(key, 256)
        encryptAndSaveString(key, data, aesSecretKey)
    }

    /**
     * Deletes String associated with key in shared prefs
     */
    fun deleteString(key: String) {
        sharedPrefs.edit().remove(key).apply()
    }

    /**
     * Gets AES Secret key form keystore
     * Generates a new one if it does not exist
     */
    private fun getAESSecretKey(
            alias: String,
            keySize: Int
    ): SecretKey {

        // Generating AES key if alias is not found
        if (!keyStore.containsAlias(alias)) {
            generateAESSecretKey(alias, keySize)
        }

        // Getting secret key from keystore and returning
        return keyStore.getKey(alias, null) as SecretKey
    }

    /**
     * Builds new AES Secret key
     */
    private fun generateAESSecretKey(alias: String, keySize: Int) {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, keyStore.provider)

        keyGenerator.init(KeyGenParameterSpec.Builder(
                alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(keySize)
                .build())

        keyGenerator.generateKey()
    }

    /**
     * Encrypts passed string and saves in shared prefs
     */
    private fun encryptAndSaveString(
            key: String,
            data: String,
            aesSecretKey: SecretKey) {

        // Encryption setup
        val encryptionCipher = Cipher.getInstance(AES_TRANSFORMATION)
        encryptionCipher.init(Cipher.ENCRYPT_MODE, aesSecretKey)

        val dataBytes = data.toByteArray(Charset.defaultCharset())
        val encryptedBytes = encryptionCipher.doFinal(dataBytes)

        // Saving base 64 encoded encrypted bytes
        val base64EncryptedBytesString = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        sharedPrefs.edit().putString(key, base64EncryptedBytesString).apply()

        // Saving base 64 encoded initialization vector
        val base64IvString = Base64.encodeToString(encryptionCipher.iv, Base64.DEFAULT)
        sharedPrefs.edit().putString(getIvKey(key), base64IvString).apply()
    }

    /**
     * Gets String associated with key in shared prefs and decrypts it
     */
    private fun getDecryptedString(
            key: String,
            aesSecretKey: SecretKey
    ): String {

        if (!sharedPrefs.contains(key) || !sharedPrefs.contains(getIvKey(key))) return ""

        // Decoding saved encrypted bytes
        val base64EncryptedBytesString = sharedPrefs.getString(key, "")
        val encryptedBytes = Base64.decode(base64EncryptedBytesString, Base64.DEFAULT)

        // Decoding saved initialization vector
        val base64IvString = sharedPrefs.getString(getIvKey(key), "")
        val savedIv = Base64.decode(base64IvString, Base64.DEFAULT)

        // Decryption setup
        val decryptionCipher = Cipher.getInstance(AES_TRANSFORMATION)
        val gcmParameterSpec = GCMParameterSpec(128, savedIv)
        decryptionCipher.init(Cipher.DECRYPT_MODE, aesSecretKey, gcmParameterSpec)

        // Decrypt encrypted bytes and return
        val decryptedBytes = decryptionCipher.doFinal(encryptedBytes)
        return decryptedBytes.toString(Charset.defaultCharset())
    }

    /**
     * Gets the iv key for the passed key
     */
    private fun getIvKey(key: String): String {
        return "iv_key_$key"
    }

    companion object {
        private val AES_TRANSFORMATION = "AES/GCM/NoPadding"
    }

}