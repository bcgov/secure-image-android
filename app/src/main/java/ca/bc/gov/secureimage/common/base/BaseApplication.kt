package ca.bc.gov.secureimage.common.base

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration
import android.os.StrictMode
import android.preference.PreferenceManager
import ca.bc.gov.secureimage.BuildConfig
import com.squareup.leakcanary.LeakCanary
import ca.bc.gov.secureimage.common.services.KeyStorageService
import ca.bc.gov.secureimage.di.Injection

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        val keyStorageService = Injection.provideKeyStorageService(
                Injection.provideKeyStore("AndroidKeyStore"))

        setUpRealm(keyStorageService)
        setUpStrictMode()
        setUpLeakCanary()
    }

    private fun setUpRealm(keyStorageService: KeyStorageService) {

        Realm.init(this)

        val key = keyStorageService.getSecureKey(
                "realm",
                64,
                PreferenceManager.getDefaultSharedPreferences(this))

        val config = RealmConfiguration.Builder()
                .deleteRealmIfMigrationNeeded()
                .encryptionKey(key)
                .build()

        Realm.setDefaultConfiguration(config)

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