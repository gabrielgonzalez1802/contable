package com.contable.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.contable.model.Empresa;
import com.contable.model.Inversionista;
import com.contable.model.PrestamoEntidad;

public interface PrestamosEntidadesRepository extends JpaRepository<PrestamoEntidad, Integer> {
	List<PrestamoEntidad> findByEmpresa(Empresa empresa);
	List<PrestamoEntidad> findByEmpresaAndInversionista(Empresa empresa, Inversionista inversionista);
}
