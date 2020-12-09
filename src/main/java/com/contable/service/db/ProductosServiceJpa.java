package com.contable.service.db;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.Producto;
import com.contable.repository.ProductosRepository;
import com.contable.service.IProductosService;

@Service
public class ProductosServiceJpa implements IProductosService{
	
	@Autowired
	private ProductosRepository repo;

	@Override
	public Producto buscarPorId(Integer id) {
		Optional<Producto> optional = repo.findById(id);
		if(optional.isPresent()) {
			return optional.get();
		}
		return null;
	}

	@Override
	public List<Producto> buscarPorEmpresa(Empresa empresa) {
		return repo.findByEmpresaOrderByNombre(empresa);
	}

	@Override
	public List<Producto> buscarPorEmpresaActivoFijo(Empresa empresa, Integer activoFijo) {
		return repo.findByEmpresaAndActivoFijo(empresa, activoFijo);
	}

	@Override
	public void guardar(Producto producto) {
		repo.save(producto);
	}

	@Override
	public void eliminar(Producto producto) {
		repo.delete(producto);
	}

	@Override
	public Page<Producto> buscarPorEmpresa(Empresa empresa, Pageable pageable) {
		return repo.findByEmpresaOrderByNombre(empresa, pageable);
	}

	@Override
	public Page<Producto> buscarPorEmpresaActivoFijo(Empresa empresa, Integer activoFijo, Pageable pageable) {
		return repo.findByEmpresaAndActivoFijoOrderByNombre(empresa, activoFijo, pageable);
	}

	@Override
	public Page<Producto> buscarPorEmpresaActivoFijoContainingNombre(Empresa empresa, Integer activoFijo,
			String nombre, Pageable pageable) {
		return repo.findByEmpresaAndActivoFijoAndNombreContainingOrderByNombre(empresa, activoFijo, nombre, pageable);
	}

	@Override
	public Page<Producto> buscarPorEmpresaContainingOrderByNombre(Empresa empresa, String nombre, Pageable pageable) {
		return repo.findByEmpresaAndNombreContaining(empresa, nombre, pageable);
	}

	@Override
	public List<Producto> buscarPorEmpresaCuentaContable(Empresa empresa, CuentaContable cuentaContable) {
		return repo.findByEmpresaAndCuentaContable(empresa, cuentaContable);
	}

	@Override
	public List<Producto> buscarPorEmpresaActivosFijos(Empresa empresa, List<Integer> activosFijos) {
		return repo.findByEmpresaAndActivoFijoIn(empresa, activosFijos);
	}

}
