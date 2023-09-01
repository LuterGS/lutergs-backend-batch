package dev.lutergs.lutergsbackendbatch.alarms

import dev.lutergs.lutergsbackendbatch.requester.ScheduledAlarmRequester
import dev.lutergs.lutergsbackendbatch.requester.TriggerTopicRequest
import dev.lutergs.lutergsbackendbatch.requester.TriggerTopicResponse
import reactor.core.publisher.Mono


abstract class Scheduler (
    private val scheduledAlarmRequester: ScheduledAlarmRequester
) {


    abstract fun batch()
    abstract fun produceMessage(): Mono<TriggerTopicRequest>
    fun sendMessage(triggerTopicRequest: TriggerTopicRequest): Mono<List<TriggerTopicResponse>> {
        return this.scheduledAlarmRequester.request(triggerTopicRequest)
    }
}