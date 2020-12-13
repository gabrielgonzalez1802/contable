package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Compra;
import com.contable.model.CompraItbis;

public interface CompraItbisRepository extends JpaRepository<CompraItbis, Integer> {
	List<CompraItbis> findByCompra(Compra compra);
}
