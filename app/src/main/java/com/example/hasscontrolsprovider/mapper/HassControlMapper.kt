package com.example.hasscontrolsprovider.mapper

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.service.controls.Control
import android.service.controls.DeviceTypes
import android.service.controls.templates.*
import androidx.annotation.RequiresApi
import com.example.hasscontrolsprovider.R
import com.example.hasscontrolsprovider.entity.*
import com.example.hasscontrolsprovider.ui.MainActivity
import java.util.*

private const val REQUEST_CODE = 100

object PendingIntentConstants {
    const val EXTRA_ENTITY_ID = "entity_id"
    const val EXTRA_NAME = "name"
    const val EXTRA_STATUS = "status"
}

@RequiresApi(Build.VERSION_CODES.R)
fun HassControl.toStatelessControl(context: Context): Control {
    val pendingIntent = createPendingIntent(this, context)
    return Control.StatelessBuilder(entityId, pendingIntent)
        .setTitle(name)
        //.setSubtitle() // TODO
        .setDeviceType(toDeviceType())
        .build()
}

@RequiresApi(Build.VERSION_CODES.R)
@OptIn(ExperimentalStdlibApi::class)
fun HassControl.toStatefulControl(context: Context): Control {
    val controlTemplate: ControlTemplate = when (this) {
        is HassLight -> ToggleRangeTemplate( // TODO: light without brightness support
            entityId,
            state,
            context.getString(R.string.control_action_range_light),
            RangeTemplate(
                entityId,
                0f,
                100f,
                brightnessPercent,
                1f,
                "• %.0f%%"
            )
        )
        is HassSwitch -> ToggleTemplate(
            entityId,
            ControlButton(
                enabled,
                context.getString(R.string.control_action_button_switch)
            )
        )
        else -> ControlTemplate.getNoTemplateObject()
    }

    val pendingIntent = createPendingIntent(this, context)
    return Control.StatefulBuilder(entityId, pendingIntent)
        .setTitle(name)
        .setSubtitle(toSubtitle(context))
        .setStatusText(status.capitalize(Locale.getDefault()))
        .setStatus(if (isAvailable) Control.STATUS_OK else Control.STATUS_ERROR)
        .setDeviceType(toDeviceType())
        .setControlTemplate(controlTemplate)
        //.setCustomColor() // TODO
        .build()
}

@RequiresApi(Build.VERSION_CODES.R)
private fun HassControl.toDeviceType() = when (this) {
    is HassLight -> DeviceTypes.TYPE_LIGHT
    is HassSwitch -> DeviceTypes.TYPE_SWITCH
    is HassCamera -> DeviceTypes.TYPE_CAMERA
    is HassVacuum -> DeviceTypes.TYPE_VACUUM
    else -> DeviceTypes.TYPE_UNKNOWN
}

private fun HassControl.toSubtitle(context: Context): String {
    return when (this) {
        is HassVacuum -> {
            if (features.contains(VacuumFeatures.BATTERY_PERCENT)) {
                context.getString(R.string.control_label_battery, batteryPercent)
            } else {
                ""
            }
        }
        else -> ""
    }
}

private fun createPendingIntent(hassControl: HassControl, context: Context): PendingIntent {
    val intent = Intent(context, MainActivity::class.java).apply {
        putExtra(PendingIntentConstants.EXTRA_ENTITY_ID, hassControl.entityId)
        putExtra(PendingIntentConstants.EXTRA_NAME, hassControl.name)
        putExtra(PendingIntentConstants.EXTRA_STATUS, hassControl.status)
    }

    return PendingIntent.getActivity(
        context,
        REQUEST_CODE,
        intent,
        PendingIntent.FLAG_CANCEL_CURRENT
    )
}