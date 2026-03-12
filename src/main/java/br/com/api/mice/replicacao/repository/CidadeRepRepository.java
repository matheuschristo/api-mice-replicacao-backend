package br.com.api.mice.replicacao.repository;

import br.com.api.mice.replicacao.entity.CidadeEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CidadeRepRepository extends JpaRepository<CidadeEntity, Long> {

    Optional<CidadeEntity> findBySourceId(Long sourceId);
}
