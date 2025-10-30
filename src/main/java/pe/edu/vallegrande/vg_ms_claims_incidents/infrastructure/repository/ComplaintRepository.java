package pe.edu.vallegrande.vg_ms_claims_incidents.infrastructure.repository;

import pe.edu.vallegrande.vg_ms_claims_incidents.domain.models.Complaint;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.stereotype.Repository;
import org.bson.types.ObjectId;

@Repository
public interface ComplaintRepository extends ReactiveMongoRepository<Complaint, ObjectId> {
}