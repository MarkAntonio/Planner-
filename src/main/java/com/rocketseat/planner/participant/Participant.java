package com.rocketseat.planner.participant;

import com.rocketseat.planner.trip.Trip;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "participants")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Participant {

    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String email;

    @Column(name = "is_confirmed", nullable = false)
    private boolean isConfirmed;

    // uma viagem pode ter muitos participantes e um participante pode ter somente uma viagem
    @ManyToOne
    @JoinColumn(name = "trip_id", nullable = false)
    private Trip trip;

    Participant(String email, Trip trip){
        this.email = email;
        this.trip = trip;
        this.name = "";
        this.isConfirmed = false;
    }
}