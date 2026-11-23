package com.catvpn.app

import android.app.Activity
import android.content.Intent
import android.net.VpnService
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.wireguard.android.backend.GoBackend
import com.wireguard.android.backend.Tunnel
import com.wireguard.config.Config
import java.util.concurrent.Executors

class MainActivity : ComponentActivity() {
    private val worker = Executors.newSingleThreadExecutor()
    private lateinit var backend: GoBackend
    private lateinit var configPicker: ActivityResultLauncher<Array<String>>
    private lateinit var vpnPermission: ActivityResultLauncher<Intent>

    private var importedConfig: Config? = null
    private var status by mutableStateOf("Сначала выбери файл конфигурации WireGuard.")
    private var configLoaded by mutableStateOf(false)
    private var connected by mutableStateOf(false)

    private val tunnel = object : Tunnel {
        override fun getName(): String = "Cat VPN"

        override fun onStateChange(newState: Tunnel.State) {
            runOnUiThread {
                connected = newState == Tunnel.State.UP
                status = if (connected) {
                    "Туннель WireGuard включён. Это ещё не подтверждает доступность интернета."
                } else {
                    "Туннель отключён."
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        backend = GoBackend(applicationContext)

        configPicker = registerForActivityResult(ActivityResultContracts.OpenDocument()) { uri ->
            if (uri != null) importConfig(uri)
        }
        vpnPermission = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                startTunnel()
            } else {
                status = "Разрешение Android на VPN не предоставлено."
            }
        }

        setContent {
            MaterialTheme {
                CatVpnScreen(
                    status = status,
                    configLoaded = configLoaded,
                    connected = connected,
                    onImport = { configPicker.launch(arrayOf("*/*")) },
                    onConnect = { requestVpnPermission() },
                    onDisconnect = { stopTunnel() }
                )
            }
        }
    }

    private fun importConfig(uri: Uri) {
        status = "Читаю конфигурацию…"
        worker.execute {
            try {
                val parsed = contentResolver.openInputStream(uri)?.use { input ->
                    Config.parse(input)
                } ?: throw IllegalStateException("Не удалось открыть файл.")
                importedConfig = parsed
                runOnUiThread {
                    configLoaded = true
                    status = "Конфигурация загружена в память телефона. Ключи не отправляются в GitHub."
                }
            } catch (error: Exception) {
                runOnUiThread {
                    configLoaded = false
                    importedConfig = null
                    status = "Не удалось прочитать конфигурацию: ${error.message ?: "проверь файл"}"
                }
            }
        }
    }

    private fun requestVpnPermission() {
        if (importedConfig == null) {
            status = "Сначала выбери файл конфигурации WireGuard."
            return
        }
        val intent = VpnService.prepare(this)
        if (intent == null) startTunnel() else vpnPermission.launch(intent)
    }

    private fun startTunnel() {
        val config = importedConfig ?: return
        status = "Подключаю WireGuard…"
        worker.execute {
            try {
                backend.setState(tunnel, Tunnel.State.UP, config)
            } catch (error: Exception) {
                runOnUiThread {
                    connected = false
                    status = "Не удалось подключиться: ${error.message ?: "проверь конфигурацию и сервер"}"
                }
            }
        }
    }

    private fun stopTunnel() {
        status = "Отключаю…"
        worker.execute {
            try {
                backend.setState(tunnel, Tunnel.State.DOWN, null)
            } catch (error: Exception) {
                runOnUiThread {
                    status = "Ошибка отключения: ${error.message ?: "неизвестная ошибка"}"
                }
            }
        }
    }

    override fun onDestroy() {
        worker.shutdown()
        super.onDestroy()
    }
}

@androidx.compose.runtime.Composable
private fun CatVpnScreen(
    status: String,
    configLoaded: Boolean,
    connected: Boolean,
    onImport: () -> Unit,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit
) {
    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(
            modifier = Modifier.fillMaxSize().padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = "🐱 Cat VPN", style = MaterialTheme.typography.headlineLarge)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = "🇳🇱 Нидерланды", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Протокол: WireGuard")
            Spacer(modifier = Modifier.height(24.dp))
            Text(text = status, style = MaterialTheme.typography.bodyMedium)
            Spacer(modifier = Modifier.height(20.dp))
            OutlinedButton(onClick = onImport, modifier = Modifier.fillMaxWidth()) {
                Text(if (configLoaded) "Выбрать другой конфиг" else "Импортировать конфиг")
            }
            Spacer(modifier = Modifier.height(12.dp))
            if (connected) {
                Button(onClick = onDisconnect, modifier = Modifier.fillMaxWidth()) {
                    Text("Отключить")
                }
            } else {
                Button(
                    onClick = onConnect,
                    enabled = configLoaded,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Подключить")
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = "Нужен действующий файл .conf от WireGuard-сервера. Не отправляй его в чат и не добавляй в GitHub: внутри находятся секретные ключи.",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}
