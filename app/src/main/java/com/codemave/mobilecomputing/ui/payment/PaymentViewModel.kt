package com.codemave.mobilecomputing.ui.payment

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.*
import com.codemave.mobilecomputing.Graph
import com.codemave.mobilecomputing.data.entity.Category
import com.codemave.mobilecomputing.data.entity.Payment
import com.codemave.mobilecomputing.data.repository.CategoryRepository
import com.codemave.mobilecomputing.data.repository.PaymentRepository
import com.codemave.mobilecomputing.util.NotificationWorker
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit
import com.codemave.mobilecomputing.R
import com.codemave.mobilecomputing.ui.home.categoryPayment.toDateString
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*

class PaymentViewModel(
    private val paymentRepository: PaymentRepository = Graph.paymentRepository,
    private val categoryRepository: CategoryRepository = Graph.categoryRepository
): ViewModel() {
    private val _state = MutableStateFlow(PaymentViewState())

    val state: StateFlow<PaymentViewState>
        get() = _state

    suspend fun savePayment(payment: Payment): Long {
        createPaymentMadeNotification(payment, 1)

        if(payment.paymentHowManyNotifications > 0){
            setOneTimeNotification(payment)
        }

        return paymentRepository.addPayment(payment)
    }

    init {
        createNotificationChannel(context = Graph.appContext)
        viewModelScope.launch {
            categoryRepository.categories().collect { categories ->
                _state.value = PaymentViewState(categories)
            }
        }
    }

    suspend fun updatePayment(payment: Payment) :Int{
        createPaymentMadeNotification(payment, 2)
        return paymentRepository.updatePayment(payment)
    }

    suspend fun deletePayment(payment: Payment) : Int {
        createPaymentMadeNotification(payment, 3)
        return paymentRepository.deletePayment(payment)
    }

    suspend fun getPaymentWithId(paymentId: Long): Payment? {
        return paymentRepository.getPaymentWithId(paymentId)
    }

}

private fun setOneTimeNotification(payment: Payment) {

    val workManager = WorkManager.getInstance(Graph.appContext)
    val constraints = Constraints.Builder()
        .setRequiredNetworkType(NetworkType.CONNECTED)
        .build()

    val notificationWorker = OneTimeWorkRequestBuilder<NotificationWorker>()
        .setInitialDelay(payment.paymentDate - Date().time, TimeUnit.MILLISECONDS)
        //.setInitialDelay(10, TimeUnit.SECONDS)
        .setConstraints(constraints)
        .build()

    workManager.enqueue(notificationWorker)

    //Monitoring for state of work
    workManager.getWorkInfoByIdLiveData(notificationWorker.id).observeForever { workInfo ->
        if (workInfo.state == WorkInfo.State.SUCCEEDED) {
            createSuccessNotification(payment, 1)
            //si queremos que al updatear se modifique
            // igual hay que pasarle el id de la notificacion
        } else {
            //createErrorNotification()
        }
    }
    if (payment.paymentHowManyNotifications > 1){
        val workManager = WorkManager.getInstance(Graph.appContext)
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val notificationWorker = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInitialDelay(payment.paymentDate - Date().time -300000, TimeUnit.MILLISECONDS)
            //.setInitialDelay(10, TimeUnit.SECONDS)
            .setConstraints(constraints)
            .build()

        workManager.enqueue(notificationWorker)

        //Monitoring for state of work
        workManager.getWorkInfoByIdLiveData(notificationWorker.id).observeForever { workInfo ->
            if (workInfo.state == WorkInfo.State.SUCCEEDED) {
                createSuccessNotification(payment, 3) //si queremos que al updatear se modifique
                // igual hay que pasarle el id de la notificacion
            } else {
                //createErrorNotification()
            }
        }
    }
}

private fun createNotificationChannel(context: Context) {

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val name = "NotificationChannelName"
        val descriptionText = "NotificationChannelDescriptionText"
        val importance = NotificationManager.IMPORTANCE_DEFAULT
        val channel = NotificationChannel("CHANNEL_ID", name, importance).apply {
            description = descriptionText
        }
        // register the channel with the system
        val notificationManager: NotificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }
}

private fun createSuccessNotification(payment: Payment, notiID: Int) {
    val notificationId = notiID
    if(notiID > 1){
        val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Reminder 5 minutes early")
            .setContentText("${payment.paymentTitle}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(Graph.appContext)) {
            //notificationId is unique for each notification that you define
            notify(notificationId, builder.build())
        }
    }
    else{
        val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Reminder")
            .setContentText("${payment.paymentTitle}")
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)

        with(NotificationManagerCompat.from(Graph.appContext)) {
            //notificationId is unique for each notification that you define
            notify(notificationId, builder.build())
        }

    }
}

private fun createErrorNotification() {
    val notificationId = 3
    val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
        .setSmallIcon(R.drawable.ic_launcher_background)
        .setContentTitle("Error")
        .setContentText("There was an unknown error")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)

    with(NotificationManagerCompat.from(Graph.appContext)) {
        //notificationId is unique for each notification that you define
        notify(notificationId, builder.build())
    }
}

private fun createPaymentMadeNotification(payment: Payment, n :Int) {
    val notificationId = 2
    if(n == 1) {
        val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("New reminder created!")
            .setContentText("You add the reminder: ${payment.paymentTitle} on: ${payment.paymentDate.toDateString()} ")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle().bigText("You add the reminder: ${payment.paymentTitle}, on: ${payment.paymentDate.toDateString()} "))

        with(NotificationManagerCompat.from(Graph.appContext)) {
            notify(notificationId, builder.build())
        }
    }
    if(n == 2) {
        val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Updated reminder!")
            .setContentText("You updated the reminder called: ${payment.paymentTitle} on: ${payment.paymentDate.toDateString()} ")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setStyle(NotificationCompat.BigTextStyle().bigText("You updated the reminder called: ${payment.paymentTitle}, on: ${payment.paymentDate.toDateString()} "))
        with(NotificationManagerCompat.from(Graph.appContext)) {
            notify(notificationId, builder.build())
        }
    }
    if(n == 3) {
        val builder = NotificationCompat.Builder(Graph.appContext, "CHANNEL_ID")
            .setSmallIcon(R.drawable.ic_launcher_background)
            .setContentTitle("Deleted reminder")
            .setContentText("You deleted the reminder successfully!")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
        with(NotificationManagerCompat.from(Graph.appContext)) {
            notify(notificationId, builder.build())
        }
    }
}

data class PaymentViewState(
    val categories: List<Category> = emptyList()
)