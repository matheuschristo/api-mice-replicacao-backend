package br.com.api.mice.replicacao.repository;

import br.com.api.mice.replicacao.entity.EstadoEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EstadoRepRepository extends JpaRepository<EstadoEntity, Long> {

    Optional<EstadoEntity> findBySourceId(Long sourceId);
}
