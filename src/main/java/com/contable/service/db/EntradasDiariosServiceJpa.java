package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.CuentaContable;
import com.contable.model.Diario;
import com.contable.model.EntradaDiario;
import com.contable.repository.EntradasDiariosRepository;
import com.contable.service.IEntradasDiariosService;

@Service
public class EntradasDiariosServiceJpa implements IEntradasDiariosService {

	@Autowired
	private EntradasDiariosRepository repo;
	
	@Override
	public EntradaDiario buscarPorId(Integer id) {
		Optional<EntradaDiario> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<EntradaDiario> buscarPorCuentaContable(CuentaContable cuentaContable) {
		return repo.findByCuentaContable(cuentaContable);
	}

	@Override
	public List<EntradaDiario> buscarPorDiario(Diario diario) {
		return repo.findByDiario(diario);
	}

	@Override
	public List<EntradaDiario> buscarPorCuentaContableDiario(CuentaContable cuentaContable, Diario diario) {
		return repo.findByCuentaContableAndDiario(cuentaContable, diario);
	}

	@Override
	public void guardar(EntradaDiario entradaDiario) {
		repo.save(entradaDiario);
	}

	@Override
	public void eliminar(EntradaDiario entradaDiario) {
		repo.delete(entradaDiario);
	}

}
