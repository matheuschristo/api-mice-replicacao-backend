package br.com.api.mice.replicacao.repository;

import br.com.api.mice.replicacao.entity.PaisEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaisRepRepository extends JpaRepository<PaisEntity, Long> {

    Optional<PaisEntity> findBySourceId(Long sourceId);
}
