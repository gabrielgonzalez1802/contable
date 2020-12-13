package com.contable.controller;

import java.math.BigDecimal;
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
import com.contable.model.CompraProductoTemp;
import com.contable.model.CuentaContable;
import com.contable.model.CuentaEnlace;
import com.contable.model.Empresa;
import com.contable.model.EntradaDiario;
import com.contable.model.EntradaDiarioTemp;
import com.contable.model.FormaPago;
import com.contable.model.Pago;
import com.contable.model.Producto;
import com.contable.model.Suplidor;
import com.contable.model.SuplidorCuentaContableTemp;
import com.contable.model.Usuario;
import com.contable.service.ICarpetasService;
import com.contable.service.IComprasProductosTempService;
import com.contable.service.IComprasService;
import com.contable.service.ICuentasContablesService;
import com.contable.service.ICuentasEnlacesService;
import com.contable.service.IEntradasDiariosService;
import com.contable.service.IEntradasDiariosTempService;
import com.contable.service.IFormasPagosService;
import com.contable.service.IPagosService;
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
	private ISuplidoresCuentasContablesTempService serviceSuplidoresCuentasContablesTemp;

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
		List<CuentaContable> cuentasContablesAuxiliares = serviceCuentasContables.buscarPorEmpresaTipoEstado(empresa, "A", 0);
		List<CuentaContable> cuentasContablesAuxiliaresIniciadas = serviceCuentasContables.buscarPorEmpresaTipoEstado(empresa, "A", 1);
		model.addAttribute("carpeta", carpeta);
		model.addAttribute("empresa", empresa);
		model.addAttribute("cuentasContablesAuxiliaresGeneral", cuentasContablesAuxiliaresGeneral);
		model.addAttribute("cuentasContablesAuxiliares", cuentasContablesAuxiliares);
		
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
		List<EntradaDiarioTemp> entradasTemp = serviceEntradasDiariosTemp.buscarPorEmpresaUsuario(empresa, usuario);
		model.addAttribute("entradasTemp", entradasTemp);
		
		double totalDebitoTemp = 0.0;
		double totalCreditoTemp = 0.0;
		
		for (EntradaDiarioTemp entradaDiarioTemp : entradasTemp) {
			totalDebitoTemp+=entradaDiarioTemp.getDebito().doubleValue();
			totalCreditoTemp+=entradaDiarioTemp.getCredito().doubleValue();
		}
		
		model.addAttribute("totalDebitoTemp", totalDebitoTemp);
		model.addAttribute("totalCreditoTemp", totalCreditoTemp);
		model.addAttribute("dif", totalDebitoTemp-totalCreditoTemp);
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
			String identificador, @RequestParam(required = false, name = "impuesto") Integer impuesto) {
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
	
	@GetMapping("/mostrarEnlacesItbis")
	public String mostrarEnlacesItbis(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		List<FormaPago> formasPagos = serviceFormasPagos.buscarPorEmpresaIdentificador(empresa, "itbis");
		model.addAttribute("formasPagos", formasPagos);
		return "contabilidad/enlaces :: #tablaEnlacesItbis";
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
	
	@GetMapping("/cuentasXPagar")
	public String cuentasXPagar(Model model, HttpSession session) {
		Empresa empresa = (Empresa) session.getAttribute("empresa");
		List<Compra> compras = serviceCompras.buscarPorEmpresa(empresa);
		
		List<FormaPago> cuentasContablesFormaPago = serviceFormasPagos.
				buscarPorEmpresaIdentificador(empresa, "formaPago");
		
		double totalBalance =0;
		
		for (Compra compra : compras) {
			totalBalance+=compra.getBalance();
		}
		
		Compra compra = new Compra();
		compra.setCuentaContable(new CuentaContable());
		compra.setUsuario(new Usuario());
		compra.setSuplidor(new Suplidor());
		
		model.addAttribute("compras", compras);
		model.addAttribute("compra", compra);
		model.addAttribute("dateAcct", new Date());
		model.addAttribute("cuentasContablesFormaPago", cuentasContablesFormaPago);
		model.addAttribute("totalBalance", totalBalance);
		return "contabilidad/cuentasXPagar :: cuentasXPagar";
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
}
