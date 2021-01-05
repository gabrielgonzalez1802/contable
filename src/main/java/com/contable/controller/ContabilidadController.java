package com.contable.controller;

import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
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

import com.contable.model.Amortizacion;
import com.contable.model.Carpeta;
import com.contable.model.Cliente;
import com.contable.model.Compra;
import com.contable.model.CompraProductoTemp;
import com.contable.model.CuentaContable;
import com.contable.model.CuentaEnlace;
import com.contable.model.Empresa;
import com.contable.model.EntradaDiario;
import com.contable.model.EntradaDiarioTemp;
import com.contable.model.EntradaIngresoContable;
import com.contable.model.FormaPago;
import com.contable.model.Inversionista;
import com.contable.model.Prestamo;
import com.contable.model.PrestamoAdicional;
import com.contable.model.PrestamoCalculoTotal;
import com.contable.model.PrestamoDetalle;
import com.contable.model.PrestamoInteresDetalle;
import com.contable.model.ProcesoBancarioTemp;
import com.contable.model.Producto;
import com.contable.model.Suplidor;
import com.contable.model.SuplidorCuentaContableTemp;
import com.contable.model.Usuario;
import com.contable.service.ICarpetasService;
import com.contable.service.IClientesService;
import com.contable.service.IComprasProductosTempService;
import com.contable.service.IComprasService;
import com.contable.service.ICuentasContablesService;
import com.contable.service.ICuentasEnlacesService;
import com.contable.service.IEntradasDiariosService;
import com.contable.service.IEntradasDiariosTempService;
import com.contable.service.IEntradasIngresosContableService;
import com.contable.service.IFormasPagosService;
import com.contable.service.IInversionistasService;
import com.contable.service.IPagosService;
import com.contable.service.IPrestamosAdicionalesService;
import com.contable.service.IPrestamosDetallesService;
import com.contable.service.IPrestamosInteresesDetallesService;
import com.contable.service.IPrestamosService;
import com.contable.service.IProcesosBancariosTempService;
import com.contable.service.IProductosService;
import com.contable.service.ISuplidoresCuentasContablesTempService;
import com.contable.service.ISuplidoresService;

@Controller
@RequestMapping("/contabilidad")
public class ContabilidadController {
	
	@Autowired
	private ICuentasContablesService serviceCuentasContables;
	
	@Autowired
	private IEntradasDiariosService serviceEntradasDiarios;
	
	@Autowired
	private ICarpetasService serviceCarpetas;
	
	@Autowired
	private ICuentasEnlacesService serviceCuentasEnlaces;
	
	@Autowired
	private IEntradasDiariosTempService serviceEntradasDiariosTemp;
	
	@Autowired
	private IProductosService serviceProductos;
	
	@Autowired
	private IComprasProductosTempService serviceComprasProductosTemp;
	
	@Autowired
	private ISuplidoresService serviceSuplidores;
	
	@Autowired
	private IComprasService serviceCompras;
	
	@Autowired
	private IFormasPagosService serviceFormasPagos;
	
	@Autowired
	private IPagosService servicePagos;
	
	@Autowired
	private IEntradasIngresosContableService serviceEntradasIngresosContables;
	
	@Autowired
	private ISuplidoresCuentasContablesTempService serviceSuplidoresCuentasContablesTemp;
	
	@Autowired
	private IClientesService serviceClientes;
	
	@Autowired
	private IInversionistasService serviceInversionistas;
	
	@Autowired
	private IProcesosBancariosTempService serviceProcesosBancariosTemp;
	
	@Autowired
	private IPrestamosService servicePrestamos;
	
	@Autowired
	private IPrestamosInteresesDetallesService servicePrestamosInteresesDetalles;
	
	@Autowired
	private IPrestamosDetallesService servicePrestamosDetalles; 
	
	@Autowired
	private IPrestamosAdicionalesService servicePrestamosAdicionales;
	
    private SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
	
	@GetMapping("/mostrarContabilidad")
	public String mostrarContabilidad(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Carpeta carpeta = null;
		
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
//			if(cuentaContable.getCuentaControl()==null && cuentaContable.getTipo().equals("C")) {
//				cuentaContable.setClase("padrecc ccpadre-"+cuentaContable.getCodigo()+" "+"cccode"+cuentaContable.getCodigo());
//				codigoPadre = cuentaContable.getCodigo();
//			}else if(cuentaContable.getCuentaControl()!=null && cuentaContable.getTipo().equals("C")) {
//				cuentaContable.setClase("hijocc cchijo-"+cuentaContable.getCuentaControl()+" "+"cccode"+cuentaContable.getCodigo()+" "+"herencia-"+codigoPadre);
//			}else if(cuentaContable.getCuentaControl()!=null && cuentaContable.getTipo().equals("A")) {
//				cuentaContable.setClase("nietocc ccnieto-"+cuentaContable.getCuentaControl()+" "+"cccode"+cuentaContable.getCodigo()+" "+"herencia-"+codigoPadre);
//			}
			
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

		List<CuentaContable> cuentasContablesAuxiliaresGeneral = serviceCuentasContables.buscarPorEmpresaTipo(empresa, "A");
		List<CuentaContable> cuentasContablesAuxiliaresIniciadas = serviceCuentasContables.buscarPorEmpresaTipoEstado(empresa, "A", 1);
		model.addAttribute("carpeta", carpeta);
		model.addAttribute("empresa", empresa);
		model.addAttribute("cuentasContablesAuxiliaresGeneral", cuentasContablesAuxiliaresGeneral);
		
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
		
		model.addAttribute("cuentasContablesAuxiliaresIniciadas", cuentasContablesAuxiliaresIniciadas);
		model.addAttribute("cuentasContables", newOrder);
		model.addAttribute("cuentaContable", new CuentaContable());
		//Cuenta de enlace	
		CuentaEnlace capitalPrestamoEnlace = serviceCuentasEnlaces.buscarPorEmpresaTipoSeccionReferencia(empresa, "credito", "capital", "prestamos");
		if(capitalPrestamoEnlace == null) {
			capitalPrestamoEnlace = new CuentaEnlace();
			capitalPrestamoEnlace.setCuentaContable(new CuentaContable());
		}
		model.addAttribute("capitalPrestamoEnlace", capitalPrestamoEnlace.getCuentaContable());
		CuentaEnlace interesEnlace = serviceCuentasEnlaces.buscarPorEmpresaTipoSeccionReferencia(empresa, "credito", "interes", "prestamos");
		if(interesEnlace == null) {
			interesEnlace = new CuentaEnlace();
			interesEnlace.setCuentaContable(new CuentaContable());
		}
		model.addAttribute("interesPrestamoEnlace", interesEnlace.getCuentaContable());
		CuentaEnlace gastosCierreEnlace = serviceCuentasEnlaces.buscarPorEmpresaTipoSeccionReferencia(empresa, "credito", "gastosCierre", "prestamos");
		if(gastosCierreEnlace == null) {
			gastosCierreEnlace = new CuentaEnlace();
			gastosCierreEnlace.setCuentaContable(new CuentaContable());
		}
		model.addAttribute("gastosCierrePrestamoEnlace", gastosCierreEnlace.getCuentaContable());
		CuentaEnlace cuentaEnlaceAdicionales = serviceCuentasEnlaces.buscarPorEmpresaTipoSeccionReferencia(empresa, "credito", "adicionales", "prestamos");
		if(cuentaEnlaceAdicionales == null) {
			cuentaEnlaceAdicionales = new CuentaEnlace();
			cuentaEnlaceAdicionales.setCuentaContable(new CuentaContable());
		}
		model.addAttribute("cobrosAdicionalesPrestamoEnlace", cuentaEnlaceAdicionales.getCuentaContable());

		Producto producto = new Producto();
		producto.setEmpresa(empresa);
		model.addAttribute("producto", producto);
		return "contabilidad/contabilidad :: contabilidad";
	}
	
	@GetMapping("/mostrarHerencia/{id}/{codigoPadre}")
	public String mostrarHerencia(Model model, HttpSession session, @PathVariable("id") Integer id, @PathVariable("codigoPadre") String codigoPadre) {
		CuentaContable cuentaContable = serviceCuentasContables.buscarPorId(id);
		if(cuentaContable.getCuentaControl()==null && cuentaContable.getTipo().equals("C")) {
			cuentaContable.setClase("padrecc ccpadre-"+cuentaContable.getCodigo()+" "+"cccode"+cuentaContable.getCodigo());
		}else if(cuentaContable.getCuentaControl()!=null && cuentaContable.getTipo().equals("C")) {
			cuentaContable.setClase("hijocc cchijo-"+cuentaContable.getCuentaControl()+" "+"cccode"+cuentaContable.getCodigo()+" "+"herencia-"+codigoPadre);
		}else if(cuentaContable.getCuentaControl()!=null && cuentaContable.getTipo().equals("A")) {
			cuentaContable.setClase("nietocc ccnieto-"+cuentaContable.getCuentaControl()+" "+"cccode"+cuentaContable.getCodigo()+" "+"herencia-"+codigoPadre);
		}
		
		String[] splitClass = cuentaContable.getClase().split(" ");
		if(splitClass.length == 4) {
			
		}else {
			
		}
		
		
		model.addAttribute("cuentaContable", new CuentaContable());
		return "contabilidad/contabilidad :: contabilidad";
	}
	
	@PostMapping("/guardarTempEntradaDiario")
	String guardarRegistrosTemporalesEntradaDiario(Model model, HttpSession session,
			BigDecimal monto, String referencia, Integer cuentaContableId, Integer tipo) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		
		Carpeta carpeta = null;
		
