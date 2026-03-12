package br.com.api.mice.replicacao.repository;

import br.com.api.mice.replicacao.entity.ReplicacaoLogEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ReplicacaoLogRepository extends JpaRepository<ReplicacaoLogEntity, Long> {

    Optional<ReplicacaoLogEntity> findTopByOrderByExecutadoEmDesc();
}
