package com.rocketseat.planner.trip;

import com.rocketseat.planner.participant.ParticipantService;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {

    // Annotation do Spring para a Injeção de Dependência
    @Autowired
    private ParticipantService participantService;

    @Autowired
    private TripRepository repository;

    //Mostra que esse EndPoint é alcançado pelo POST..
    @PostMapping
    //inicialmene retorna um ResponseEntity<String>, pra a gente testar
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload) {
        Trip newTrip = new Trip(payload);
        this.repository.save(newTrip);
        this.participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip.getId());
        return ResponseEntity.ok(new TripCreateResponse(newTrip.getId()));
    }

    @GetMapping("/{id}")
    //@PathVariable indica que o parametro será passado na url
    public ResponseEntity<Trip> getTripDetails(@PathVariable UUID id){
        // é opcional pois o cliente pode procurar um id que não existe e nao encontrar
        Optional<Trip> trip = this.repository.findById(id);
        //ResponseEntity::ok -> monta uma resposta desse tipo com o status ok
        // () -> ResponseEntity.notFound().build() -> monta uma resposta com o status notFound
        return trip.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }
}
