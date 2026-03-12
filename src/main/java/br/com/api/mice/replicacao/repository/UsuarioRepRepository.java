package br.com.api.mice.replicacao.repository;

import br.com.api.mice.replicacao.entity.UsuarioEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UsuarioRepRepository extends JpaRepository<UsuarioEntity, Long> {

    Optional<UsuarioEntity> findBySourceId(Long sourceId);
}
