package com.rocketseat.planner.participant;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/participants")
public class ParticipantController {

    @Autowired
    private ParticipantRepository repository;

    // Aqui será post mapping, pois apesar de já termos seu email,
    // é só no momento da confirmação da viagem é que recebemos o seu nome
    @PostMapping("/{id}/confirm")
    // o frontend não precisa que eu retorne as informações do participante,
    // mas para deixar com o mesmo padrão do TripController, vamos fazer assim
    public ResponseEntity<Participant> confirmParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload){
        Optional<Participant> participant = repository.findById(id);

        if (participant.isPresent()){
            Participant rawParticipant = participant.get();
            rawParticipant.setConfirmed(true);
            rawParticipant.setName(payload.name());

            this.repository.save(rawParticipant);

            return ResponseEntity.ok(rawParticipant);
        }

        return ResponseEntity.notFound().build();
    }
}
