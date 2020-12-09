package com.contable.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contable.model.Compra;
import com.contable.model.CompraProductoTemp;
import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.EntradaIngresoContable;
import com.contable.model.Suplidor;
import com.contable.model.Usuario;
import com.contable.service.IComprasProductosTempService;
import com.contable.service.IComprasService;
import com.contable.service.ICuentasContablesService;
import com.contable.service.IEntradasIngresosContableService;
import com.contable.service.ISuplidoresService;

@Controller
@RequestMapping("/compras")
public class ComprasController {
	
	@Autowired
	private IComprasService serviceCompras;
	
	@Autowired
	private ISuplidoresService serviceSuplidores;
	
	@Autowired
	private ICuentasContablesService serviceCuentasContables;
	
	@Autowired
	private IEntradasIngresosContableService serviceEntradasIngresosContables;
	
	@Autowired
	private IComprasProductosTempService serviceComprasProductosTemp;
	
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	@PostMapping("/guardarCompra")
	public ResponseEntity<String> guardarCompra(Model model, HttpSession session, Double total,
			@RequestParam(required = false, name = "fecha") String fecha,
			@RequestParam(required = false, name = "suplidor") String idSuplidor,
			@RequestParam(required = false, name = "factura") String factura,
			@RequestParam(required = false, name = "comprobante") String comprobante, 
			@RequestParam(required = false, name = "subTotal", defaultValue = "0.0") double subTotal, 
			@RequestParam(required = false, name = "itbis", defaultValue = "0.0") double itbis, Integer cuentaContableId,
			Integer tipo
			) throws ParseException{
		String response = "0";
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		CuentaContable cuentaContable = serviceCuentasContables.buscarPorId(cuentaContableId);
		List<CompraProductoTemp> productosTemp = serviceComprasProductosTemp.buscarPorEmpresaUsuario(empresa, usuario);
		Compra compra = new Compra();
		compra.setEmpresa(empresa);
		compra.setFactura(factura);
		compra.setUsuario(usuario);
		compra.setCuentaContable(cuentaContable);
		compra.setFecha(formatter.parse(fecha));
		compra.setComprobante(comprobante);
		compra.setItbis(itbis);
		compra.setSubtotal(subTotal);
		if(!idSuplidor.equals("")) {
			Suplidor suplidor = serviceSuplidores.buscarPorId(Integer.parseInt(idSuplidor));
			compra.setSuplidor(suplidor);
		}
		compra.setTipo(tipo);
		compra.setTotal(total);
		serviceCompras.guardar(compra);

		if(compra.getId()!=null) {
			for (CompraProductoTemp compraProductoTemp : productosTemp) {
				EntradaIngresoContable entradaIngresoContable = new EntradaIngresoContable();
				entradaIngresoContable.setCompra(compra);
				entradaIngresoContable.setCantidad(compraProductoTemp.getCantidad());
				entradaIngresoContable.setCosto(compraProductoTemp.getCosto());
				entradaIngresoContable.setCuentaContable(compraProductoTemp.getCuentaContable());
				entradaIngresoContable.setEmpresa(empresa);
				entradaIngresoContable.setFecha(compra.getFecha());
				entradaIngresoContable.setInfo(compraProductoTemp.getInfo());
				entradaIngresoContable.setTotal(compraProductoTemp.getTotal());
				entradaIngresoContable.setUsuario(usuario);
				serviceEntradasIngresosContables.guardar(entradaIngresoContable);
			}
			serviceComprasProductosTemp.eliminar(productosTemp);
			response = "1";
		}
		
		if(response.equals("0")) {
			List<EntradaIngresoContable> tempIngresos = serviceEntradasIngresosContables.buscarPorEmpresaCompra(empresa, compra);
			serviceEntradasIngresosContables.eliminar(tempIngresos);
			serviceCompras.eliminar(compra);
		}
		return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
	}
}
