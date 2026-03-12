package br.com.api.mice.replicacao.repository;

import br.com.api.mice.replicacao.entity.EmpresaEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EmpresaRepRepository extends JpaRepository<EmpresaEntity, Long> {

    Optional<EmpresaEntity> findBySourceId(Long sourceId);
}
