package com.nexusai.dto;

public class DashboardResponse {

    private Long id;
    private String name;
    private String email;
    private int totalDebates;
    private int totalPresentations;
    private double averageScore;

    public DashboardResponse(
            Long id,
            String name,
            String email,
            int totalDebates,
            int totalPresentations,
            double averageScore
    ) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.totalDebates = totalDebates;
        this.totalPresentations = totalPresentations;
        this.averageScore = averageScore;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public int getTotalDebates() {
        return totalDebates;
    }

    public int getTotalPresentations() {
        return totalPresentations;
    }

    public double getAverageScore() {
        return averageScore;
    }
}