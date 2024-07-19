package com.rocketseat.planner.activity;

// estou colocando o ocurrs_a como String mesmo para depois colcoar o parse no controler para LocalDateTime
public record ActivityRequestPayload(String title, String occurs_at) {
}
