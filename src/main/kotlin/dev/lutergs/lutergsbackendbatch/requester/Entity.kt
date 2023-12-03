package dev.lutergs.lutergsbackendbatch.requester

data class TriggerTopicRequest(
    val title: String,
    val message: String,
    val imageUrl: String?
)

// need to receive this as array
data class TriggerTopicResponse(
    val auth: String,
    val responseCode: Int,
    val responseBody: String
)