package dev.lutergs.lutergsbackendbatch.alarms.sunriseAlarmer

import dev.lutergs.lutergsbackendbatch.alarms.Scheduler
import dev.lutergs.lutergsbackendbatch.alarms.sunriseAlarmer.openWeather.OpenWeatherResponse
import dev.lutergs.lutergsbackendbatch.requester.ScheduledAlarmRequester
import dev.lutergs.lutergsbackendbatch.requester.TriggerTopicRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.sql.Timestamp
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset

@Component
class SunriseAlarmer(
    scheduledAlarmRequester: ScheduledAlarmRequester,
    @Value("\${custom.batch.sunrise-alarmer.weather-url}") baseUrl: String,
    @Value("\${custom.batch.sunrise-alarmer.token}") private val token: String,
    @Value("\${custom.batch.sunrise-alarmer.location.latitude}") private val latitude: Float,
    @Value("\${custom.batch.sunrise-alarmer.location.longitude}") private val longitude: Float,
    @Value("\${custom.batch.sunrise-alarmer.uuid}") private val uuid: String
): Scheduler(scheduledAlarmRequester) {
    private val weatherRequester: WebClient = WebClient.builder()
        .baseUrl(baseUrl)
        .build()

    @Scheduled(cron = "0 0 4 * * *")
    override fun batch() {
        this.produceMessage()
            .flatMap { this.sendMessage(it) }
            .subscribe()
    }

    override fun produceMessage(): Mono<TriggerTopicRequest> {
        return this.weatherRequester
            .get()
            .uri {
                it.queryParam("appid", this.token)
                it.queryParam("lat", this.latitude)
                it.queryParam("lon", this.longitude)
                it.queryParam("units", "metric")
                it.queryParam("lang", "kr")
                it.build() }
            .retrieve()
            .bodyToMono(OpenWeatherResponse::class.java)
            .flatMap { response ->
                Duration
                    .between(LocalDateTime.now(), response.getSunrise())
                    .let { Mono.just(response).delayElement(it) } }
            .flatMap {
                Mono.just(TriggerTopicRequest(
                    this.uuid,
                    "일출입니다!",
                    "해가 떠오릅니다. 지금은 ${it.current.weather[0].description} 날씨입니다.",
                    it.current.weather[0].getIconUrl()
                ))
            }
    }
}