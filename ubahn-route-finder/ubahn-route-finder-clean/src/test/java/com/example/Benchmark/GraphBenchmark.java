package com.example.Benchmark;

import com.example.Model.GraphNodeAL;
import com.example.Util.GraphAlgorithms;
import org.openjdk.jmh.annotations.*;

import java.util.List;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
@State(Scope.Thread) // Each test thread will use a separate instance of this benchmark class
@Fork(1) // Run one fork to minimize interference from JVM adaptations
@Warmup(iterations = 2, time = 2, timeUnit = TimeUnit.SECONDS) // Prepare the JVM for benchmarking by running some preliminary trials
@Measurement(iterations = 2, time = 2, timeUnit = TimeUnit.SECONDS) // Define how the benchmark trials are measured
public class GraphBenchmark {

    public static void main(String[] args) throws Exception {
        org.openjdk.jmh.Main.main(args);
    }
    GraphNodeAL<String> a, b, c, d;

    @Setup(Level.Invocation)
    public void setup() {
        a = new GraphNodeAL<>("A", "A");
        b = new GraphNodeAL<>("B", "B");
        c = new GraphNodeAL<>("C", "C");
        d = new GraphNodeAL<>("D", "D");

        a.addLink(b, 5, "U1", "#FF0000");
        b.addLink(c, 3, "U1", "#FF0000");
        c.addLink(d, 2, "U1", "#FF0000");
        a.addLink(d, 20, "U2", "#0000FF");
    }

    @Benchmark
    public void benchmarkDijkstra() {
        GraphAlgorithms.findCheapestPathDijkstra(a, "D", List.of());
    }

    @Benchmark
    public void benchmarkBFS() {
        GraphAlgorithms.findAnyRouteBFS(a, "D", List.of());
    }

    @Benchmark
    public void benchmarkWithPenalty() {
        GraphAlgorithms.findCheapestPathWithPenalty(a, "D", 10, List.of());
    }

    @Benchmark
    public void benchmarkAllRoutesDFS() {
        GraphAlgorithms.findAllDFSPaths(a, null, 0, "D");
    }

    @Benchmark
    public void benchmarkBFSWithAvoid() {
        GraphAlgorithms.findAnyRouteBFS(a, "D", List.of("C"));
    }

}