		if(session.getAttribute("carpeta") != null) {
			 carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
		}else {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, empresa);
			if(!carpetas.isEmpty()) {
				carpeta = carpetas.get(0);
			}
		}
		
		CuentaContable cuentaContable = serviceCuentasContables.buscarPorId(cuentaContableId);
		
		BigDecimal valor = new BigDecimal(0.0);
		
		List<EntradaDiarioTemp> entradasTempInit = serviceEntradasDiariosTemp.buscarPorEmpresaUsuario(empresa, usuario);
		
		if(!entradasTempInit.isEmpty()) {
			valor = entradasTempInit.get(entradasTempInit.size()-1).getBalanceFinal();
		}
		
		//Verificamos el balance inicial de la cuenta contable
		List<EntradaDiario> entradasDiarios = serviceEntradasDiarios.buscarPorCuentaContableEmpresaCarpeta(cuentaContable, empresa, carpeta);
		double montoTotal = 0.0;
		for (EntradaDiario entradaDiario : entradasDiarios) {
			
			BigDecimal montoT = entradaDiario.getCredito().subtract(entradaDiario.getDebito());
			cuentaContable.setMonto(montoT.doubleValue());
			
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
			
			montoTotal+=cuentaContable.getMonto();
			
		}
		
		EntradaDiarioTemp entradaDiarioTemp = new EntradaDiarioTemp();
		entradaDiarioTemp.setCuentaContable(cuentaContable);
		entradaDiarioTemp.setEmpresa(empresa);
		entradaDiarioTemp.setMonto(monto);
		entradaDiarioTemp.setReferencia(referencia);
		entradaDiarioTemp.setTipo(tipo);
