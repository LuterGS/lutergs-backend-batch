package dev.lutergs.lutergsbackendbatch.util

import com.fasterxml.jackson.core.JsonParser
import com.fasterxml.jackson.databind.DeserializationContext
import com.fasterxml.jackson.databind.JsonDeserializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.deser.std.StdDeserializer
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import java.time.ZoneOffset
import java.util.TimeZone

@Configuration
class ObjectMapperConfiguration {

    @Bean
    fun objectMapper(): ObjectMapper {
        return ObjectMapper().registerKotlinModule()
    }
}

class TimeZoneDeserializer: JsonDeserializer<TimeZone>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): TimeZone {
        return TimeZone.getTimeZone(p.valueAsString)
    }
}

class ZoneOffsetDeserializer: JsonDeserializer<ZoneOffset>() {
    override fun deserialize(p: JsonParser, ctxt: DeserializationContext): ZoneOffset {
        return ZoneOffset.ofTotalSeconds(p.intValue)
    }
}
