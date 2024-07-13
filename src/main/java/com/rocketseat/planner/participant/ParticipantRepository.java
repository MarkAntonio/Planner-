package com.rocketseat.planner.participant;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ParticipantRepository extends JpaRepository<Participant, UUID> {

    // Somente em eu declarar o método (sem corpo) seguindo a sintaxe do JPA, ele é capaz de criar todo o SQL por debaixo.
    List<Participant> findByTripId(UUID id);
}