//		entradaDiarioTemp.setBalanceInicial(valor);
		entradaDiarioTemp.setBalanceInicial(new BigDecimal(montoTotal));

		if(tipo==1) {
			//debito
			entradaDiarioTemp.setDebito(monto);
			entradaDiarioTemp.setCredio(new BigDecimal("0.0"));
			entradaDiarioTemp.setBalanceFinal(new BigDecimal(entradaDiarioTemp.getBalanceInicial().doubleValue()+monto.doubleValue()));
		}else {
			//credito
			entradaDiarioTemp.setDebito(new BigDecimal("0.0"));
			entradaDiarioTemp.setCredio(monto);
			entradaDiarioTemp.setBalanceFinal(new BigDecimal(entradaDiarioTemp.getBalanceInicial().doubleValue()-monto.doubleValue()));
		}
		
		entradaDiarioTemp.setUsuario(usuario);
		serviceEntradasDiariosTemp.guardar(entradaDiarioTemp);
		
		List<EntradaDiarioTemp> entradasTemp = serviceEntradasDiariosTemp.buscarPorEmpresaUsuario(empresa, usuario);
		model.addAttribute("entradasTemp", entradasTemp);
		
		double totalDebitoTemp = 0.0;
		double totalCreditoTemp = 0.0;
		
		for (EntradaDiarioTemp entradaDiarioTemp2 : entradasTemp) {
			totalDebitoTemp+=entradaDiarioTemp2.getDebito().doubleValue();
			totalCreditoTemp+=entradaDiarioTemp2.getCredito().doubleValue();
		}
		
		model.addAttribute("totalDebitoTemp", totalDebitoTemp);
		model.addAttribute("totalCreditoTemp", totalCreditoTemp);
		model.addAttribute("dif", totalDebitoTemp-totalCreditoTemp);
		return "contabilidad/contabilidad :: #tablaEntradasTemp";
	}
	
	@GetMapping("/eliminarEntradasDiariosTemp/{id}")
	String eliminarEntradasDiariosTemp(Model model, HttpSession session,
			@PathVariable("id") Integer idEntradaDiarioTemp) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		
		EntradaDiarioTemp entradaDiarioTemp = serviceEntradasDiariosTemp.buscarPorId(idEntradaDiarioTemp);
		serviceEntradasDiariosTemp.eliminar(entradaDiarioTemp);

		List<EntradaDiarioTemp> entradasTemp = serviceEntradasDiariosTemp.buscarPorEmpresaUsuario(empresa, usuario);
		model.addAttribute("entradasTemp", entradasTemp);
		
		double totalDebitoTemp = 0.0;
		double totalCreditoTemp = 0.0;
		
		for (EntradaDiarioTemp entradaDiarioTemp2 : entradasTemp) {
			totalDebitoTemp+=entradaDiarioTemp2.getDebito().doubleValue();
			totalCreditoTemp+=entradaDiarioTemp2.getCredito().doubleValue();
		}
		
		model.addAttribute("totalDebitoTemp", totalDebitoTemp);
		model.addAttribute("totalCreditoTemp", totalCreditoTemp);
		model.addAttribute("dif", totalDebitoTemp-totalCreditoTemp);
		return "contabilidad/contabilidad :: #tablaEntradasTemp";
	}
	
	@PostMapping("/guardarEntradasDiario")
	public ResponseEntity<String> guardarEntradasDiario(HttpSession session){
		String response = "0";
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		Carpeta carpeta = null;
		
		if(session.getAttribute("carpeta") != null) {
			 carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
		}else {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, empresa);
			if(!carpetas.isEmpty()) {
				carpeta = carpetas.get(0);
			}
		}
		List<EntradaDiarioTemp> entradasTemp = serviceEntradasDiariosTemp.buscarPorEmpresaUsuario(empresa, usuario);
		
		if(!entradasTemp.isEmpty()) {
			double totalDebito = 0.0;
			double totalCredito = 0.0;
			//Verificamos que la suma de los debitos y creditos sean iguales
			for (EntradaDiarioTemp entradaDiarioTemp : entradasTemp) {
				totalDebito+=entradaDiarioTemp.getDebito().doubleValue();
				totalCredito+=entradaDiarioTemp.getCredito().doubleValue();
			}
			
			if(totalDebito == totalCredito) {
				for (EntradaDiarioTemp entradaDiarioTemp : entradasTemp) {
					EntradaDiario entradaDiario = new EntradaDiario();
					entradaDiario.setCarpeta(carpeta);
					entradaDiario.setCredito(entradaDiarioTemp.getCredito());
					entradaDiario.setCuentaContable(entradaDiarioTemp.getCuentaContable());
					entradaDiario.setDebito(entradaDiarioTemp.getDebito());
					entradaDiario.setDetalle(entradaDiarioTemp.getReferencia());
					entradaDiario.setEmpresa(empresa);
					entradaDiario.setFecha(new Date());
					entradaDiario.setUsuario(usuario);
					entradaDiario.setBalanceInicial(entradaDiarioTemp.getBalanceInicial());
					entradaDiario.setBalanceFinal(entradaDiarioTemp.getBalanceFinal());
					serviceEntradasDiarios.guardar(entradaDiario);
					response = "1";
				}
				serviceEntradasDiariosTemp.eliminar(entradasTemp);
			}else {
				response = "2";
			}
		}
		
		return new ResponseEntity<>(response, HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/libros")
	public String libros(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		List<CuentaContable> cuentasContablesAuxiliares = serviceCuentasContables.buscarPorEmpresaTipo(empresa, "A");
		List<EntradaIngresoContable> entradasIngresosContables = serviceEntradasIngresosContables.
				buscarPorEmpresaFechaCurrent(empresa);
		model.addAttribute("entradasIngresosContables", entradasIngresosContables);
		model.addAttribute("cuentasContables", cuentasContablesAuxiliares);
		model.addAttribute("fecha", new Date());
		return "contabilidad/libros :: libros";
	}
	
	@PostMapping("/buscarLibroDiario")
	public String buscarLibroDiario(Model model, HttpSession session, String desde, String hasta, String cuentaContableId) throws ParseException {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Date fechaDesde = formatter.parse(desde);
		Date fechaHasta = formatter.parse(hasta);
		List<EntradaIngresoContable> entradasIngresosContables = new LinkedList<>();
		if(cuentaContableId == null || cuentaContableId.equals("")) {
			entradasIngresosContables = serviceEntradasIngresosContables.buscarPorEmpresaFechaBetween(empresa, fechaDesde, fechaHasta);
		}else {
			CuentaContable cuentaContable = serviceCuentasContables.buscarPorId(Integer.parseInt(cuentaContableId));
			entradasIngresosContables = serviceEntradasIngresosContables.buscarPorEmpresaCuentaContableFechas(empresa, cuentaContable, fechaDesde, fechaHasta);
		}
		model.addAttribute("entradasIngresosContables", entradasIngresosContables);
		return "contabilidad/libros :: #tablaLibroDiario";
	}
	
	@GetMapping("/procesosBancarios")
	public String procesosBancarios(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<FormaPago> formasPagos = serviceFormasPagos.buscarPorEmpresaIdentificador(empresa, "formaPago");
		List<ProcesoBancarioTemp> procesosBancariosTemp = serviceProcesosBancariosTemp.buscarPorEmpresaUsuario(empresa, usuario);
		model.addAttribute("formasPagos", formasPagos);
		model.addAttribute("procesosBancariosTemp", procesosBancariosTemp);
		return "contabilidad/procesosBancarios :: procesosBancarios";
	}
	
	@PostMapping("/guardarProcesoBancarioTemp")
	public String guardarProcesoBancarioTemp(Model model, HttpSession session, 
			Integer desde, Integer hasta,
			Double monto, Double tasa, Double total) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		ProcesoBancarioTemp procesoBancarioTemp = new ProcesoBancarioTemp();
		FormaPago formaPagoDesde = serviceFormasPagos.buscarPorId(desde);
		FormaPago formaPagoHasta = serviceFormasPagos.buscarPorId(hasta);
		procesoBancarioTemp.setEmpresa(empresa);
		procesoBancarioTemp.setFormaPagoDesde(formaPagoDesde);
		procesoBancarioTemp.setFormaPagoHasta(formaPagoHasta);
		procesoBancarioTemp.setMonto(monto);
		procesoBancarioTemp.setTasa(tasa);
		procesoBancarioTemp.setTotal(total);
		procesoBancarioTemp.setUsuario(usuario);
		serviceProcesosBancariosTemp.guardar(procesoBancarioTemp);
		List<ProcesoBancarioTemp> procesosBancariosTemp = serviceProcesosBancariosTemp.buscarPorEmpresaUsuario(empresa, usuario);
		model.addAttribute("procesosBancariosTemp", procesosBancariosTemp);
		return "contabilidad/procesosBancarios :: #tablaProcesosBancariosTemp";
	}
	
	@PostMapping("/guardarProcesoBancario")
	public ResponseEntity<Integer> guardarProcesoBancario(HttpSession session){
		Integer response = 0;
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		List<ProcesoBancarioTemp> procesosBancariosTemp = serviceProcesosBancariosTemp.buscarPorEmpresaUsuario(empresa, usuario);
		if(!procesosBancariosTemp.isEmpty()) {
			for (ProcesoBancarioTemp procesoBancarioTemp : procesosBancariosTemp) {
				//Por cada iteracion afecta 2 cuentas contables
				EntradaIngresoContable entradaIngresoContableDesde = new EntradaIngresoContable();
				entradaIngresoContableDesde.setCuentaContable(procesoBancarioTemp.getFormaPagoDesde().getCuentaContable());
				entradaIngresoContableDesde.setEmpresa(empresa);
				entradaIngresoContableDesde.setFecha(new Date());
				entradaIngresoContableDesde.setTotal(procesoBancarioTemp.getMonto()*-1.0);
				entradaIngresoContableDesde.setBalance(procesoBancarioTemp.getMonto()*-1.0);
				entradaIngresoContableDesde.setUsuario(usuario);
				entradaIngresoContableDesde.setInfo("transferencia desde " +  
						procesoBancarioTemp.getFormaPagoDesde().getCuentaContable().getCodigo() + " " + 
						procesoBancarioTemp.getFormaPagoDesde().getCuentaContable().getNombreCuenta() +
						" - " + procesoBancarioTemp.getFormaPagoDesde().getTasaCambio() +
						" hasta " + procesoBancarioTemp.getFormaPagoHasta().getCuentaContable().getCodigo() + " " + 
						procesoBancarioTemp.getFormaPagoHasta().getCuentaContable().getNombreCuenta() +
						" - " + procesoBancarioTemp.getFormaPagoHasta().getTasaCambio());
				serviceEntradasIngresosContables.guardar(entradaIngresoContableDesde);
				
				EntradaIngresoContable entradaIngresoContableHasta = new EntradaIngresoContable();
				entradaIngresoContableHasta.setCuentaContable(procesoBancarioTemp.getFormaPagoHasta().getCuentaContable());
				entradaIngresoContableHasta.setEmpresa(empresa);
				entradaIngresoContableHasta.setFecha(new Date());
				entradaIngresoContableHasta.setTotal(procesoBancarioTemp.getTotal());
				entradaIngresoContableHasta.setBalance(procesoBancarioTemp.getTotal());
				entradaIngresoContableHasta.setUsuario(usuario);
				entradaIngresoContableHasta.setInfo("transferencia desde " +  
						procesoBancarioTemp.getFormaPagoDesde().getCuentaContable().getCodigo() + " " + 
						procesoBancarioTemp.getFormaPagoDesde().getCuentaContable().getNombreCuenta() +
						" - " + procesoBancarioTemp.getFormaPagoDesde().getTasaCambio() +
						" hasta " + procesoBancarioTemp.getFormaPagoHasta().getCuentaContable().getCodigo() + " " + 
						procesoBancarioTemp.getFormaPagoHasta().getCuentaContable().getNombreCuenta() +
						" - " + procesoBancarioTemp.getFormaPagoHasta().getTasaCambio());
				serviceEntradasIngresosContables.guardar(entradaIngresoContableHasta);
				
				response = 1;
			}
		}
		
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
		
		serviceProcesosBancariosTemp.eliminar(procesosBancariosTemp); 
		return new ResponseEntity<Integer>(response, HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/eliminarProcesoBancarioTemporal/{id}")
	public String eliminarProcesoBancarioTemporal(Model model, HttpSession session, @PathVariable("id") Integer id) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		ProcesoBancarioTemp procesoBancarioTemp = serviceProcesosBancariosTemp.buscarPorId(id);
		serviceProcesosBancariosTemp.eliminar(procesoBancarioTemp);
		List<ProcesoBancarioTemp> procesosBancariosTemp = serviceProcesosBancariosTemp.buscarPorEmpresaUsuario(empresa, usuario);
		model.addAttribute("procesosBancariosTemp", procesosBancariosTemp);
		return "contabilidad/procesosBancarios :: #tablaProcesosBancariosTemp";
	}
	
	@PostMapping("/conversion")
	public ResponseEntity<String> conversion(Model model, HttpSession session, Integer desde, Integer hasta, double monto, double tasa) {
		FormaPago formaPagoDesde = serviceFormasPagos.buscarPorId(desde);
		FormaPago formaPagoHasta = serviceFormasPagos.buscarPorId(hasta);
		double total = 0;
		if(formaPagoDesde.getTasaCambio().equalsIgnoreCase("peso") && formaPagoHasta.getTasaCambio().equalsIgnoreCase("dolar")) {
			total = monto / tasa;
		}else if(formaPagoDesde.getTasaCambio().equalsIgnoreCase("dolar") && formaPagoHasta.getTasaCambio().equalsIgnoreCase("peso")) {
			total = monto * tasa;
		}else if(formaPagoDesde.getTasaCambio().equalsIgnoreCase("peso") && formaPagoHasta.getTasaCambio().equalsIgnoreCase("peso")) {
			total = monto * tasa;
		}else if(formaPagoDesde.getTasaCambio().equalsIgnoreCase("dolar") && formaPagoHasta.getTasaCambio().equalsIgnoreCase("dolar")) {
			total = monto * tasa;
		}
		total = formato2d(total);
		return new ResponseEntity<>(String.valueOf(total), HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/enlaces")
	public String enlaces(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		List<CuentaContable> cuentasContablesAuxiliares = serviceCuentasContables.buscarPorEmpresaTipo(empresa, "A");
		model.addAttribute("cuentasContablesAuxiliares", cuentasContablesAuxiliares);
		List<FormaPago> formasPagos = serviceFormasPagos.buscarPorEmpresa(empresa);
		model.addAttribute("formasPagos", formasPagos);
		return "contabilidad/enlaces :: enlaces";
	}
	
	@PostMapping("/agregarEnlace")
	public ResponseEntity<String> agregarEnlace(Model model, HttpSession session, Integer cuentaContableId,
			String identificador, @RequestParam(required = false, name = "impuesto") Integer impuesto,
			@RequestParam(required = false, name = "tasaCambio") String tasaCambio) {
		String response = "0";
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		CuentaContable cuentaContable = serviceCuentasContables.buscarPorId(cuentaContableId);
		FormaPago formaPago = new FormaPago();
		formaPago.setCuentaContable(cuentaContable);
		formaPago.setEmpresa(empresa);
		formaPago.setIdentificador(identificador);
		if(impuesto!=null) {
			formaPago.setImpuesto(impuesto);
		}
		if(tasaCambio!=null) {
			formaPago.setTasaCambio(tasaCambio);
		}
		List<FormaPago>formasPagosTemp = serviceFormasPagos.buscarPorEmpresaCuentaContableIdentificador(empresa,
				cuentaContable, identificador);
		if(!formasPagosTemp.isEmpty()) {
			response = "2";
		}else {
			serviceFormasPagos.guardar(formaPago);
			if(formaPago.getId()!=null) {
				response = "1";
			}
		}
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}
	
	@PostMapping("/borrarEnlace")
	public ResponseEntity<String> borrarEnlace(Model model, HttpSession session, Integer id) {
		String response = "0";
		FormaPago formaPago = serviceFormasPagos.buscarPorId(id);
		serviceFormasPagos.eliminar(formaPago);
		FormaPago formaPagoTemp = serviceFormasPagos.buscarPorId(id);
		if(formaPagoTemp==null) {
			response="1";
		}
		return new ResponseEntity<String>(response, HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/mostrarEnlaces")
	public String mostrarEnlaces(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		List<FormaPago> formasPagos = serviceFormasPagos.buscarPorEmpresaIdentificador(empresa, "formaPago");
		model.addAttribute("formasPagos", formasPagos);
		return "contabilidad/enlaces :: #tablaEnlaces";
	}
	
	@GetMapping("/mostrarEnlacesAdicionales")
	public String mostrarEnlacesAdicionales(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		List<FormaPago> formasPagos = serviceFormasPagos.buscarPorEmpresaIdentificador(empresa, "enlaceAdicional");
		model.addAttribute("formasPagos", formasPagos);
		return "contabilidad/enlaces :: #tablaEnlacesAdicionales";
	}
	
	@GetMapping("/mostrarEnlacesMoras")
	public String mostrarEnlacesMoras(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		List<FormaPago> formasPagos = serviceFormasPagos.buscarPorEmpresaIdentificador(empresa, "enlaceMora");
		model.addAttribute("formasPagos", formasPagos);
		return "contabilidad/enlaces :: #tablaEnlacesMoras";
	}
	
	@GetMapping("/mostrarEnlacesIntereses")
	public String mostrarEnlacesIntereses(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		List<FormaPago> formasPagos = serviceFormasPagos.buscarPorEmpresaIdentificador(empresa, "enlaceInteres");
		model.addAttribute("formasPagos", formasPagos);
		return "contabilidad/enlaces :: #tablaEnlacesIntereses";
	}

	@GetMapping("/mostrarEnlacesCapitales")
	public String mostrarEnlacesCapitales(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		List<FormaPago> formasPagos = serviceFormasPagos.buscarPorEmpresaIdentificador(empresa, "enlaceCapital");
		model.addAttribute("formasPagos", formasPagos);
		return "contabilidad/enlaces :: #tablaEnlacesCapitales";
	}
	
	@GetMapping("/mostrarEnlacesItbis")
	public String mostrarEnlacesItbis(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		List<FormaPago> formasPagos = serviceFormasPagos.buscarPorEmpresaIdentificador(empresa, "itbis");
		model.addAttribute("formasPagos", formasPagos);
		return "contabilidad/enlaces :: #tablaEnlacesItbis";
	}
	
	@GetMapping("mostrarEnlacesProcesosBancarios")
	public String mostrarEnlacesProcesosBancarios(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		List<FormaPago> formasPagos = serviceFormasPagos.buscarPorEmpresaIdentificador(empresa, "formaPago");
		model.addAttribute("formasPagos", formasPagos);
		return "contabilidad/enlaces :: #tablaEnlacesProcesosBancarios";
	}
	
	@GetMapping("/mostrarEnlacesProcesos")
	public String mostrarEnlacesProcesos(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		List<FormaPago> formasPagos = serviceFormasPagos.buscarPorEmpresaIdentificador(empresa, "procesos");
		model.addAttribute("formasPagos", formasPagos);
		return "contabilidad/enlaces :: #tablaEnlacesProcesos";
	}
	
	@GetMapping("/mostrarEnlacesRetencion")
	public String mostrarEnlacesRetencion(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		List<FormaPago> formasPagos = serviceFormasPagos.buscarPorEmpresaIdentificador(empresa, "retencion");
		model.addAttribute("formasPagos", formasPagos);
		return "contabilidad/enlaces :: #tablaEnlacesRetencion";
	}
	
	@GetMapping("/mostrarEnlacesEntidadEnlace")
	public String mostrarEnlacesEntidadEnlace(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		List<FormaPago> formasPagos = serviceFormasPagos.buscarPorEmpresaIdentificador(empresa, "entidadEnlace");
		model.addAttribute("formasPagos", formasPagos);
		return "contabilidad/enlaces :: #tablaEnlacesEntidadEnlace";
	}
	
	@GetMapping("/cuentasXPagar")
	public String cuentasXPagar(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		List<Compra> compras = serviceCompras.buscarPorEmpresaTotalMayorque(empresa, 0);
		
		List<FormaPago> cuentasContablesFormaPago = serviceFormasPagos.
				buscarPorEmpresaIdentificador(empresa, "formaPago");
		
		List<FormaPago> cuentasProcesos = serviceFormasPagos.buscarPorEmpresaIdentificador(empresa, "procesos");
		
		double totalBalance =0;
		
		for (Compra compra : compras) {
			totalBalance+=compra.getBalance();
		}
		
		Compra compra = new Compra();
		compra.setCuentaContable(new CuentaContable());
		compra.setUsuario(new Usuario());
		compra.setSuplidor(new Suplidor());
		
		//Calculo del balance de la cuenta contable
		
		Carpeta carpeta = null;
		
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
		model.addAttribute("compras", compras);
		model.addAttribute("compra", compra);
		model.addAttribute("dateAcct", new Date());
		model.addAttribute("cuentasContablesFormaPago", cuentasContablesFormaPago);
		model.addAttribute("totalBalance", totalBalance);
		return "contabilidad/cuentasXPagar :: cuentasXPagar";
	}	
	
	@GetMapping("/cuentasXCobrar")
	public String cuentasXCobrar(Model model, HttpSession session) throws ParseException {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		
		Carpeta carpeta = null;
		
		double totalPesoAdicionales = 0;
		double totalPesoCapital = 0;
		double totalPesoInteres = 0;
		double totalPesoMora = 0;
		
		double totalDolarAdicionales = 0;
		double totalDolarCapital = 0;
		double totalDolarInteres = 0;
		double totalDolarMora = 0;
		
		double totalAdicionales = 0;
		double totalCapital = 0;
		double totalInteres = 0;
		double totalMora = 0;
		
		if(session.getAttribute("carpeta") != null) {
			 carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
		}else {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, empresa);
			if(!carpetas.isEmpty()) {
				carpeta = carpetas.get(0);
			}
		}
		
		List<PrestamoCalculoTotal> prestamosTotales = new LinkedList<>();
		
		List<Integer> estados = new LinkedList<>();
		estados.add(1);
		
		List<Prestamo> prestamos = servicePrestamos.buscarPorEmpresaMonedaEstadoNotIn(empresa, "pesos", estados);
		
		for (Prestamo prestamo : prestamos) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  
			List<Amortizacion> detalles = new LinkedList<>();
			List<PrestamoInteresDetalle> prestamoInteresDetalles = servicePrestamosInteresesDetalles.buscarPorPrestamo(prestamo);
			double totalPendiente = 0;
			double sumaInteresGen = 0;
			double sumaMora = 0;
			double sumaCargo = 0;
			double sumaAbono = 0;
			double sumaBalance = 0;
			
			if(prestamo.getTipo().equals("2")) {
				//Interes
				int count = 0;
				double mora = 0;
				double interes = 0;
				double capital = 0;
				if(!prestamoInteresDetalles.isEmpty()) {
					for (PrestamoInteresDetalle prestamoInteresDetalle : prestamoInteresDetalles) {
						count++;
						String estado = "Normal";
						if(prestamoInteresDetalle.getEstado()==2) {
							estado = "Atraso";
						}else if(prestamoInteresDetalle.getEstado()==1) {
							estado = "Saldo";
						}else if(prestamoInteresDetalle.getEstado()==3) {
							estado = "Legal";
						}
						
						double cargoPagadoTemp = 0;
						
						Amortizacion amortizacion = new Amortizacion();
						
						double descuentoAdicionales = 0;
						double descuentoCargo = 0;
						
						List<PrestamoAdicional> adicionales = servicePrestamosAdicionales.buscarPorPrestamoNumeroCuota(prestamoInteresDetalle.getPrestamo(), prestamoInteresDetalle.getNumero_cuota());
						if(!adicionales.isEmpty()) {
							double cargo = 0;
							for (PrestamoAdicional adicionalTemp : adicionales) {
								cargo+=adicionalTemp.getMonto()-adicionalTemp.getMonto_pagado();
								cargoPagadoTemp+=adicionalTemp.getMonto_pagado();
								descuentoAdicionales+=adicionalTemp.getDescuento_adicionales();
							}
							amortizacion.setCargo(cargo);
							descuentoCargo = amortizacion.getCargo()-descuentoAdicionales;
							amortizacion.setCargo(cargo-descuentoAdicionales);
						}
						
						LocalDateTime fechaTemp = convertToLocalDateTimeViaInstant(prestamoInteresDetalle.getFecha_cuota()).minusMonths(1);
						LocalDateTime dateAcct = LocalDateTime.now();
						int vencidos = (int) diasVencidos(fechaTemp, dateAcct);
						double interesXHoy = prestamoInteresDetalle.getInteres()/30.00*vencidos;
						
						if(interesXHoy>=prestamoInteresDetalle.getInteres()) {
							interesXHoy = prestamoInteresDetalle.getInteres()-prestamoInteresDetalle.getInteres_pagado();
						}
						
						double balance = (prestamoInteresDetalle.getInteres()-prestamoInteresDetalle.getInteres_pagado())+(prestamoInteresDetalle.getMora()-prestamoInteresDetalle.getMora_pagada()-prestamoInteresDetalle.getDescuento_mora())+(amortizacion.getCargo()==null?0:descuentoCargo);
						double abono = prestamoInteresDetalle.getInteres_pagado()+prestamoInteresDetalle.getMora_pagada()+cargoPagadoTemp;
						
						amortizacion.setBalance(formato2d(balance));
						amortizacion.setAbono(formato2d(abono));
						amortizacion.setInteresXhoy(balance==0?0:formato2d(interesXHoy));
						amortizacion.setNumero(count);
						amortizacion.setFecha(sdf.format(prestamoInteresDetalle.getFecha_cuota()));
						capital = prestamoInteresDetalle.getCapital();
						capital-= prestamoInteresDetalle.getCapital_pagado();
						amortizacion.setInteres(formato2d(prestamoInteresDetalle.getInteres()-prestamoInteresDetalle.getInteres_pagado()));
						amortizacion.setTipo(2);
						
						mora = prestamoInteresDetalle.getMora()-prestamoInteresDetalle.getMora_pagada()-prestamoInteresDetalle.getDescuento_mora();

						amortizacion.setMora(formato2d(mora));
						amortizacion.setEstado(estado);
						amortizacion.setAtraso(prestamoInteresDetalle.getDias_atraso());
						
						double descuentoAdicional = 0;
						
						List<PrestamoAdicional> adicionalTemp = servicePrestamosAdicionales
								.buscarPorPrestamoNumeroCuota(prestamo, prestamoInteresDetalle.getNumero_cuota());
						for (PrestamoAdicional adicional : adicionalTemp) {
							descuentoAdicional += adicional.getDescuento_adicionales();
						}

						amortizacion.setDescuento(prestamoInteresDetalle.getDescuento_mora()+descuentoAdicional);
						
						amortizacion.setId(prestamoInteresDetalle.getId());
						detalles.add(amortizacion);
						mora+=prestamoInteresDetalle.getMora();
						interes+=prestamoInteresDetalle.getInteres();
						
						sumaInteresGen+= amortizacion.getInteres();
						sumaMora += amortizacion.getMora();
						sumaAbono += amortizacion.getAbono();
						sumaBalance += amortizacion.getBalance();
											
					}
					detalles.get(0).setCuota(formato2d(mora+interes));
					detalles.get(0).setCapital(formato2d(capital));
				}
				
				List<PrestamoAdicional> pretamoAdicionalTemp = servicePrestamosAdicionales.buscarPorPrestamoEstado(prestamo, 0);
				for (PrestamoAdicional prestamoAdicional : pretamoAdicionalTemp) {
					sumaCargo+=prestamoAdicional.getMonto()-prestamoAdicional.getMonto_pagado()-prestamoAdicional.getDescuento_adicionales();
				}
				
			}else {
				//Cuotas
				
				int count = 0;
				double mora = 0;
				double interes = 0;
				double capital = 0;

				List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorPrestamo(prestamo);
				
				detalles = new LinkedList<>();
				for (PrestamoDetalle prestamoDetalle : prestamoDetalles) {
					
					List<PrestamoAdicional> adicionales = servicePrestamosAdicionales.buscarPorPrestamoNumeroCuota(prestamoDetalle.getPrestamo(), prestamoDetalle.getNumero());

					Amortizacion amortizacion = new Amortizacion();
					
					double cargo = 0;
					
					count++;
					String estado = "Normal";
					if(prestamoDetalle.getEstado()==2) {
						estado = "Atraso";
					}else if(prestamoDetalle.getEstado()==1) {
						estado = "Saldo";
					}
					
					double cargoPagadoTemp = 0;
					double descuentoAdicionales = 0;
					double descuentoCargo = 0;
					
					if(!adicionales.isEmpty()) {
						for (PrestamoAdicional adicionalTemp : adicionales) {
							cargo+=adicionalTemp.getMonto()-adicionalTemp.getMonto_pagado();
							cargoPagadoTemp+=adicionalTemp.getMonto_pagado();
							descuentoAdicionales+=adicionalTemp.getDescuento_adicionales();
						}
						amortizacion.setCargo(cargo);
						descuentoCargo = amortizacion.getCargo()-descuentoAdicionales;
						amortizacion.setCargo(formato2d(cargo-descuentoAdicionales));
					}
					
					double abono = prestamoDetalle.getCapital_pagado()+prestamoDetalle.getInteres_pagado()+prestamoDetalle.getMora_pagada()+cargoPagadoTemp;
					
					double descuentoAdicional = 0;
					
					List<PrestamoAdicional> adicionalTemp = servicePrestamosAdicionales
							.buscarPorPrestamoNumeroCuota(prestamo, prestamoDetalle.getNumero());
					for (PrestamoAdicional adicional : adicionalTemp) {
						descuentoAdicional += adicional.getDescuento_adicionales();
					}

					amortizacion.setDescuento(formato2d(prestamoDetalle.getDescuento_mora()+descuentoAdicional));
					
					amortizacion.setAbono(formato2d(abono));
					amortizacion.setId(prestamoDetalle.getId());
					amortizacion.setFecha(sdf.format(prestamoDetalle.getFechaGenerada()));
					amortizacion.setCuota(prestamoDetalle.getCuota());
					amortizacion.setTipo(Integer.parseInt(prestamoDetalle.getPrestamo().getTipo()));
					amortizacion.setCapital(formato2d(prestamoDetalle.getCapital()-prestamoDetalle.getCapital_pagado()));
					amortizacion.setInteres(formato2d(prestamoDetalle.getInteres()-prestamoDetalle.getInteres_pagado()));
					amortizacion.setSaldo(formato2d(prestamoDetalle.getBalance()));
					amortizacion.setNumero(prestamoDetalle.getNumero());
					amortizacion.setMora(formato2d(formato2d(prestamoDetalle.getMora())-formato2d(prestamoDetalle.getMora_pagada())-formato2d(prestamoDetalle.getDescuento_mora())));
					amortizacion.setEstado(prestamoDetalle.getEstado_cuota());
					amortizacion.setAtraso(prestamoDetalle.getDias_atraso());
					amortizacion.setBalance(formato2d((amortizacion.getCapital()==null?0:amortizacion.getCapital()) + (amortizacion.getInteres()==null?0:amortizacion.getInteres()) + (amortizacion.getMora()==null?0:amortizacion.getMora()) + (amortizacion.getCargo()==null?0:amortizacion.getCargo())));
					detalles.add(amortizacion);
				}
			}
			
			Integer idCliente; 
					
			if(session.getAttribute("cliente")!=null) {
				if((Integer) session.getAttribute("cliente") == 0) {
					idCliente = prestamo.getCliente().getId();
				}else {
					idCliente = (Integer) session.getAttribute("cliente");
				}
			}else {
				idCliente = prestamo.getCliente().getId();
			}

			Cliente cliente = serviceClientes.buscarPorId(idCliente);
			String datosCliente  = cliente.getNombre()+" / Tel.: "+cliente.getTelefono();

			if(prestamo.getTipo().equals("2")) {
				
				if(formato2d(sumaCargo) > 0 || formato2d(prestamo.getBalance()) > 0 
						|| formato2d(sumaInteresGen) > 0 || formato2d(sumaMora) > 0 ) {
					
					if(prestamo.getMoneda().equalsIgnoreCase("pesos")) {
						PrestamoCalculoTotal prestamoCalculoTotal = new PrestamoCalculoTotal();
						prestamoCalculoTotal.setAdicionales(formato2d(sumaCargo));
						prestamoCalculoTotal.setCapital(formato2d(prestamo.getBalance()));
						prestamoCalculoTotal.setInteres(formato2d(sumaInteresGen));
						prestamoCalculoTotal.setMora(formato2d(sumaMora));
						prestamoCalculoTotal.setPrestamo(prestamo);
						prestamoCalculoTotal.setBalance(formato2d(sumaBalance));
						prestamoCalculoTotal.setMoneda("pesos");
						prestamosTotales.add(prestamoCalculoTotal);
						
						totalPesoAdicionales += formato2d(sumaCargo);
						totalPesoCapital += formato2d(prestamo.getBalance());
						totalPesoInteres += formato2d(sumaInteresGen);
						totalPesoMora += formato2d(sumaMora);

					}else if(prestamo.getMoneda().equalsIgnoreCase("dolar")) {
						PrestamoCalculoTotal prestamoCalculoTotal = new PrestamoCalculoTotal();
						prestamoCalculoTotal.setAdicionales(formato2d(sumaCargo));
						prestamoCalculoTotal.setCapital(formato2d(prestamo.getBalance()));
						prestamoCalculoTotal.setInteres(formato2d(sumaInteresGen));
						prestamoCalculoTotal.setMora(formato2d(sumaMora));
						prestamoCalculoTotal.setPrestamo(prestamo);
						prestamoCalculoTotal.setBalance(formato2d(sumaBalance));
						prestamoCalculoTotal.setMoneda("dolar");
						prestamosTotales.add(prestamoCalculoTotal);
						
						totalDolarAdicionales += formato2d(sumaCargo);
						totalDolarCapital += formato2d(prestamo.getBalance());
						totalDolarInteres += formato2d(sumaInteresGen);
						totalDolarMora += formato2d(sumaMora);
					}
					
					totalAdicionales += formato2d(sumaCargo);
					totalCapital += formato2d(prestamo.getBalance());
					totalInteres += formato2d(sumaInteresGen);
					totalMora += formato2d(sumaMora);
				}
			}

			double sumaCapital = 0;
			double montoCuota = 0;
			double sumaMoraTemp = 0;
			double sumaInteres = 0;
			double sumaCargos = 0;
			double sumaAbonoCuota = 0;
			double sumaDescuento = 0;
			double sumaTotales = 0;
			
			for (Amortizacion detalle : detalles) {
				sumaCapital+=detalle.getCapital()==null?0:detalle.getCapital();
				montoCuota+=detalle.getCuota()==null?0:detalle.getCuota();
				sumaMoraTemp+=detalle.getMora()==null?0:detalle.getMora();
				sumaInteres+=detalle.getInteres()==null?0:detalle.getInteres();
				sumaCargos+=detalle.getCargo()==null?0:detalle.getCargo();
				sumaAbonoCuota+=detalle.getAbono()==null?0:detalle.getAbono();
				sumaDescuento+=detalle.getDescuento()==null?0:detalle.getDescuento();
				sumaTotales+=detalle.getBalance()==null?0:detalle.getBalance();
			}

			if(!prestamo.getTipo().equals("2")) {
				
				if(formato2d(sumaCargos) > 0 || formato2d(sumaCapital) > 0 || 
						formato2d(sumaInteres) > 0 || formato2d(sumaMoraTemp) > 0) {
					if(prestamo.getMoneda().equalsIgnoreCase("pesos")) {	
						PrestamoCalculoTotal prestamoCalculoTotal = new PrestamoCalculoTotal();
						prestamoCalculoTotal.setAdicionales(formato2d(sumaCargos));
						prestamoCalculoTotal.setCapital(formato2d(sumaCapital));
						prestamoCalculoTotal.setInteres(formato2d(sumaInteres));
						prestamoCalculoTotal.setMora(formato2d(sumaMoraTemp));
						prestamoCalculoTotal.setPrestamo(prestamo);
						prestamoCalculoTotal.setBalance(formato2d(sumaTotales));
						prestamoCalculoTotal.setMoneda("pesos");
						prestamosTotales.add(prestamoCalculoTotal);
						
						totalPesoAdicionales += formato2d(sumaCargos);
						totalPesoCapital += formato2d(sumaCapital);
						totalPesoInteres += formato2d(sumaInteres);
						totalPesoMora += formato2d(sumaMoraTemp);
		
					}else if(prestamo.getMoneda().equalsIgnoreCase("dolar")) {
						PrestamoCalculoTotal prestamoCalculoTotal = new PrestamoCalculoTotal();
						prestamoCalculoTotal.setAdicionales(formato2d(sumaCargos));
						prestamoCalculoTotal.setCapital(formato2d(sumaCapital));
						prestamoCalculoTotal.setInteres(formato2d(sumaInteres));
						prestamoCalculoTotal.setMora(formato2d(sumaMoraTemp));
						prestamoCalculoTotal.setPrestamo(prestamo);
						prestamoCalculoTotal.setBalance(formato2d(sumaTotales));
						prestamoCalculoTotal.setMoneda("dolar");
						prestamosTotales.add(prestamoCalculoTotal);
						
						totalDolarAdicionales += formato2d(sumaCargos);
						totalDolarCapital += formato2d(sumaCapital);
						totalDolarInteres += formato2d(sumaInteres);
						totalDolarMora += formato2d(sumaMoraTemp);
					}
					
					totalAdicionales += formato2d(sumaCargos);
					totalCapital += formato2d(sumaCapital);
					totalInteres += formato2d(sumaInteres);
					totalMora += formato2d(sumaMoraTemp);
				}
			}
		}
		
		List<Cliente> clientes = serviceClientes.buscarPorEmpresaEstado(empresa, 1);
		
		model.addAttribute("clientes", clientes);
		model.addAttribute("dateAcct", new Date());
		
		model.addAttribute("totalCapital", formato2d(totalPesoCapital));
		model.addAttribute("totalInteres", formato2d(totalPesoInteres));
		model.addAttribute("totalMora",  formato2d(totalPesoMora));
		model.addAttribute("totalAdicional", formato2d(totalPesoAdicionales));

		model.addAttribute("prestamosTotales", prestamosTotales);
		return "contabilidad/cuentasXCobrar :: cuentasXCobrar";
	}	
	
	@PostMapping("/buscarPrestamosXCobrar")
	public String buscarPrestamosXCobrar(Model model, HttpSession session, 
			String clienteId, String moneda
//			,String desde, String hasta
			) throws ParseException {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
//		Date fechaDesde = formatter.parse(desde);
//		Date fechaHasta = formatter.parse(hasta);

		Carpeta carpeta = null;
		
		if(moneda.equals("peso")) {
			moneda = "pesos";
		}
		
		double totalAdicionales = 0;
		double totalCapital = 0;
		double totalInteres = 0;
		double totalMora = 0;
		
		double totalPesoAdicionales = 0;
		double totalPesoCapital = 0;
		double totalPesoInteres = 0;
		double totalPesoMora = 0;
		
		double totalDolarAdicionales = 0;
		double totalDolarCapital = 0;
		double totalDolarInteres = 0;
		double totalDolarMora = 0;
		
		if(session.getAttribute("carpeta") != null) {
			 carpeta = serviceCarpetas.buscarPorId((Integer) session.getAttribute("carpeta"));
		}else {
			List<Carpeta> carpetas = serviceCarpetas.buscarTipoCarpetaEmpresa(1, empresa);
			if(!carpetas.isEmpty()) {
				carpeta = carpetas.get(0);
			}
		}
		
		List<PrestamoCalculoTotal> prestamosTotales = new LinkedList<>();
		
		List<Prestamo> prestamos = new LinkedList<>();
		
		List<Integer> estados = new LinkedList<>();
		estados.add(1);
		
		if(!clienteId.equals("")) {
			Cliente cliente = serviceClientes.buscarPorId(Integer.parseInt(clienteId));
			prestamos = servicePrestamos.buscarPorClienteCarpetaEmpresaMonedaEstadoNotIn(cliente, carpeta, empresa, moneda, estados);
		}else {
			prestamos = servicePrestamos.buscarPorCarpetaEmpresaMonedaEstadoNotIn(carpeta, empresa, moneda, estados);
		}

		for (Prestamo prestamo : prestamos) {
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");  
			List<Amortizacion> detalles = new LinkedList<>();
			List<PrestamoInteresDetalle> prestamoInteresDetalles = servicePrestamosInteresesDetalles.buscarPorPrestamo(prestamo);
			double totalPendiente = 0;
			double sumaInteresGen = 0;
			double sumaMora = 0;
			double sumaCargo = 0;
			double sumaAbono = 0;
			double sumaBalance = 0;
			
			if(prestamo.getTipo().equals("2")) {
				//Interes
				int count = 0;
				double mora = 0;
				double interes = 0;
				double capital = 0;
				if(!prestamoInteresDetalles.isEmpty()) {
					for (PrestamoInteresDetalle prestamoInteresDetalle : prestamoInteresDetalles) {
						count++;
						String estado = "Normal";
						if(prestamoInteresDetalle.getEstado()==2) {
							estado = "Atraso";
						}else if(prestamoInteresDetalle.getEstado()==1) {
							estado = "Saldo";
						}else if(prestamoInteresDetalle.getEstado()==3) {
							estado = "Legal";
						}
						
						double cargoPagadoTemp = 0;
						
						Amortizacion amortizacion = new Amortizacion();
						
						double descuentoAdicionales = 0;
						double descuentoCargo = 0;
						
						List<PrestamoAdicional> adicionales = servicePrestamosAdicionales.buscarPorPrestamoNumeroCuota(prestamoInteresDetalle.getPrestamo(), prestamoInteresDetalle.getNumero_cuota());
						if(!adicionales.isEmpty()) {
							double cargo = 0;
							for (PrestamoAdicional adicionalTemp : adicionales) {
								cargo+=adicionalTemp.getMonto()-adicionalTemp.getMonto_pagado();
								cargoPagadoTemp+=adicionalTemp.getMonto_pagado();
								descuentoAdicionales+=adicionalTemp.getDescuento_adicionales();
							}
							amortizacion.setCargo(cargo);
							descuentoCargo = amortizacion.getCargo()-descuentoAdicionales;
							amortizacion.setCargo(cargo-descuentoAdicionales);
						}
						
						LocalDateTime fechaTemp = convertToLocalDateTimeViaInstant(prestamoInteresDetalle.getFecha_cuota()).minusMonths(1);
						LocalDateTime dateAcct = LocalDateTime.now();
						int vencidos = (int) diasVencidos(fechaTemp, dateAcct);
						double interesXHoy = prestamoInteresDetalle.getInteres()/30.00*vencidos;
						
						if(interesXHoy>=prestamoInteresDetalle.getInteres()) {
							interesXHoy = prestamoInteresDetalle.getInteres()-prestamoInteresDetalle.getInteres_pagado();
						}
						
						double balance = (prestamoInteresDetalle.getInteres()-prestamoInteresDetalle.getInteres_pagado())+(prestamoInteresDetalle.getMora()-prestamoInteresDetalle.getMora_pagada()-prestamoInteresDetalle.getDescuento_mora())+(amortizacion.getCargo()==null?0:descuentoCargo);
						double abono = prestamoInteresDetalle.getInteres_pagado()+prestamoInteresDetalle.getMora_pagada()+cargoPagadoTemp;
						
						amortizacion.setBalance(formato2d(balance));
						amortizacion.setAbono(formato2d(abono));
						amortizacion.setInteresXhoy(balance==0?0:formato2d(interesXHoy));
						amortizacion.setNumero(count);
						amortizacion.setFecha(sdf.format(prestamoInteresDetalle.getFecha_cuota()));
						capital = prestamoInteresDetalle.getCapital();
						capital-= prestamoInteresDetalle.getCapital_pagado();
						amortizacion.setInteres(formato2d(prestamoInteresDetalle.getInteres()-prestamoInteresDetalle.getInteres_pagado()));
						amortizacion.setTipo(2);
						
						mora = prestamoInteresDetalle.getMora()-prestamoInteresDetalle.getMora_pagada()-prestamoInteresDetalle.getDescuento_mora();

						amortizacion.setMora(formato2d(mora));
						amortizacion.setEstado(estado);
						amortizacion.setAtraso(prestamoInteresDetalle.getDias_atraso());
						
						double descuentoAdicional = 0;
						
						List<PrestamoAdicional> adicionalTemp = servicePrestamosAdicionales
								.buscarPorPrestamoNumeroCuota(prestamo, prestamoInteresDetalle.getNumero_cuota());
						for (PrestamoAdicional adicional : adicionalTemp) {
							descuentoAdicional += adicional.getDescuento_adicionales();
						}

						amortizacion.setDescuento(prestamoInteresDetalle.getDescuento_mora()+descuentoAdicional);
						
						amortizacion.setId(prestamoInteresDetalle.getId());
						detalles.add(amortizacion);
						mora+=prestamoInteresDetalle.getMora();
						interes+=prestamoInteresDetalle.getInteres();
						
						sumaInteresGen+= amortizacion.getInteres();
						sumaMora += amortizacion.getMora();
						sumaAbono += amortizacion.getAbono();
						sumaBalance += amortizacion.getBalance();
											
					}
					detalles.get(0).setCuota(formato2d(mora+interes));
					detalles.get(0).setCapital(formato2d(capital));
				}
				
				List<PrestamoAdicional> pretamoAdicionalTemp = servicePrestamosAdicionales.buscarPorPrestamoEstado(prestamo, 0);
				for (PrestamoAdicional prestamoAdicional : pretamoAdicionalTemp) {
					sumaCargo+=prestamoAdicional.getMonto()-prestamoAdicional.getMonto_pagado()-prestamoAdicional.getDescuento_adicionales();
				}
				
			}else {
				//Cuotas
				
				int count = 0;
				double mora = 0;
				double interes = 0;
				double capital = 0;

				List<PrestamoDetalle> prestamoDetalles = servicePrestamosDetalles.buscarPorPrestamo(prestamo);
				
				detalles = new LinkedList<>();
				for (PrestamoDetalle prestamoDetalle : prestamoDetalles) {
					
					List<PrestamoAdicional> adicionales = servicePrestamosAdicionales.buscarPorPrestamoNumeroCuota(prestamoDetalle.getPrestamo(), prestamoDetalle.getNumero());

					Amortizacion amortizacion = new Amortizacion();
					
					double cargo = 0;
					
					count++;
					String estado = "Normal";
					if(prestamoDetalle.getEstado()==2) {
						estado = "Atraso";
					}else if(prestamoDetalle.getEstado()==1) {
						estado = "Saldo";
					}
					
					double cargoPagadoTemp = 0;
					double descuentoAdicionales = 0;
					double descuentoCargo = 0;
					
					if(!adicionales.isEmpty()) {
						for (PrestamoAdicional adicionalTemp : adicionales) {
							cargo+=adicionalTemp.getMonto()-adicionalTemp.getMonto_pagado();
							cargoPagadoTemp+=adicionalTemp.getMonto_pagado();
							descuentoAdicionales+=adicionalTemp.getDescuento_adicionales();
						}
						amortizacion.setCargo(cargo);
						descuentoCargo = amortizacion.getCargo()-descuentoAdicionales;
						amortizacion.setCargo(formato2d(cargo-descuentoAdicionales));
					}
					
					double abono = prestamoDetalle.getCapital_pagado()+prestamoDetalle.getInteres_pagado()+prestamoDetalle.getMora_pagada()+cargoPagadoTemp;
					
					double descuentoAdicional = 0;
					
					List<PrestamoAdicional> adicionalTemp = servicePrestamosAdicionales
							.buscarPorPrestamoNumeroCuota(prestamo, prestamoDetalle.getNumero());
					for (PrestamoAdicional adicional : adicionalTemp) {
						descuentoAdicional += adicional.getDescuento_adicionales();
					}

					amortizacion.setDescuento(formato2d(prestamoDetalle.getDescuento_mora()+descuentoAdicional));
					
					amortizacion.setAbono(formato2d(abono));
					amortizacion.setId(prestamoDetalle.getId());
					amortizacion.setFecha(sdf.format(prestamoDetalle.getFechaGenerada()));
					amortizacion.setCuota(prestamoDetalle.getCuota());
					amortizacion.setTipo(Integer.parseInt(prestamoDetalle.getPrestamo().getTipo()));
					amortizacion.setCapital(formato2d(prestamoDetalle.getCapital()-prestamoDetalle.getCapital_pagado()));
					amortizacion.setInteres(formato2d(prestamoDetalle.getInteres()-prestamoDetalle.getInteres_pagado()));
					amortizacion.setSaldo(formato2d(prestamoDetalle.getBalance()));
					amortizacion.setNumero(prestamoDetalle.getNumero());
					amortizacion.setMora(formato2d(prestamoDetalle.getMora()-prestamoDetalle.getMora_pagada()-prestamoDetalle.getDescuento_mora()));
					amortizacion.setEstado(prestamoDetalle.getEstado_cuota());
					amortizacion.setAtraso(prestamoDetalle.getDias_atraso());
					amortizacion.setBalance(formato2d((amortizacion.getCapital()==null?0:amortizacion.getCapital()) + (amortizacion.getInteres()==null?0:amortizacion.getInteres()) + (amortizacion.getMora()==null?0:amortizacion.getMora()) + (amortizacion.getCargo()==null?0:amortizacion.getCargo())));
					detalles.add(amortizacion);
				}
			}
			
			Integer idCliente; 
					
			if(session.getAttribute("cliente")!=null) {
				if((Integer) session.getAttribute("cliente") == 0) {
					idCliente = prestamo.getCliente().getId();
				}else {
					idCliente = (Integer) session.getAttribute("cliente");
				}
			}else {
				idCliente = prestamo.getCliente().getId();
			}

			Cliente cliente = serviceClientes.buscarPorId(idCliente);
			String datosCliente  = cliente.getNombre()+" / Tel.: "+cliente.getTelefono();

			if(prestamo.getTipo().equals("2")) {
				
				if(formato2d(sumaCargo) > 0 || formato2d(prestamo.getBalance()) > 0 || 
						formato2d(sumaInteresGen) > 0 || formato2d(sumaMora) > 0) {
					if(prestamo.getMoneda().equalsIgnoreCase("pesos")) {	
						PrestamoCalculoTotal prestamoCalculoTotal = new PrestamoCalculoTotal();
						prestamoCalculoTotal.setAdicionales(formato2d(sumaCargo));
						prestamoCalculoTotal.setCapital(formato2d(prestamo.getBalance()));
						prestamoCalculoTotal.setInteres(formato2d(sumaInteresGen));
						prestamoCalculoTotal.setMora(formato2d(sumaMora));
						prestamoCalculoTotal.setPrestamo(prestamo);
						prestamoCalculoTotal.setBalance(formato2d(sumaBalance));
						prestamoCalculoTotal.setMoneda("pesos");
						prestamosTotales.add(prestamoCalculoTotal);
						
						totalPesoAdicionales += formato2d(sumaCargo);
						totalPesoCapital += formato2d(prestamo.getBalance());
						totalPesoInteres += formato2d(sumaInteresGen);
						totalPesoMora += formato2d(sumaMora);
						
					}else if(prestamo.getMoneda().equalsIgnoreCase("dolar")) {
						PrestamoCalculoTotal prestamoCalculoTotal = new PrestamoCalculoTotal();
						prestamoCalculoTotal.setAdicionales(formato2d(sumaCargo));
						prestamoCalculoTotal.setCapital(formato2d(prestamo.getBalance()));
						prestamoCalculoTotal.setInteres(formato2d(sumaInteresGen));
						prestamoCalculoTotal.setMora(formato2d(sumaMora));
						prestamoCalculoTotal.setPrestamo(prestamo);
						prestamoCalculoTotal.setBalance(formato2d(sumaBalance));
						prestamoCalculoTotal.setMoneda("dolar");
						prestamosTotales.add(prestamoCalculoTotal);
						
						totalDolarAdicionales += formato2d(sumaCargo);
						totalDolarCapital += formato2d(prestamo.getBalance());
						totalDolarInteres += formato2d(sumaInteresGen);
						totalDolarMora += formato2d(sumaMora);
					}
					
					totalAdicionales += formato2d(sumaCargo);
					totalCapital += formato2d(prestamo.getBalance());
					totalInteres += formato2d(sumaInteresGen);
					totalMora += formato2d(sumaMora);
				}
			}

			double sumaCapital = 0;
			double montoCuota = 0;
			double sumaMoraTemp = 0;
			double sumaInteres = 0;
			double sumaCargos = 0;
			double sumaAbonoCuota = 0;
			double sumaDescuento = 0;
			double sumaTotales = 0;

			for (Amortizacion detalle : detalles) {
				sumaCapital+=detalle.getCapital()==null?0:detalle.getCapital();
				montoCuota+=detalle.getCuota()==null?0:detalle.getCuota();
				sumaMoraTemp+=detalle.getMora()==null?0:detalle.getMora();
				sumaInteres+=detalle.getInteres()==null?0:detalle.getInteres();
				sumaCargos+=detalle.getCargo()==null?0:detalle.getCargo();
				sumaAbonoCuota+=detalle.getAbono()==null?0:detalle.getAbono();
				sumaDescuento+=detalle.getDescuento()==null?0:detalle.getDescuento();
				sumaTotales+=detalle.getBalance()==null?0:detalle.getBalance();
			}

			if(!prestamo.getTipo().equals("2")) {
				if(formato2d(sumaCargos) > 0 || formato2d(sumaCapital) > 0 || 
						formato2d(sumaInteres) > 0 || formato2d(sumaMoraTemp) > 0) {
					if(prestamo.getMoneda().equalsIgnoreCase("pesos")) {		
						PrestamoCalculoTotal prestamoCalculoTotal = new PrestamoCalculoTotal();
						prestamoCalculoTotal.setAdicionales(formato2d(sumaCargos));
						prestamoCalculoTotal.setCapital(formato2d(sumaCapital));
						prestamoCalculoTotal.setInteres(formato2d(sumaInteres));
						prestamoCalculoTotal.setMora(formato2d(sumaMoraTemp));
						prestamoCalculoTotal.setPrestamo(prestamo);
						prestamoCalculoTotal.setBalance(formato2d(sumaTotales));
						prestamoCalculoTotal.setMoneda("pesos");
						prestamosTotales.add(prestamoCalculoTotal);
						
						totalPesoAdicionales += formato2d(sumaCargos);
						totalPesoCapital += formato2d(sumaCapital);
						totalPesoInteres += formato2d(sumaInteres);
						totalPesoMora += formato2d(sumaMoraTemp);
						
					}else if(prestamo.getMoneda().equalsIgnoreCase("dolar")) {
						PrestamoCalculoTotal prestamoCalculoTotal = new PrestamoCalculoTotal();
						prestamoCalculoTotal.setAdicionales(formato2d(sumaCargos));
						prestamoCalculoTotal.setCapital(formato2d(sumaCapital));
						prestamoCalculoTotal.setInteres(formato2d(sumaInteres));
						prestamoCalculoTotal.setMora(formato2d(sumaMoraTemp));
						prestamoCalculoTotal.setPrestamo(prestamo);
						prestamoCalculoTotal.setBalance(formato2d(sumaTotales));
						prestamoCalculoTotal.setMoneda("dolar");
						prestamosTotales.add(prestamoCalculoTotal);
						
						totalDolarAdicionales += formato2d(sumaCargos);
						totalDolarCapital += formato2d(sumaCapital);
						totalDolarInteres += formato2d(sumaInteres);
						totalDolarMora += formato2d(sumaMoraTemp);
					}
					
					totalAdicionales += formato2d(sumaCargos);
					totalCapital += formato2d(sumaCapital);
					totalInteres += formato2d(sumaInteres);
					totalMora += formato2d(sumaMoraTemp);
				}
			}					
		}

		model.addAttribute("totalCapital", moneda.equalsIgnoreCase("pesos")?formato2d(totalPesoCapital): formato2d(totalDolarCapital));
		model.addAttribute("totalInteres", moneda.equalsIgnoreCase("pesos")?formato2d(totalPesoInteres): formato2d(totalDolarInteres));
		model.addAttribute("totalMora", moneda.equalsIgnoreCase("pesos")?formato2d(totalPesoMora): formato2d(totalDolarMora));
		model.addAttribute("totalAdicional", moneda.equalsIgnoreCase("pesos")?formato2d(totalPesoAdicionales): formato2d(totalDolarAdicionales));

		model.addAttribute("prestamosTotales", prestamosTotales);
		return "contabilidad/cuentasXCobrar :: #tablaPrestamosPorCobrar";
	}	
	
	@GetMapping("/listaItbisXPagar")
	public String listaItbisXPagar(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		List<FormaPago> formasPagoItbis = serviceFormasPagos.buscarPorEmpresaIdentificador(empresa, "itbis");
		
		List<EntradaIngresoContable> entradasTemp = new LinkedList<>();
		
		for (FormaPago formaPago : formasPagoItbis) {
			double monto = 0;
			CuentaContable cuentaContableTemp = new CuentaContable();
			List<EntradaIngresoContable> entradasIngresosContables = serviceEntradasIngresosContables.
					buscarPorEmpresaCuentaContable(empresa, formaPago.getCuentaContable());
			for (EntradaIngresoContable entradaIngresoContable : entradasIngresosContables) {
				monto+=entradaIngresoContable.getBalance();
				cuentaContableTemp = entradaIngresoContable.getCuentaContable();
			}
			EntradaIngresoContable entradaTemp = new EntradaIngresoContable();
			entradaTemp.setBalance(monto);
			entradaTemp.setCuentaContable(cuentaContableTemp);
			entradasTemp.add(entradaTemp);
		}
		model.addAttribute("entradas", entradasTemp);
		return "contabilidad/itbisXPagar :: itbisXPagar";
	}
	
	@PostMapping("/agregarInversionista")
	public ResponseEntity<Integer> agregarInversionista(HttpSession session, String nombre, String telefono, 
			String direccion) {
		Integer response = 0;
		Inversionista inversionista = new Inversionista();
		inversionista.setNombre(nombre);
		inversionista.setDireccion(direccion);
		inversionista.setTelefono(telefono);
		serviceInversionistas.guardar(inversionista);
		if(inversionista.getId()!=null) {
			response = 1;
		}
		return new ResponseEntity<Integer>(response, HttpStatus.ACCEPTED);
	}
	
	@GetMapping("/guardarCuentaTempSuplidor/{id}")
	public String guardarCuentaTempSuplidor(Model model, HttpSession session, 
			@PathVariable(name = "id") Integer id) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		FormaPago formaPago = serviceFormasPagos.buscarPorId(id);
		
		SuplidorCuentaContableTemp suplidorCuentaTemp = new SuplidorCuentaContableTemp();
		suplidorCuentaTemp.setCuentaContable(formaPago.getCuentaContable());
		suplidorCuentaTemp.setEmpresa(empresa);
		suplidorCuentaTemp.setUsuario(usuario);
		suplidorCuentaTemp.setImpuesto(formaPago.getImpuesto());
		
		List<SuplidorCuentaContableTemp> listTemp = serviceSuplidoresCuentasContablesTemp.buscarPorEmpresaCuentaContable(empresa, formaPago.getCuentaContable());
		
		if(listTemp.isEmpty()) {
			serviceSuplidoresCuentasContablesTemp.guardar(suplidorCuentaTemp);
		}
		
		List<SuplidorCuentaContableTemp> cuentasTempSuplidores =  serviceSuplidoresCuentasContablesTemp.buscarPorEmpresaUsuario(empresa, usuario);
		model.addAttribute("cuentasTempSuplidores", cuentasTempSuplidores);
		return "contabilidad/compras :: #tablaCuentasTempSuplidor";
	}
	
	@GetMapping("/eliminarCuentaTempSuplidor/{id}")
	public String eliminarCuentaTempSuplidor(Model model, HttpSession session, 
			@PathVariable(name = "id") Integer id) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		SuplidorCuentaContableTemp  cuentaContableTempSuplidor = serviceSuplidoresCuentasContablesTemp.buscarPorId(id);
		serviceSuplidoresCuentasContablesTemp.eliminar(cuentaContableTempSuplidor);
		List<SuplidorCuentaContableTemp> cuentasTempSuplidores =  serviceSuplidoresCuentasContablesTemp.buscarPorEmpresaUsuario(empresa, usuario);
		model.addAttribute("cuentasTempSuplidores", cuentasTempSuplidores);
		return "contabilidad/compras :: #tablaCuentasTempSuplidor";
	}
	
	@GetMapping("/compras")
	public String compras(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		Usuario usuario = (Usuario) session.getAttribute("usuario");
		
		double totalProductoTemp = 0;
		
		List<FormaPago> cuentasProcesos = serviceFormasPagos.buscarPorEmpresaIdentificador(empresa, "procesos");
		List<Integer> activosFijos = new LinkedList<>();
		activosFijos.add(1);
		
		List<Producto> productos = serviceProductos.buscarPorEmpresaActivosFijos(empresa, activosFijos);
		List<CompraProductoTemp> productosTemp = serviceComprasProductosTemp.buscarPorEmpresaUsuario(empresa, usuario);

		for (CompraProductoTemp compraProducto : productosTemp) {
			totalProductoTemp+=compraProducto.getCantidad()*compraProducto.getCosto();
		}
		
		List<FormaPago> cuentasItbis = serviceFormasPagos.buscarPorEmpresaIdentificador(empresa, "itbis");
		List<SuplidorCuentaContableTemp> cuentasTempSuplidores =  serviceSuplidoresCuentasContablesTemp.buscarPorEmpresaUsuario(empresa, usuario);
		
		List<CuentaContable> cuentasContables = serviceCuentasContables.buscarPorEmpresa(empresa);
		model.addAttribute("cuentasContables", cuentasContables);
		model.addAttribute("cuentasTempSuplidores", cuentasTempSuplidores);
		model.addAttribute("totalProductoTemp" , formato2d(totalProductoTemp));
		List<Suplidor> suplidores = serviceSuplidores.buscarPorEmpresa(empresa);
		model.addAttribute("suplidores", suplidores);
		model.addAttribute("productos" , productos);
		model.addAttribute("fecha" , new Date());
		model.addAttribute("cuentasProcesos", cuentasProcesos);
		model.addAttribute("producto", new Producto());
		model.addAttribute("productosTemps", productosTemp);
		model.addAttribute("cuentasItbis", cuentasItbis);
		model.addAttribute("subTotal", formato2d(totalProductoTemp));
		model.addAttribute("total", formato2d(totalProductoTemp));
		return "contabilidad/compras :: compras";
	}
	
	public double formato2d(double number) {
		number = Math.round(number * 100);
		number = number/100;
		return number;
	}
	
	public double diasVencidos(LocalDateTime fecha1, LocalDateTime fecha2) throws ParseException {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
         String fecha1Temp = fecha1.getDayOfMonth()+"/"+fecha1.getMonthValue()+"/"+fecha1.getYear();
         String fecha2Temp = fecha2.getDayOfMonth()+"/"+fecha2.getMonthValue()+"/"+fecha2.getYear();
         Date dt1 = sdf.parse(fecha1Temp);
         Date dt2 = sdf.parse(fecha2Temp);
         long diff = dt2.getTime() - dt1.getTime();
         return diff / 1000L / 60L / 60L / 24L;
	}
	
	public LocalDateTime convertToLocalDateTimeViaInstant(Date dateToConvert) {
	    return dateToConvert.toInstant()
	      .atZone(ZoneId.systemDefault())
	      .toLocalDateTime();
	}
}
