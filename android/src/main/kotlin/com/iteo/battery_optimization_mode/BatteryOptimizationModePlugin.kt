package com.iteo.battery_optimization_mode

import android.content.ActivityNotFoundException
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.PowerManager
import android.provider.Settings
import androidx.annotation.NonNull
import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result
import java.util.*

/** BatteryOptimizationModePlugin */
class BatteryOptimizationModePlugin : FlutterPlugin, MethodCallHandler {
    private lateinit var channel: MethodChannel
    private var applicationContext: Context? = null

    override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
        applicationContext = flutterPluginBinding.applicationContext
        channel = MethodChannel(flutterPluginBinding.binaryMessenger, "battery_optimization_mode")
        channel.setMethodCallHandler(this)
    }

    override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
        when (call.method) {
            "isInPowerSaveMode" -> {
                isInPowerSaveMode(result)
            }
            "isIgnoringPowerSaveMode" -> {
                isIgnoringPowerSaveMode(result)
            }
            "openBatterySettings" -> {
                openBatterySettings(result)
            }
            else -> {
                result.notImplemented()
            }
        }
    }

    override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
        channel.setMethodCallHandler(null)
        applicationContext = null
    }

    private fun isInPowerSaveMode(@NonNull result: Result) {
        applicationContext?.apply {
            return if (Build.MANUFACTURER.toLowerCase(Locale.ROOT) == "huawei") {
                isPowerSaveModeHuawei(this, result)
            } else {
                isPowerSaveModeAndroid(this, result)
            }
        }

        result.notImplemented()
    }

    private fun isIgnoringPowerSaveMode(@NonNull result: Result) {
        applicationContext?.apply {
            val pm = this.getSystemService(Context.POWER_SERVICE) as PowerManager?
            pm?.let {
                result.success(it.isIgnoringBatteryOptimizations(packageName))
                return
            }
        }

        result.notImplemented()
    }

    private fun openBatterySettings(@NonNull result: Result) {
        applicationContext?.apply {
            when {
                Build.MANUFACTURER.toLowerCase(Locale.ROOT) == "samsung" -> {
                    val intent = Intent()
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.N) {
                        intent.component = ComponentName("com.samsung.android.lool", "com.samsung.android.sm.ui.battery.BatteryActivity")
                    } else if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
                        intent.component = ComponentName("com.samsung.android.sm", "com.samsung.android.sm.ui.battery.BatteryActivity")
                    }

                    tryToOpenBatterySettingsWithFallback(this, intent)
                }
                Build.MANUFACTURER.toLowerCase(Locale.ROOT) == "huawei" -> {
                    val intent = Intent()
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.component = ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity")
                    tryToOpenBatterySettingsWithFallback(this, intent)
                }
                Build.MANUFACTURER.toLowerCase(Locale.ROOT) == "xiaomi" -> {
                    val intent = Intent()
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                    intent.component = ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity")
                    tryToOpenBatterySettingsWithFallback(this, intent)
                }
                else -> {
                    openFallbackScreen(this)
                }
            }
        }
        result.success(true)
    }

    private fun tryToOpenBatterySettingsWithFallback(context: Context, intent: Intent) {
        try {
            context.startActivity(intent);
        } catch (ex: ActivityNotFoundException) {
            openFallbackScreen(context)
        }
    }

    private fun openFallbackScreen(context: Context) {
        try {
            val fallbackIntent = Intent(Settings.ACTION_BATTERY_SAVER_SETTINGS)
            fallbackIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(fallbackIntent)
        } catch (ex: ActivityNotFoundException) {
            val fallbackIntent = Intent(Settings.ACTION_SETTINGS)
            fallbackIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            context.startActivity(fallbackIntent)
        }
    }

    private fun isPowerSaveModeHuawei(context: Context, @NonNull result: Result) {
        try {
            val value = Settings.System.getInt(context.contentResolver, "SmartModeStatus")
            val isInPowerSaveMode = value == 4
            result.success(isInPowerSaveMode)
        } catch (e: Settings.SettingNotFoundException) {
            isPowerSaveModeAndroid(context, result)
        }
    }

    private fun isPowerSaveModeAndroid(context: Context, @NonNull result: Result) {
        var isPowerSaveMode = false

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager?
            if (pm != null) isPowerSaveMode = pm.isPowerSaveMode
        }

        result.success(isPowerSaveMode)
    }
}
