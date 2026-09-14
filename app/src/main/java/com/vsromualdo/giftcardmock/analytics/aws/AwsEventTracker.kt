package com.vsromualdo.giftcardmock.analytics.aws

import android.os.Build
import com.vsromualdo.giftcardmock.BuildConfig
import java.net.HttpURLConnection
import java.net.URL
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject

// TODO: substituir pela URL real do API Gateway quando o pipeline estiver publicado.
private const val API_GATEWAY_URL = "https://TODO-REPLACE-WITH-REAL-API-GATEWAY-URL.execute-api.sa-east-1.amazonaws.com/prod/events"

// TODO: substituir pela chave real. Ver nota de dívida técnica em plan.md sobre API key
// fixa no código de um app de produção.
private const val API_KEY = "TODO-REPLACE-WITH-REAL-API-KEY"

private val eventDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale("pt", "BR"))

/**
 * Envia um evento no formato legado (channel + event-category/event-action/event-label/hit-type)
 * para o pipeline próprio na AWS (App -> API Gateway -> Lambda -> MSK/Kinesis -> Databricks).
 * Roda em paralelo ao Firebase Analytics; nunca lança exceção para o chamador (RF-25).
 */
fun sendAwsEvent(
    eventCategory: String,
    eventAction: String,
    eventLabel: String,
    hitType: String = "1",
    channelName: String = "1",
) {
    CoroutineScope(Dispatchers.IO).launch {
        runCatching {
            val payload = JSONObject().apply {
                put(
                    "channel",
                    JSONObject().apply {
                        put("appVersion", BuildConfig.VERSION_NAME)
                        put("deviceManufacturer", Build.MANUFACTURER.uppercase())
                        put("deviceModel", Build.MODEL)
                        put("deviceOS", "ANDROID")
                        put("name", channelName)
                    },
                )
                put("event-action", eventAction)
                put("event-category", eventCategory)
                put("event-date", eventDateFormat.format(Date()))
                put("event-label", eventLabel)
                put("hit-type", hitType)
            }

            val connection = URL(API_GATEWAY_URL).openConnection() as HttpURLConnection
            try {
                connection.requestMethod = "POST"
                connection.doOutput = true
                connection.setRequestProperty("Content-Type", "application/json")
                connection.setRequestProperty("x-api-key", API_KEY)
                connection.outputStream.use { it.write(payload.toString().toByteArray(Charsets.UTF_8)) }
                connection.responseCode
            } finally {
                connection.disconnect()
            }
        }
    }
}
