package dev.lutergs.lutergsbackendbatch.requester

import com.fasterxml.jackson.databind.ObjectMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono

@Component
class ScheduledAlarmRequester(
    private val objectMapper: ObjectMapper,
    @Value("\${custom.requester.base-url}") baseUrl: String,
    @Value("\${custom.requester.token}") private val token: String
) {
    private val topicRequester: WebClient = WebClient.builder()
        .baseUrl(baseUrl)
        .build()
    private val logger = LoggerFactory.getLogger(this::class.java)

    fun request(triggerTopicRequest: TriggerTopicRequest, topicUUID: String): Mono<List<TriggerTopicResponse>> {
        return this.topicRequester
            .post()
            .uri { it.path("/${topicUUID}")
                .build()
            }
            .headers {
                it.contentType = MediaType.APPLICATION_JSON
                it.set("Authorization", this.token)
            }
            .body(BodyInserters.fromValue(this.objectMapper.writeValueAsString(triggerTopicRequest)))
            .exchangeToMono {
                when {
                    it.statusCode().is2xxSuccessful -> {
                        it.bodyToMono(Array<TriggerTopicResponse>::class.java)
                    }
                    else -> {
                        it.createException()
                            .flatMap { exception ->
                                this.logger.error("Error on ${topicUUID}!\n" +
                                        "\tstatusCode : ${exception.statusCode}\n" +
                                        "\trawMessage : ${exception.responseBodyAsString}")
                                Mono.error(exception)
                            }
                    }
                }
            }.flatMap {
                Mono.just(it.toList()) }
    }
}