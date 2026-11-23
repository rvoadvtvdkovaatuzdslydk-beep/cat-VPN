package com.catvpn.app

import android.net.VpnService
import android.content.Intent
import android.os.IBinder

/**
 * Android VPN service entry point.
 *
 * This is only the platform integration scaffold. It intentionally does not
 * create a TUN interface or claim to connect until a real VPN protocol engine,
 * server endpoint, and securely supplied client configuration are added.
 */
class CatVpnService : VpnService() {

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        // A real tunnel must be started here after user consent and validated
        // protocol configuration are available.
        stopSelf(startId)
        return START_NOT_STICKY
    }

    override fun onRevoke() {
        // Android calls this when the user revokes VPN permission.
        stopSelf()
        super.onRevoke()
    }

    override fun onBind(intent: Intent?): IBinder? = super.onBind(intent)
}
