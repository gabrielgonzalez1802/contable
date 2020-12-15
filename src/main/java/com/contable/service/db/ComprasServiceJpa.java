package com.contable.service.db;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Compra;
import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.Suplidor;
import com.contable.repository.ComprasServiceRepository;
import com.contable.service.IComprasService;

@Service
public class ComprasServiceJpa implements IComprasService{
	
	@Autowired
	private ComprasServiceRepository repo;
	
	@Override
	public Compra buscarPorId(Integer id) {
		Optional<Compra> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<Compra> buscarPorEmpresa(Empresa empresa) {
		return repo.findByEmpresa(empresa);
	}

	@Override
	public List<Compra> buscarPorEmpresaCuentaContable(Empresa empresa, CuentaContable cuentaContable) {
		return repo.findByEmpresaAndCuentaContable(empresa, cuentaContable);
	}

	@Override
	public List<Compra> buscarPorEmpresaCuentaContableSuplidor(Empresa empresa, CuentaContable cuentaContable,
			Suplidor suplidor) {
		return repo.findByEmpresaAndCuentaContableAndSuplidor(empresa, cuentaContable, suplidor);
	}

	@Override
	public List<Compra> buscarPorEmpresaFechas(Empresa empresa, Date desde, Date hasta) {
		return repo.findByEmpresaAndFechaBetween(empresa, desde, hasta);
	}

	@Override
	public List<Compra> buscarPorEmpresaCuentaContableFechas(Empresa empresa, CuentaContable cuentaContable, Date desde,
			Date hasta) {
		return repo.findByEmpresaAndCuentaContableAndFechaBetween(empresa, cuentaContable, desde, hasta);
	}

	@Override
	public void guardar(Compra compra) {
		repo.save(compra);
	}

	@Override
	public void eliminar(Compra compra) {
		repo.delete(compra);
	}

	@Override
	public List<Compra> buscarPorEmpresaTotalMayorque(Empresa empresa, double valor) {
		return repo.findByEmpresaAndBalanceGreaterThan(empresa, valor);
	}
	
}
