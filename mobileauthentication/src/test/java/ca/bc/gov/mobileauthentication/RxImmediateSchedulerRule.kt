package ca.bc.gov.mobileauthentication

import org.junit.rules.TestRule
import org.junit.runner.Description
import org.junit.runners.model.Statement

import java.util.concurrent.Callable

import io.reactivex.Scheduler
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.functions.Function
import io.reactivex.plugins.RxJavaPlugins
import io.reactivex.schedulers.Schedulers

/**
 * JUnit Test Rule which overrides RxJava and Android schedulers for use in unit tests.
 *
 *
 * All schedulers are replaced with Schedulers.trampoline().
 */
class RxImmediateSchedulerRule: TestRule {

    private val schedulerInstance = Schedulers.trampoline()

    private val schedulerFunction = Function<Scheduler, Scheduler> { schedulerInstance }

    private val schedulerFunctionLazy = Function<Callable<Scheduler>, Scheduler> { schedulerInstance }

    override fun apply(base: Statement, description: Description): Statement {
        return object: Statement() {

            @Throws(Throwable::class)
            override fun evaluate() {
                RxAndroidPlugins.reset()
                RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerFunctionLazy)

                RxJavaPlugins.reset()
                RxJavaPlugins.setIoSchedulerHandler(schedulerFunction)
                RxJavaPlugins.setNewThreadSchedulerHandler(schedulerFunction)
                RxJavaPlugins.setComputationSchedulerHandler(schedulerFunction)

                base.evaluate()

                RxAndroidPlugins.reset()
                RxJavaPlugins.reset()
            }
        }
    }
}