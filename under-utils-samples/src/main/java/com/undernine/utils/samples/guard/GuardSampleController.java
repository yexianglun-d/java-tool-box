package com.undernine.utils.samples.guard;

import com.undernine.utils.spring.annotation.PreventRepeat;
import com.undernine.utils.spring.annotation.RateLimit;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.Map;

@RestController
@RequestMapping("/samples/guard")
public class GuardSampleController {

    @RateLimit(namespace = "sample:sms", limit = 3, period = 60, message = "too many sms requests")
    @PostMapping("/sms")
    public Map<String, Object> sendSms(@RequestBody SmsCommand command) {
        return Map.of(
                "accepted", true,
                "phone", command.phone(),
                "time", Instant.now().toString()
        );
    }

    @PreventRepeat(namespace = "sample:order", timeout = 10, message = "duplicate submit")
    @PostMapping("/orders")
    public Map<String, Object> createOrder(@RequestBody CreateOrderCommand command) {
        return Map.of(
                "created", true,
                "requestNo", command.requestNo(),
                "skuId", command.skuId(),
                "quantity", command.quantity()
        );
    }

    public record SmsCommand(String phone, String templateCode) {
    }

    public record CreateOrderCommand(String requestNo, String skuId, int quantity) {
    }
}
