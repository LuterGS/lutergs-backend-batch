package dev.lutergs.lutergsbackendbatch.requester

import com.fasterxml.jackson.databind.ObjectMapper
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

    fun request(triggerTopicRequest: TriggerTopicRequest): Mono<List<TriggerTopicResponse>> {
        return this.topicRequester
            .post()
            .headers {
                it.contentType = MediaType.APPLICATION_JSON
                it.set("Authorization", this.token)
            }
            .body(BodyInserters.fromValue(this.objectMapper.writeValueAsString(triggerTopicRequest)))
            .retrieve()
            .bodyToMono(Array<TriggerTopicResponse>::class.java)
            .onErrorResume {
                println(it)
                Mono.justOrEmpty(null)
            }
            .flatMap { Mono.just(it.toList()) }
    }
}