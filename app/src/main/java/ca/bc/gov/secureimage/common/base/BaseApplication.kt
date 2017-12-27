package ca.bc.gov.secureimage.common.base

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration
import android.os.StrictMode
import android.preference.PreferenceManager
import ca.bc.gov.secureimage.BuildConfig
import com.squareup.leakcanary.LeakCanary
import java.security.*
import android.security.keystore.KeyProperties
import android.security.keystore.KeyGenParameterSpec
import android.util.Base64
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        setUpRealm()
        setUpStrictMode()
        setUpLeakCanary()
    }

    private fun setUpRealm() {

        Realm.init(this)

        val config = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .encryptionKey(loadKey("realm"))
                .build()

        Realm.setDefaultConfiguration(config)

    }

    private fun loadKey(
            alias: String,
            realmKeySize: Int = 64,
            keyStoreKeySize: Int = 256,
            keyStoreType: String = "AndroidKeyStore",
            transformation: String = "AES/GCM/NoPadding",
            tLen: Int = 128,
            ivKey: String = "ivKey"
    ): ByteArray {

        // Key store setup
        val keystore = KeyStore.getInstance(keyStoreType)
        keystore.load(null)

        // Generating AES key if alias is not found
        if (!keystore.containsAlias(alias)) {
            val gen = KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES, keyStoreType)

            gen.init(KeyGenParameterSpec.Builder(
                    alias, KeyProperties.PURPOSE_ENCRYPT or KeyProperties.PURPOSE_DECRYPT)
                    .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                    .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                    .setKeySize(keyStoreKeySize)
                    .build())

            gen.generateKey()
        }

        // Grabbing secret key from keystore
        val secretKey = keystore.getKey(alias, null) as SecretKey

        // Shared Prefs setup
        val sharedPrefs = PreferenceManager.getDefaultSharedPreferences(this)

        // If shared prefs doesn't have saved key generate random, encrypt, and save to shared prefs
        if(!sharedPrefs.contains(alias)) {

            // Encryption setup
            val encryptionCipher = Cipher.getInstance(transformation)
            encryptionCipher.init(Cipher.ENCRYPT_MODE, secretKey)

            // Random key
            val randomKey = ByteArray(realmKeySize)
            SecureRandom().nextBytes(randomKey)

            // Encrypt random key
            val encryptedBytes = encryptionCipher.doFinal(randomKey)

            // Saving base 64 encoded encrypted bytes
            val base64EncryptedBytesString = Base64.encodeToString(encryptedBytes, Base64.DEFAULT)
            sharedPrefs.edit().putString(alias, base64EncryptedBytesString).apply()

            // Saving base 64 encoded initialization vector
            val base64IvString = Base64.encodeToString(encryptionCipher.iv, Base64.DEFAULT)
            sharedPrefs.edit().putString(ivKey, base64IvString).apply()
        }

        // Decoding saved encrypted bytes
        val base64EncryptedBytesString = sharedPrefs.getString(alias, "")
        val encryptedBytes = Base64.decode(base64EncryptedBytesString, Base64.DEFAULT)

        // Decoding saved initialization vector
        val base64IvString = sharedPrefs.getString(ivKey, "")
        val savedIv = Base64.decode(base64IvString, Base64.DEFAULT)

        // Decrypting setup
        val decryptionCipher = Cipher.getInstance(transformation)
        val gcmParameterSpec = GCMParameterSpec(tLen, savedIv)
        decryptionCipher.init(Cipher.DECRYPT_MODE, secretKey, gcmParameterSpec)

        // Decrypting encrypted bytes
        return decryptionCipher.doFinal(encryptedBytes)
    }

    // Strict mode
    private fun setUpStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyDialog()
                    .build())
        }
    }

    // Leak canary
    private fun setUpLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
    }
}