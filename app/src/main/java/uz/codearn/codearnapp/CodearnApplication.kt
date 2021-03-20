package uz.codearn.codearnapp

import android.app.Application
import android.os.Build
import androidx.work.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import uz.codearn.codearnapp.worker.RefreshDataWorker
import java.util.concurrent.TimeUnit

class CodearnApplication : Application() {

    private val applicationScope = CoroutineScope(Dispatchers.Default)

    private fun delayInit() = applicationScope.launch {
        setUpRecurringWork()
    }

    private fun setUpRecurringWork() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .apply {
                if (Build.VERSION.SDK_INT > Build.VERSION_CODES.M) {
                    setRequiresDeviceIdle(true)
                }
            }.build()

        val repeatingRequest = PeriodicWorkRequestBuilder<RefreshDataWorker>(30, TimeUnit.DAYS)
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance().enqueueUniquePeriodicWork(
            RefreshDataWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            repeatingRequest
        )
    }

    override fun onCreate() {
        super.onCreate()
        delayInit()
    }
}