package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.CuentaContable;
import com.contable.model.CuentaEnlace;
import com.contable.model.Empresa;
import com.contable.repository.CuentasEnlacesRepository;
import com.contable.service.ICuentasEnlacesService;

@Service
public class CuentasEnlacesServiceJpa implements ICuentasEnlacesService{
	
	@Autowired
	private CuentasEnlacesRepository repo;

	@Override
	public CuentaEnlace buscarPorId(Integer id) {
		Optional<CuentaEnlace> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<CuentaEnlace> buscarPorEmpresaCuentaContable(Empresa empresa, CuentaContable cuentaContable) {
		return repo.findByEmpresaAndCuentaContable(empresa, cuentaContable);
	}

	@Override
	public List<CuentaEnlace> buscarPorEmpresa(Empresa empresa) {
		return repo.findByEmpresa(empresa);
	}

	@Override
	public void guardar(CuentaEnlace cuentaEnlace) {
		repo.save(cuentaEnlace);
	}

	@Override
	public void eliminar(CuentaEnlace cuentaEnlace) {
		repo.delete(cuentaEnlace);
	}

	@Override
	public CuentaEnlace buscarPorEmpresaTipoSeccionReferencia(Empresa empresa, String tipo, String seccion, String referencia) {
		return repo.findByEmpresaAndTipoAndSeccionAndReferencia(empresa, tipo, seccion, referencia);
	}

}
