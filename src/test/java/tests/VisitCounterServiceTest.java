package tests;

import static org.assertj.core.api.Assertions.assertThat;

import buysell.services.VisitCounterService;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class VisitCounterServiceTest {

    private VisitCounterService visitCounterService;

    @BeforeEach
    void setUp() {
        visitCounterService = new VisitCounterService();
    }

    @Test
    void incrementCount_shouldIncrementTotalCounter() {
        visitCounterService.incrementCount("/test");
        visitCounterService.incrementCount("/test2");

        assertThat(visitCounterService.getTotalVisitCount()).isEqualTo(2);
    }

    @Test
    void incrementCount_shouldIncrementUrlCounter() {

        visitCounterService.incrementCount("/test");
        visitCounterService.incrementCount("/test");
        visitCounterService.incrementCount("/test2");

        assertThat(visitCounterService.getVisitCount("/test")).isEqualTo(2);
        assertThat(visitCounterService.getVisitCount("/test2")).isEqualTo(1);
    }

    @Test
    void incrementCount_shouldHandleNewUrl() {
        visitCounterService.incrementCount("/new-url");

        assertThat(visitCounterService.getVisitCount("/new-url")).isEqualTo(1);
    }

    @Test
    void getVisitCount_shouldReturnZeroForUnknownUrl() {
        assertThat(visitCounterService.getVisitCount("/unknown")).isEqualTo(0);
    }

    @Test
    void getTotalVisitCount_shouldReturnZeroInitially() {
        assertThat(visitCounterService.getTotalVisitCount()).isEqualTo(0);
    }

    @Test
    void getAllVisitCounts_shouldReturnAllCounters() {
        visitCounterService.incrementCount("/url1");
        visitCounterService.incrementCount("/url1");
        visitCounterService.incrementCount("/url2");

        Map<String, Integer> result = visitCounterService.getAllVisitCounts();

        assertThat(result)
            .hasSize(2)
            .containsEntry("/url1", 2)
            .containsEntry("/url2", 1);
    }

    @Test
    void getAllVisitCounts_shouldReturnEmptyMapInitially() {
        assertThat(visitCounterService.getAllVisitCounts()).isEmpty();
    }

    @Test
    void concurrentIncrement_shouldBeThreadSafe() throws InterruptedException {
        int threadCount = 10;
        int incrementsPerThread = 1000;
        String testUrl = "/concurrent-test";

        Runnable task = () -> {
            for (int i = 0; i < incrementsPerThread; i++) {
                visitCounterService.incrementCount(testUrl);
            }
        };

        Thread[] threads = new Thread[threadCount];
        for (int i = 0; i < threadCount; i++) {
            threads[i] = new Thread(task);
            threads[i].start();
        }

        for (Thread thread : threads) {
            thread.join();
        }

        assertThat(visitCounterService.getTotalVisitCount())
            .isEqualTo(threadCount * incrementsPerThread);

        assertThat(visitCounterService.getVisitCount(testUrl))
            .isEqualTo(threadCount * incrementsPerThread);
    }

    @Test
    void service_shouldHandleEmptyUrl() {
        visitCounterService.incrementCount("");
        visitCounterService.incrementCount("");

        assertThat(visitCounterService.getVisitCount("")).isEqualTo(2);
        assertThat(visitCounterService.getTotalVisitCount()).isEqualTo(2);
    }
}
