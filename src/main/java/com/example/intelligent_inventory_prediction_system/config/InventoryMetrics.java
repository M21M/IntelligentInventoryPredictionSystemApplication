package com.example.intelligent_inventory_prediction_system.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import lombok.Getter;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicLong;

@Component
@Getter
public class InventoryMetrics {

    private final Counter inventoryCreatedCounter;
    private final Counter inventoryUpdatedCounter;
    private final Counter inventoryDeletedCounter;
    private final Counter stockUpdateCounter;
    private final Timer searchDurationTimer;
    private final AtomicLong totalRecordsGauge;

    public InventoryMetrics(MeterRegistry meterRegistry) {
        this.inventoryCreatedCounter = Counter.builder("inventory.operations.created")
                .description("Total number of inventory records created")
                .tag("operation", "create")
                .register(meterRegistry);

        this.inventoryUpdatedCounter = Counter.builder("inventory.operations.updated")
                .description("Total number of inventory records updated")
                .tag("operation", "update")
                .register(meterRegistry);

        this.inventoryDeletedCounter = Counter.builder("inventory.operations.deleted")
                .description("Total number of inventory records deleted")
                .tag("operation", "delete")
                .register(meterRegistry);

        this.stockUpdateCounter = Counter.builder("inventory.stock.updates")
                .description("Total number of stock level changes")
                .tag("operation", "stock-update")
                .register(meterRegistry);

        this.searchDurationTimer = Timer.builder("inventory.search.duration")
                .description("Time taken to perform inventory search operations")
                .tag("operation", "search")
                .register(meterRegistry);

        this.totalRecordsGauge = new AtomicLong(0);
        Gauge.builder("inventory.total.records", totalRecordsGauge, AtomicLong::get)
                .description("Current total number of inventory records in database")
                .register(meterRegistry);
    }

    public void incrementCreated() {
        inventoryCreatedCounter.increment();
    }

    public void incrementUpdated() {
        inventoryUpdatedCounter.increment();
    }

    public void incrementDeleted() {
        inventoryDeletedCounter.increment();
    }

    public void incrementStockUpdate() {
        stockUpdateCounter.increment();
    }

    public void updateTotalRecords(long count) {
        totalRecordsGauge.set(count);
    }
}
