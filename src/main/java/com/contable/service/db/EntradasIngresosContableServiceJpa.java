package com.contable.service.db;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.contable.model.Compra;
import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.EntradaIngresoContable;
import com.contable.model.Usuario;
import com.contable.repository.EntradasIngresosContablesRepository;
import com.contable.service.IEntradasIngresosContableService;

@Service
public class EntradasIngresosContableServiceJpa implements IEntradasIngresosContableService{

	@Autowired
	private EntradasIngresosContablesRepository repo;
	
	@Override
	public EntradaIngresoContable buscarPorId(Integer id) {
		Optional<EntradaIngresoContable> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<EntradaIngresoContable> buscarPorEmpresa(Empresa empresa) {
		return repo.findByEmpresa(empresa);
	}

	@Override
	public List<EntradaIngresoContable> buscarPorEmpresaUsuario(Empresa empresa, Usuario usuario) {
		return repo.findByEmpresaAndUsuario(empresa, usuario);
	}

	@Override
	public List<EntradaIngresoContable> buscarPorEmpresaUsuarioCuentaContable(Empresa empresa, Usuario usuario,
			CuentaContable cuentaContable) {
		return repo.findByEmpresaAndUsuarioAndCuentaContable(empresa, usuario, cuentaContable);
	}

	@Override
	public List<EntradaIngresoContable> buscarPorEmpresaUsuarioFechas(Empresa empresa, Usuario usuario, Date desde,
			Date hasta) {
		return repo.findByEmpresaAndUsuarioAndFechaBetween(empresa, usuario, desde, hasta);
	}

	@Override
	public void guardar(EntradaIngresoContable entradaIngresoContable) {
		repo.save(entradaIngresoContable);
	}

	@Override
	public void eliminar(EntradaIngresoContable entradaIngresoContable) {
		repo.delete(entradaIngresoContable);
	}

	@Override
	public List<EntradaIngresoContable> buscarPorEmpresaCompra(Empresa empresa, Compra compra) {
		return repo.findByEmpresaAndCompra(empresa, compra);
	}

	@Override
	public void eliminar(List<EntradaIngresoContable> entradasIngresosContables) {
		repo.deleteAll(entradasIngresosContables);
	}

	@Override
	public List<EntradaIngresoContable> buscarPorEmpresaCuentaContable(Empresa empresa, CuentaContable cuentaContable) {
		return repo.findByEmpresaAndCuentaContable(empresa, cuentaContable);
	}

	@Override
	public List<EntradaIngresoContable> buscarPorEmpresaCompraCuentaContable(Empresa empresa, Compra compra,
			CuentaContable cuentaContable) {
		return repo.findByEmpresaAndCompraAndCuentaContable(empresa, compra, cuentaContable);
	}

	@Override
	public List<EntradaIngresoContable> buscarPorEmpresaCuentasContables(Empresa empresa,
			List<CuentaContable> cuentasContables) {
		return repo.findByEmpresaAndCuentaContableIn(empresa, cuentasContables);
	}

	@Override
	public List<EntradaIngresoContable> buscarPorEmpresaBalanceContableNotNull(Empresa empresa) {
		return repo.findByEmpresaAndBalanceContableIsNotNullOrderByIdDesc(empresa);
	}

	@Override
	public List<EntradaIngresoContable> buscarPorEmpresaCuentaContableBalanceContableNotNull(Empresa empresa, CuentaContable cuentaContable) {
		return repo.findByEmpresaAndCuentaContableAndBalanceContableIsNotNullOrderByIdDesc(empresa, cuentaContable);
	}

	@Override
	public List<EntradaIngresoContable> buscarPorEmpresaBalanceContableNullDESC(Empresa empresa) {
		return repo.findByEmpresaAndBalanceContableIsNullOrderByIdDesc(empresa);
	}

	@Override
	public List<EntradaIngresoContable> buscarPorEmpresaBalanceContableNullASC(Empresa empresa) {
		return repo.findByEmpresaAndBalanceContableIsNullOrderByIdAsc(empresa);
	}

	@Override
	public List<EntradaIngresoContable> buscarPorEmpresaCuentaContableBalanceContableNotNullMenorQueID(Empresa empresa,
			CuentaContable cuentaContable, Integer id) {
		return repo.findByEmpresaAndCuentaContableAndBalanceContableIsNotNullAndIdLessThanOrderByIdDesc(empresa, cuentaContable, id);
	}

	@Override
	public List<EntradaIngresoContable> buscarPorEmpresaFecha(Empresa empresa, Date fecha) {
		return repo.findByEmpresaAndFecha(empresa, fecha);
	}

	@Override
	public List<EntradaIngresoContable> buscarPorEmpresaFechaBetween(Empresa empresa, Date desde, Date hasta) {
		return repo.findByEmpresaAndFechaBetween(empresa, desde, hasta);
	}

	@Override
	public List<EntradaIngresoContable> buscarPorEmpresaFechaCurrent(Empresa empresa) {
		return repo.buscarPorEmpresaIdCurrentDate(empresa.getId());
	}

	@Override
	public List<EntradaIngresoContable> buscarPorEmpresaCuentaContableFechas(Empresa empresa,
			CuentaContable cuentaContable, Date desde, Date hasta) {
		return repo.findByEmpresaAndCuentaContableAndFechaBetween(empresa, cuentaContable, desde, hasta);
	}
}
