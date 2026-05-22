package com.undernine.utils.samples.mybatis;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.undernine.utils.mybatis.handler.AuditorProvider;
import com.undernine.utils.mybatis.page.SafePageQuery;
import com.undernine.utils.mybatis.page.SortFieldMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/samples/mybatis")
public class MyBatisSampleController {

    private final AuditorProvider auditorProvider;

    public MyBatisSampleController(AuditorProvider auditorProvider) {
        this.auditorProvider = auditorProvider;
    }

    @GetMapping("/page")
    public Map<String, Object> page(@RequestParam(defaultValue = "1") long current,
                                    @RequestParam(defaultValue = "20") long size,
                                    @RequestParam(defaultValue = "createdAt") String sort,
                                    @RequestParam(defaultValue = "desc") String direction) {
        SortFieldMapping mapping = SortFieldMapping.builder()
                .add("createdAt", "create_time")
                .add("status", "status")
                .add("amount", "pay_amount")
                .build();

        SafePageQuery query = SafePageQuery.of(current, size);
        if ("asc".equalsIgnoreCase(direction)) {
            query.orderByAsc(sort);
        } else {
            query.orderByDesc(sort);
        }

        Page<Object> page = query.buildPage(mapping, SafePageQuery.SortOrder.desc("createdAt"));
        List<Map<String, Object>> orders = page.orders().stream()
                .map(MyBatisSampleController::describeOrder)
                .toList();

        Long auditor = auditorProvider.getCurrentAuditor();
        return Map.of(
                "current", page.getCurrent(),
                "size", page.getSize(),
                "orders", orders,
                "auditor", auditor == null ? "" : auditor
        );
    }

    private static Map<String, Object> describeOrder(OrderItem order) {
        return Map.of(
                "column", order.getColumn(),
                "asc", order.isAsc()
        );
    }
}
