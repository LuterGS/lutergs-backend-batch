package dev.lutergs.lutergsbackendbatch.alarms.sharpHourAlarmer

import dev.lutergs.lutergsbackendbatch.alarms.Scheduler
import dev.lutergs.lutergsbackendbatch.requester.ScheduledAlarmRequester
import dev.lutergs.lutergsbackendbatch.requester.TriggerTopicRequest
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.sql.Timestamp
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset

@Component
class SharpHourAlarmer(
    scheduledAlarmRequester: ScheduledAlarmRequester,
    @Value("\${custom.batch.sharp-hour-alarmer.uuid}") private val uuid: String
): Scheduler(scheduledAlarmRequester) {

    @Scheduled(cron = "0 0 * * * *")
    override fun batch() {
        this.produceMessage()
            .flatMap { this.sendMessage(it) }
            .subscribe()
    }

    override fun produceMessage(): Mono<TriggerTopicRequest> {
        return LocalDateTime.now()
            .let { TriggerTopicRequest(
                uuid,
                "정각 알림",
                "${it.hour}시입니다",
                null ) }
            .let { Mono.just(it) }
    }
}