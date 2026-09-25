package com.paimana.insight.dto;

public final class DTOs {

    public record Login(
            String email,
            String password
    ) {}

    public record Signup(
            String name,
            String email,
            String password
    ) {}

    public record LoginOut(
            String token,
            String name,
            String email,
            String role
    ) {}

    public record Chat(
            String message
    ) {}

    public record WhatIf(
            String scenarioName,
            java.math.BigDecimal testCost,
            java.time.LocalDate testCompletionDate
    ) {}

    public record Risk(
            String projectCode,
            String costRisk,
            String delayRisk,
            String overallRisk,
            String modelName,
            String modelVersion,
            java.util.List<Evidence> evidence
    ) {}

    public record Evidence(
            String factorType,
            String description,
            Double evidenceWeight,
            String historicalEvidenceRef
    ) {}

    private DTOs() {}
}