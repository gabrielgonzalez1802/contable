package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.FormaPago;
import com.contable.repository.FormasPagosRepository;
import com.contable.service.IFormasPagosService;

@Service
public class FormasPagosServiceJpa implements IFormasPagosService {
	
	@Autowired
	private FormasPagosRepository repo;

	@Override
	public FormaPago buscarPorId(Integer id) {
		Optional<FormaPago> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<FormaPago> buscarPorEmpresa(Empresa empresa) {
		return repo.findByEmpresa(empresa);
	}

	@Override
	public List<FormaPago> buscarPorCuentaContable(CuentaContable cuentaContable) {
		return repo.findByCuentaContable(cuentaContable);
	}

	@Override
	public List<FormaPago> buscarPorEmpresaCuentaContableIdentificador(Empresa empresa, CuentaContable cuentaContable,
			String identificador) {
		return repo.findByEmpresaAndCuentaContableAndIdentificador(empresa, cuentaContable, identificador);
	}
	
	@Override
	public List<FormaPago> buscarPorEmpresaCuentaContable(Empresa empresa, CuentaContable cuentaContable) {
		return repo.findByEmpresaAndCuentaContable(empresa, cuentaContable);
	}
	
	@Override
	public List<FormaPago> buscarPorEmpresaIdentificador(Empresa empresa, String identificador) {
		return repo.findByEmpresaAndIdentificador(empresa, identificador);
	}

	@Override
	public void guardar(FormaPago formaPago) {
		repo.save(formaPago);
	}

	@Override
	public void eliminar(FormaPago formaPago) {
		repo.delete(formaPago);
	}
}
