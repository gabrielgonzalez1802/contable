package com.contable.controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
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

import com.contable.model.Carpeta;
import com.contable.model.Compra;
import com.contable.model.CompraItbis;
import com.contable.model.CompraPago;
import com.contable.model.CompraPagoTemp;
import com.contable.model.CompraProductoTemp;
import com.contable.model.CuentaContable;
import com.contable.model.Empresa;
import com.contable.model.EntradaDiario;
import com.contable.model.EntradaIngresoContable;
import com.contable.model.FormaPago;
import com.contable.model.Suplidor;
import com.contable.model.SuplidorCuentaContable;
import com.contable.model.Usuario;
import com.contable.service.ICarpetasService;
import com.contable.service.IComprasItbisService;
import com.contable.service.IComprasPagosService;
import com.contable.service.IComprasPagosTempService;
import com.contable.service.IComprasProductosTempService;
import com.contable.service.IComprasService;
import com.contable.service.ICuentasContablesService;
import com.contable.service.IEntradasDiariosService;
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
	
	@Autowired
	private ICarpetasService serviceCarpetas;
	
	@Autowired
	private IEntradasDiariosService serviceEntradasDiarios;
	
	private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	@GetMapping("/listaCompras")
	public String listaCompras(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		List<Compra> compras = serviceCompras.buscarPorEmpresaTotalMayorque(empresa, 0);
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
		
		List<FormaPago> formPagosRetencion = serviceFormasPagos.buscarPorEmpresaIdentificador(empresa, "retencion");		
		
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
		
		EntradaIngresoContable entradaIngresoContableCompra = new EntradaIngresoContable();
		
		if(compra.getId()!=null) {
			//Cuentas contables de los productos
			for (CompraProductoTemp compraProductoTemp : productosTemp) {
				EntradaIngresoContable entradaIngresoContableProducto = new EntradaIngresoContable();
				entradaIngresoContableProducto.setCompra(compra);
				entradaIngresoContableProducto.setCantidad(compraProductoTemp.getCantidad());
				entradaIngresoContableProducto.setCosto(compraProductoTemp.getCosto());
				entradaIngresoContableProducto.setCuentaContable(compraProductoTemp.getCuentaContable());
				entradaIngresoContableProducto.setEmpresa(empresa);
				entradaIngresoContableProducto.setFecha(compra.getFecha());
				entradaIngresoContableProducto.setInfo(compraProductoTemp.getInfo());
				entradaIngresoContableProducto.setTotal(compraProductoTemp.getTotal());
				entradaIngresoContableProducto.setBalance(compraProductoTemp.getTotal());
				entradaIngresoContableProducto.setUsuario(usuario);
				serviceEntradasIngresosContables.guardar(entradaIngresoContableProducto);
			}
			
			//Cuenta contable de la compra
			if(cuentaContable!=null) {
				entradaIngresoContableCompra.setCompra(compra);
				entradaIngresoContableCompra.setCuentaContable(cuentaContable);
				entradaIngresoContableCompra.setEmpresa(empresa);
				entradaIngresoContableCompra.setFecha(compra.getFecha());
				entradaIngresoContableCompra.setTotal(total);
				entradaIngresoContableCompra.setInfo("fact no "+factura+" - "+compra.getSuplidor().getNombre());
				entradaIngresoContableCompra.setBalance(total);
				entradaIngresoContableCompra.setUsuario(usuario);
				serviceEntradasIngresosContables.guardar(entradaIngresoContableCompra);
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
						EntradaIngresoContable entradaIngresoContableSuplidor = new EntradaIngresoContable();
						entradaIngresoContableSuplidor.setCompra(compra);
						entradaIngresoContableSuplidor.setCuentaContable(compraItbis.getCuentaContable());
						entradaIngresoContableSuplidor.setEmpresa(empresa);
						entradaIngresoContableSuplidor.setFecha(compra.getFecha());
						entradaIngresoContableSuplidor.setTotal(compraItbis.getMontoItbis());
						entradaIngresoContableSuplidor.setBalance(compraItbis.getMontoItbis());
						entradaIngresoContableSuplidor.setUsuario(usuario);
						
						boolean esRetencion = false;
						
						//Verificamos si la cuenta contable es de retencion
						for (FormaPago formaPago : formPagosRetencion) {
							if(formaPago.getCuentaContable().getId().intValue() == 
									entradaIngresoContableSuplidor.getCuentaContable().getId().intValue()) {
								esRetencion = true;
								break;
							}
						}
						
						if(esRetencion) {
							entradaIngresoContableSuplidor.setInfo("retencion impuesto - fact no "+factura+" - "+compra.getSuplidor().getNombre());
						}else {
							entradaIngresoContableSuplidor.setInfo("impuesto - fact no "+factura+" - "+compra.getSuplidor().getNombre());
						}
						
						serviceEntradasIngresosContables.guardar(entradaIngresoContableSuplidor);
					}
				}
			}
			serviceComprasProductosTemp.eliminar(productosTemp);
			response = "1";
		}
		
		List<CompraItbis> comprasItbis = serviceComprasItbis.buscarPorCompra(compra);
				
		for (CompraItbis compraItbis : comprasItbis) {
			for (FormaPago formaPago : formPagosRetencion) {
				//Verificamos si la compra tiene cuenta contable de retencion
				if(formaPago.getCuentaContable().getId().intValue()==compraItbis.getCuentaContable().getId().intValue()) {
					montoRetencion+=compraItbis.getMontoItbis();
				}
			}
		}
		
		//Actualizamos el balance de Entrada ingreso contable
		entradaIngresoContableCompra.setBalance(entradaIngresoContableCompra.getTotal()-montoRetencion);
		serviceEntradasIngresosContables.guardar(entradaIngresoContableCompra);
				
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
				
		//Buscamos las entradas ingresos contables null ASCENDENTE
		List<EntradaIngresoContable> entradasIngresosContablesNullTemp = serviceEntradasIngresosContables.
				buscarPorEmpresaBalanceContableNullASC(empresa);
		
		if(!entradasIngresosContablesNullTemp.isEmpty()) {
			for (EntradaIngresoContable entradaIngresoContableNull : entradasIngresosContablesNullTemp) {
				double balanceContableTemp = 0;
				//Buscamos las entradas ingresos contables anteriores con la cuenta contable de la iteracion
				List<EntradaIngresoContable> entradasIngresosContablesXCCNotNUll = serviceEntradasIngresosContables.
						buscarPorEmpresaCuentaContableBalanceContableNotNullMenorQueID(empresa, 
								entradaIngresoContableNull.getCuentaContable(), entradaIngresoContableNull.getId());
				if(!entradasIngresosContablesXCCNotNUll.isEmpty()) {
					for (EntradaIngresoContable entradaIngresoContableNotNull : entradasIngresosContablesXCCNotNUll) {
						balanceContableTemp = entradaIngresoContableNotNull.getBalanceContable()==null?0:entradaIngresoContableNotNull.getBalanceContable();
						break;
					}
					entradaIngresoContableNull.setBalanceContableInicial(balanceContableTemp);
					entradaIngresoContableNull.setBalanceContable(entradaIngresoContableNull.getBalanceContableInicial()+entradaIngresoContableNull.getBalance());
					serviceEntradasIngresosContables.guardar(entradaIngresoContableNull);
				}else {
					entradaIngresoContableNull.setBalanceContableInicial(balanceContableTemp);
					entradaIngresoContableNull.setBalanceContable(entradaIngresoContableNull.getBalanceContableInicial()+entradaIngresoContableNull.getBalance());
					serviceEntradasIngresosContables.guardar(entradaIngresoContableNull);
				}
			}
		}
		
		return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/actualizarTablaVerificarCuenta")
	public String actualizarTablaVerificarCuenta(Model model, HttpSession session) {
		//Calculo del balance de la cuenta contable
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Carpeta carpeta = null;
		
		List<FormaPago> cuentasProcesos = serviceFormasPagos.buscarPorEmpresaIdentificador(empresa, "procesos");
		
		if(session.getAttribute("carpeta") != null) {
			 carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
		}else {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, empresa);
			if(!carpetas.isEmpty()) {
				carpeta = carpetas.get(0);
			}
		}
		
		List<CuentaContable> cuentasContables = serviceCuentasContables.buscarPorEmpresaOrderByCodigoDesc(empresa);
		//String codigoPadre = "";

		for (CuentaContable cuentaContable : cuentasContables) {

			List<EntradaDiario> entradasDiarios = serviceEntradasDiarios.buscarPorCuentaContableEmpresaCarpeta(cuentaContable, empresa, carpeta);
			for (EntradaDiario entradaDiario : entradasDiarios) {
				BigDecimal monto = entradaDiario.getCredito().subtract(entradaDiario.getDebito());
				cuentaContable.setMonto(monto.doubleValue());
			}
			
			if(cuentaContable.getTipo().equals("C")) {
				List<CuentaContable> cuentasContablesTemp = serviceCuentasContables.
							buscarPorEmpresaCuentaControl(empresa, 
									cuentaContable.getCodigo());
				
				if(!cuentasContablesTemp.isEmpty()) {
					Double montoTemp = 0.0;
					for (CuentaContable cuentaContableTemp : cuentasContablesTemp) {
						List<EntradaDiario> entradasDiariosTemp = serviceEntradasDiarios.buscarPorCuentaContableEmpresaCarpeta(cuentaContableTemp, empresa, carpeta);
						for (EntradaDiario entradaDiarioTemp : entradasDiariosTemp) {
							montoTemp+=entradaDiarioTemp.getCredito().subtract(entradaDiarioTemp.getDebito()).doubleValue();
						}
						cuentaContable.setMonto(montoTemp);
					}
				}
			}
		}	
		
		for (CuentaContable cuentaContable2 : cuentasContables) {
			List<EntradaDiario> entradasDiarios = serviceEntradasDiarios.buscarPorCuentaContableEmpresaCarpeta(cuentaContable2, empresa, carpeta);
			double valorInicial = 0.0;
			for (EntradaDiario entradaDiario : entradasDiarios) {
					valorInicial += entradaDiario.getCredito().doubleValue()-entradaDiario.getDebito().doubleValue();
			}
				
			cuentaContable2.setMonto(valorInicial);
			
			//Toma el valor de los productos que coincida la cuenta contable (Compras)
			List<EntradaIngresoContable> entradasIngresosContables = serviceEntradasIngresosContables.buscarPorEmpresaCuentaContable(empresa, cuentaContable2);
			double costo = 0;
			for (EntradaIngresoContable ingresosContables : entradasIngresosContables) {
				costo += ingresosContables.getBalance();
			}
			cuentaContable2.setMonto(cuentaContable2.getMonto()+costo);
		}
			
		for (CuentaContable cuentaContableTemp : cuentasContables) {

			List<CuentaContable> cuentasTemp = serviceCuentasContables.
						buscarPorEmpresaIdCuentaControl(empresa, cuentaContableTemp.getId());
				
			double valor = 0;
							
			for (CuentaContable cuentaContableTemp2 : cuentasTemp) {
					valor += cuentaContableTemp2.getMonto();
			}
			
			double valorFinal = cuentaContableTemp.getMonto() + valor;

			cuentaContableTemp.setMonto(valorFinal);		
		}
	
		List<CuentaContable> newOrder = new LinkedList<>();
		
		for (int i = cuentasContables.size(); i > 0; i--) {
			newOrder.add(cuentasContables.get(i-1));
		}

		List<CuentaContable> cuentasContablesAuxiliaresIniciadas = serviceCuentasContables.buscarPorEmpresaTipoEstado(empresa, "A", 1);
		
		for (CuentaContable cuentaContableIni : cuentasContablesAuxiliaresIniciadas) {
			//Verificamos el balance total de la cuenta contable
			List<EntradaDiario> entradasDiarios = serviceEntradasDiarios.buscarPorCuentaContableEmpresaCarpeta(cuentaContableIni, empresa, carpeta);
			double montoTotal = 0.0;
			CuentaContable cuentaContableTemp = cuentaContableIni;
			for (EntradaDiario entradaDiario : entradasDiarios) {
				
				BigDecimal montoT = entradaDiario.getCredito().subtract(entradaDiario.getDebito());
				cuentaContableTemp.setMonto(montoT.doubleValue());
				
				if(cuentaContableTemp.getTipo().equals("C")) {
					List<CuentaContable> cuentasContablesTemp = serviceCuentasContables.
								buscarPorEmpresaCuentaControl(empresa, 
										cuentaContableTemp.getCodigo());
					
					if(!cuentasContablesTemp.isEmpty()) {
						Double montoTemp = 0.0;
						for (CuentaContable cuentaContableTemp2 : cuentasContablesTemp) {
							List<EntradaDiario> entradasDiariosTemp = serviceEntradasDiarios.buscarPorCuentaContableEmpresaCarpeta(cuentaContableTemp2, empresa, carpeta);
							for (EntradaDiario entradaDiarioTemp2 : entradasDiariosTemp) {
								montoTemp+=entradaDiarioTemp2.getCredito().subtract(entradaDiarioTemp2.getDebito()).doubleValue();
							}
							cuentaContableTemp.setMonto(montoTemp);
						}
					}
				}
				
				montoTotal+=cuentaContableTemp.getMonto();
				
			}
			
			cuentaContableIni.setNombreCuenta(cuentaContableIni.getNombreCuenta()+" -- $ "+montoTotal);
		}
		
		Double totalBalanceCuentaCompra = 0.0;
		
		for (CuentaContable cuentaContable : newOrder) {
			for (FormaPago cuentaProceso : cuentasProcesos) {
				if(cuentaContable.getId().intValue() == cuentaProceso.getCuentaContable().getId()) {
					CuentaContable cuentaContableTemp = cuentaProceso.getCuentaContable();
					cuentaContableTemp.setMonto(cuentaContable.getMonto());
					cuentaProceso.setCuentaContable(cuentaContableTemp);
					totalBalanceCuentaCompra+=cuentaContable.getMonto();
				}
			}
		}
				
		//Fin calculo del balance
		model.addAttribute("totalBalanceCuentaCompra", totalBalanceCuentaCompra);
		model.addAttribute("cuentasProcesos", cuentasProcesos);
		return "contabilidad/cuentasXPagar :: #tablaVerificarCuenta";
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
		double totalAbonoTemp = 0;
		List<CompraPagoTemp> pagosTemp = serviceComprasPagosTemp.buscarPorEmpresaUsuarioCompra(empresa, usuario, compra);
		for (CompraPagoTemp compraPagoTemp : pagosTemp) {
			CompraPago pago = new CompraPago();
			pago.setCompra(compra);
			pago.setCuentaContable(compraPagoTemp.getCuentaContable());
			pago.setEmpresa(empresa);
			pago.setFecha(compraPagoTemp.getFecha());
			pago.setMonto(compraPagoTemp.getMonto());
			totalAbonoTemp+=compraPagoTemp.getMonto();
			pago.setReferencia(compraPagoTemp.getReferencia());
			pago.setUsuario(usuario);
			serviceComprasPagos.guardar(pago);
			response=1;
			
			//Creamos el movimiento contable
			EntradaIngresoContable entradaIngresoContableBanco = new EntradaIngresoContable();
			entradaIngresoContableBanco.setFecha(compraPagoTemp.getFecha());
			entradaIngresoContableBanco.setTotal(compraPagoTemp.getMonto()*-1.0);
			entradaIngresoContableBanco.setBalance(compraPagoTemp.getMonto()*-1.0);
			entradaIngresoContableBanco.setCuentaContable(compraPagoTemp.getCuentaContable());
			entradaIngresoContableBanco.setUsuario(usuario);
			entradaIngresoContableBanco.setEmpresa(empresa);
			entradaIngresoContableBanco.setCompra(compra);
			serviceEntradasIngresosContables.guardar(entradaIngresoContableBanco);
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
		
		//Actualizamos el balance de la entrada ingreso contable
		List<EntradaIngresoContable> entradasIngresoContable = serviceEntradasIngresosContables.
				buscarPorEmpresaCompraCuentaContable(empresa, compra, compra.getCuentaContable());
		
		for (EntradaIngresoContable entradaIngresoContable : entradasIngresoContable) {
			entradaIngresoContable.setBalance(entradaIngresoContable.getBalance().doubleValue()-totalAbonoTemp);
			serviceEntradasIngresosContables.guardar(entradaIngresoContable);
		}
		
		//Actualizacion contable
		// Buscamos las entradas ingresos contables null ASCENDENTE
		List<EntradaIngresoContable> entradasIngresosContablesNullTemp = serviceEntradasIngresosContables
				.buscarPorEmpresaBalanceContableNullASC(empresa);

		if (!entradasIngresosContablesNullTemp.isEmpty()) {
			for (EntradaIngresoContable entradaIngresoContableNull : entradasIngresosContablesNullTemp) {
				double balanceContableTemp = 0;
				// Buscamos las entradas ingresos contables anteriores con la cuenta contable de
				// la iteracion
				List<EntradaIngresoContable> entradasIngresosContablesXCCNotNUll = serviceEntradasIngresosContables
						.buscarPorEmpresaCuentaContableBalanceContableNotNullMenorQueID(empresa,
								entradaIngresoContableNull.getCuentaContable(), entradaIngresoContableNull.getId());
				if (!entradasIngresosContablesXCCNotNUll.isEmpty()) {
					for (EntradaIngresoContable entradaIngresoContableNotNull : entradasIngresosContablesXCCNotNUll) {
						balanceContableTemp = entradaIngresoContableNotNull.getBalanceContable() == null ? 0
								: entradaIngresoContableNotNull.getBalanceContable();
						break;
					}
					entradaIngresoContableNull.setBalanceContableInicial(balanceContableTemp);
					entradaIngresoContableNull.setBalanceContable(entradaIngresoContableNull.getBalanceContableInicial()
							+ entradaIngresoContableNull.getBalance());
					serviceEntradasIngresosContables.guardar(entradaIngresoContableNull);
				} else {
					entradaIngresoContableNull.setBalanceContableInicial(balanceContableTemp);
					entradaIngresoContableNull.setBalanceContable(entradaIngresoContableNull.getBalanceContableInicial()
							+ entradaIngresoContableNull.getBalance());
					serviceEntradasIngresosContables.guardar(entradaIngresoContableNull);
				}
			}
		}
				
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
