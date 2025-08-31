package com.insightra.app.network.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponsesCreateResult(
    val id: String? = null,
    val status: String? = null,
    val output: List<OutputMessage> = emptyList()
)

@Serializable
data class OutputMessage(
    val id: String? = null,
    val role: String? = null,
    val content: List<MessageContent> = emptyList()
)

@Serializable
data class MessageContent(
    val type: String,
    val text: String? = null,
    @SerialName("output_text")
    val outputText: OutputText? = null
)

@Serializable
data class OutputText(
    val content: String? = null
)
