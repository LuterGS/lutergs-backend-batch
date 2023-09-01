package dev.lutergs.lutergsbackendbatch.alarms.sunsetAlarmer

import dev.lutergs.lutergsbackendbatch.alarms.Scheduler
import dev.lutergs.lutergsbackendbatch.alarms.sunsetAlarmer.openWeather.OpenWeatherResponse
import dev.lutergs.lutergsbackendbatch.requester.ScheduledAlarmRequester
import dev.lutergs.lutergsbackendbatch.requester.TriggerTopicRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.sql.Timestamp
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneOffset

@Component
class SunsetAlarmer(
    scheduledAlarmRequester: ScheduledAlarmRequester,
    @Value("\${custom.batch.sunset-alarmer.weather-url}") baseUrl: String,
    @Value("\${custom.batch.sunset-alarmer.token}") private val token: String,
    @Value("\${custom.batch.sunset-alarmer.location.latitude}") private val latitude: Float,
    @Value("\${custom.batch.sunset-alarmer.location.longitude}") private val longitude: Float,
    @Value("\${custom.batch.sunset-alarmer.uuid}") private val uuid: String
): Scheduler(scheduledAlarmRequester) {
    private val weatherRequester: WebClient = WebClient.builder()
        .baseUrl(baseUrl)
        .build()

    @Scheduled(cron = "0 0 16 * * *")
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
                    .between(LocalDateTime.now(), response.getSunset())
                    .let { Mono.just(response).delayElement(it) } }
            .flatMap {
                Mono.just(TriggerTopicRequest(
                    this.uuid,
                    "일몰입니다!",
                    "밤이 왔습니다. 지금은 ${it.current.weather[0].description} 날씨입니다.",
                    it.current.weather[0].getIconUrl()
                ))
            }
    }
}