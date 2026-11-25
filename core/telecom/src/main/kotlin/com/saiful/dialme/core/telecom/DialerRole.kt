package com.saiful.dialme.core.telecom

import android.app.role.RoleManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.telecom.TelecomManager

/** Helpers around the default Phone app role (ROLE_DIALER). */
object DialerRole {

    private fun roleManager(context: Context): RoleManager? =
        context.getSystemService(RoleManager::class.java)

    fun isHeld(context: Context): Boolean {
        val rm = roleManager(context) ?: return false
        return rm.isRoleAvailable(RoleManager.ROLE_DIALER) && rm.isRoleHeld(RoleManager.ROLE_DIALER)
    }

    /** Intent that shows the system dialog asking the user to make DialMe the default Phone app. */
    fun requestIntent(context: Context): Intent? {
        val rm = roleManager(context) ?: return null
        if (!rm.isRoleAvailable(RoleManager.ROLE_DIALER)) return null
        return rm.createRequestRoleIntent(RoleManager.ROLE_DIALER)
    }

    /** Package name of the current default Phone app, if the system reports one. */
    fun defaultDialerPackage(context: Context): String? =
        context.getSystemService(TelecomManager::class.java)?.defaultDialerPackage

    /** Human readable name of [packageName], or null if it cannot be looked up. */
    fun appLabel(context: Context, packageName: String): String? {
        return try {
            val pm = context.packageManager
            pm.getApplicationLabel(pm.getApplicationInfo(packageName, 0)).toString()
        } catch (e: PackageManager.NameNotFoundException) {
            null
        } catch (e: Exception) {
            null
        }
    }
}
