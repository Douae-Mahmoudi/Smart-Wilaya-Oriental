package com.wilaya.ressource_service.repository;

import com.wilaya.ressource_service.model.Materiel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface MaterielRepository extends JpaRepository<Materiel, UUID> {

    List<Materiel> findByIdEquipeAssociee(UUID idEquipe);
}