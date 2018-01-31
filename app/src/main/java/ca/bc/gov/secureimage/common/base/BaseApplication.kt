package ca.bc.gov.secureimage.common.base

import android.app.Application
import io.realm.Realm
import io.realm.RealmConfiguration
import android.os.StrictMode
import android.preference.PreferenceManager
import ca.bc.gov.secureimage.BuildConfig
import com.squareup.leakcanary.LeakCanary
import ca.bc.gov.secureimage.common.managers.KeyStorageManager
import ca.bc.gov.secureimage.di.Injection
import com.crashlytics.android.Crashlytics
import io.fabric.sdk.android.Fabric

/**
 * Created by Aidan Laing on 2017-12-12.
 *
 */
class BaseApplication: Application() {

    override fun onCreate() {
        super.onCreate()

        setUpFabric()

        val keyStorageManager = Injection.provideKeyStorageManager(
                Injection.provideKeyStore())

        setUpRealm(keyStorageManager)
        setUpStrictMode()
        setUpLeakCanary()
    }

    // Fabric
    fun setUpFabric() {
        Fabric.with(this, Crashlytics())
    }

    // Realm
    fun setUpRealm(keyStorageManager: KeyStorageManager) {

        Realm.init(this)

        val key = keyStorageManager.getSecureKey(
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
    fun setUpStrictMode() {
        if (BuildConfig.DEBUG) {
            StrictMode.setThreadPolicy(StrictMode.ThreadPolicy.Builder()
                    .detectAll()
                    .penaltyDialog()
                    .build())
        }
    }

    // Leak canary
    fun setUpLeakCanary() {
        if (LeakCanary.isInAnalyzerProcess(this)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return
        }
        LeakCanary.install(this)
    }
}