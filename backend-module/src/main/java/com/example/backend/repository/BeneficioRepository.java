package com.example.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.ejb.Beneficio;

@Repository
public interface BeneficioRepository extends JpaRepository<Beneficio, Long>  {

	Optional<Beneficio> findByIdAndAtivaTrue(Long id);


	List<Beneficio> findAllByAtivaTrue();

}
