package br.com.api.mice.replicacao.repository;

import br.com.api.mice.replicacao.entity.FilialEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FilialRepRepository extends JpaRepository<FilialEntity, Long> {

    Optional<FilialEntity> findBySourceId(Long sourceId);
}
