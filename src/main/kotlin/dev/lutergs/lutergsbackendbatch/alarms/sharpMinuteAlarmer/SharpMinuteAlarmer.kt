package dev.lutergs.lutergsbackendbatch.alarms.sharpMinuteAlarmer

import dev.lutergs.lutergsbackendbatch.alarms.Scheduler
import dev.lutergs.lutergsbackendbatch.requester.ScheduledAlarmRequester
import dev.lutergs.lutergsbackendbatch.requester.TriggerTopicRequest
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Component
class SharpMinuteAlarmer(
    scheduledAlarmRequester: ScheduledAlarmRequester,
    @Value("\${custom.batch.sharp-minute-alarmer.uuid}") private val uuid: String
): Scheduler(scheduledAlarmRequester) {

    @Scheduled(cron = "0 * * * * *")
    override fun batch() {
        this.produceMessage()
            .flatMap { this.sendMessage(it) }
            .subscribe()
    }

    override fun produceMessage(): Mono<TriggerTopicRequest> {
        return LocalDateTime.now()
            .let { TriggerTopicRequest(
                this.uuid,
                "매분 알림",
                "${it.minute}분입니다",
                null ) }
            .let { Mono.just(it) }
    }
}