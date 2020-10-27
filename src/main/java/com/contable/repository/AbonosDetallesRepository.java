package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Abono;
import com.contable.model.AbonoDetalle;

public interface AbonosDetallesRepository extends JpaRepository<AbonoDetalle, Integer> {
	List<AbonoDetalle> findByAbono(Abono abono);
}
