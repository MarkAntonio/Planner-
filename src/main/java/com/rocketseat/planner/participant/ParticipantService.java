package com.rocketseat.planner.participant;

import com.rocketseat.planner.trip.Trip;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
public class ParticipantService {

    @Autowired
    private ParticipantRepository repository;

    public void registerParticipantsToEvent(List<String> participantsToInvite, Trip trip) {
        // Rodando por todos, pegando o email e criando um novo participante.
        // No final fica um objeto do tipo Stream, que então eu reconverto uma lista.
        List<Participant> participants = participantsToInvite.stream().map(email -> new Participant(email, trip)).toList();
        this.repository.saveAll(participants);
        // Só depois de salvar é que o ID é gerado
        System.out.println(participants.get(0).getId());
    }

    public ParticipantCreateResponse registerParticipantToEvent(String email, Trip trip) {
        Participant newParticipant = new Participant(email, trip);
        this.repository.save(newParticipant);

        return new ParticipantCreateResponse(newParticipant.getId());
    }

    // responsável por recuperar todos os participantes de uma viagem e disparar os emails pra eles
    public void triggerConfirmationEmailToParticipants(UUID tripId) {

    }

    public void triggerConfirmationEmailToParticipant(String email) {

    }

    public List<ParticipantData> getAllParticipantsFromEvent(UUID tripId) {
        return this.repository.findByTripId(tripId)
                .stream()
                .map(participant -> new ParticipantData(
                        participant.getId(),
                        participant.getName(),
                        participant.getEmail(),
                        participant.isConfirmed())
                )
                .toList();
    }
}
