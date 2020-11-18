package com.contable.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Diario;

public interface DiariosRepository extends JpaRepository<Diario, Integer> {
	List<Diario> findByFechaBetween(Date desde, Date hasta);
}
