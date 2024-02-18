package dev.lutergs.lutergsbackendbatch.alarms.profitMessageSender

import dev.lutergs.lutergsbackendbatch.alarms.Scheduler
import dev.lutergs.lutergsbackendbatch.requester.ScheduledAlarmRequester
import dev.lutergs.lutergsbackendbatch.requester.TriggerTopicRequest
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.client.WebClient
import reactor.core.publisher.Mono
import java.time.LocalDateTime

@Component
class ProfitMessageSender(
    scheduledAlarmRequester: ScheduledAlarmRequester,
): Scheduler(scheduledAlarmRequester) {

    @Scheduled(cron = "0 0 20 * * *")
    override fun batch() {
        WebClient.builder()
            .baseUrl("http://coin-trader-service/trigger-hour-analytic")
            .build()
            .post()
            .uri { it.queryParam("hour", "24").build() }
            .header("Content-Type", "application/json")
            .retrieve()
            .toBodilessEntity()
            .subscribe()
    }

    override fun produceMessage(): Mono<TriggerTopicRequest> {
        return TriggerTopicRequest(
            "정각 알림",
            "${LocalDateTime.now().hour}시입니다",
            null
        ).let { Mono.just(it) }
    }
}