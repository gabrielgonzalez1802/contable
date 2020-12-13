package com.contable.controller;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.contable.model.CompraProductoTemp;
import com.contable.model.Empresa;
import com.contable.model.Suplidor;
import com.contable.model.SuplidorCuentaContable;
import com.contable.model.SuplidorCuentaContableTemp;
import com.contable.model.Usuario;
import com.contable.service.IComprasProductosTempService;
import com.contable.service.ISuplidoresCuentasContablesService;
import com.contable.service.ISuplidoresCuentasContablesTempService;
import com.contable.service.ISuplidoresService;

@Controller
@RequestMapping("/suplidores")
public class SuplidoresController {
	
	@Autowired
	private ISuplidoresService serviceSuplidores;
	
	@Autowired
	private ISuplidoresCuentasContablesTempService serviceSuplidoresCuentasContablesTemp;
	
	@Autowired
	private ISuplidoresCuentasContablesService serviceSuplidoresCuentasContables;
	
	@Autowired
	private IComprasProductosTempService serviceComprasProductosTemp;

	@GetMapping("/listaSuplidores")
	public String listaSuplidores(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		List<Suplidor> suplidores = serviceSuplidores.buscarPorEmpresa(empresa);
		model.addAttribute("suplidores", suplidores);
		return "suplidores/listaSuplidores :: suplidores";
	}
	
	@GetMapping("/borrarSuplidoresTemp")
	public String borrarSuplidoresTemp(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<SuplidorCuentaContableTemp> cuentasTempSuplidoresTemp =  serviceSuplidoresCuentasContablesTemp.buscarPorEmpresaUsuario(empresa, usuario);
		serviceSuplidoresCuentasContablesTemp.eliminar(cuentasTempSuplidoresTemp);
		model.addAttribute("cuentasTempSuplidores", cuentasTempSuplidoresTemp);
		return "contabilidad/compras :: #tablaCuentasTempSuplidor";
	}
	
	@GetMapping("/cuentasDelSuplidor/{id}")
	public String cuentasDelSuplidor(Model model, HttpSession session, @PathVariable(name = "id") Integer id) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Suplidor suplidor = serviceSuplidores.buscarPorId(id);
		List<SuplidorCuentaContable> suplidoresCuentasContables = serviceSuplidoresCuentasContables.
				buscarPorEmpresaSuplidor(empresa, suplidor);
		double totalProductoTemp = 0;
		List<CompraProductoTemp> productosTemp = serviceComprasProductosTemp.buscarPorEmpresaUsuario(empresa, usuario);
		for (CompraProductoTemp compraProducto : productosTemp) {
			if(compraProducto.getProducto().getExento().intValue()==0) {
				totalProductoTemp+=formato2d(compraProducto.getCantidad()*compraProducto.getCosto());
			}
		}
		for (SuplidorCuentaContable suplidorCuentaContable : suplidoresCuentasContables) {
			suplidorCuentaContable.setValorPorcentajeItbis(formato2d(totalProductoTemp * (suplidorCuentaContable.getImpuesto()/100.00)));
		}
		model.addAttribute("cuentasDelSuplidor", suplidoresCuentasContables);
		return "contabilidad/compras :: #cuentasDelSuplidor"; 
	}
	
	@PostMapping("/agregarSuplidorForCompra")
	public String agregarSuplidorForCompra(Model model, HttpSession session, 
			String nombre, String telefono, String direccion, String rnc) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Suplidor suplidor = new Suplidor();
		suplidor.setDireccion(direccion);
		suplidor.setEmpresa(empresa);
		suplidor.setFecha(new Date());
		suplidor.setNombre(nombre);
		suplidor.setRnc(rnc);
		suplidor.setTelefono(telefono);
		suplidor.setUsuario(usuario);
		serviceSuplidores.guardar(suplidor);
		
		List<SuplidorCuentaContableTemp> SuplidoresCuentasContablesTemp = serviceSuplidoresCuentasContablesTemp.
				buscarPorEmpresaUsuario(empresa, usuario);
		
		if(!SuplidoresCuentasContablesTemp.isEmpty()) {
			for (SuplidorCuentaContableTemp suplidorCuentaContableTemp : SuplidoresCuentasContablesTemp) {
				SuplidorCuentaContable suplidorCuentaContable = new SuplidorCuentaContable();
				suplidorCuentaContable.setCuentaContable(suplidorCuentaContableTemp.getCuentaContable());
				suplidorCuentaContable.setEmpresa(empresa);
				suplidorCuentaContable.setImpuesto(suplidorCuentaContableTemp.getImpuesto());
				suplidorCuentaContable.setUsuario(usuario);
				suplidorCuentaContable.setSuplidor(suplidor);
				serviceSuplidoresCuentasContables.guardar(suplidorCuentaContable);
			}
			
			serviceSuplidoresCuentasContablesTemp.eliminar(SuplidoresCuentasContablesTemp);
		}
		
		List<Suplidor> suplidores = serviceSuplidores.buscarPorEmpresa(empresa);
		model.addAttribute("suplidores", suplidores);
		return "contabilidad/compras :: #selectSuplidorForProduct";
	}
	
	public double formato2d(double number) {
		number = Math.round(number * 100);
		number = number/100;
		return number;
	}
}
