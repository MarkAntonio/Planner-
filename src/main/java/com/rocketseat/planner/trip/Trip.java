package com.rocketseat.planner.trip;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

// Mostra pro JPA que essa classe é uma entidade (tabela)
@Entity
// Uso para mapear o nome da tabela, quando o nome é diferente do nome da classe (trips e trip)
@Table(name = "trips")
//Lombok usado para gerar código e diminuir a repetição
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Trip {
    @Id
    @GeneratedValue(strategy= GenerationType.AUTO)
    private UUID id;

    @Column(nullable = false)
    // como o nome da coluna é o mesmo do atributo, então não preciso passá-lo
    private String destination;

    @Column(name="starts_at", nullable = false)
    private LocalDateTime startsAt;

    @Column(name="ends_at", nullable = false)
    private LocalDateTime endsAt;

    @Column(name="is_confirmed", nullable = false)
    private boolean isConfirmed;

    @Column(name="owner_name", nullable = false)
    private String ownerName;

    @Column(name="owner_email", nullable = false)
    private String ownerEmail;

    public Trip(TripRequestPayload payload){
        this.destination = payload.destination();
        this.isConfirmed = false; // padrão é não estar confirmado. Só confirmará quando a pessoa aceitar no email
        this.ownerEmail = payload.owner_email();
        this.ownerName = payload.owner_name();
        // Vou receber minha data no formato ISO, então terei de transformar
        this.startsAt = LocalDateTime.parse(payload.starts_at(), DateTimeFormatter.ISO_DATE_TIME);
        this.endsAt = LocalDateTime.parse(payload.ends_at(), DateTimeFormatter.ISO_DATE_TIME);

        // o id não é atribuído pois é gerado quando chegar na base;
    }
}