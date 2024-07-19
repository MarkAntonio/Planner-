package com.rocketseat.planner.trip;

import com.rocketseat.planner.activity.ActivityData;
import com.rocketseat.planner.activity.ActivityRequestPayload;
import com.rocketseat.planner.activity.ActivityResponse;
import com.rocketseat.planner.activity.ActivityService;
import com.rocketseat.planner.link.LinkData;
import com.rocketseat.planner.link.LinkRequestPayload;
import com.rocketseat.planner.link.LinkResponse;
import com.rocketseat.planner.link.LinkService;
import com.rocketseat.planner.participant.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/trips")
public class TripController {

    // Annotation do Spring para a Injeção de Dependência
    @Autowired
    private ParticipantService participantService;
    @Autowired
    private ActivityService activityService;
    @Autowired
    private LinkService linkService;
    @Autowired
    private TripRepository repository;

    // TRIPS

    //Mostra que esse EndPoint é alcançado pelo POST..
    @PostMapping
    //inicialmene retorna um ResponseEntity<String>, pra a gente testar
    public ResponseEntity<TripCreateResponse> createTrip(@RequestBody TripRequestPayload payload) {
        Trip newTrip = new Trip(payload);
        this.repository.save(newTrip);
        this.participantService.registerParticipantsToEvent(payload.emails_to_invite(), newTrip);
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

    @PutMapping("/{id}")
    public ResponseEntity<Trip> updateTrip(@PathVariable UUID id, @RequestBody TripRequestPayload payload){
        Optional<Trip> trip = this.repository.findById(id);

        //se Existe
        if (trip.isPresent()){
            //pega o objeto do optional
            Trip rawTrip = trip.get();
            rawTrip.setEndsAt(LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME));
            rawTrip.setStartsAt(LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME));
            rawTrip.setDestination(payload.destination());

            // Só aqui é que atualiza minha Viagem no Banco
            this.repository.save(rawTrip);

            return ResponseEntity.ok(rawTrip);
        }
        // caso não encontre já retorno um NotFound;
        return ResponseEntity.notFound().build();
    }

    // o /confirm é apenas para deixar mais organizado
    @GetMapping("/{id}/confirm")
    public ResponseEntity<Trip> updateTripDetails(@PathVariable UUID id){
        Optional<Trip> trip = this.repository.findById(id);

        if (trip.isPresent()){
            Trip rawTrip = trip.get();
            rawTrip.setConfirmed(true);

            this.repository.save(rawTrip);
            // responsável por recuperar todos os participantes de uma viagem e disparar os emails pra eles
            this.participantService.triggerConfirmationEmailToParticipants(id);

            return ResponseEntity.ok(rawTrip);
        }
        return ResponseEntity.notFound().build();
    }

    // PARTICIPANT

    @PostMapping("/{id}/invite ")
    //Estou deixando esse método aqui porque eu estou convidando um participante para uma viagem e não o contrário
    public ResponseEntity<ParticipantCreateResponse> inviteParticipant(@PathVariable UUID id, @RequestBody ParticipantRequestPayload payload) {
        Optional<Trip> trip = this.repository.findById(id);

        if (trip.isPresent()){
            // preciso da viagem porque eu quero criar um novo participant para ela
            Trip rawTrip = trip.get();

            ParticipantCreateResponse participantResponse = this.participantService.registerParticipantToEvent(payload.email(), rawTrip);

            if(rawTrip.isConfirmed()) this.participantService.triggerConfirmationEmailToParticipant(payload.email());

            return ResponseEntity.ok(participantResponse);
        }
        return ResponseEntity.notFound().build();

    }

    @GetMapping("/{id}/participants")
    public ResponseEntity<List<ParticipantData>> getAllParticipants(@PathVariable UUID id){
            List<ParticipantData> participantsList = this.participantService.getAllParticipantsFromEvent(id);
            return ResponseEntity.ok(participantsList);
    }

    // ACTIVITIES

    @PostMapping("/{id}/activities")
    //Estou deixando esse método aqui porque eu estou convidando um participante para uma viagem e não o contrário
    public ResponseEntity<ActivityResponse> registerAtivity(@PathVariable UUID id, @RequestBody ActivityRequestPayload payload) {
        Optional<Trip> trip = this.repository.findById(id);

        if (trip.isPresent()){
            // preciso da viagem porque eu quero criar um novo participant para ela
            Trip rawTrip = trip.get();

            ActivityResponse activityResponse = this.activityService.registerActivity(payload, rawTrip);

            return ResponseEntity.ok(activityResponse);
        }
        return ResponseEntity.notFound().build();

    }

    @GetMapping("/{id}/activities")
    public ResponseEntity<List<ActivityData>> getAllActivities(@PathVariable UUID id){
        List<ActivityData> activityDataList = this.activityService.getAllActivitiesFromId(id);
        return ResponseEntity.ok(activityDataList);

    }

    // LINKS

    @PostMapping("/{id}/links")
    //Estou deixando esse método aqui porque eu estou convidando um participante para uma viagem e não o contrário
    public ResponseEntity<LinkResponse> registerLink(@PathVariable UUID id, @RequestBody LinkRequestPayload payload) {
        Optional<Trip> trip = this.repository.findById(id);

        if (trip.isPresent()){
            // preciso da viagem porque eu quero criar um novo participant para ela
            Trip rawTrip = trip.get();

            LinkResponse linkResponse = this.linkService.registerLink(payload, rawTrip);

            return ResponseEntity.ok(linkResponse);
        }
        return ResponseEntity.notFound().build();

    }

    @GetMapping("/{id}/links")
    public ResponseEntity<List<LinkData>> getAllLinks(@PathVariable UUID id){
        List<LinkData> linksDataList = this.linkService.getAllLinksFromTrip(id);
        return ResponseEntity.ok(linksDataList);

    }

}
