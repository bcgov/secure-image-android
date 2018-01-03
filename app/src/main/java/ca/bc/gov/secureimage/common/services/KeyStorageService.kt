package ca.bc.gov.secureimage.common.services

import android.content.SharedPreferences
import android.security.keystore.KeyGenParameterSpec
import android.security.keystore.KeyProperties
import android.util.Base64
import java.security.KeyStore
import java.security.SecureRandom
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.GCMParameterSpec

/**
 * Created by Aidan Laing on 2017-12-31.
 *
 */
class KeyStorageService(private val keyStore: KeyStore) {

    init {
        keyStore.load(null)
    }

    /**
     * Gets secure key using AES encryption
     */
    fun getSecureKey(
            alias: String,
            keySize: Int,
            sharedPrefs: SharedPreferences
    ): ByteArray {

        // Gets AES secret key from key store
        val aesSecretKey = getAESSecretKey(alias, 256)

        // Creates new random key and encrypts it using the AES secret key
        if(!sharedPrefs.contains(alias)) {
            generateRandomAESEncryptedKey(alias, keySize, aesSecretKey, sharedPrefs)
        }

        // Gets stored key and decrypts it using the AES secret key
        return getDecryptedKey(alias, aesSecretKey, sharedPrefs)
    }

    fun getAESSecretKey(
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

    fun generateAESSecretKey(alias: String, keySize: Int) {
        val keyGenerator = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, keyStore.provider)

        keyGenerator.init(KeyGenParameterSpec.Builder(
                alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                .setKeySize(keySize)
                .build())

        keyGenerator.generateKey()
    }

    fun generateRandomAESEncryptedKey(
            alias: String,
            keySize: Int,
            aesSecretKey: SecretKey,
            sharedPrefs: SharedPreferences) {

        // Encryption setup
        val encryptionCipher = Cipher.getInstance(AES_TRANSFORMATION)
        encryptionCipher.init(Cipher.ENCRYPT_MODE, aesSecretKey)

        // New random encrypted key
        val randomKey = ByteArray(keySize)
        SecureRandom().nextBytes(randomKey)
        val encryptedBytes = encryptionCipher.doFinal(randomKey)

        // Saving base 64 encoded encrypted bytes
        val base64EncryptedBytesString = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
        sharedPrefs.edit().putString(alias, base64EncryptedBytesString).apply()

        // Saving base 64 encoded initialization vector
        val base64IvString = Base64.encodeToString(encryptionCipher.iv, Base64.DEFAULT)
        sharedPrefs.edit().putString(IV_KEY, base64IvString).apply()
    }

    fun getDecryptedKey(
            alias: String,
            aesSecretKey: SecretKey,
            sharedPrefs: SharedPreferences
    ): ByteArray {

        // Decoding saved encrypted bytes
        val base64EncryptedBytesString = sharedPrefs.getString(alias, "")
        val encryptedBytes = Base64.decode(base64EncryptedBytesString, Base64.DEFAULT)

        // Decoding saved initialization vector
        val base64IvString = sharedPrefs.getString(IV_KEY, "")
        val savedIv = Base64.decode(base64IvString, Base64.DEFAULT)

        // Decryption setup
        val decryptionCipher = Cipher.getInstance(AES_TRANSFORMATION)
        val gcmParameterSpec = GCMParameterSpec(128, savedIv)
        decryptionCipher.init(Cipher.DECRYPT_MODE, aesSecretKey, gcmParameterSpec)

        // Decrypt encrypted bytes and return
        return decryptionCipher.doFinal(encryptedBytes)
    }

    companion object {
        private val AES_TRANSFORMATION = "AES/GCM/NoPadding"
        private val IV_KEY = "IV_KEY"
    }

}