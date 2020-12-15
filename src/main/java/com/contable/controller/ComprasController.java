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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.contable.model.Compra;
import com.contable.model.CompraItbis;
import com.contable.model.CompraPago;
import com.contable.model.CompraPagoTemp;
import com.contable.model.CompraProductoTemp;
import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.EntradaIngresoContable;
import com.contable.model.FormaPago;
import com.contable.model.Suplidor;
import com.contable.model.SuplidorCuentaContable;
import com.contable.model.Usuario;
import com.contable.service.IComprasItbisService;
import com.contable.service.IComprasPagosService;
import com.contable.service.IComprasPagosTempService;
import com.contable.service.IComprasProductosTempService;
import com.contable.service.IComprasService;
import com.contable.service.ICuentasContablesService;
import com.contable.service.IEntradasIngresosContableService;
import com.contable.service.IFormasPagosService;
import com.contable.service.ISuplidoresCuentasContablesService;
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
	
	@Autowired
	private ISuplidoresCuentasContablesService serviceSuplidoresCuentasContables;
	
	@Autowired
	private IComprasItbisService serviceComprasItbis;
	
	@Autowired
	private IFormasPagosService serviceFormasPagos;
	
	@Autowired
	private IComprasPagosTempService serviceComprasPagosTemp;
	
	@Autowired
	private IComprasPagosService serviceComprasPagos;
	
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	@GetMapping("/listaCompras")
	public String listaCompras(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		List<Compra> compras = serviceCompras.buscarPorEmpresa(empresa);
		double totalBalance = 0;
		model.addAttribute("compras", compras);
		for (Compra compra : compras) {
			totalBalance+=compra.getBalance();
		}
		model.addAttribute("totalBalance", totalBalance);
		return "contabilidad/cuentasXPagar :: #tablaCompras";
	}
	
	@PostMapping("/guardarCompra")
	public ResponseEntity<String> guardarCompra(Model model, HttpSession session, Double total,
			@RequestParam(required = false, name = "fecha") String fecha,
			@RequestParam(required = false, name = "suplidor") String idSuplidor,
			@RequestParam(required = false, name = "factura") String factura,
			@RequestParam(required = false, name = "comprobante") String comprobante, 
			@RequestParam(required = false, name = "subTotal", defaultValue = "0.0") double subTotal, 
			Integer cuentaContableId, Integer tipo, String impuestos, String valorImpuestos, String idCuentaContable
			) throws ParseException{
		String response = "0";
		double montoRetencion = 0;
		double montoAbono = 0;
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
		compra.setSubtotal(subTotal);
		if(!idSuplidor.equals("")) {
			Suplidor suplidor = serviceSuplidores.buscarPorId(Integer.parseInt(idSuplidor));
			compra.setSuplidor(suplidor);
		}
		compra.setTipo(tipo);
		compra.setTotal(total);
		compra.setBalance(total);
		serviceCompras.guardar(compra);

		if(compra.getId()!=null) {
			//Cuentas contables de los productos
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
			
			//Cuenta contable de la compra
			if(cuentaContable!=null) {
				EntradaIngresoContable entradaIngresoContable2 = new EntradaIngresoContable();
				entradaIngresoContable2.setCompra(compra);
				entradaIngresoContable2.setCuentaContable(cuentaContable);
				entradaIngresoContable2.setEmpresa(empresa);
				entradaIngresoContable2.setFecha(compra.getFecha());
				entradaIngresoContable2.setTotal(total);
				entradaIngresoContable2.setUsuario(usuario);
				serviceEntradasIngresosContables.guardar(entradaIngresoContable2);
			}

			if(!impuestos.equals("")) {
				String[] impuestosArray = impuestos.split(",");
				valorImpuestos = valorImpuestos.replace("%", "");
				String[] valorImpuestosArray = valorImpuestos.split(",");
				String[] idCuentaContableArray = idCuentaContable.split(",");
						
				//Guardamos los impuestos
				for (int i = 0; i < valorImpuestosArray.length; i++) {
					CompraItbis compraItbis = new CompraItbis();
					compraItbis.setCompra(compra);
					compraItbis.setItbis(Integer.parseInt(valorImpuestosArray[i]));
					for (int j = 0; j < impuestosArray.length; j++) {
						if(j==i) {
							compraItbis.setMontoItbis(Double.parseDouble(impuestosArray[j]));
						}
					}
					for (int k = 0; k < idCuentaContableArray.length; k++) {
						if(k==i) {
							CuentaContable cuentaContableTemp = serviceCuentasContables.buscarPorId(Integer.parseInt(idCuentaContableArray[k]));
							compraItbis.setCuentaContable(cuentaContableTemp);
						}
					}
					serviceComprasItbis.guardar(compraItbis);
					
					//Cuenta contable del suplidor
					if(compraItbis.getMontoItbis().doubleValue() > 0) {
						EntradaIngresoContable entradaIngresoContable3 = new EntradaIngresoContable();
						entradaIngresoContable3.setCompra(compra);
						entradaIngresoContable3.setCuentaContable(compraItbis.getCuentaContable());
						entradaIngresoContable3.setEmpresa(empresa);
						entradaIngresoContable3.setFecha(compra.getFecha());
						entradaIngresoContable3.setTotal(compraItbis.getMontoItbis());
						entradaIngresoContable3.setUsuario(usuario);
						serviceEntradasIngresosContables.guardar(entradaIngresoContable3);
					}
				}
			}
			serviceComprasProductosTemp.eliminar(productosTemp);
			response = "1";
		}
		
		List<FormaPago> formPagosRetencion = serviceFormasPagos.buscarPorEmpresaIdentificador(empresa, "retencion");		
		List<CompraItbis> comprasItbis = serviceComprasItbis.buscarPorCompra(compra);
		
		for (CompraItbis compraItbis : comprasItbis) {
			for (FormaPago formaPago : formPagosRetencion) {
				//Verificamos si la compra tiene cuenta contable de retencion
				if(formaPago.getCuentaContable().getId().intValue()==compraItbis.getCuentaContable().getId().intValue()) {
					montoRetencion+=compraItbis.getMontoItbis();
				}
			}
		}
		
		List<CompraPago> abonos = serviceComprasPagos.buscarPorEmpresaCompra(empresa, compra);
		for (CompraPago compraPago : abonos) {
			montoAbono+=compraPago.getMonto();
		}
		
		compra.setAbono(montoAbono);
		compra.setRetencion(montoRetencion);
		compra.setBalance(compra.getTotal().doubleValue()-montoRetencion-montoAbono);
		serviceCompras.guardar(compra);
		
		if(response.equals("0")) {
			List<EntradaIngresoContable> tempIngresos = serviceEntradasIngresosContables.buscarPorEmpresaCompra(empresa, compra);
			serviceEntradasIngresosContables.eliminar(tempIngresos);
			serviceCompras.eliminar(compra);
		}
		return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/totalesCompras/{id}")
	public ResponseEntity<String[]> totalesCompras(Model model, HttpSession session, @PathVariable(name = "id") Integer id) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		double totalProductoTemp = 0;
		double totalImpuestos = 0;
		double totalExento = 0;
		
		Suplidor suplidor = serviceSuplidores.buscarPorId(id);
		
		List<SuplidorCuentaContable> suplidoresCuentasContables = serviceSuplidoresCuentasContables.
				buscarPorEmpresaSuplidor(empresa, suplidor);
		
		List<CompraProductoTemp> productosTemp = serviceComprasProductosTemp.buscarPorEmpresaUsuario(empresa, usuario);

		for (CompraProductoTemp compraProducto : productosTemp) {
			totalProductoTemp+=formato2d(compraProducto.getCantidad()*compraProducto.getCosto());
		}
		
		for (SuplidorCuentaContable suplidorCuentaContable : suplidoresCuentasContables) {
			for (CompraProductoTemp compraProducto : productosTemp) {
				//Si el producto no es exento suma el impuesto
				if(compraProducto.getProducto().getExento().intValue()==0) {
					totalExento=formato2d(compraProducto.getCantidad()*compraProducto.getCosto());
					totalImpuestos+=formato2d(totalExento * (suplidorCuentaContable.getImpuesto()/100.00));
				}
			}
		}
		
		totalProductoTemp = formato2d(totalProductoTemp);
		
		String subTotalString = String.valueOf(totalProductoTemp);
		String totalString = String.valueOf(formato2d(totalProductoTemp+totalImpuestos));
		
		String response[] = {subTotalString,totalString};
		
		return new ResponseEntity<String[]>(response, HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/pagosTemporales/{id}")
	public String pagosTemporales(@PathVariable(name = "id") Integer idCompra, HttpSession session, Model model) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Compra compra = serviceCompras.buscarPorId(idCompra);
		double totalPagoTempCompra = 0;
		List<CompraPagoTemp> pagosTemp = serviceComprasPagosTemp.
				buscarPorEmpresaUsuarioCompra(empresa, usuario, compra);
		for (CompraPagoTemp compraPagoTemp : pagosTemp) {
			totalPagoTempCompra+=compraPagoTemp.getMonto();
		}
		model.addAttribute("pagosTemp", pagosTemp);
		model.addAttribute("totalPagoTempCompra", totalPagoTempCompra);
		return "contabilidad/cuentasXPagar :: #tablaPagosCompraTemp";
	}
	
	@PostMapping("/guardarPagoTemp")
	public ResponseEntity<Integer> guardarPagoTemp(Integer id, HttpSession session,
			String fecha, Double monto, Integer cuentaContableId, String referencia) throws ParseException {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Compra compra = serviceCompras.buscarPorId(id);
		CuentaContable cuentaContable = serviceCuentasContables.buscarPorId(cuentaContableId);
		Integer response = 0;
		CompraPagoTemp pagoTemp = new CompraPagoTemp();
		pagoTemp.setCompra(compra);
		pagoTemp.setCuentaContable(cuentaContable);
		pagoTemp.setFecha(formatter.parse(fecha));
		pagoTemp.setEmpresa(empresa);
		pagoTemp.setMonto(monto);
		pagoTemp.setReferencia(referencia);
		pagoTemp.setUsuario(usuario);
		serviceComprasPagosTemp.guardar(pagoTemp);
		
		if(pagoTemp.getId()!=null) {
			response=1;
		}
		return new ResponseEntity<Integer>(response, HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/guardarPagos")
	public ResponseEntity<Integer> guardarPagos(Integer id, HttpSession session) {
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Compra compra = serviceCompras.buscarPorId(id);
		Integer response = 0;
		List<CompraPagoTemp> pagosTemp = serviceComprasPagosTemp.buscarPorEmpresaUsuarioCompra(empresa, usuario, compra);
		for (CompraPagoTemp compraPagoTemp : pagosTemp) {
			CompraPago pago = new CompraPago();
			pago.setCompra(compra);
			pago.setCuentaContable(compraPagoTemp.getCuentaContable());
			pago.setEmpresa(empresa);
			pago.setFecha(compraPagoTemp.getFecha());
			pago.setMonto(compraPagoTemp.getMonto());
			pago.setReferencia(compraPagoTemp.getReferencia());
			pago.setUsuario(usuario);
			serviceComprasPagos.guardar(pago);
			response=1;
		}
		List<CompraPago> comprasPagos = serviceComprasPagos.buscarPorEmpresaUsuarioCompra(empresa, usuario, compra);
		double totalAbono = 0;
		for (CompraPago compraPago : comprasPagos) {
			totalAbono += compraPago.getMonto();
			Compra compraTemp = compraPago.getCompra();
			compraTemp.setAbono(totalAbono);
			compraTemp.setBalance(compraTemp.getTotal().doubleValue()-compraTemp.getRetencion().doubleValue()-compraTemp.getAbono().doubleValue());
			serviceCompras.guardar(compraTemp);
		}
		serviceComprasPagosTemp.eliminar(pagosTemp);
		return new ResponseEntity<Integer>(response, HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/reportePagosCompra")
	public String reportePagosCompra(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		List<CompraPago> comprasPagos = serviceComprasPagos.buscarPorEmpresa(empresa);
		model.addAttribute("comprasPagos", comprasPagos);
		return "contabilidad/listaComprasPagos :: listaCompraPagos";
	}
	
	@GetMapping("/miniInfoCompra/{id}")
	public String miniInfoCompra(@PathVariable(name = "id") Integer id, Model model) {
		Compra compra = serviceCompras.buscarPorId(id);
		model.addAttribute("compra", compra);
		return "contabilidad/cuentasXPagar :: #miniInfoCompra";
	}
	
	@GetMapping("/eliminarPagoTemp/{id}")
	public ResponseEntity<Integer> eliminarPagoTemp(HttpSession session, @PathVariable("id") Integer id) throws ParseException {
		Integer response = 0;
		CompraPagoTemp pagoTemp = serviceComprasPagosTemp.buscarPorId(id);
		serviceComprasPagosTemp.eliminar(pagoTemp);
		CompraPagoTemp pagoTemp2 = serviceComprasPagosTemp.buscarPorId(id);
		if(pagoTemp2==null) {
			response=1;
		}
		return new ResponseEntity<Integer>(response, HttpStatus.ACCEPTED);
	}
	
	public double formato2d(double number) {
		number = Math.round(number * 100);
		number = number/100;
		return number;
	}
}
