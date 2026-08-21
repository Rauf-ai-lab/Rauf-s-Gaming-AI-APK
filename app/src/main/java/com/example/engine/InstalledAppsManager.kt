package com.example.engine

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import androidx.core.graphics.drawable.toBitmap

data class AppItem(
    val appName: String,
    val packageName: String,
    val isGame: Boolean,
    val iconBitmap: Bitmap? = null,
    val versionName: String = ""
)

object InstalledAppsManager {

    private val KNOWN_GAME_KEYWORDS = listOf(
        "game", "freefire", "pubg", "bgmi", "cod", "callofduty", "genshin", "roblox",
        "minecraft", "clash", "asphalt", "shadowfight", "fortnite", "mobilelegends",
        "arena", "speed", "racing", "craft", "rpg", "mmo", "fifa", "efootball",
        "pokemon", "dragon", "battle", "shooter", "sniper", "strike", "war", "survival"
    )

    fun getInstalledApps(context: Context): List<AppItem> {
        val pm = context.packageManager
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = try {
            pm.queryIntentActivities(mainIntent, 0)
        } catch (e: Exception) {
            emptyList()
        }

        val appList = mutableListOf<AppItem>()
        val seenPackages = mutableSetOf<String>()

        for (resolveInfo in resolveInfos) {
            val pkgName = resolveInfo.activityInfo?.packageName ?: continue
            if (pkgName == context.packageName || seenPackages.contains(pkgName)) {
                continue
            }
            seenPackages.add(pkgName)

            val appLabel = try {
                resolveInfo.loadLabel(pm).toString()
            } catch (e: Exception) {
                pkgName
            }

            val appInfo = resolveInfo.activityInfo?.applicationInfo
            val isGame = checkIfGame(appInfo, appLabel, pkgName)

            val iconDrawable = try {
                resolveInfo.loadIcon(pm)
            } catch (e: Exception) {
                null
            }

            val bitmap = iconDrawable?.let { drawableToBitmap(it) }

            appList.add(
                AppItem(
                    appName = appLabel,
                    packageName = pkgName,
                    isGame = isGame,
                    iconBitmap = bitmap,
                    versionName = ""
                )
            )
        }

        // Return sorted alphabetically
        return appList.sortedBy { it.appName.lowercase() }
    }

    private fun checkIfGame(appInfo: ApplicationInfo?, label: String, pkgName: String): Boolean {
        if (appInfo != null) {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                if (appInfo.category == ApplicationInfo.CATEGORY_GAME) {
                    return true
                }
            }
            if ((appInfo.flags and ApplicationInfo.FLAG_IS_GAME) != 0) {
                return true
            }
        }

        val lowerLabel = label.lowercase()
        val lowerPkg = pkgName.lowercase()
        return KNOWN_GAME_KEYWORDS.any { lowerLabel.contains(it) || lowerPkg.contains(it) }
    }

    private fun drawableToBitmap(drawable: Drawable): Bitmap? {
        return try {
            if (drawable is BitmapDrawable && drawable.bitmap != null) {
                drawable.bitmap
            } else {
                val width = if (drawable.intrinsicWidth > 0) drawable.intrinsicWidth.coerceAtMost(128) else 96
                val height = if (drawable.intrinsicHeight > 0) drawable.intrinsicHeight.coerceAtMost(128) else 96
                val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                val canvas = Canvas(bitmap)
                drawable.setBounds(0, 0, canvas.width, canvas.height)
                drawable.draw(canvas)
                bitmap
            }
        } catch (e: Exception) {
            null
        }
    }

    fun launchPackage(context: Context, packageName: String): Boolean {
        return try {
            val launchIntent = context.packageManager.getLaunchIntentForPackage(packageName)
            if (launchIntent != null) {
                launchIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                context.startActivity(launchIntent)
                true
            } else {
                false
            }
        } catch (e: Exception) {
            false
        }
    }
}
