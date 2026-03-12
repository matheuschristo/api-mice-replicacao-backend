package br.com.api.mice.replicacao.repository;

import br.com.api.mice.replicacao.entity.NodeEntity;
import br.com.api.mice.replicacao.entity.enums.NodeStatus;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NodeRepository extends JpaRepository<NodeEntity, Long> {

    Optional<NodeEntity> findByNodeId(String nodeId);

    Optional<NodeEntity> findByLeaderTrue();

    List<NodeEntity> findByStatus(NodeStatus status);
}
