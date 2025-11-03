package com.example.intelligent_inventory_prediction_system.config;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("Inventory Metrics Unit Tests")
class InventoryMetricsTest {

    private MeterRegistry meterRegistry;
    private InventoryMetrics inventoryMetrics;

    @BeforeEach
    void setUp() {
        meterRegistry = new SimpleMeterRegistry();
        inventoryMetrics = new InventoryMetrics(meterRegistry);
    }

    @Test
    @DisplayName("Should increment created counter when incrementCreated is called")
    void incrementCreated_ShouldIncrementCounter() {
        // Arrange
        double initialCount = inventoryMetrics.getInventoryCreatedCounter().count();

        // Act
        inventoryMetrics.incrementCreated();
        inventoryMetrics.incrementCreated();

        // Assert
        double finalCount = inventoryMetrics.getInventoryCreatedCounter().count();
        assertThat(finalCount).isEqualTo(initialCount + 2);
    }

    @Test
    @DisplayName("Should increment updated counter when incrementUpdated is called")
    void incrementUpdated_ShouldIncrementCounter() {
        // Arrange
        double initialCount = inventoryMetrics.getInventoryUpdatedCounter().count();

        // Act
        inventoryMetrics.incrementUpdated();

        // Assert
        double finalCount = inventoryMetrics.getInventoryUpdatedCounter().count();
        assertThat(finalCount).isEqualTo(initialCount + 1);
    }

    @Test
    @DisplayName("Should increment deleted counter when incrementDeleted is called")
    void incrementDeleted_ShouldIncrementCounter() {
        // Arrange
        double initialCount = inventoryMetrics.getInventoryDeletedCounter().count();

        // Act
        inventoryMetrics.incrementDeleted();
        inventoryMetrics.incrementDeleted();
        inventoryMetrics.incrementDeleted();

        // Assert
        double finalCount = inventoryMetrics.getInventoryDeletedCounter().count();
        assertThat(finalCount).isEqualTo(initialCount + 3);
    }

    @Test
    @DisplayName("Should increment stock update counter when incrementStockUpdate is called")
    void incrementStockUpdate_ShouldIncrementCounter() {
        // Arrange
        double initialCount = inventoryMetrics.getStockUpdateCounter().count();

        // Act
        inventoryMetrics.incrementStockUpdate();

        // Assert
        double finalCount = inventoryMetrics.getStockUpdateCounter().count();
        assertThat(finalCount).isEqualTo(initialCount + 1);
    }

    @Test
    @DisplayName("Should update total records gauge when updateTotalRecords is called")
    void updateTotalRecords_ShouldUpdateGauge() {
        // Act
        inventoryMetrics.updateTotalRecords(150L);

        // Assert
        Gauge gauge = meterRegistry.find("inventory.total.records").gauge();
        assertThat(gauge).isNotNull();
        assertThat(gauge.value()).isEqualTo(150.0);
    }

    @Test
    @DisplayName("Should update total records gauge multiple times correctly")
    void updateTotalRecords_MultipleTimes_ShouldReflectLatestValue() {
        // Act
        inventoryMetrics.updateTotalRecords(100L);
        inventoryMetrics.updateTotalRecords(200L);
        inventoryMetrics.updateTotalRecords(50L);

        // Assert
        Gauge gauge = meterRegistry.find("inventory.total.records").gauge();
        assertThat(gauge).isNotNull();
        assertThat(gauge.value()).isEqualTo(50.0);
    }

    @Test
    @DisplayName("Should register search duration timer correctly")
    void searchDurationTimer_ShouldBeRegistered() {
        // Assert
        Timer timer = meterRegistry.find("inventory.search.duration").timer();
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("Should record search duration correctly")
    void searchDurationTimer_ShouldRecordDuration() {
        // Act
        inventoryMetrics.getSearchDurationTimer().record(() -> {
            try {
                Thread.sleep(100); // Simulate search operation
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // Assert
        Timer timer = meterRegistry.find("inventory.search.duration").timer();
        assertThat(timer).isNotNull();
        assertThat(timer.count()).isEqualTo(1);
        assertThat(timer.totalTime(java.util.concurrent.TimeUnit.MILLISECONDS))
                .isGreaterThan(90); // حداقل 90ms
    }

    @Test
    @DisplayName("All metrics should have correct tags")
    void allMetrics_ShouldHaveCorrectTags() {
        // Assert
        Counter createdCounter = meterRegistry.find("inventory.operations.created")
                .tag("operation", "create")
                .counter();
        assertThat(createdCounter).isNotNull();

        Counter updatedCounter = meterRegistry.find("inventory.operations.updated")
                .tag("operation", "update")
                .counter();
        assertThat(updatedCounter).isNotNull();

        Counter deletedCounter = meterRegistry.find("inventory.operations.deleted")
                .tag("operation", "delete")
                .counter();
        assertThat(deletedCounter).isNotNull();

        Counter stockCounter = meterRegistry.find("inventory.stock.updates")
                .tag("operation", "stock-update")
                .counter();
        assertThat(stockCounter).isNotNull();

        Timer searchTimer = meterRegistry.find("inventory.search.duration")
                .tag("operation", "search")
                .timer();
        assertThat(searchTimer).isNotNull();
    }
}
