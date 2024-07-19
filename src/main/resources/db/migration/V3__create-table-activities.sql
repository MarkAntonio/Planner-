--Sintaxe do banco H2 Hibernate
--RANDOM_UUID() função que gera um UUID
-- Cada participante pertence à uma viagem
CREATE TABLE activities(
    id UUID DEFAULT RANDOM_UUID() PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    occurs_at TIMESTAMP NOT NULL,
    trip_id UUID,
    FOREIGN KEY (trip_id) REFERENCES trips(id) ON DELETE CASCADE
    -- Ao deletar uma Trip, todas as Activities linkadas a ela serão deletadas
);
