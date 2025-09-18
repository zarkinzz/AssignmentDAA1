package org.example;

public class Metrics {
    public long comparisons;
    public long allocations;
    public long depth;
    public long maxDepth;

    public void incComp{comparisons++;}
    public void addComp(long c){comparisons+=c;}
    public void alloc(long c){allocations+=c;}
    public void push(){depth++; if (depth > maxDepth) maxDepth = depth;}
    public void pop(){depth--;}
    public void reset(){comparisons=0;allocations=0;depth=0;maxDepth=0;}
}
