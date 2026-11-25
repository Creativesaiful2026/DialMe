package com.saiful.dialme.core.telecom.notification

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Icon
import com.saiful.dialme.core.common.logging.DialMeLog
import com.saiful.dialme.core.telecom.CallDirection
import com.saiful.dialme.core.telecom.CallInfo
import com.saiful.dialme.core.telecom.CallStatus
import com.saiful.dialme.core.telecom.InCallLauncher
import com.saiful.dialme.core.telecom.R

/**
 * Shows the incoming-call notification (with a full-screen intent for the lock screen) and an
 * ongoing-call notification, and removes them when a call ends.
 */
object CallNotifier {
    private const val TAG = "CallNotifier"
    private const val CHANNEL_INCOMING = "dialme_incoming_calls"
    private const val CHANNEL_ONGOING = "dialme_ongoing_calls"
    private const val NOTIFICATION_ID_BASE = 4000

    private val lock = Any()
    private val posted = HashSet<Int>()

    fun sync(context: Context, calls: List<CallInfo>) {
        synchronized(lock) {
            val nm = context.getSystemService(NotificationManager::class.java) ?: return
            ensureChannels(context, nm)

            val current = HashSet<Int>()
            for (call in calls) {
                val incomingRinging =
                    call.direction == CallDirection.INCOMING && call.status == CallStatus.RINGING
                val ongoing = call.status == CallStatus.DIALING ||
                    call.status == CallStatus.CONNECTING ||
                    call.status == CallStatus.ACTIVE ||
                    call.status == CallStatus.HOLDING
                try {
                    when {
                        incomingRinging -> {
                            nm.notify(NOTIFICATION_ID_BASE + call.id, buildIncoming(context, call))
                            current.add(call.id)
                        }
                        ongoing -> {
                            nm.notify(NOTIFICATION_ID_BASE + call.id, buildOngoing(context, call))
                            current.add(call.id)
                        }
                    }
                } catch (e: Exception) {
                    DialMeLog.e(TAG, "Could not post notification for call ${call.id}", e)
                }
            }
            for (oldId in posted - current) {
                nm.cancel(NOTIFICATION_ID_BASE + oldId)
            }
            posted.clear()
            posted.addAll(current)
        }
    }

    private fun ensureChannels(context: Context, nm: NotificationManager) {
        val incoming = NotificationChannel(
            CHANNEL_INCOMING,
            context.getString(R.string.notif_channel_incoming_name),
            NotificationManager.IMPORTANCE_HIGH,
        ).apply {
            description = context.getString(R.string.notif_channel_incoming_desc)
            // Telecom plays the ringtone itself, so the notification stays silent.
            setSound(null, null)
            enableVibration(false)
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        val ongoing = NotificationChannel(
            CHANNEL_ONGOING,
            context.getString(R.string.notif_channel_ongoing_name),
            NotificationManager.IMPORTANCE_LOW,
        ).apply {
            lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        }
        nm.createNotificationChannel(incoming)
        nm.createNotificationChannel(ongoing)
    }

    private fun title(context: Context, call: CallInfo): String =
        call.displayName ?: call.number ?: context.getString(R.string.notif_unknown_caller)

    private fun buildIncoming(context: Context, call: CallInfo): Notification {
        val openUi = activityIntent(context, call.id, autoAnswer = false, slot = 0)
        val answer = activityIntent(context, call.id, autoAnswer = true, slot = 1)
        val decline = receiverIntent(context, CallActionReceiver.ACTION_DECLINE, call.id, slot = 2)
        return Notification.Builder(context, CHANNEL_INCOMING)
            .setSmallIcon(android.R.drawable.sym_action_call)
            .setContentTitle(title(context, call))
            .setContentText(context.getString(R.string.notif_incoming_call))
            .setCategory(Notification.CATEGORY_CALL)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setContentIntent(openUi)
            .setFullScreenIntent(openUi, true)
            .addAction(action(context, android.R.drawable.ic_menu_close_clear_cancel, R.string.notif_action_decline, decline))
            .addAction(action(context, android.R.drawable.ic_menu_call, R.string.notif_action_answer, answer))
            .build()
    }

    private fun buildOngoing(context: Context, call: CallInfo): Notification {
        val openUi = activityIntent(context, call.id, autoAnswer = false, slot = 0)
        val hangUp = receiverIntent(context, CallActionReceiver.ACTION_HANGUP, call.id, slot = 3)
        val builder = Notification.Builder(context, CHANNEL_ONGOING)
            .setSmallIcon(android.R.drawable.sym_action_call)
            .setContentTitle(title(context, call))
            .setContentText(context.getString(R.string.notif_ongoing_call))
            .setCategory(Notification.CATEGORY_CALL)
            .setVisibility(Notification.VISIBILITY_PUBLIC)
            .setOngoing(true)
            .setContentIntent(openUi)
            .addAction(action(context, android.R.drawable.ic_menu_close_clear_cancel, R.string.notif_action_hangup, hangUp))
        if (call.status == CallStatus.ACTIVE && call.connectTimeMillis > 0) {
            builder.setUsesChronometer(true).setWhen(call.connectTimeMillis)
        }
        return builder.build()
    }

    private fun action(context: Context, iconRes: Int, titleRes: Int, intent: PendingIntent): Notification.Action =
        Notification.Action.Builder(
            Icon.createWithResource(context, iconRes),
            context.getString(titleRes),
            intent,
        ).build()

    private fun activityIntent(context: Context, callId: Int, autoAnswer: Boolean, slot: Int): PendingIntent {
        val intent = InCallLauncher.intent(context, if (autoAnswer) callId else null)
        return PendingIntent.getActivity(
            context,
            callId * 10 + slot,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }

    private fun receiverIntent(context: Context, action: String, callId: Int, slot: Int): PendingIntent {
        val intent = Intent(context, CallActionReceiver::class.java)
            .setAction(action)
            .putExtra(CallActionReceiver.EXTRA_CALL_ID, callId)
        return PendingIntent.getBroadcast(
            context,
            callId * 10 + slot,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
    }
}
