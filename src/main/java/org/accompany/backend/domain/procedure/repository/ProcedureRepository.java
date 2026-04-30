package org.accompany.backend.domain.procedure.repository;

import org.accompany.backend.domain.procedure.entity.Procedure;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProcedureRepository extends JpaRepository<Procedure, Long> {

	@Query("select distinct p from Procedure p left join fetch p.procedureDocuments")
	List<Procedure> findAllWithDocuments();

	@Query("SELECT DISTINCT p FROM Procedure p LEFT JOIN FETCH p.procedureDocuments WHERE p.procedureId IN :ids")
	List<Procedure> findAllWithDocumentsByIds(@Param("ids") Set<Long> ids);

	@EntityGraph(attributePaths = {
			"procedureChannels"
	})
	Optional<Procedure> findWithDetailsByProcedureId(Long procedureId);
}
