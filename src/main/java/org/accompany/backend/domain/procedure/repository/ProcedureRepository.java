package org.accompany.backend.domain.procedure.repository;

import org.accompany.backend.domain.procedure.entity.Procedure;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProcedureRepository extends JpaRepository<Procedure, Long> {

	@Query("select distinct p from Procedure p left join fetch p.procedureDocuments")
	List<Procedure> findAllWithDocuments();

	@EntityGraph(attributePaths = {
			"procedureChannels"
	})
	Optional<Procedure> findWithDetailsByProcedureId(Long procedureId);
}


